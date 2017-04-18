package sonar.flux.connection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.UUID;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import sonar.core.api.utils.ActionType;
import sonar.core.helpers.SonarHelper;
import sonar.core.network.sync.IDirtyPart;
import sonar.core.utils.CustomColour;
import sonar.flux.FluxNetworks;
import sonar.flux.api.ClientFlux;
import sonar.flux.api.EnergyStats;
import sonar.flux.api.FluxPlayer;
import sonar.flux.api.IFlux;
import sonar.flux.api.IFlux.ConnectionType;
import sonar.flux.api.IFluxController;
import sonar.flux.api.IFluxController.PriorityMode;
import sonar.flux.api.IFluxController.TransferMode;
import sonar.flux.api.IFluxNetwork;
import sonar.flux.api.PlayerAccess;
import sonar.flux.common.tileentity.TileEntityFlux;
import sonar.flux.common.tileentity.TileEntityStorage;
import sonar.flux.network.NetworkStatistics;

public class BasicFluxNetwork extends FluxNetworkCommon implements IFluxNetwork {

	// connections
	private IFluxController controller = null;
	private final HashMap<ConnectionType, ArrayList<IFlux>> connections = Maps.newHashMap();

	private ArrayList<IFlux> receivers = Lists.newArrayList();
	private ArrayList<IFlux> senders = Lists.newArrayList();
	public boolean updateSenders, updateReceivers;

	// statistics
	private long maxTransfer = 0, lastTransfer;

	public BasicFluxNetwork() {
		super();
	}

	public BasicFluxNetwork(int ID, UUID owner, String name, CustomColour colour, AccessType type) {
		super(ID, owner, name, colour, type);
	}

	public ArrayList<IFlux> getReceivers() {
		ArrayList<IFlux> receivers = Lists.newArrayList();
		receivers.addAll(connections.getOrDefault(ConnectionType.POINT, Lists.newArrayList()));
		receivers.addAll(connections.getOrDefault(ConnectionType.STORAGE, Lists.newArrayList()));
		receivers.addAll(connections.getOrDefault(ConnectionType.CONTROLLER, Lists.newArrayList()));
		sortFluxNetwork(receivers, hasController() ? controller.getReceiveMode() : PriorityMode.DEFAULT);
		return receivers;
	}

	public ArrayList<IFlux> getSenders() {
		ArrayList<IFlux> senders = Lists.newArrayList();
		senders.addAll(connections.getOrDefault(ConnectionType.PLUG, Lists.newArrayList()));
		senders.addAll(connections.getOrDefault(ConnectionType.STORAGE, Lists.newArrayList()));
		sortFluxNetwork(senders, hasController() ? controller.getSendMode() : PriorityMode.DEFAULT);
		return senders;
	}

	public void updateNetwork() {

		if (updateSenders) {
			senders = getSenders();
			updateSenders = false;
		}
		if (updateReceivers) {
			receivers = getReceivers();
			updateReceivers = false;
		}
		if (networkID.getObject() == -1 || receivers.isEmpty() || senders.isEmpty()) {
			return;
		}
		long maxStored = 0;
		long energyStored = 0;
		EnergyStats stats = new EnergyStats(0, 0, 0);

		for (IFlux plug : senders) {
			if (plug != null && plug.canTransfer()) {
				stats.maxSent += FluxHelper.pullEnergy(plug, plug.getCurrentTransferLimit(), ActionType.SIMULATE);
			}
		}
		for (IFlux point : receivers) {
			if (point != null && point.canTransfer()) {
				stats.maxReceived += FluxHelper.pushEnergy(point, point.getCurrentTransferLimit(), ActionType.SIMULATE);
				if (point instanceof TileEntityStorage) {
					TileEntityStorage fluxStorage = (TileEntityStorage) point;
					maxStored += fluxStorage.storage.getMaxEnergyStored();
					energyStored += fluxStorage.storage.getEnergyStored();
				}
			}
		}

		this.maxStored.setObject(maxStored);
		this.energyStored.setObject(energyStored);

		long currentTransfer = maxTransfer;
		lastTransfer = currentTransfer;
		if (stats.maxReceived != 0 && stats.maxSent != 0) {
			TransferMode mode = hasController() ? controller.getTransferMode().isBanned() ? TransferMode.DEFAULT : controller.getTransferMode() : TransferMode.DEFAULT;
			int current = mode.repeat;
			while (current != 0) {
				for (IFlux plug : senders) {
					long limit = FluxHelper.pullEnergy(plug, plug.getCurrentTransferLimit(), ActionType.SIMULATE);
					long currentLimit = limit;
					for (IFlux point : receivers) {
						if (currentLimit <= 0) {
							break;
						}
						if (point.getConnectionType() != plug.getConnectionType()) {// storages can be both
							long toTransfer = (long) (mode == TransferMode.EVEN ? Math.min(Math.ceil(((double) limit / (double) receivers.size())), currentLimit) : currentLimit);
							long pointRec = FluxHelper.pushEnergy(point, toTransfer, ActionType.PERFORM);
							currentLimit -= FluxHelper.pullEnergy(plug, pointRec, ActionType.PERFORM);
						}
					}
					networkStats.latestRecords.transfer += limit - currentLimit;
				}
				current--;
			}
		}
		networkStats.inputStatistics(stats, connections);

	}

	private void sortFluxNetwork(ArrayList<IFlux> flux, PriorityMode mode) {
		switch (mode) {
		case DEFAULT:
			break;
		case LARGEST:
			Collections.sort(flux, new Comparator<IFlux>() {
				public int compare(IFlux o1, IFlux o2) {
					return o2.getCurrentPriority() - o1.getCurrentPriority();
				}
			});
			break;
		case SMALLEST:
			Collections.sort(flux, new Comparator<IFlux>() {
				public int compare(IFlux o1, IFlux o2) {
					return o1.getCurrentPriority() - o2.getCurrentPriority();
				}
			});
			break;
		default:
			break;
		}
	}

