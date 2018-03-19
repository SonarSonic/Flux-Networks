package sonar.flux.connection.transfer.handlers;

import sonar.core.api.utils.ActionType;
import sonar.flux.api.energy.IEnergyTransfer;
import sonar.flux.api.energy.IFluxTransfer;
import sonar.flux.api.energy.ITransferHandler;

public abstract class BaseTransferHandler implements ITransferHandler {

	public double max_remove;
	public double max_add;

	public abstract double getMaxRemove();

	public abstract double getMaxAdd();

	@Override
	public void onStartServerTick() {
		max_remove = getMaxRemove();
		max_add = getMaxAdd();		
	}
	
	@Override
	public void onEndWorldTick() {
		
	}

	@Override
	public long addToNetwork(long maxTransferRF, ActionType actionType) {
		long added = 0;
		for (IFluxTransfer transfer : getTransfers()) {
			if (transfer != null && transfer instanceof IEnergyTransfer) {
				long toTransfer = getValidAdditionL(maxTransferRF - added);
				long add = ((IEnergyTransfer)transfer).addToNetwork(toTransfer, actionType);
				added += add;
				if (!actionType.shouldSimulate()) {
					max_add -= add;
				}
			}
		}
		return added;
	}

	@Override
	public long removeFromNetwork(long maxTransferRF, ActionType actionType) {
		long removed = 0;
		for (IFluxTransfer transfer : getTransfers()) {
			if (transfer != null && transfer instanceof IEnergyTransfer) {
				long toTransfer = getValidRemovalL(maxTransferRF - removed);
				long remove = ((IEnergyTransfer)transfer).removeFromNetwork(toTransfer, actionType);
				removed += remove;
				if (!actionType.shouldSimulate()) {
					max_remove -= remove;
				}
			}
		}
		return removed;
	}

	public long getValidAdditionL(double maxReceive) {
		return (long)getValidAddition(maxReceive);
	}

	public long getValidRemovalL(double maxRemoval) {
		return (long)getValidRemoval(maxRemoval);
	}

	public double getValidAddition(double maxReceive) {
		return Math.min(maxReceive, getValidMaxAddition());
	}

	public double getValidRemoval(double maxRemoval) {
		return Math.min(maxRemoval, getValidMaxRemoval());
	}

	public double getValidMaxAddition() {
		return Math.floor(max_add);
	}

	public double getValidMaxRemoval() {
		return Math.floor(max_remove);
	}
}
