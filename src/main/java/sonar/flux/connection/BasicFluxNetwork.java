package sonar.flux.connection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
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
	private final ArrayList<IFlux> plugs = new ArrayList();
	private final ArrayList<IFlux> points = new ArrayList();

	// statistics
	private long maxTransfer = 0, lastTransfer;

	public BasicFluxNetwork() {
		super();
	}

	public BasicFluxNetwork(int ID, UUID owner, String name, CustomColour colour, AccessType type) {
		super(ID, owner, name, colour, type);
	}

	public void updateNetwork() {
		if (networkID.getObject() == -1) {
			return;
		}
		long maxStored = 0;
		long energyStored = 0;

		sortFluxNetwork(plugs, hasController() ? controller.getSendMode() : PriorityMode.DEFAULT);
		sortFluxNetwork(points, hasController() ? controller.getReceiveMode() : PriorityMode.DEFAULT);

		ArrayList<TileEntity> senders = new ArrayList();
		ArrayList<TileEntity> receivers = new ArrayList();

		EnergyStats stats = new EnergyStats(0, 0, 0);
		for (IFlux plug : (ArrayList<IFlux>) plugs.clone()) {
			if (plug != null) {
				stats.maxSent += FluxAPI.getFluxHelper().pullEnergy(plug, plug.getTransferLimit(), ActionType.SIMULATE);
			}
		}
		for (IFlux point : (ArrayList<IFlux>) points.clone()) {
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
				for (IFlux plug : (ArrayList<IFlux>) plugs.clone()) {
					long limit = FluxAPI.getFluxHelper().pullEnergy(plug, plug.getTransferLimit(), ActionType.SIMULATE);
					long currentLimit = limit;
					if (currentLimit == 0) {
						continue;
					}
					for (IFlux point : (ArrayList<IFlux>) points.clone()) {
						if (point.getConnectionType() != plug.getConnectionType()) {// storages can be both
							long toTransfer = mode == TransferMode.EVEN ? Math.min((long) Math.ceil(((double) limit / (double) points.size())), 1) : limit;
							currentLimit -= FluxAPI.getFluxHelper().pullEnergy(plug, FluxAPI.getFluxHelper().pushEnergy(point, toTransfer, ActionType.PERFORM), ActionType.PERFORM);
						}
					}
					networkStats.getObject().latestRecords.transfer += limit - currentLimit;

				}
				current--;
			}
		}
		networkStats.getObject().inputStatistics(stats, plugs.size(), points.size());
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
		for (ArrayList<IFlux> array : Lists.newArrayList(points, plugs)) {
			for (IFlux flux : array) {
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

		for (IFlux flux : (ArrayList<IFlux>) points.clone()) {
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
		for (IFlux flux : (ArrayList<IFlux>) plugs.clone()) {
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
		if (type.canReceive()) {
			if (!points.contains(flux))
				points.add(flux);
		}
		if (type.canSend()) {
			if (!plugs.contains(flux))
				plugs.add(flux);
		}
		FluxNetworks.getServerCache().markNetworkDirty(getNetworkID());
	}

	@Override
	public void removeFluxConnection(IFlux flux) {
		ConnectionType type = flux.getConnectionType();
		if (flux instanceof IFluxController && type == ConnectionType.CONTROLLER) {
			this.setController(null);
		}
		if (plugs.contains(flux))
			plugs.remove(flux);
		if (points.contains(flux))
			points.remove(flux);

		FluxNetworks.getServerCache().markNetworkDirty(getNetworkID());
	}

	@Override
	public void buildFluxConnections() {
		ArrayList<ClientFlux> clientConnections = new ArrayList();
		Iterator<IFlux> iterator = points.iterator();
		iterator.forEachRemaining(point -> clientConnections.add(new ClientFlux(point)));
		plugs.iterator().forEachRemaining(plug -> {
			if (plug.getConnectionType() != ConnectionType.STORAGE) { // storage is added to both
				clientConnections.add(new ClientFlux(plug));
			}
		});
		this.fluxConnections = clientConnections;
	}

	@Override
	public PlayerAccess getPlayerAccess(EntityPlayer player) {
		UUID playerID = player.getGameProfile().getId();
		if (this.getOwnerUUID().equals(playerID)) {
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