	@Override
	public boolean hasController() {
		return controller != null && controller.getNetwork() == this;
	}

	@Override
	public IFluxController getController() {
		return controller;
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
			FluxNetworks.getServerCache().markNetworkDirty(getNetworkID());
		}
		return;
	}

	@Override
	public void setCustomColour(CustomColour colour) {
		this.colour.setObject(colour);
	}

	public boolean setController(IFluxController tile) {
		if (tile == null && connections.get(ConnectionType.CONTROLLER) != null) {
			connections.get(ConnectionType.CONTROLLER).clear();
		}
		controller = tile;
		return false;
	}

	@Override
	public void sendChanges() {
		for (Entry<ConnectionType, ArrayList<IFlux>> entry : connections.entrySet()) {
			for (IFlux flux : entry.getValue()) {
				TileEntity tile = flux.getCoords().getTileEntity();
				if (tile != null && tile instanceof TileEntityFlux) {
					((TileEntityFlux) tile).changeNetwork(this);
				}
			}
		}
		FluxNetworks.getServerCache().markNetworkDirty(getNetworkID());
	}

	@Override
	public void removePlayerAccess(UUID playerUUID, PlayerAccess access) {
		ArrayList<FluxPlayer> toDelete = Lists.newArrayList();

		for (FluxPlayer player : players) {
			if (player.getUUID().equals(playerUUID)) {
				toDelete.add(player);
			}
		}
		toDelete.forEach(delete -> players.remove(delete));
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
	public long receiveEnergy(long maxReceive, ActionType type) {
		long used = 0;
		for (IFlux flux : receivers) {
			long toTransfer = Math.min(flux.getCurrentTransferLimit(), maxReceive - used);
			if (maxReceive - used <= 0) {
				break;
			}
			long receive = FluxHelper.pushEnergy(flux, toTransfer, type);
			used += receive;
		}
		if (type == ActionType.PERFORM)
			networkStats.latestRecords.transfer += used;

		return used;
	}

	@Override
	public long extractEnergy(long maxExtract, ActionType type) {
		long used = 0;
		for (IFlux flux : senders) {
			long toTransfer = Math.min(flux.getCurrentTransferLimit(), maxExtract - used);
			if (maxExtract - used <= 0) {
				break;
			}
			used += FluxHelper.pullEnergy(flux, toTransfer, type);
		}
		if (type == ActionType.PERFORM)
			networkStats.latestRecords.transfer += used;
		return used;
	}

	@Override
	public void addFluxConnection(IFlux flux) {
		ConnectionType type = flux.getConnectionType();
		if (flux instanceof IFluxController && type == ConnectionType.CONTROLLER) {
			if (hasController()) {
				return;
			} else {
				this.setController((IFluxController) flux);
			}
		}
		if (connections.get(type) == null) {
			connections.put(type, Lists.newArrayList());
		}
		if (!connections.get(type).contains(flux))
			connections.get(type).add(flux);

		this.updateReceivers();
		this.updateSenders();
		FluxNetworks.getServerCache().markNetworkDirty(getNetworkID());
	}

	@Override
	public void removeFluxConnection(IFlux flux) {
		ConnectionType type = flux.getConnectionType();
		if (flux instanceof IFluxController && type == ConnectionType.CONTROLLER) {
			this.setController(null);
		}
		connections.getOrDefault(flux.getConnectionType(), new ArrayList<IFlux>()).removeIf(connection -> connection.getCoords().equals(flux.getCoords()));
		this.updateReceivers();
		this.updateSenders();
		FluxNetworks.getServerCache().markNetworkDirty(getNetworkID());
	}

	@Override
	public void buildFluxConnections() {
		ArrayList<ClientFlux> clientConnections = Lists.newArrayList();
		Iterator<IFlux> iterator = receivers.iterator();
		iterator.forEachRemaining(point -> clientConnections.add(new ClientFlux(point)));
		senders.iterator().forEachRemaining(plug -> {
			if (plug.getConnectionType() != ConnectionType.STORAGE) { // storage is added to both
				clientConnections.add(new ClientFlux(plug));
			}
		});
		this.fluxConnections = clientConnections;
	}

	@Override
	public PlayerAccess getPlayerAccess(EntityPlayer player) {
		UUID playerID = player.getGameProfile().getId();
		if (getOwnerUUID().equals(playerID)) {
			return PlayerAccess.OWNER;
		}
		if (accessType.getObject() != AccessType.PRIVATE) {
			for (FluxPlayer fluxPlayer : players) {
				if (playerID.equals(fluxPlayer.getUUID())) {
					return fluxPlayer.getAccess();
				}
			}
			if (accessType.getObject() == AccessType.PUBLIC) {
				return PlayerAccess.USER;
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
		this.energyStored.setObject(network.getEnergyAvailable());
		this.maxStored.setObject(network.getMaxEnergyStored());
		this.networkStats = (NetworkStatistics) network.getStatistics();
		return this;
	}

	@Override
	public void markChanged(IDirtyPart part) {
		this.parts.markSyncPartChanged(part);
		FluxNetworks.getServerCache().markNetworkDirty(getNetworkID());
	}

	@Override
	public void updateSenders() {
		this.updateSenders = true;
	}

	@Override
	public void updateReceivers() {
		this.updateReceivers = true;
	}

	@Override
	public void onDeleted() {
		for (Entry<ConnectionType, ArrayList<IFlux>> entry : connections.entrySet()) {
			entry.getValue().forEach(flux -> flux.changeNetwork(EmptyFluxNetwork.INSTANCE));
		}
	}

}
