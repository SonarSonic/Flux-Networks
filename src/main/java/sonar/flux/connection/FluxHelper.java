package sonar.flux.connection;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import sonar.core.SonarCore;
import sonar.core.api.energy.EnergyType;
import sonar.core.api.energy.ISonarEnergyContainerHandler;
import sonar.core.api.utils.ActionType;
import sonar.core.listener.ListenerTally;
import sonar.core.listener.PlayerListener;
import sonar.core.utils.Pair;
import sonar.flux.FluxConfig;
import sonar.flux.FluxNetworks;
import sonar.flux.api.AdditionType;
import sonar.flux.api.RemovalType;
import sonar.flux.api.energy.IFluxEnergyHandler;
import sonar.flux.api.network.IFluxNetwork;
import sonar.flux.api.tiles.IFlux;
import sonar.flux.api.tiles.IFluxController.PriorityMode;
import sonar.flux.api.tiles.IFluxController.TransferMode;
import sonar.flux.api.tiles.IFluxListenable;
import sonar.flux.api.tiles.IFluxPlug;
import sonar.flux.api.tiles.IFluxPoint;
import sonar.flux.common.tileentity.TileFlux;
import sonar.flux.network.FluxNetworkCache;

public class FluxHelper {

	public static void addConnection(IFluxListenable flux, AdditionType type) {
		FluxNetworkCache.instance().getListenerList().addSubListenable(flux);
		if (flux.getNetworkID() != -1) {
			IFluxNetwork network = FluxNetworks.getServerCache().getNetwork(flux.getNetworkID());
			if (!network.isFakeNetwork()) {
				network.addConnection(flux, type);
			}
		}
	}

	public static void removeConnection(IFluxListenable flux, RemovalType type) {
		FluxNetworkCache.instance().getListenerList().removeSubListenable(flux);
		if (flux.getNetworkID() != -1) {
			IFluxNetwork network = FluxNetworks.getServerCache().getNetwork(flux.getNetworkID());
			if (!network.isFakeNetwork()) {
				network.removeConnection(flux, type);
			}
		}
	}

	public static UUID getOwnerUUID(EntityPlayer player) {
		return player.getGameProfile().getId();
	}

	public static boolean isPlayerAdmin(EntityPlayer player) {
		return player.isCreative();
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

	public static void sendPacket(IFluxNetwork network, TileFlux flux, ListenerTally<PlayerListener> tally) {
		for (int i = 0; i < tally.tallies.length; i++) {
			if (tally.tallies[i] > 0) {
				
			}
		}
	}

	public static long transferEnergy(IFluxPlug plug, List<IFluxPoint> points, EnergyType type, TransferMode mode) {
		long currentLimit = Long.MAX_VALUE;
		
		for (IFluxPoint point : points) {
			if (currentLimit <= 0) {
				break;
			}
			if (point.getConnectionType() != plug.getConnectionType()) {// storages can be both
				long toTransfer = addEnergyToNetwork(plug, type, removeEnergyFromNetwork(point, type, currentLimit, ActionType.SIMULATE), ActionType.SIMULATE);
				if (toTransfer > 0) {
					long pointRec = removeEnergyFromNetwork(point, type, toTransfer, ActionType.PERFORM);
					currentLimit -= addEnergyToNetwork(plug, type, pointRec, ActionType.PERFORM);
				}
			}
		}
		
		return Long.MAX_VALUE - currentLimit;
	}

	public static long addEnergyToNetwork(IFlux from, EnergyType type, long maxTransferRF, ActionType actionType) {
		return from.getTransferHandler().addToNetwork(maxTransferRF, type, actionType);
	}

	public static long removeEnergyFromNetwork(IFlux from, EnergyType type, long maxTransferRF, ActionType actionType) {
		return from.getTransferHandler().removeFromNetwork(maxTransferRF, type, actionType);
	}

	/* @Deprecated public static long pullEnergy(IFlux from, long maxTransferRF, ActionType actionType) { long extracted = 0; maxTransferRF = Math.min(maxTransferRF, from.getCurrentTransferLimit()); if (from != null && maxTransferRF != 0) { switch (from.getConnectionType()) { case PLUG: extracted += from.getTransferHandler().addToNetwork(maxTransferRF - extracted, actionType); break; case STORAGE: break; default: break; } } return extracted; }
	 * @Deprecated public static long pushEnergy(IFlux to, long maxTransferRF, ActionType actionType) { long received = 0; maxTransferRF = Math.min(maxTransferRF, to.getCurrentTransferLimit()); if (to != null && maxTransferRF != 0 && to.hasTransfers()) { switch (to.getConnectionType()) { case POINT: received += to.getTransferHandler().removeFromNetwork(maxTransferRF - received, actionType); break; case STORAGE: break; case CONTROLLER: break; default: break; } } return received; } */

	public static boolean canConnect(TileEntity tile, EnumFacing dir) {
		return tile != null && !(tile instanceof IFlux) && getValidHandler(tile, dir) != null;// || SonarLoader.rfLoaded && tile instanceof IEnergyConnection && FluxConfig.transfers.get(EnergyType.RF).a);
	}

	public static List<IFluxEnergyHandler> getEnergyHandlers() {
		List<IFluxEnergyHandler> handlers = new ArrayList<>();
		for (IFluxEnergyHandler handler : FluxNetworks.loadedEnergyHandlers) {
			Pair<Boolean, Boolean> canTransfer = FluxConfig.transfer_types.get(handler.getEnergyType());
			if (canTransfer!=null && canTransfer.a) {
				handlers.add(handler);
			}
		}
		return handlers;
	}

	public static List<ISonarEnergyContainerHandler> getEnergyContainerHandlers() {
		List<ISonarEnergyContainerHandler> handlers = new ArrayList<>();
		for (ISonarEnergyContainerHandler handler : SonarCore.energyContainerHandlers) {
			Pair<Boolean, Boolean> canTransfer = FluxConfig.transfer_types.get(handler.getProvidedType());
			if (canTransfer!=null &&canTransfer.b) {
				handlers.add(handler);
			}
		}
		return handlers;
	}

	public static IFluxEnergyHandler getValidHandler(TileEntity tile, EnumFacing dir) {
		if(tile == null || tile instanceof IFlux){
			return null;
		}
		List<IFluxEnergyHandler> handlers = FluxNetworks.enabledEnergyHandlers;
		for (IFluxEnergyHandler handler : handlers) {
			if (handler.canRenderConnection(tile, dir)) {
				return handler;
			}
		}
		return null;
	}

	public static ISonarEnergyContainerHandler canTransferEnergy(ItemStack stack) {
		if (stack.isEmpty()) {
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
}
