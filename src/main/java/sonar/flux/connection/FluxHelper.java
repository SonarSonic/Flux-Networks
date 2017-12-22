package sonar.flux.connection;

import cofh.redstoneflux.api.IEnergyConnection;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.common.FMLCommonHandler;
import sonar.core.SonarCore;
import sonar.core.api.SonarAPI;
import sonar.core.api.energy.EnergyType;
import sonar.core.api.energy.ISonarEnergyContainerHandler;
import sonar.core.api.energy.ISonarEnergyHandler;
import sonar.core.api.utils.ActionType;
import sonar.core.integration.SonarLoader;
import sonar.core.listener.ListenerTally;
import sonar.core.listener.PlayerListener;
import sonar.flux.FluxConfig;
import sonar.flux.FluxNetworks;
import sonar.flux.api.FluxListener;
import sonar.flux.api.network.FluxPlayer;
import sonar.flux.api.network.IFluxNetwork;
import sonar.flux.api.tiles.*;
import sonar.flux.api.tiles.IFluxController.PriorityMode;
import sonar.flux.api.tiles.IFluxController.TransferMode;
import sonar.flux.api.tiles.IFluxController.TransmitterMode;
import sonar.flux.common.tileentity.TileEntityStorage;
import sonar.flux.network.FluxNetworkCache;
import sonar.flux.network.PacketFluxConnectionsList;
import sonar.flux.network.PacketFluxNetworkList;
import sonar.flux.network.PacketNetworkStatistics;

import java.util.*;

public class FluxHelper {

    public static void addConnection(IFluxListenable flux) {
        FluxNetworkCache.instance().getListenerList().addSubListenable(flux);
        if (flux.getNetworkID() != -1) {
            IFluxNetwork network = FluxNetworks.getServerCache().getNetwork(flux.getNetworkID());
            if (!network.isFakeNetwork()) {
                network.addConnection(flux);
            }
        }
    }

    public static void removeConnection(IFluxListenable flux) {
        FluxNetworkCache.instance().getListenerList().removeSubListenable(flux);
        if (flux.getNetworkID() != -1) {
            IFluxNetwork network = FluxNetworks.getServerCache().getNetwork(flux.getNetworkID());
            if (!network.isFakeNetwork()) {
                network.removeConnection(flux);
            }
        }
    }

    public static UUID getOwnerUUID(EntityPlayer player) {
        return player.getGameProfile().getId();
    }

    public static void sortConnections(List<IFlux> flux, PriorityMode mode) {
        switch (mode) {
            case DEFAULT:
                break;
            case LARGEST:
                flux.sort((o1, o2) -> o2.getCurrentPriority() - o1.getCurrentPriority());
                break;
            case SMALLEST:
                flux.sort(Comparator.comparingInt(IFlux::getCurrentPriority));
                break;
            default:
                break;
        }
    }

    public static void sendPacket(IFluxNetwork network, ListenerTally<PlayerListener> tally) {
        for (int i = 0; i < tally.tallies.length; i++) {
            if (tally.tallies[i] > 0) {
                FluxListener type = FluxListener.values()[i];
                switch (type) {
                    case CONNECTIONS:
                        network.buildFluxConnections();
                        FluxNetworks.network.sendTo(new PacketFluxConnectionsList(network.getClientFluxConnection(), network.getNetworkID()), tally.listener.player);
                        break;
                    case FULL_NETWORK:
                        ArrayList<IFluxNetwork> toSend = FluxNetworkCache.instance().getAllowedNetworks(tally.listener.player, false);
                        FluxNetworks.network.sendTo(new PacketFluxNetworkList(toSend, false), tally.listener.player);
                        tally.removeTallies(1, type);
                        tally.addTallies(1, FluxListener.SYNC_NETWORK);
                        break;
                    case STATISTICS:
                        FluxNetworks.network.sendTo(new PacketNetworkStatistics(network.getNetworkID(), network.getStatistics()), tally.listener.player);
                        break;
                    case SYNC_NETWORK:
                        break;
                    default:
                        break;
                }
            }
        }
    }

