package sonar.flux.connection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import sonar.core.api.SonarAPI;
import sonar.core.api.utils.ActionType;
import sonar.core.api.utils.BlockCoords;
import sonar.flux.FluxNetworks;
import sonar.flux.api.FluxWrapper;
import sonar.flux.api.IFlux;
import sonar.flux.api.IFluxCommon;
import sonar.flux.common.tileentity.TileEntityStorage;

public class FluxHelper extends FluxWrapper {

	public long pullEnergy(IFlux from, long maxTransferRF, ActionType actionType) {
		long extracted = 0;
		maxTransferRF = Math.min(maxTransferRF, from.getCurrentTransferLimit());
		if (from != null && maxTransferRF != 0) {
			BlockCoords coords = from.getCoords();
			if (coords == null) {
				return extracted;
			}
			switch (from.getConnectionType()) {
			case PLUG:
				for (EnumFacing face : EnumFacing.VALUES) {
					BlockCoords translate = BlockCoords.translateCoords(coords, face);
					TileEntity tile = translate.getTileEntity();
					if (tile != null && !(tile instanceof IFlux)) {
						long remove = SonarAPI.getEnergyHelper().extractEnergy(tile, Math.min(maxTransferRF - extracted, from.getCurrentTransferLimit()), face.getOpposite(), actionType);
						if (!actionType.shouldSimulate())
							from.onEnergyRemoved(remove);
						extracted += remove;

					}
				}
				break;
			case STORAGE:
				TileEntity tile = coords.getTileEntity();
				if (tile != null) {
					int remove = ((TileEntityStorage) tile).storage.extractEnergy((int) Math.min(maxTransferRF - extracted, Integer.MAX_VALUE), actionType.shouldSimulate());
					if (!actionType.shouldSimulate())
						from.onEnergyRemoved(remove);
					extracted += remove;
				}
				break;
			default:
				break;
			}
		}
		return extracted;
	}

	public long pushEnergy(IFlux to, long maxTransferRF, ActionType actionType) {
		long received = 0;
		maxTransferRF = Math.min(maxTransferRF, to.getCurrentTransferLimit());
		if (to != null && maxTransferRF != 0) {
			BlockCoords coords = to.getCoords();
			switch (to.getConnectionType()) {
			case POINT:
				for (EnumFacing face : EnumFacing.VALUES) {
					BlockCoords translate = BlockCoords.translateCoords(coords, face);
					TileEntity tile = translate.getTileEntity();
					if (tile != null && !(tile instanceof IFlux)) {
						long added = SonarAPI.getEnergyHelper().receiveEnergy(tile, Math.min(maxTransferRF - received, to.getCurrentTransferLimit()), face.getOpposite(), actionType);
						if (!actionType.shouldSimulate())
							to.onEnergyAdded(added);
						received += added;
					}
				}
				break;
			case STORAGE:
				TileEntity tile = coords.getTileEntity();
				if (tile != null && tile instanceof TileEntityStorage) {
					int added = ((TileEntityStorage) coords.getTileEntity()).storage.receiveEnergy((int) Math.min(maxTransferRF - received, Integer.MAX_VALUE), actionType.shouldSimulate());
					if (!actionType.shouldSimulate())
						to.onEnergyAdded(added);
					received += added;
				}
				break;
			case CONTROLLER:
				break;
			default:
				break;
			}
		}
		return received;
	}

	/** gets all the TileEntities which can send/receive energy adjacent to the given IFlux */
	public Map<TileEntity, EnumFacing> getConnections(IFlux flux) {
		Map<TileEntity, EnumFacing> tiles = new HashMap();
		for (EnumFacing face : EnumFacing.VALUES) {
			World world = flux.getDimension();
			TileEntity tile = world.getTileEntity(flux.getCoords().getBlockPos().offset(face));
			if (tile == null || tile.isInvalid()) {
				continue;
			}
			if (SonarAPI.getEnergyHelper().canTransferEnergy(tile, face) != null) {
				tiles.put(tile, face);
			}
		}
		return tiles;
	}
}
