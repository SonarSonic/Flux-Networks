package sonar.flux.connection;

import com.google.common.collect.Lists;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.MinecraftForge;
import sonar.core.api.energy.EnergyType;
import sonar.core.api.utils.ActionType;
import sonar.core.helpers.FunctionHelper;
import sonar.core.helpers.ListHelper;
import sonar.core.listener.ListenerList;
import sonar.core.listener.PlayerListener;
import sonar.core.utils.CustomColour;
import sonar.flux.FluxNetworks;
import sonar.flux.api.*;
import sonar.flux.api.network.FluxCache;
import sonar.flux.api.network.FluxPlayer;
import sonar.flux.api.network.IFluxNetwork;
import sonar.flux.api.network.PlayerAccess;
import sonar.flux.api.tiles.IFlux;
import sonar.flux.api.tiles.IFluxListenable;
import sonar.flux.api.tiles.IFluxPlug;
import sonar.flux.api.tiles.IFluxPoint;
import sonar.flux.common.events.FluxConnectionEvent;
import sonar.flux.common.events.FluxNetworkEvent;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;

public class FluxNetworkServer extends FluxNetworkBase implements IFluxNetwork {

	// connections
	public HashMap<FluxCache, List<IFluxListenable>> connections = new HashMap<>();
	public Queue<IFluxListenable> toAdd = new ConcurrentLinkedQueue<>();
	public Queue<IFluxListenable> toRemove = new ConcurrentLinkedQueue<>();
	public List<IFluxListenable> flux_tile_listeners = new ArrayList<>();
	public Map<Integer, ListenerList<PlayerListener>> flux_stack_listeners = new HashMap<>();
	public boolean sortConnections = true;
	public long buffer_limiter = 0;

	//TODO network transfer limits
	public long network_transfer_limit = Long.MAX_VALUE;

    public List<PriorityGrouping<IFluxPlug>> sorted_plugs = new ArrayList<>();
    public List<PriorityGrouping<IFluxPoint>> sorted_points = new ArrayList<>();

	public FluxNetworkServer() {
		super();
	}

	public FluxNetworkServer(int ID, UUID playerUUID, String playerName, String networkName, CustomColour colour, AccessType type, boolean disableConvert, EnergyType defaultEnergy) {
		super(ID, playerUUID, playerName, networkName, colour, type, disableConvert, defaultEnergy);
	}

	public void addConnections() {
		if (toAdd.isEmpty())
			return;
		Iterator<IFluxListenable> iterator = toAdd.iterator();
		while (iterator.hasNext()) {
			IFluxListenable tile = iterator.next();
			FluxCache.getValidTypes(tile).forEach(type -> ListHelper.addWithCheck(getConnections(type), tile));
			MinecraftForge.EVENT_BUS.post(new FluxConnectionEvent.Connected(tile, this));
			iterator.remove();
			sortConnections = true;
		}
	}

	public void removeConnections() {
		if (toRemove.isEmpty())
			return;
		Iterator<IFluxListenable> iterator = toRemove.iterator();
		while (iterator.hasNext()) {
			IFluxListenable tile = iterator.next();
			FluxCache.getValidTypes(tile).forEach(type -> ((List<IFluxListenable>)getConnections(type)).removeIf(F -> F == tile));
			MinecraftForge.EVENT_BUS.post(new FluxConnectionEvent.Disconnected(tile, this));
			iterator.remove();
			sortConnections = true;
		}
	}

	public <T extends IFluxListenable> List<T> getConnections(FluxCache<T> type) {
		return (List<T>) connections.computeIfAbsent(type, FunctionHelper.ARRAY);
	}

	public void onStartServerTick() {
		this.network_stats.getValue().onStartServerTick();
	}

	private TransferIterator<IFluxPlug> PLUG_ITERATOR = new TransferIterator<>();
	private TransferIterator<IFluxPoint> POINT_ITERATOR = new TransferIterator<>();

