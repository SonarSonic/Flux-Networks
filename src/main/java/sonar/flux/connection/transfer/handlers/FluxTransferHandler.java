package sonar.flux.connection.transfer.handlers;

import sonar.core.api.energy.EnergyType;
import sonar.core.api.utils.ActionType;
import sonar.flux.FluxNetworks;
import sonar.flux.api.energy.internal.IEnergyTransfer;
import sonar.flux.api.energy.internal.IFluxTransfer;
import sonar.flux.api.network.IFluxNetwork;
import sonar.flux.api.tiles.IFlux;
import sonar.flux.connection.BasicFluxNetwork;

public abstract class FluxTransferHandler<T extends IFlux> extends BaseTransferHandler {

	public final T flux;

	public FluxTransferHandler(T flux) {
		this.flux = flux;
	}

	public IFluxNetwork getNetwork(){
		return flux.getNetwork();
	}
	
	@Override
	public long addToNetwork(long maxTransferRF, EnergyType energyType, ActionType actionType) {
		long added = Math.min(getAddRate() - buffer_transfer, Math.min(toFE(maxTransferRF, energyType), buffer));
		if(!actionType.shouldSimulate() && added > 0){
			buffer -= added;
			buffer_transfer += added;
		}
		return convert(added, EnergyType.FE, energyType);
	}

	@Override
	public long removeFromNetwork(long maxTransferRF, EnergyType energyType, ActionType actionType) {
		long actualMax = Math.min(maxTransferRF, getValidRemoval(maxTransferRF, energyType));
		long removed = 0;
		for (IFluxTransfer transfer : getTransfers()) {
			if (transfer != null && getNetwork().canConvert(energyType, transfer.getEnergyType()) && transfer instanceof IEnergyTransfer) {
				long toTransfer = actualMax - removed;
				long remove = ((IEnergyTransfer)transfer).removeFromNetworkWithConvert(toTransfer, energyType, actionType);
				removed += remove;
				if (!actionType.shouldSimulate()) {
					this.removed += FluxNetworks.TRANSFER_HANDLER.convert(remove, energyType, getNetwork().getDefaultEnergyType());
				}
			}
		}
		return removed;
	}

	@Override
	public long getAddRate() {
		return flux.getNetwork().isFakeNetwork() ? 0 : flux.getTransferLimit();
	}

	@Override
	public long getRemoveRate() {
		return flux.getTransferLimit();
	}

	@Override
	public long getBufferLimiter(){
		return flux.getNetwork().isFakeNetwork() ? 0 : ((BasicFluxNetwork)getNetwork()).buffer_limiter;
	}

}
