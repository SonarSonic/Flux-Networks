package sonar.flux.connection.transfer.handlers;

import sonar.core.api.utils.ActionType;
import sonar.flux.api.energy.IEnergyTransfer;
import sonar.flux.api.energy.IFluxTransfer;
import sonar.flux.api.energy.ITransferHandler;

public abstract class BaseTransferHandler implements ITransferHandler {

	public long remove_limit;
	public long max_remove;
	public long add_limit;
	public long max_add;

	public abstract long getMaxRemove();

	public abstract long getMaxAdd();

	@Override
	public void onStartServerTick() {
		max_remove = remove_limit = getMaxRemove();
		max_add = add_limit = getMaxAdd();		
	}
	
	@Override
	public void onEndWorldTick() {
		
	}
	
	public long getAdded(){
		return add_limit - max_add;
	}
	
	public long getRemoved(){
		return remove_limit - max_remove;
	}

	public long getValidAddition(long maxReceive) {
		return Math.min(maxReceive, getValidMaxAddition());
	}

	public long getValidRemoval(long maxRemoval) {
		return Math.min(maxRemoval, getValidMaxRemoval());
	}

	public long getValidMaxAddition() {
		return max_add;
	}

	public long getValidMaxRemoval() {
		return max_remove;
	}
}
