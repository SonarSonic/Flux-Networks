package sonar.flux.connection.transfer.handlers;

import sonar.core.api.energy.EnergyType;
import sonar.core.api.utils.ActionType;
import sonar.flux.FluxNetworks;
import sonar.flux.api.energy.internal.IEnergyTransfer;
import sonar.flux.api.energy.internal.IFluxTransfer;
import sonar.flux.api.network.IFluxNetwork;
import sonar.flux.api.tiles.IFlux;

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
		long added = 0;
		for (IFluxTransfer transfer : getTransfers()) {
			if (transfer != null && getNetwork().canConvert(energyType, transfer.getEnergyType()) && transfer instanceof IEnergyTransfer) {		
				long toTransfer = getValidAddition(maxTransferRF - added, energyType);				
				long add = ((IEnergyTransfer)transfer).addToNetworkWithConvert(toTransfer, energyType, actionType);				
				added += add;
				if (!actionType.shouldSimulate()) {
					max_add -= FluxNetworks.TRANSFER_HANDLER.convert(add, energyType, getNetwork().getDefaultEnergyType());
				}
			}
		}
		return added;
	}

	@Override
	public long removeFromNetwork(long maxTransferRF, EnergyType energyType, ActionType actionType) {
		long removed = 0;
		for (IFluxTransfer transfer : getTransfers()) {
			if (transfer != null && getNetwork().canConvert(energyType, transfer.getEnergyType()) && transfer instanceof IEnergyTransfer) {
				long toTransfer = getValidRemoval(maxTransferRF - removed);				
				long remove = ((IEnergyTransfer)transfer).removeFromNetworkWithConvert(toTransfer, energyType, actionType);				
				removed += remove;
				if (!actionType.shouldSimulate()) {
					max_remove -= FluxNetworks.TRANSFER_HANDLER.convert(remove, energyType, getNetwork().getDefaultEnergyType());
				}
			}
		}
		return removed;
	}

	@Override
	public long getMaxRemove() {
		return flux.getTransferLimit();
	}

	@Override
	public long getMaxAdd() {
		return flux.getTransferLimit();
	}

	public long getValidAddition(long maxReceive, EnergyType type) {
		return Math.min(maxReceive, FluxNetworks.TRANSFER_HANDLER.convert(getValidMaxAddition(), getNetwork().getDefaultEnergyType(), type));
	}

	public long getValidRemoval(long maxRemoval, EnergyType type) {
		return Math.min(maxRemoval, FluxNetworks.TRANSFER_HANDLER.convert(getValidMaxRemoval(), getNetwork().getDefaultEnergyType(), type));
	}
}
