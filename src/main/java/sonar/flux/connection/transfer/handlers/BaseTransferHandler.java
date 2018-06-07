package sonar.flux.connection.transfer.handlers;

import sonar.flux.api.energy.internal.ITransferHandler;

public abstract class BaseTransferHandler implements ITransferHandler {

	public long remove_limit;
	public long max_remove;
	public long add_limit;
	public long max_add;

	//// THE BUFFER WILL ALWAYS BE STORED RELATIVE TO FE \\\\
	public long buffer;
	public long current_addition;
	public long current_removal;

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

	public long addToBuffer(long add, boolean simulate){
		long canAdd = Math.min(add, getValidMaxAddition() - getBuffer());
		if(canAdd > 0){
			if(!simulate){
				buffer += canAdd;
				max_add += canAdd;
			}
			return canAdd;
		}
		return 0;
	}

	public long getBuffer(){
		return buffer;
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
