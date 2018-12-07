package sonar.flux.connection;

import net.minecraft.entity.player.EntityPlayer;
import sonar.core.api.energy.EnergyType;
import sonar.core.api.utils.ActionType;
import sonar.flux.FluxNetworks;
import sonar.flux.api.AdditionType;
import sonar.flux.api.RemovalType;
import sonar.flux.api.network.IFluxNetwork;
import sonar.flux.api.tiles.IFlux;
import sonar.flux.api.tiles.IFluxController.TransferMode;
import sonar.flux.api.tiles.IFluxListenable;
import sonar.flux.api.tiles.IFluxPlug;
import sonar.flux.api.tiles.IFluxPoint;
import sonar.flux.network.FluxNetworkCache;

import java.util.List;

public class FluxHelper {

	public static void addConnection(IFluxListenable flux, AdditionType type) {
		FluxNetworkCache.instance().getListenerList().addSubListenable(flux);
		if (flux.getNetworkID() != -1) {
			IFluxNetwork network = FluxNetworks.getServerCache().getNetwork(flux.getNetworkID());
			if (!network.isFakeNetwork()) {
				network.queueConnectionAddition(flux, type);
				return;
			}
		}
		FluxNetworkCache.instance().onTileDisconnected(flux);
	}

	public static void removeConnection(IFluxListenable flux, RemovalType type) {
		FluxNetworkCache.instance().getListenerList().removeSubListenable(flux);
		if(type != RemovalType.REMOVE){
			FluxNetworkCache.instance().onTileRemoved(flux);
		}
		if (flux.getNetworkID() != -1) {
			IFluxNetwork network = FluxNetworks.getServerCache().getNetwork(flux.getNetworkID());
			if (!network.isFakeNetwork()) {
				network.queueConnectionRemoval(flux, type);
				return;
			}
		}

	}

	public static boolean isPlayerAdmin(EntityPlayer player) {
		return player.isCreative();
	}

	public static long transferEnergy(long max, List<IFluxPoint> points, EnergyType type, TransferMode mode) {
		long currentLimit = max;
		for (IFluxPoint point : points) {
			currentLimit -= removeEnergyFromNetwork(point, type, currentLimit, ActionType.PERFORM);
			if (currentLimit <= 0) {
				break;
			}
		}
		return max - currentLimit;
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
}
