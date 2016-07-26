package sonar.flux.connection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.common.FMLCommonHandler;
import sonar.core.api.SonarAPI;
import sonar.core.api.utils.ActionType;
import sonar.core.utils.CustomColour;
import sonar.flux.FluxNetworks;
import sonar.flux.api.ClientFlux;
import sonar.flux.api.EnergyStats;
import sonar.flux.api.FluxAPI;
import sonar.flux.api.IFlux;
import sonar.flux.api.IFlux.ConnectionType;
import sonar.flux.api.IFluxController;
import sonar.flux.api.IFluxController.PriorityMode;
import sonar.flux.api.IFluxController.TransferMode;
import sonar.flux.api.IFluxController.TransmitterMode;
import sonar.flux.api.IFluxNetwork;
import sonar.flux.common.tileentity.TileEntityFlux;

import com.google.common.collect.Lists;

public class BasicFluxNetwork extends FluxNetworkCommon implements IFluxNetwork {

	// settings
	private final ArrayList<String> players = new ArrayList();

	// connections
	private IFluxController controller = null;
	private final ArrayList<IFlux> plugs = new ArrayList();
	private final ArrayList<IFlux> points = new ArrayList();

	//private final ArrayList<ClientFlux> plugs = new ArrayList();
	//private final ArrayList<ClientFlux> points = new ArrayList();

	// statistics
	private long maxTransfer = 0, lastTransfer;

	public BasicFluxNetwork(NBTTagCompound tag) {
		super(tag);
		players.add(ownerName.getObject());
	}

	public BasicFluxNetwork(int ID, String owner, String name, CustomColour colour, AccessType type) {
		super(ID, owner, name, colour, type);
		players.add(owner);
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
		for (IFlux plug : plugs) {
			if (plug != null) {
				stats.maxSent += FluxAPI.getFluxHelper().pullEnergy(plug, plug.getTransferLimit(), ActionType.SIMULATE);
			}
		}
		for (IFlux point : points) {
			if (point != null) {
				stats.maxReceived += FluxAPI.getFluxHelper().pushEnergy(point, point.getTransferLimit(), ActionType.SIMULATE);
				if (point instanceof TileEntityFlux.Storage) {
					TileEntityFlux.Storage fluxStorage = (TileEntityFlux.Storage) point;
					maxStored += fluxStorage.storage.getMaxEnergyStored();
					energyStored += fluxStorage.storage.getEnergyStored();
				}
			}
		}
		this.maxStored.setObject(maxStored);
		this.energyStored.setObject(energyStored);

		//maxTransfer = Math.min(stats.sent, stats.received);
		long currentTransfer = maxTransfer;

		lastTransfer = currentTransfer;
		//System.out.print(stats.maxReceived);
		if (this.hasController() && controller.getTransferMode() != TransferMode.DEFAULT) {
			TransferMode mode = controller.getTransferMode();
			int current = mode.repeat;
			while (current != 0) {
				for (IFlux plug : plugs) {
					long limit = FluxAPI.getFluxHelper().pullEnergy(plug, plug.getTransferLimit(), ActionType.SIMULATE);
					long currentLimit = limit;
					if (currentLimit == 0) {
						break;
					}
					for (IFlux point : points) {
						if (mode == TransferMode.EVEN) {
							currentLimit -= FluxAPI.getFluxHelper().pullEnergy(plug, FluxAPI.getFluxHelper().pushEnergy(point, Math.min((long) Math.ceil(((double) limit / (double) points.size())), 1), ActionType.PERFORM), ActionType.PERFORM);
						} else {
							currentLimit -= FluxAPI.getFluxHelper().pullEnergy(plug, FluxAPI.getFluxHelper().pushEnergy(point, limit, ActionType.PERFORM), ActionType.PERFORM);
						}
					}
					networkStats.latestRecords.transfer += limit - currentLimit;

				}
				current--;
			}
		}

		networkStats.inputStatistics(stats, plugs.size(), points.size());
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
	public boolean isPlayerAllowed(String playerName) {
		if (playerName == null) {
			return false;
		}
		switch (accessType.getObject()) {
		case PUBLIC:
			return true;
		case PRIVATE:
			return ownerName.getObject().equals(playerName);
		case RESTRICTED:
			return players.contains(playerName);
		default:
			return false;
		}
	}

	@Override
	public void setNetworkName(String name) {
		if (name != null && !name.isEmpty())
			networkName.setObject(name);
	}

	@Override
	public void setAccessType(AccessType type) {
		if (type != null)
			accessType.setObject(type);
	}

	@Override
	public void setCustomColour(CustomColour colour) {
		this.colour = colour;
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
		FluxNetworks.cache.markNetworkDirty(getNetworkID());
	}

	@Override
	public void removePlayerAccess(String playerName) {
		if (players.contains(playerName))
			players.remove(playerName);

	}

	@Override
	public void addPlayerAccess(String playerName) {
		if (!players.contains(playerName))
			players.add(playerName);

	}

	@Override
	public long receiveEnergy(long maxReceive, ActionType type) {
		long used = 0;

		for (IFlux flux : points) {
			long toTransfer = Math.min(flux.getTransferLimit(), maxReceive - used);
			if (flux instanceof IFluxController) {
				IFluxController controller = (IFluxController) flux;
				if (controller.getTransmitterMode() == TransmitterMode.OFF) {
					break;
				}
				ArrayList<String> playerNames = (ArrayList<String>) players.clone();
				ArrayList<EntityPlayer> players = new ArrayList();
				for (String player : playerNames) {
					List<EntityPlayerMP> server = FMLCommonHandler.instance().getMinecraftServerInstance().getServer().getPlayerList().getPlayerList();
					for (EntityPlayerMP entityPlayer : server) {
						if (entityPlayer.getName().equals(player)) {
							players.add(entityPlayer);
						}
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
			networkStats.latestRecords.transfer += used;

		return used;
	}

	@Override
	public long extractEnergy(long maxExtract, ActionType type) {
		long used = 0;
		for (IFlux flux : plugs) {
			long toTransfer = Math.min(flux.getTransferLimit(), maxExtract - used);
			if (maxExtract - used <= 0) {
				break;
			}
			used += FluxAPI.getFluxHelper().pullEnergy(flux, toTransfer, type);
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
		if (type.canReceive()) {
			if (!points.contains(flux))
				points.add(flux);
		}
		if (type.canSend()) {
			if (!plugs.contains(flux))
				plugs.add(flux);
		}
		FluxNetworks.cache.markNetworkDirty(getNetworkID());
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

		FluxNetworks.cache.markNetworkDirty(getNetworkID());
	}

	@Override
	public void buildFluxConnections() {
		ArrayList<ClientFlux> clientConnections = new ArrayList();
		for (IFlux flux : points) {
			clientConnections.add(new ClientFlux(flux));
		}
		for (IFlux flux : plugs) {
			clientConnections.add(new ClientFlux(flux));
		}
		this.fluxConnections = clientConnections;
	}
}
