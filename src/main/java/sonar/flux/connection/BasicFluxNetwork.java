package sonar.flux.connection;

import com.google.common.collect.Lists;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import sonar.core.api.energy.EnergyType;
import sonar.core.api.utils.ActionType;
import sonar.core.helpers.FunctionHelper;
import sonar.core.helpers.ListHelper;
import sonar.core.network.sync.IDirtyPart;
import sonar.core.utils.CustomColour;
import sonar.flux.FluxNetworks;
import sonar.flux.api.*;
import sonar.flux.api.network.FluxCache;
import sonar.flux.api.network.FluxPlayer;
import sonar.flux.api.network.IFluxNetwork;
import sonar.flux.api.network.PlayerAccess;
import sonar.flux.api.tiles.IFluxController;
import sonar.flux.api.tiles.IFluxListenable;
import sonar.flux.api.tiles.IFluxPlug;
import sonar.flux.api.tiles.IFluxPoint;
import sonar.flux.network.FluxNetworkCache;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;

public class BasicFluxNetwork extends FluxNetworkCommon implements IFluxNetwork {

	// connections
	public HashMap<FluxCache, List<IFluxListenable>> connections = new HashMap<>();
	public List<FluxCache> changedTypes = Lists.newArrayList(FluxCache.types);
	public Queue<IFluxListenable> toAdd = new ConcurrentLinkedQueue<>();
	public Queue<IFluxListenable> toRemove = new ConcurrentLinkedQueue<>();
	public List<IFluxListenable> flux_listeners = new ArrayList<>();
	public List<ClientFlux> unloaded = new ArrayList<>();
	public boolean hasConnections;
	public boolean sortConnections = true;
	public long buffer_limiter = 0;

	//TODO network transfer limits
	public long network_transfer_limit = Long.MAX_VALUE;

    public List<PriorityGrouping<IFluxPlug>> sorted_plugs = new ArrayList<>();
    public List<PriorityGrouping<IFluxPoint>> sorted_points = new ArrayList<>();

	public BasicFluxNetwork() {
		super();
	}

	public BasicFluxNetwork(int ID, UUID playerUUID, String playerName, String networkName, CustomColour colour, AccessType type, boolean disableConvert, EnergyType defaultEnergy) {
		super(ID, playerUUID, playerName, networkName, colour, type, disableConvert, defaultEnergy);
	}

	public void addConnections() {
		if (toAdd.isEmpty())
			return;
		Iterator<IFluxListenable> iterator = toAdd.iterator();
		while (iterator.hasNext()) {
			IFluxListenable tile = iterator.next();
			FluxCache.getValidTypes(tile).forEach(type -> {
				if (!getConnections(type).contains(tile) && getConnections(type).add(tile)) {
					type.connect(this, tile);
					markTypeDirty(type);
				}
			});
			tile.connect(this);
			iterator.remove();
		}
	}

	public void removeConnections() {
		if (toRemove.isEmpty())
			return;
		Iterator<IFluxListenable> iterator = toRemove.iterator();
		while (iterator.hasNext()) {
			IFluxListenable tile = iterator.next();
			FluxCache.getValidTypes(tile).forEach(type -> {
				if (((List<IFluxListenable>) getConnections(type)).removeIf(F -> F.getCoords().equals(tile.getCoords()))) {
					type.disconnect(this, tile);
					markTypeDirty(type);
				}
			});
			iterator.remove();
		}
	}

	public void markConnectionsForSorting(){
	    this.sortConnections = true;
    }

	public void markTypeDirty(FluxCache... caches) {
		for (FluxCache cache : caches) {
			if (!changedTypes.contains(cache)) {
				changedTypes.add(cache);
			}
		}
	}

	public void updateTypes() {
		if (!changedTypes.isEmpty()) {
			changedTypes.forEach(type -> type.update(this));
			changedTypes.clear();
		}
	}

