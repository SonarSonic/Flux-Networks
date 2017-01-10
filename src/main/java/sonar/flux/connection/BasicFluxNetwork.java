package sonar.flux.connection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.UUID;

import com.google.common.collect.Lists;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.common.FMLCommonHandler;
import sonar.core.api.SonarAPI;
import sonar.core.api.utils.ActionType;
import sonar.core.helpers.SonarHelper;
import sonar.core.utils.CustomColour;
import sonar.flux.FluxNetworks;
import sonar.flux.api.ClientFlux;
import sonar.flux.api.EnergyStats;
import sonar.flux.api.FluxAPI;
import sonar.flux.api.FluxPlayer;
import sonar.flux.api.IFlux;
import sonar.flux.api.IFlux.ConnectionType;
import sonar.flux.api.IFluxController;
import sonar.flux.api.IFluxController.PriorityMode;
import sonar.flux.api.IFluxController.TransferMode;
import sonar.flux.api.IFluxController.TransmitterMode;
import sonar.flux.api.IFluxNetwork;
import sonar.flux.api.PlayerAccess;
import sonar.flux.common.tileentity.TileEntityFlux;
import sonar.flux.common.tileentity.TileEntityStorage;
import sonar.flux.network.NetworkStatistics;

public class BasicFluxNetwork extends FluxNetworkCommon implements IFluxNetwork {

	// connections
	private IFluxController controller = null;
	private final HashMap<ConnectionType, ArrayList<IFlux>> connections = new HashMap();

	private ArrayList<IFlux> receivers = new ArrayList();
	private ArrayList<IFlux> senders = new ArrayList();

	// statistics
	private long maxTransfer = 0, lastTransfer;

	public BasicFluxNetwork() {
		super();
	}

	public BasicFluxNetwork(int ID, UUID owner, String name, CustomColour colour, AccessType type) {
		super(ID, owner, name, colour, type);
	}

	public ArrayList<IFlux> getReceivers() {
		ArrayList<IFlux> receivers = new ArrayList();
		receivers.addAll(connections.getOrDefault(ConnectionType.POINT, new ArrayList()));
		receivers.addAll(connections.getOrDefault(ConnectionType.STORAGE, new ArrayList()));
		receivers.addAll(connections.getOrDefault(ConnectionType.CONTROLLER, new ArrayList()));
		sortFluxNetwork(receivers, hasController() ? controller.getSendMode() : PriorityMode.DEFAULT);
		return receivers;
	}

	public ArrayList<IFlux> getSenders() {
		ArrayList<IFlux> senders = new ArrayList();
		senders.addAll(connections.getOrDefault(ConnectionType.PLUG, new ArrayList()));
		senders.addAll(connections.getOrDefault(ConnectionType.STORAGE, new ArrayList()));
		sortFluxNetwork(senders, hasController() ? controller.getSendMode() : PriorityMode.DEFAULT);
		return senders;
	}

