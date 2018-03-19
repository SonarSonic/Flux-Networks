package sonar.flux.connection.transfer.handlers;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import sonar.core.SonarCore;
import sonar.core.api.energy.EnergyType;
import sonar.core.api.energy.ISonarEnergyHandler;
import sonar.core.api.energy.StoredEnergyStack;
import sonar.core.api.utils.ActionType;
import sonar.flux.api.energy.IFluxTransfer;
import sonar.flux.api.energy.ITransferHandler;
import sonar.flux.api.tiles.IFlux;
import sonar.flux.connection.FluxHelper;
import sonar.flux.connection.transfer.ConnectionTransfer;
import sonar.flux.connection.transfer.ISidedTransfer;
import sonar.flux.connection.transfer.PhantomTransfer;
import sonar.flux.connection.transfer.SidedPhantomTransfer;

public class ConnectionTransferHandler extends FluxTransferHandler implements ITransferHandler {

	public final TileEntity tile;
	public final List<EnumFacing> validFaces;
	public boolean hasTransfers;

	public ConnectionTransferHandler(TileEntity tile, IFlux flux, List<EnumFacing> validFaces) {
		super(flux);
		this.tile = tile;
		this.validFaces = validFaces;
	}

	Map<EnumFacing, IFluxTransfer> transfers = Maps.newHashMap();
	{
		for (EnumFacing face : EnumFacing.VALUES) {
			transfers.put(face, null);
		}
	}

	@Override
	public void onStartServerTick() {
		super.onStartServerTick();
		transfers.entrySet().stream().filter(E -> E.getValue() != null).forEach(E -> E.getValue().onStartServerTick());
	}

	@Override
	public void onEndWorldTick() {
		super.onEndWorldTick();
		transfers.entrySet().stream().filter(E -> E.getValue() != null).forEach(E -> E.getValue().onEndWorldTick());
	}

	public IFluxTransfer getValidPhantomTransfer(EnumFacing from, EnergyType energy_type) {
		if (flux.getNetwork().isFakeNetwork()) {
			return null;
		}
		IFluxTransfer transfer = transfers.get(from);
		TileEntity expected_source = from == null ? null : tile.getWorld().getTileEntity(tile.getPos().offset(from));
		if (from == null || expected_source == null || (transfer != null && transfer instanceof ISidedTransfer && ((ISidedTransfer) transfer).getTile() != expected_source)) {
			transfer = transfers.computeIfAbsent(null, E -> new PhantomTransfer(energy_type));
			/// FIXME MAKE THE PHANTOM HANDLER RF ONLY?
		} else if (transfer == null) {
			ISonarEnergyHandler handler = FluxHelper.canTransferEnergy(expected_source, from);
			if (handler != null) {
				transfer = transfers.computeIfAbsent(null, E -> new ConnectionTransfer(this, handler, tile, from));
			} else {
				transfer = transfers.computeIfAbsent(null, E -> new SidedPhantomTransfer(energy_type, expected_source, from));
			}
		}
		return transfer;
	}

	public long addPhantomEnergyToNetwork(EnumFacing from, long maxReceive, EnergyType energy_type, ActionType type) {
		IFluxTransfer transfer = getValidPhantomTransfer(from, energy_type);
		if (transfer != null) {
			long toTransfer = StoredEnergyStack.convert(maxReceive, energy_type, EnergyType.RF);
			// FIXME this could override priority!!!!
			long added = flux.getNetwork().receiveEnergy(Math.min(toTransfer, getValidAdditionL(toTransfer)), type);
			if (!type.shouldSimulate()) {
				transfer.addedToNetwork(added);
				max_add -= added;
			}
			return StoredEnergyStack.convert(added, EnergyType.RF, energy_type);
		}
		return 0;
	}

	public long removePhantomEnergyFromNetwork(EnumFacing from, long maxReceive, EnergyType energy_type, ActionType type) {
		IFluxTransfer transfer = getValidPhantomTransfer(from, energy_type);
		if (transfer != null) {
			long toTransfer = StoredEnergyStack.convert(maxReceive, energy_type, EnergyType.RF);
			// FIXME this could override priority!!!!
			long removed = flux.getNetwork().extractEnergy(Math.min(toTransfer, getValidRemovalL(toTransfer)), type);
			if (!type.shouldSimulate()) {
				transfer.removedFromNetwork(removed);
				max_remove -= removed;
			}
			return StoredEnergyStack.convert(removed, EnergyType.RF, energy_type);
		}
		return 0;
	}

	@Override
	public List<IFluxTransfer> getTransfers() {
		return Lists.newArrayList(transfers.values());
	}

	public void setTransfer(EnumFacing face, TileEntity tile) {
		IFluxTransfer transfer = transfers.get(face);
		ISonarEnergyHandler handler;
		if (tile == null || (handler = FluxHelper.canTransferEnergy(tile, face.getOpposite())) == null) {
			transfers.put(face, null);
		} else if (transfer == null || !(transfer instanceof ConnectionTransfer) || ((ConnectionTransfer) transfer).getTile() != tile) {
			ConnectionTransfer newTransfer = new ConnectionTransfer(this, handler, tile, face);
			transfers.put(face, newTransfer);
		} else if (transfer.isInvalid()) {
			transfers.put(face, null);
		}
	}

	public void updateTransfers() {
		boolean change = false;
		for (EnumFacing face : validFaces) {
			int index = face.getIndex();
			BlockPos neighbour_pos = tile.getPos().offset(face);
			TileEntity neighbour_tile = tile.getWorld().getTileEntity(neighbour_pos);

			boolean wasConnected = transfers.get(face) != null;
			setTransfer(face, neighbour_tile);
			boolean isConnected = transfers.get(face) != null;
			if (wasConnected != isConnected) {
				change = true;
			}
		}
		if (change) {
			hasTransfers = transfers.entrySet().stream().anyMatch((E) -> E.getValue() != null);
			// connections.markChanged();
			//SonarCore.sendFullSyncAroundWithRenderUpdate(tile, 128);
		}
	}

	@Override
	public boolean hasTransfers() {
		return hasTransfers;
	}

}