    public static long transferEnergy(IFluxPlug plug, List<IFluxPoint> points, TransferMode mode) {
        long limit = FluxHelper.pullEnergy(plug, plug.getCurrentTransferLimit(), ActionType.SIMULATE);
        long currentLimit = limit;
        for (IFluxPoint point : points) {
            if (currentLimit <= 0) {
                break;
            }
            if (point.getConnectionType() != plug.getConnectionType()) {// storages can be both
                long toTransfer = (long) (mode == TransferMode.EVEN ? Math.min(Math.ceil((double) limit / (double) points.size()), currentLimit) : currentLimit);
                long pointRec = FluxHelper.pushEnergy(point, toTransfer, ActionType.PERFORM);
                currentLimit -= FluxHelper.pullEnergy(plug, pointRec, ActionType.PERFORM);
            }
        }
        return limit - currentLimit;
    }

	public static long pullEnergy(IFlux from, long maxTransferRF, ActionType actionType) {
		long extracted = 0;
		maxTransferRF = Math.min(maxTransferRF, from.getCurrentTransferLimit());
		if (from != null && maxTransferRF != 0) {
			switch (from.getConnectionType()) {
			case PLUG:
				TileEntity[] tiles = from.cachedTiles();
				for (int i = 0; i < 6; i++) {
					TileEntity tile = tiles[i];
					if (tile != null) {
                            EnumFacing face = EnumFacing.values()[i].getOpposite();
                            long simulate = SonarAPI.getEnergyHelper().extractEnergy(tile, Math.min(maxTransferRF - extracted, from.getCurrentTransferLimit()), face, ActionType.SIMULATE);
                            long remove = SonarAPI.getEnergyHelper().extractEnergy(tile, from.getValidTransfer(simulate, face), face, actionType);
						if (!actionType.shouldSimulate())
                                from.onEnergyRemoved(EnumFacing.VALUES[i], remove);
						extracted += remove;
					}
				}
				break;
			case STORAGE:
				TileEntityStorage tile = (TileEntityStorage) from;
				int remove = tile.storage.extractEnergy((int) Math.min(maxTransferRF - extracted, Integer.MAX_VALUE), actionType.shouldSimulate());
				if (!actionType.shouldSimulate())
                        from.onEnergyRemoved(EnumFacing.VALUES[0], remove);
				extracted += remove;
				break;
			default:
				break;
			}
		}
		return extracted;
	}

	public static long pushEnergy(IFlux to, long maxTransferRF, ActionType actionType) {
		long received = 0;
		maxTransferRF = Math.min(maxTransferRF, to.getCurrentTransferLimit());
        if (to != null && maxTransferRF != 0 && to.canTransfer()) {
			// BlockCoords coords = to.getCoords();
			switch (to.getConnectionType()) {
			case POINT:
				TileEntity[] tiles = to.cachedTiles();
				for (int i = 0; i < 6; i++) {
					TileEntity tile = tiles[i];
					if (tile != null) {
                            EnumFacing face = EnumFacing.values()[i].getOpposite();
                            long simulate = SonarAPI.getEnergyHelper().receiveEnergy(tile, Math.min(maxTransferRF - received, to.getCurrentTransferLimit()), face, ActionType.SIMULATE);
                            long added = SonarAPI.getEnergyHelper().receiveEnergy(tile, to.getValidTransfer(simulate, face), face, actionType);
						if (!actionType.shouldSimulate())
                                to.onEnergyAdded(EnumFacing.VALUES[i], added);
						received += added;
					}
				}
				break;
			case STORAGE:
				TileEntityStorage tile = (TileEntityStorage) to;
				int added = tile.storage.receiveEnergy((int) Math.min(maxTransferRF - received, Integer.MAX_VALUE), actionType.shouldSimulate());
				if (!actionType.shouldSimulate())
                        to.onEnergyAdded(EnumFacing.VALUES[0], added);
				received += added;
				break;
			case CONTROLLER:
				IFluxController controller = (IFluxController) to;
				if (controller.getTransmitterMode() == TransmitterMode.OFF) {
					break;
				}
				ArrayList<FluxPlayer> playerNames = (ArrayList<FluxPlayer>) controller.getNetwork().getPlayers().clone();
                    ArrayList<EntityPlayer> players = new ArrayList<>();
				for (FluxPlayer player : playerNames) {
					Entity entity = FMLCommonHandler.instance().getMinecraftServerInstance().getEntityFromUuid(player.id);
					if (entity != null && entity instanceof EntityPlayer) {
						players.add((EntityPlayer) entity);
					}
				}
				for (EntityPlayer player : players) {
                        long receive;
					switch (controller.getTransmitterMode()) {
					case HELD_ITEM:
						ItemStack stack = player.getHeldItemMainhand();
						if (FluxHelper.canTransferEnergy(stack) != null) {
							receive = SonarAPI.getEnergyHelper().receiveEnergy(stack, maxTransferRF - received, actionType);
							received += receive;
                                    if (!actionType.shouldSimulate())
                                        to.onEnergyRemoved(EnumFacing.VALUES[0], receive);
							if (maxTransferRF - received <= 0) {
								break;
							}
						}
						break;
					case HOTBAR:
					case ON:
						IInventory inv = player.inventory;
                                for (int i = 0; i < (controller.getTransmitterMode() == TransmitterMode.ON ? inv.getSizeInventory() : 9); i++) {
							ItemStack itemStack = inv.getStackInSlot(i);
							if (FluxHelper.canTransferEnergy(itemStack) != null) {
								receive = SonarAPI.getEnergyHelper().receiveEnergy(itemStack, maxTransferRF - received, actionType);
								received += receive;
                                        if (!actionType.shouldSimulate())
                                            to.onEnergyRemoved(EnumFacing.VALUES[0], receive);
								if (maxTransferRF - received <= 0) {
									break;
								}
							}
						}
						break;
					default:
						break;
					}
				}

				break;
			default:
				break;
			}
		}
		return received;
	}

