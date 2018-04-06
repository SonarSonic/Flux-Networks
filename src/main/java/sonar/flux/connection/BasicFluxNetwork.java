package sonar.flux.connection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;

import com.google.common.collect.Lists;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import sonar.core.SonarCore;
import sonar.core.api.energy.EnergyType;
import sonar.core.api.utils.ActionType;
import sonar.core.helpers.FunctionHelper;
import sonar.core.helpers.ListHelper;
import sonar.core.helpers.NBTHelper.SyncType;
import sonar.core.helpers.SonarHelper;
import sonar.core.network.PacketTileSync;
import sonar.core.network.sync.IDirtyPart;
import sonar.core.utils.CustomColour;
import sonar.flux.FluxConfig;
import sonar.flux.FluxNetworks;
import sonar.flux.api.AccessType;
import sonar.flux.api.AdditionType;
import sonar.flux.api.ClientFlux;
import sonar.flux.api.FluxListener;
import sonar.flux.api.RemovalType;
import sonar.flux.api.network.FluxCache;
import sonar.flux.api.network.FluxPlayer;
import sonar.flux.api.network.IFluxNetwork;
import sonar.flux.api.network.PlayerAccess;
import sonar.flux.api.tiles.IFluxController;
import sonar.flux.api.tiles.IFluxController.TransferMode;
import sonar.flux.api.tiles.IFluxListenable;
import sonar.flux.api.tiles.IFluxPlug;
import sonar.flux.api.tiles.IFluxPoint;
import sonar.flux.api.tiles.IFluxStorage;
import sonar.flux.common.tileentity.TileFlux;
import sonar.flux.connection.transfer.stats.NetworkStatistics;
import sonar.flux.network.FluxNetworkCache;
import sonar.flux.network.PacketFluxConnectionsList;
import sonar.flux.network.PacketFluxNetworkList;
import sonar.flux.network.PacketNetworkStatistics;

public class BasicFluxNetwork extends FluxNetworkCommon implements IFluxNetwork {

	// connections
	public HashMap<FluxCache, List<IFluxListenable>> connections = new HashMap<>();
	public List<FluxCache> changedTypes = Lists.newArrayList(FluxCache.types);
	public Queue<IFluxListenable> toAdd = new ConcurrentLinkedQueue<>();
	public Queue<IFluxListenable> toRemove = new ConcurrentLinkedQueue<>();
	public List<IFluxListenable> flux_listeners = new ArrayList<>();
	public List<ClientFlux> unloaded = new ArrayList<>();
	public boolean hasConnections;

	public BasicFluxNetwork() {
		super();
	}