	public <T extends IFluxListenable> List<T> getConnections(FluxCache<T> type) {
		return connections.computeIfAbsent(type, FunctionHelper.ARRAY);
	}

	public void onStartServerTick() {
		addConnections();
		removeConnections();
		updateTypes();
		if(sortConnections){
		    sortConnections();
            sortConnections = false;
        }
		this.networkStats.onStartServerTick();
	}

	private TransferIterator<IFluxPlug> PLUG_ITERATOR = new TransferIterator<>();
	private TransferIterator<IFluxPoint> POINT_ITERATOR = new TransferIterator<>();

    @Override
    public void onEndServerTick() {
		buffer_limiter = 0;
		sorted_points.forEach(g -> g.getEntries().forEach(p -> buffer_limiter += p.getTransferHandler().removeFromNetwork(p.getTransferLimit(), getDefaultEnergyType(), ActionType.SIMULATE)));;

		////we iterate through points as this causes less overhead, as any point transfer involves interactions with other tiles
		if(!sorted_plugs.isEmpty() && !sorted_points.isEmpty()) {
			POINT_ITERATOR.update(sorted_points, getDefaultEnergyType(), 1);
			POINTS:	while (POINT_ITERATOR.hasNext()) {
				IFluxPoint point = POINT_ITERATOR.getCurrentFlux();
				PLUG_ITERATOR.update(sorted_plugs, this.getDefaultEnergyType(), 0);
				while (PLUG_ITERATOR.hasNext()) {
					IFluxPlug plug = PLUG_ITERATOR.getCurrentFlux();
					if (plug.getConnectionType() != point.getConnectionType()) { // storages are both points and plugs
						long max_pull = plug.getTransferHandler().addToNetwork(plug.getTransferLimit(), getDefaultEnergyType(), ActionType.SIMULATE);
						long max_push = point.getTransferHandler().removeFromNetwork(max_pull, getDefaultEnergyType(), ActionType.SIMULATE);
						if (max_push > 0) {
							long pulled = plug.getTransferHandler().addToNetwork(max_push, getDefaultEnergyType(), ActionType.PERFORM);
							long pushed = point.getTransferHandler().removeFromNetwork(pulled, getDefaultEnergyType(), ActionType.PERFORM);
						}
					}
					PLUG_ITERATOR.incrementFlux();
				}
				POINT_ITERATOR.incrementFlux();
			}
		}
        this.networkStats.onEndWorldTick();
        if (!this.flux_listeners.isEmpty()) {
            sendPacketToListeners();
        }
    }

	/**pull energy into the network, you must update the group current addition before calling this*/
	/*
	private <T extends IFlux> long pullFromGrouping(PriorityGrouping<T> group, long maxPull, EnergyType energyType, ActionType type) {
		long actualTransfer = Math.min(group.updateTotalAddition(getDefaultEnergyType(), maxPull), maxPull);
		if(actualTransfer <= 0){
			return 0;
		}
		long pulled = 0;
		while(pulled < actualTransfer) {
			for (T flux : group.getEntries()) {
				long allowedTransfer = group.getAllowedAddition(flux, actualTransfer - pulled);
				pulled += flux.getTransferHandler().addToNetwork(allowedTransfer, energyType, type);
				if (pulled >= actualTransfer) {
					break;
				}
			}
		}
		return pulled;
	}

	/**push energy out of the network, you must update the group current removal before calling this*/
	/*
    private <T extends IFlux> long pushFromGrouping(PriorityGrouping<T> group, long maxPush, EnergyType energyType, ActionType type) {
		long actualTransfer = Math.min(group.updateTotalRemoval(getDefaultEnergyType(), maxPush), maxPush);
		if(actualTransfer <= 0){
			return 0;
		}
		long pushed = 0;
		while(pushed < actualTransfer) {
			for (T flux : group.getEntries()) {
				long allowedTransfer = group.getAllowedRemoval(flux, actualTransfer - pushed);
				pushed += flux.getTransferHandler().removeFromNetwork(allowedTransfer, energyType, type);

				if (pushed >= actualTransfer) {
					break;
				}
			}
		}
        return pushed;
    }

    */