	public void updateNetwork() {
		if (networkID.getObject() == -1) {
			return;
		}
		long maxStored = 0;
		long energyStored = 0;
		senders = getSenders();
		receivers = getReceivers();

		EnergyStats stats = new EnergyStats(0, 0, 0);
		for (IFlux plug : senders) {
			if (plug != null) {
				stats.maxSent += FluxAPI.getFluxHelper().pullEnergy(plug, plug.getTransferLimit(), ActionType.SIMULATE);
			}
		}
		for (IFlux point : receivers) {
			if (point != null) {
				stats.maxReceived += FluxAPI.getFluxHelper().pushEnergy(point, point.getTransferLimit(), ActionType.SIMULATE);
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
					long limit = FluxAPI.getFluxHelper().pullEnergy(plug, plug.getTransferLimit(), ActionType.SIMULATE);
					long currentLimit = limit;
					for (IFlux point : receivers) {
						if (currentLimit == 0) {
							break;
						}
						if (point.getConnectionType() != plug.getConnectionType()) {// storages can be both
							long toTransfer = mode == TransferMode.EVEN ? Math.min((long) Math.ceil(((double) currentLimit / (double) receivers.size())), 1) : currentLimit;
							long pointRec = FluxAPI.getFluxHelper().pushEnergy(point, toTransfer, ActionType.PERFORM);							
							currentLimit -= FluxAPI.getFluxHelper().pullEnergy(plug, pointRec, ActionType.PERFORM);
						}
					}
					networkStats.getObject().latestRecords.transfer += limit - currentLimit;

				}
				current--;
			}
		}
		networkStats.getObject().inputStatistics(stats, connections);
	}

	private void sortFluxNetwork(ArrayList<IFlux> flux, PriorityMode mode) {
		switch (mode) {
		case DEFAULT:
			break;
		case LARGEST:
			Collections.sort(flux, new Comparator<IFlux>() {
				public int compare(IFlux o1, IFlux o2) {
					return o1.getCurrentPriority() - o2.getCurrentPriority();
				}
			});
			break;
		case SMALLEST:
			Collections.sort(flux, new Comparator<IFlux>() {
				public int compare(IFlux o1, IFlux o2) {
					return o2.getCurrentPriority() - o1.getCurrentPriority();
				}
			});
			break;
		default:
			break;
		}
	}

	@Override
	public boolean hasController() {
		return controller != null;
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
		ArrayList<FluxPlayer> toDelete = new ArrayList();

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
			long toTransfer = Math.min(flux.getTransferLimit(), maxReceive - used);
			if (flux instanceof IFluxController) {
				IFluxController controller = (IFluxController) flux;
				if (controller.getTransmitterMode() == TransmitterMode.OFF) {
					break;
				}
				ArrayList<FluxPlayer> playerNames = (ArrayList<FluxPlayer>) players.clone();
				ArrayList<EntityPlayer> players = new ArrayList();
				for (FluxPlayer player : playerNames) {
					Entity entity = FMLCommonHandler.instance().getMinecraftServerInstance().getEntityFromUuid(player.id);
					if (entity != null && entity instanceof EntityPlayer) {
						players.add((EntityPlayer) entity);
					}
				}
				for (EntityPlayer player : players) {
					long receive = 0;
					switch (controller.getTransmitterMode()) {
					case HELD_ITEM:
						ItemStack stack = player.getHeldItemMainhand();
						receive = SonarAPI.getEnergyHelper().receiveEnergy(stack, toTransfer, type);
						used += receive;
						if (maxReceive - used <= 0) {
							break;
						}

						break;
					case HOTBAR:
					case ON:
						IInventory inv = player.inventory;
						for (int i = 0; i < ((controller.getTransmitterMode() == TransmitterMode.ON) ? inv.getSizeInventory() : 9); i++) {
							ItemStack itemStack = inv.getStackInSlot(i);
							receive = SonarAPI.getEnergyHelper().receiveEnergy(itemStack, toTransfer, type);
							used += receive;
							if (maxReceive - used <= 0) {
								break;
							}
						}
						break;
					default:
						break;
					}
				}
			} else {
				if (maxReceive - used <= 0) {
					break;
				}
				long receive = FluxAPI.getFluxHelper().pushEnergy(flux, toTransfer, type);
				used += receive;
			}
		}
		if (type == ActionType.PERFORM)
			networkStats.getObject().latestRecords.transfer += used;

		return used;
	}

	@Override
	public long extractEnergy(long maxExtract, ActionType type) {
		long used = 0;
		for (IFlux flux : senders) {
			long toTransfer = Math.min(flux.getTransferLimit(), maxExtract - used);
			if (maxExtract - used <= 0) {
				break;
			}
			used += FluxAPI.getFluxHelper().pullEnergy(flux, toTransfer, type);
		}
		if (type == ActionType.PERFORM)
			networkStats.getObject().latestRecords.transfer += used;

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
			connections.put(type, new ArrayList());
		}
		if (!connections.get(type).contains(flux))
			connections.get(type).add(flux);
		FluxNetworks.getServerCache().markNetworkDirty(getNetworkID());
	}

	@Override
	public void removeFluxConnection(IFlux flux) {
		ConnectionType type = flux.getConnectionType();
		if (flux instanceof IFluxController && type == ConnectionType.CONTROLLER) {
			this.setController(null);
		}
		connections.getOrDefault(flux.getConnectionType(), new ArrayList()).remove(flux);
		FluxNetworks.getServerCache().markNetworkDirty(getNetworkID());
	}

	@Override
	public void buildFluxConnections() {
		ArrayList<ClientFlux> clientConnections = new ArrayList();
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
		this.networkStats.setObject((NetworkStatistics) network.getStatistics());
		return this;
	}

}