	public BasicFluxNetwork(int ID, UUID owner, String name, CustomColour colour, AccessType type, boolean disableConvert, EnergyType defaultEnergy) {
		super(ID, owner, name, colour, type, disableConvert, defaultEnergy);
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
				if (((List<IFluxListenable>)getConnections(type)).removeIf(F -> F.getCoords().equals(tile.getCoords()))) {
					type.disconnect(this, tile);
					markTypeDirty(type);
				}
			});
			iterator.remove();
		}
	}

	// TODO way to quickly update priorities
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

	public TransferMode getTransferMode() {
		IFluxController controller = getController();
		return controller != null ? controller.getTransferMode().isBanned() ? TransferMode.DEFAULT : controller.getTransferMode() : TransferMode.DEFAULT;
	}

	public void onStartServerTick() {
		addConnections();
		removeConnections();
		updateTypes();
		this.networkStats.onStartServerTick();
		List<IFluxStorage> storage = getConnections(FluxCache.storage);
		List<IFluxPoint> points = getConnections(FluxCache.point);
		if (!storage.isEmpty() && !points.isEmpty()) {
			storage.forEach(s -> FluxHelper.transferEnergy(s, points, getDefaultEnergyType(), TransferMode.DEFAULT));
		}
	}

	@Override
	public void onEndServerTick() {
		this.networkStats.onEndWorldTick();
		if (!this.flux_listeners.isEmpty()) {
			sendPacketToListeners();
		}
	}

	public int listenerTicks = 0;

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
	public void removePlayerAccess(UUID playerUUID, PlayerAccess access) {
		List<FluxPlayer> toDelete = new ArrayList<>();

		players.stream().filter(p -> p.getUUID().equals(playerUUID)).forEach(toDelete::add);
		players.removeAll(toDelete);
	}

	@Override
	public void addPlayerAccess(UUID playerUUID, PlayerAccess access) {
		for (FluxPlayer player : players) {
			if (player.getUUID().equals(playerUUID)) {
				player.setAccess(access);
				return;
			}
		}
		FluxPlayer player = new FluxPlayer(playerUUID, access);
		player.cachedName = SonarHelper.getProfileByUUID(playerUUID).getName();
		players.add(player);
	}

	@Override
	public long addPhantomEnergyToNetwork(long maxReceive, EnergyType energyType, ActionType type) {
		long used = 0;
		List<IFluxPoint> points = getConnections(FluxCache.point);
		for (IFluxPoint flux : points) {
			long toTransfer = maxReceive - used;
			if (toTransfer <= 0) {
				break;
			}
			long receive = FluxHelper.removeEnergyFromNetwork(flux, energyType, toTransfer, type);
			used += receive;
		}
		return used;
	}

	@Override
	public long removePhantomEnergyFromNetwork(long maxExtract, EnergyType energyType, ActionType type) {
		long used = 0;
		List<IFluxPlug> plugs = getConnections(FluxCache.plug);
		for (IFluxPlug flux : plugs) {
			long toTransfer = maxExtract - used;
			if (toTransfer <= 0) {
				break;
			}
			used += FluxHelper.addEnergyToNetwork(flux, energyType, toTransfer, type);
		}
		return used;
		// return 0;
	}

	@Override
	public void addConnection(IFluxListenable tile, AdditionType type) {
		toAdd.add(tile);
		toRemove.remove(tile); // prevents tiles being removed if it's unnecessary
		unloaded.removeIf(flux -> flux != null && flux.coords.equals(tile.getCoords()));
	}

	@Override
	public void removeConnection(IFluxListenable tile, RemovalType type) {
		toRemove.add(tile);
		toAdd.remove(tile); // prevents tiles being removed if it's unnecessary
		if (type == RemovalType.CHUNK_UNLOAD) {
			ClientFlux flux_unload = new ClientFlux(tile);
			flux_unload.setChunkLoaded(false);
			unloaded.add(flux_unload);
		}
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

    public List<IFluxListenable> getFluxListeners(){
    	return this.flux_listeners;
    }
    
	@Override
	public PlayerAccess getPlayerAccess(EntityPlayer player) {
		if (FluxHelper.isPlayerAdmin(player)) {
			return PlayerAccess.CREATIVE;
		}
		UUID playerID = FluxHelper.getOwnerUUID(player);
		if (getOwnerUUID().equals(playerID)) {
			return PlayerAccess.OWNER;
		}
		if (accessType.getObject() != AccessType.PRIVATE) {
			if (accessType.getObject() == AccessType.PUBLIC) {
				return PlayerAccess.SHARED_OWNER;
			}
			for (FluxPlayer fluxPlayer : players) {
				if (playerID.equals(fluxPlayer.getUUID())) {
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
		this.networkStats = (NetworkStatistics) network.getStatistics();
		return this;
	}

	@Override
	public void markChanged(IDirtyPart part) {
		this.parts.markSyncPartChanged(part);
		// this.markDirty();
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
	}

	public void setHasConnections(boolean bool) {
		hasConnections = bool;
	}

	@Override
	public boolean canConvert(EnergyType from, EnergyType to) {
		return (from == to || !disabledConversion() && FluxConfig.conversion.get(from).contains(to)) || FluxConfig.conversion_override.get(from).contains(to);
	}

	@Override
	public boolean canTransfer(EnergyType type) {
		return type == getDefaultEnergyType() || canConvert(type, getDefaultEnergyType());
	}
}