	public void sendPacketToListeners() {
		FluxListener.SYNC_INDEX.sendPackets(this, flux_listeners);
		FluxListener.SYNC_NETWORK_STATS.sendPackets(this, flux_listeners);
		FluxListener.SYNC_NETWORK_CONNECTIONS.sendPackets(this, flux_listeners);
	}

	@Override
	public boolean hasController() {
		return getController() != null;
	}

	@Override
	public IFluxController getController() {
		List<IFluxController> flux = getConnections(FluxCache.controller);
		return !flux.isEmpty() ? flux.get(0) : null;
	}

	@Override
	public void setNetworkName(String name) {
		if (name != null && !name.isEmpty())
			networkName.setObject(name);
	}

	@Override
	public void setAccessType(AccessType type) {
		if (type != null) {
			accessType.setObject(type);
			markDirty();
		}
	}

	@Override
	public void setCustomColour(CustomColour colour) {
		this.colour.setObject(colour);
	}

	@Override
	public void setDisableConversion(boolean disable) {
		this.disableConversion.setObject(disable);
	}

	@Override
	public void setDefaultEnergyType(EnergyType type) {
		this.defaultEnergyType.setEnergyType(type);
	}

	public void markDirty() {
		connectAll();
		FluxNetworkCache.instance().onNetworksChanged();
	}

	@Override
	public void removePlayerAccess(UUID uuid, PlayerAccess access){
		List<FluxPlayer> toDelete = new ArrayList<>();

		players.stream().filter(p -> p.getOnlineUUID().equals(uuid) || p.getOfflineUUID().equals(uuid)).forEach(toDelete::add);
		players.removeAll(toDelete);
	}

	@Override
	public Optional<FluxPlayer> getValidFluxPlayer(UUID uuid){
		return players.stream().filter(p -> p.getOnlineUUID().equals(uuid) || p.getOfflineUUID().equals(uuid)).findFirst();
	}

	@Override
	public void addPlayerAccess(String username, PlayerAccess access){
		FluxPlayer created = FluxPlayer.createFluxPlayer(username, access);
		for (FluxPlayer player : players) {
			if (created.getOnlineUUID().equals(player.getOnlineUUID()) || created.getOfflineUUID().equals(player.getOfflineUUID())) {
				player.setAccess(access);
				return;
			}
		}
		players.add(created);
	}

	@Override
	public void addConnection(IFluxListenable tile, AdditionType type) {
		toAdd.add(tile);
		toRemove.remove(tile); // prevents tiles being removed if it's unnecessary
        removeFromUnloaded(tile);
	}

	@Override
	public void removeConnection(IFluxListenable tile, RemovalType type) {
		toRemove.add(tile);
		toAdd.remove(tile); // prevents tiles being added if it's unnecessary
		if (type == RemovalType.CHUNK_UNLOAD) {
            addToUnloaded(tile);
		}else{
            removeFromUnloaded(tile);
		}
	}

	public void addToUnloaded(IFluxListenable tile){
    	boolean contains = unloaded.stream().anyMatch(f -> f != null && f.getCoords().equals(tile.getCoords()));
    	if(!contains) {
			ClientFlux flux_unload = new ClientFlux(tile);
			flux_unload.setChunkLoaded(false);
			unloaded.add(flux_unload);
		}
	}

	public void removeFromUnloaded(IFluxListenable tile){
		unloaded.removeIf(f -> f != null && f.getCoords().equals(tile.getCoords()));
	}

