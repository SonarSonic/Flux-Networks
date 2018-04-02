package sonar.flux.connection.transfer.handlers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import com.google.common.collect.Lists;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import sonar.core.api.energy.EnergyType;
import sonar.core.api.energy.StoredEnergyStack;
import sonar.core.api.utils.ActionType;
import sonar.flux.api.energy.IFluxEnergyHandler;
import sonar.flux.api.energy.internal.IFluxTransfer;
import sonar.flux.api.energy.internal.ITransferHandler;
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
	public boolean wasChanged = true;

	public ConnectionTransferHandler(TileEntity tile, IFlux flux, List<EnumFacing> validFaces) {
		super(flux);
		this.tile = tile;
		this.validFaces = validFaces;
	}

	public Map<EnumFacing, IFluxTransfer> transfers = new HashMap<>();
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

	public IFluxTransfer getValidPhantomTransfer(EnumFacing from, EnergyType energy_type, ActionType type) {
		if (getNetwork().isFakeNetwork()) {
			return null;
		}
		IFluxTransfer transfer = transfers.get(from);
		TileEntity expected_source = from == null ? null : tile.getWorld().getTileEntity(tile.getPos().offset(from));
		if (from == null || expected_source == null || (transfer != null && transfer instanceof ISidedTransfer && ((ISidedTransfer) transfer).getTile() != expected_source)) {
			if (type.shouldSimulate()) {
				transfer = transfers.getOrDefault(null, new PhantomTransfer(energy_type));
			} else {
				if (energy_type == EnergyType.EU) {
					Optional<Entry<EnumFacing, IFluxTransfer>> firstEUTransfer = transfers.entrySet().stream().filter(E -> E.getValue() != null && E.getValue().getEnergyType() == EnergyType.EU).findAny();
					if (firstEUTransfer.isPresent()) {
						return firstEUTransfer.get().getValue();
					}
				}
				transfer = transfers.computeIfAbsent(null, E -> new PhantomTransfer(energy_type));
			}

		} else if (transfer == null) {
			IFluxEnergyHandler handler = FluxHelper.getValidHandler(expected_source, from);
			if (handler != null) {
				transfer = transfers.computeIfAbsent(from, E -> new ConnectionTransfer(this, handler, expected_source, from));
			} else {
				transfer = transfers.computeIfAbsent(from, E -> new SidedPhantomTransfer(energy_type, expected_source, from));
			}
		}
		return transfer;
	}

	public long addPhantomEnergyToNetwork(EnumFacing from, long maxReceive, EnergyType energy_type, ActionType type) {
		IFluxTransfer transfer = getValidPhantomTransfer(from, energy_type, type);
		if (transfer != null && getNetwork().canTransfer(energy_type)) {
			// FIXME this could override priority!!!!
			long added = flux.getNetwork().addPhantomEnergyToNetwork(getValidAddition(maxReceive, energy_type), energy_type, type);
			if (!type.shouldSimulate()) {
				transfer.addedToNetwork(added, energy_type);
				max_add -= EnergyType.convert(added, energy_type, getNetwork().getDefaultEnergyType());
			}
			return added;
		}
		return 0;
	}

	public long removePhantomEnergyFromNetwork(EnumFacing from, long maxReceive, EnergyType energy_type, ActionType type) {
		IFluxTransfer transfer = getValidPhantomTransfer(from, energy_type, type);
		if (transfer != null && getNetwork().canTransfer(energy_type)) {
			// FIXME this could override priority!!!!
			long removed = flux.getNetwork().removePhantomEnergyFromNetwork(getValidRemoval(maxReceive, energy_type), energy_type, type);
			if (!type.shouldSimulate()) {
				transfer.removedFromNetwork(removed, energy_type);
				max_remove -= EnergyType.convert(removed, energy_type, getNetwork().getDefaultEnergyType());
			}
			return removed;
		}
		return 0;
	}

	@Override
	public List<IFluxTransfer> getTransfers() {
		return Lists.newArrayList(transfers.values());
	}

	public void setTransfer(EnumFacing face, TileEntity tile) {
		IFluxTransfer transfer = transfers.get(face);
		IFluxEnergyHandler handler;
		if (tile == null || (handler = FluxHelper.getValidHandler(tile, face.getOpposite())) == null) {
			transfers.put(face, null);
		} else if (transfer == null || !(transfer instanceof ConnectionTransfer) || ((ConnectionTransfer) transfer).getTile() != tile) {
			ConnectionTransfer newTransfer = new ConnectionTransfer(this, handler, tile, face);
			transfers.put(face, newTransfer);
		} else if (transfer.isInvalid()) {
			transfers.put(face, null);
		}
	}

	public void updateTransfers(EnumFacing... faces) {
		boolean change = false;
		for (EnumFacing face : faces) {
			if (validFaces.contains(face)) {
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
		}
		wasChanged = change;
	}

	@Override
	public boolean hasTransfers() {
		return hasTransfers;
	}

}
