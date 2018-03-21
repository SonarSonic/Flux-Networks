package sonar.flux.connection.transfer.handlers;

import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.util.EnumFacing;
import sonar.core.api.energy.EnergyType;
import sonar.core.api.utils.ActionType;
import sonar.flux.api.energy.IEnergyTransfer;
import sonar.flux.api.energy.IFluxTransfer;
import sonar.flux.api.energy.ITransferHandler;
import sonar.flux.api.tiles.IFlux;

public class SingleTransferHandler extends FluxTransferHandler implements ITransferHandler {

	public final IEnergyTransfer transfer;

	public SingleTransferHandler(IFlux flux, IEnergyTransfer transfer) {
		super(flux);
		this.transfer = transfer;
	}

	@Override
	public long addToNetwork(long maxTransferRF, EnergyType energyType, ActionType actionType) {
		if (getNetwork().canConvert(energyType, getNetwork().getDefaultEnergyType())) {
			long add = transfer.addToNetwork(getValidAddition(maxTransferRF), energyType, actionType);
			if (!actionType.shouldSimulate()) {
				max_add -= add;
			}
			return add;
		}
		return 0;
	}

	@Override
	public long removeFromNetwork(long maxTransferRF, EnergyType energyType, ActionType actionType) {
		if (getNetwork().canConvert(energyType, getNetwork().getDefaultEnergyType())) {
			long remove = transfer.removeFromNetwork(getValidRemoval(maxTransferRF), energyType, actionType);
			if (!actionType.shouldSimulate()) {
				max_remove -= remove;
			}
			return remove;
		}
		return 0;
	}

	@Override
	public boolean hasTransfers() {
		return true;
	}

	@Override
	public void updateTransfers(EnumFacing... faces) {}

	@Override
	public List<IFluxTransfer> getTransfers() {
		return Lists.newArrayList(transfer);
	}

}