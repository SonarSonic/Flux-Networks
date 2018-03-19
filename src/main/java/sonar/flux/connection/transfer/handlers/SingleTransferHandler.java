package sonar.flux.connection.transfer.handlers;

import java.util.List;

import com.google.common.collect.Lists;

import sonar.core.api.utils.ActionType;
import sonar.flux.api.energy.IEnergyTransfer;
import sonar.flux.api.energy.IFluxTransfer;
import sonar.flux.api.energy.ITransferHandler;
import sonar.flux.api.tiles.IFlux;
import sonar.flux.common.tileentity.TileStorage;
import sonar.flux.connection.transfer.StorageTransfer;

public class SingleTransferHandler extends FluxTransferHandler implements ITransferHandler {

	public final IEnergyTransfer transfer;

	public SingleTransferHandler(IFlux flux, IEnergyTransfer transfer) {
		super(flux);
		this.transfer = transfer;
	}

	@Override
	public long addToNetwork(long maxTransferRF, ActionType actionType) {
		long add = transfer.addToNetwork(getValidAdditionL(maxTransferRF), actionType);
		if (!actionType.shouldSimulate()) {
			max_add -= add;
		}
		return 0;
	}

	@Override
	public long removeFromNetwork(long maxTransferRF, ActionType actionType) {
		long remove = transfer.removeFromNetwork(getValidRemovalL(maxTransferRF), actionType);
		if (!actionType.shouldSimulate()) {
			max_remove -= remove;
		}
		return 0;
	}

	@Override
	public boolean hasTransfers() {
		return true;
	}

	@Override
	public void updateTransfers() {}

	@Override
	public List<IFluxTransfer> getTransfers() {
		return Lists.newArrayList(transfer);
	}

}