    @Override
    public void onEndServerTick() {
		addConnections();
		removeConnections();
		if(sortConnections){
			sortConnections();
			sortConnections = false;
		}
		buffer_limiter = 0;
		sorted_points.forEach(g -> g.getEntries().forEach(p -> buffer_limiter += p.getTransferHandler().removeFromNetwork(p.getTransferLimit(), network_energy_type.getValue(), ActionType.SIMULATE)));

		////we iterate through points as this causes less overhead, as any point transfer involves interactions with other tiles
		if(!sorted_plugs.isEmpty() && !sorted_points.isEmpty()) {
			POINT_ITERATOR.update(sorted_points, network_energy_type.getValue(), 1);
			POINTS:	while (POINT_ITERATOR.hasNext()) {
				IFluxPoint point = POINT_ITERATOR.getCurrentFlux();
				PLUG_ITERATOR.update(sorted_plugs, network_energy_type.getValue(), 0);
				while (PLUG_ITERATOR.hasNext()) {
					IFluxPlug plug = PLUG_ITERATOR.getCurrentFlux();
					if (plug.getConnectionType() != point.getConnectionType()) { // storages are both points and plugs
						long max_pull = plug.getTransferHandler().addToNetwork(plug.getTransferLimit(), network_energy_type.getValue(), ActionType.SIMULATE);
						long max_push = point.getTransferHandler().removeFromNetwork(max_pull, network_energy_type.getValue(), ActionType.SIMULATE);
						if (max_push > 0) {
							long pulled = plug.getTransferHandler().addToNetwork(max_push, network_energy_type.getValue(), ActionType.PERFORM);
							long pushed = point.getTransferHandler().removeFromNetwork(pulled, network_energy_type.getValue(), ActionType.PERFORM);
						}
					}
					PLUG_ITERATOR.incrementFlux();
				}
				POINT_ITERATOR.incrementFlux();
			}
		}
        this.network_stats.getValue().onEndWorldTick();
        if (!this.flux_tile_listeners.isEmpty()) {
            sendPacketToListeners();
        }
        if(isDirty()) {
			MinecraftForge.EVENT_BUS.post(new FluxNetworkEvent.SettingsChanged(this));
			watched_values.forEach(value -> value.setDirty(false));
			setDirty(false);
		}
		flushDirtySettings();
    }

    public Map<ConnectionSettings, List<IFlux>> dirtySettings = new HashMap<>();

    public void markSettingDirty(ConnectionSettings setting, IFlux flux){
    	dirtySettings.putIfAbsent(setting, new ArrayList<>());
    	dirtySettings.get(setting).add(flux);
	}

	public boolean isSettingDirty(ConnectionSettings setting){
		return dirtySettings.containsKey(setting);
	}

	public List<IFlux> getChangedConnections(ConnectionSettings setting){
		return dirtySettings.get(setting);
	}

	public void flushDirtySettings(){
		dirtySettings.clear();
	}

	public void sendPacketToListeners() {
		for (FluxListener type : FluxListener.values()) {
			type.syncPacket.sync(this);
		}
	}

	public void markDirty() {
		connectAll();
	}

	public void removePlayerAccess(UUID uuid, PlayerAccess access){
		List<FluxPlayer> toDelete = new ArrayList<>();

		network_players.getValue().stream().filter(p -> p.getOnlineUUID().equals(uuid) || p.getOfflineUUID().equals(uuid)).forEach(toDelete::add);
		network_players.getValue().removeAll(toDelete);
	}

	public Optional<FluxPlayer> getValidFluxPlayer(UUID uuid){
		return network_players.getValue().stream().filter(p -> p.getOnlineUUID().equals(uuid) || p.getOfflineUUID().equals(uuid)).findFirst();
	}

	@Override
	public boolean isFakeNetwork() {
		return false;
	}

	public void addPlayerAccess(String username, PlayerAccess access){
		FluxPlayer created = FluxPlayer.createFluxPlayer(username, access);
		for (FluxPlayer player : network_players.getValue()) {
			if (created.getOnlineUUID().equals(player.getOnlineUUID()) || created.getOfflineUUID().equals(player.getOfflineUUID())) {
				player.setAccess(access);
				return;
			}
		}
		network_players.getValue().add(created);
	}

	public void queueConnectionAddition(IFluxListenable tile, AdditionType type) {
		toAdd.add(tile);
		toRemove.remove(tile); // prevents tiles being removed if it's unnecessary
        removeFromUnloaded(tile);
	}

	public void queueConnectionRemoval(IFluxListenable tile, RemovalType type) {
		toRemove.add(tile);
		toAdd.remove(tile); // prevents tiles being added if it's unnecessary
		if (type == RemovalType.CHUNK_UNLOAD) {
            addToUnloaded(tile);
		}else{
            removeFromUnloaded(tile);
		}
	}