    public static boolean canConnect(TileEntity tile, EnumFacing dir) {
        return tile != null && !(tile instanceof IFlux) && (canTransferEnergy(tile, dir) != null || SonarLoader.rfLoaded && tile instanceof IEnergyConnection && FluxConfig.transfers.get(EnergyType.RF).a);
    }

	public static List<ISonarEnergyHandler> getEnergyHandlers() {
        ArrayList<ISonarEnergyHandler> handlers = new ArrayList<>();
		for (ISonarEnergyHandler handler : SonarCore.energyHandlers) {
			if (FluxConfig.transfers.get(handler.getProvidedType()).a) {
				handlers.add(handler);
			}
		}
		return handlers;
	}

	public static List<ISonarEnergyContainerHandler> getEnergyContainerHandlers() {
        ArrayList<ISonarEnergyContainerHandler> handlers = new ArrayList<>();
		for (ISonarEnergyContainerHandler handler : SonarCore.energyContainerHandlers) {
			if (FluxConfig.transfers.get(handler.getProvidedType()).b) {
				handlers.add(handler);
			}
		}
		return handlers;
	}

	public static ISonarEnergyHandler canTransferEnergy(TileEntity tile, EnumFacing dir) {
		List<ISonarEnergyHandler> handlers = FluxNetworks.energyHandlers;
		for (ISonarEnergyHandler handler : handlers) {
			if (handler.canProvideEnergy(tile, dir)) {
				return handler;
			}
		}
		return null;
	}

	public static ISonarEnergyContainerHandler canTransferEnergy(ItemStack stack) {
		if(stack.isEmpty()){
			return null;
		}
		List<ISonarEnergyContainerHandler> handlers = FluxNetworks.energyContainerHandlers;
		for (ISonarEnergyContainerHandler handler : handlers) {
			if (handler.canHandleItem(stack)) {
				return handler;
			}
		}
		return null;
	}
/*	/** gets all the TileEntities which can send/receive energy adjacent to the given IFlux */
    /* public Map<TileEntity, EnumFacing> getConnections(IFlux flux) { Map<TileEntity, EnumFacing> tiles = new HashMap<>(); for (EnumFacing face : EnumFacing.VALUES) { World world = flux.getDimension(); TileEntity tile = world.getTileEntity(flux.getCoords().getBlockPos().offset(face)); if (tile == null || tile.isInvalid()) { continue; } if (SonarAPI.getEnergyHelper().canTransferEnergy(tile, face) != null) { tiles.put(tile, face); } } return tiles; } */
}