	@Override
	public void changeConnection(IFluxListenable flux){
		FluxCache.getValidTypes(flux).forEach(this::markTypeDirty);
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

	@Override
	public void buildFluxConnections() {
		List<ClientFlux> clientConnections = new ArrayList<>();
		List<IFluxListenable> connections = getConnections(FluxCache.flux);
		connections.forEach(flux -> clientConnections.add(new ClientFlux(flux)));
		clientConnections.addAll(unloaded);
		this.fluxConnections = clientConnections;
	}

	@Override
	public void addFluxListener(IFluxListenable listener) {
		ListHelper.addWithCheck(flux_listeners, listener);
		for (FluxListener listen : FluxListener.values()) {
			listen.sendPackets(this, Lists.newArrayList(listener));
		}
	}

	@Override
	public void removeFluxListener(IFluxListenable listener) {
		flux_listeners.removeIf(f -> f == listener);
	}

	public List<IFluxListenable> getFluxListeners() {
		return this.flux_listeners;
	}

	@Override
	public PlayerAccess getPlayerAccess(EntityPlayer player) {
		if (FluxHelper.isPlayerAdmin(player)) {
			return PlayerAccess.CREATIVE;
		}
		if (isOwner(player)) {
			return PlayerAccess.OWNER;
		}
		if (accessType.getObject() == AccessType.PUBLIC) {
			return PlayerAccess.SHARED_OWNER;
		}
		if (accessType.getObject() == AccessType.RESTRICTED) {
			for (FluxPlayer fluxPlayer : players) {
				if (fluxPlayer.matches(player)) {
					return fluxPlayer.getAccess();
				}
			}
		}
		return PlayerAccess.BLOCKED;
	}

	@Override
	public IFluxNetwork updateNetworkFrom(IFluxNetwork network) {
		this.setAccessType(network.getAccessType());
		this.setCustomColour(network.getNetworkColour());
		this.setNetworkName(network.getNetworkName());
		this.players = network.getPlayers();
		this.networkStats = network.getStatistics();
		return this;
	}

	@Override
	public void markChanged(IDirtyPart part) {
		this.parts.markSyncPartChanged(part);
	}

	public void connectAll() {
		forEachConnection(FluxCache.flux, flux -> flux.connect(this));
	}

	public void disconnectAll() {
		forEachConnection(FluxCache.flux, flux -> flux.disconnect(this));
	}

	public void forEachConnection(FluxCache type, Consumer<? super IFluxListenable> action) {
		getConnections(type).forEach(action);
	}

	public void forEachViewer(FluxListener listener, Consumer<EntityPlayerMP> action) {
		forEachConnection(FluxCache.flux, f -> f.getListenerList().getListeners(listener).forEach(p -> action.accept(p.player)));
	}

	@Override
	public void onRemoved() {
		disconnectAll();
		connections.clear();
		toAdd.clear();
		toRemove.clear();
		sorted_plugs.clear();
		sorted_points.clear();
	}

	public void setHasConnections(boolean bool) {
		hasConnections = bool;
	}

	@Override
	public boolean canConvert(EnergyType from, EnergyType to) {
		return (from == to || !disabledConversion() && FluxNetworks.TRANSFER_HANDLER.getProxy().canConvert(to, from)) || FNEnergyTransferProxy.checkOverride(to, from);
	}

	@Override
	public boolean canTransfer(EnergyType type) {
		return type == getDefaultEnergyType() || canConvert(type, getDefaultEnergyType());
	}

	@Override
	public void debugConnectedBlocks() {
		List<IFluxListenable> flux = getConnections(FluxCache.flux);
		flux.forEach(f -> f.getTransferHandler().updateTransfers(EnumFacing.VALUES));
	}

	@Override
	public void debugValidateFluxConnections() {
		List<IFluxListenable> flux = Lists.newArrayList(getConnections(FluxCache.flux));
		
		flux.forEach(f -> removeConnection(f, RemovalType.REMOVE));
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
		
		copy.forEach(f -> this.addConnection(f, AdditionType.ADD));
		addConnections();
		buildFluxConnections();
	}
}