	private void addToUnloaded(IFluxListenable tile){
    	boolean contains = unloaded_connections.getValue().stream().anyMatch(f -> f != null && f.getCoords().equals(tile.getCoords()));
    	if(!contains) {
			ClientFlux flux_unload = new ClientFlux(tile);
			flux_unload.setChunkLoaded(false);
			unloaded_connections.getValue().add(flux_unload);
		}
	}

	private void removeFromUnloaded(IFluxListenable tile){
		unloaded_connections.getValue().removeIf(f -> f != null && f.getCoords().equals(tile.getCoords()));
	}

    private void sortConnections(){
        sorted_plugs.clear();
        sorted_points.clear();
        List<IFluxPlug> plugs = getConnections(FluxCache.plug);
        List<IFluxPoint> points = getConnections(FluxCache.point);
        plugs.forEach(P -> PriorityGrouping.getOrCreateGrouping(P.getCurrentPriority(), sorted_plugs).getEntries().add(P));
        points.forEach(P -> PriorityGrouping.getOrCreateGrouping(P.getCurrentPriority(), sorted_points).getEntries().add(P));
		sorted_plugs.sort(Comparator.comparingInt(g -> -g.getPriority()));
        sorted_points.sort(Comparator.comparingInt(g -> -g.getPriority()));
    }

	public void buildFluxConnections() {
		List<ClientFlux> clientConnections = new ArrayList<>();
		List<IFluxListenable> connections = getConnections(FluxCache.flux);
		connections.forEach(flux -> clientConnections.add(new ClientFlux(flux)));
		clientConnections.addAll(unloaded_connections.getValue());
		this.client_connections.setValueInternal(clientConnections);
	}

	public void connectAll() {
		forEachConnection(FluxCache.flux, flux -> MinecraftForge.EVENT_BUS.post(new FluxConnectionEvent.Connected(flux, this)));
	}

	public void disconnectAll() {
		forEachConnection(FluxCache.flux, flux -> MinecraftForge.EVENT_BUS.post(new FluxConnectionEvent.Disconnected(flux, this)));
	}

	public void forEachConnection(FluxCache type, Consumer<? super IFluxListenable> action) {
		getConnections(type).forEach(action);
	}

	public void forEachViewer(FluxListener listener, Consumer<EntityPlayerMP> action) {
		forEachConnection(FluxCache.flux, f -> f.getListenerList().getListeners(listener).forEach(p -> action.accept(p.player)));
	}

	public void onRemoved() {
		disconnectAll();
		connections.clear();
		toAdd.clear();
		toRemove.clear();
		sorted_plugs.clear();
		sorted_points.clear();
	}

	public boolean canConvert(EnergyType from, EnergyType to) {
		return (from == to || network_conversion.getValue() && FluxNetworks.TRANSFER_HANDLER.getProxy().canConvert(to, from)) || FNEnergyTransferProxy.checkOverride(to, from);
	}

	public boolean canTransfer(EnergyType type) {
		return type == network_energy_type.getValue() || canConvert(type, network_energy_type.getValue());
	}

	public void debugConnectedBlocks() {
		List<IFluxListenable> flux = getConnections(FluxCache.flux);
		flux.forEach(f -> f.getTransferHandler().updateTransfers(EnumFacing.VALUES));
	}

	public void debugValidateFluxConnections() {
		List<IFluxListenable> flux = Lists.newArrayList(getConnections(FluxCache.flux));
		
		flux.forEach(f -> queueConnectionRemoval(f, RemovalType.REMOVE));
		removeConnections();

		List<IFluxListenable> copy = new ArrayList<>();
		for (IFluxListenable fl : flux) {
			boolean match = copy.removeIf(f -> f.getCoords()!=null && f.getCoords().equals(fl.getCoords()));
			if (!match) {
				copy.add(fl);
			} else {
				TileEntity tile = fl.getCoords().getTileEntity();
				if (tile instanceof IFluxListenable) {
					copy.add((IFluxListenable) tile);
				}
			}
		}
		
		copy.forEach(f -> this.queueConnectionAddition(f, AdditionType.ADD));
		addConnections();
		buildFluxConnections();
	}

	public boolean hasGuiListeners(){
    	return !flux_tile_listeners.isEmpty() || !flux_stack_listeners.isEmpty();
	}

	public void forEachListener(FluxListener listener, Consumer<PlayerListener> action){
		flux_tile_listeners.forEach(flux -> flux.getListenerList().getListeners(listener).forEach(action));
		flux_stack_listeners.values().forEach(list -> list.getListeners(listener).forEach(action));
	}
}
