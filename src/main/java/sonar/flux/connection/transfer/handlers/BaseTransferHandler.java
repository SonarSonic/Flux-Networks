package sonar.flux.connection.transfer.handlers;

import sonar.core.api.energy.EnergyType;
import sonar.flux.FluxNetworks;
import sonar.flux.api.energy.internal.ITransferHandler;

public abstract class BaseTransferHandler implements ITransferHandler {

	///energy added this tick
	public long added;
	public long removed;

	//// THE BUFFER WILL ALWAYS BE STORED RELATIVE TO FE \\\\
	public long buffer;
	public long buffer_transfer;

	public long current_addition;
	public long current_removal;
	public long simulated_this_tick;
	public long attempted_this_tick;
	private long buffer_size;

	@Override
	public void onStartServerTick() {
		simulated_this_tick = 0;
		attempted_this_tick = 0;
		added = 0;
		removed = 0;
	}
	
	@Override
	public void onEndWorldTick() {
		buffer_size = Math.max(buffer_size, Math.max(simulated_this_tick, attempted_this_tick));
		buffer_transfer = 0;
	}

	public long addToBuffer(long add, EnergyType energyType, boolean simulate){
		long canAdd = getValidAddition_FE(add, energyType);
		if(canAdd > 0){
			if(!simulate){
				buffer += canAdd;
				added += canAdd;
			}
			return convert(canAdd, EnergyType.FE, energyType);
		}
		return 0;
	}

	public abstract long getAddRate();

	public abstract long getRemoveRate();

	public abstract long getBufferLimiter();

	@Override
	public final long getAdded(){
		return added;
	}

	@Override
	public final long getRemoved(){
		return removed;
	}

	@Override
	public final long getBuffer() {
		return buffer;
	}

	private long getBufferSize(){
		return buffer_size;
	}

	private void checkBufferSize(long toAdd_FE){
		buffer_size = Math.min(Math.max(buffer_size, this.getAddRate()), getBufferLimiter());
	}

	private long getMaxAddition(){
		long valid = Math.min(getAddRate() - getAdded(), getBufferSize() - getBuffer());
		return valid > 0 ? valid : 0;
	}

	private long getMaxRemoval(){
		return getRemoveRate() - getRemoved();
	}

	private long getValidAddition(long toAdd_FE){
		checkBufferSize(toAdd_FE);
		return Math.min(getMaxAddition(), toAdd_FE);
	}

	private long getValidRemoval(long toRemove_FE){
		return Math.min(getMaxRemoval(), toRemove_FE);
	}

	///returns valid addition relative Forge Energy
	public long getValidAddition_FE(long maxReceive, EnergyType type) {
		long maxReceive_FE = toFE(maxReceive, type);
		return getValidAddition(maxReceive_FE);
	}

	public long getValidRemoval_FE(long maxRemoval, EnergyType type) {
		long maxReceive_FE = toFE(maxRemoval, type);
		return getValidRemoval(maxReceive_FE);
	}

	///returns valid addition in Forge Energy
	public long getValidAddition(long maxReceive, EnergyType type) {
		return convert(getValidAddition_FE(maxReceive, type), EnergyType.FE, type);
	}

	public long getValidRemoval(long maxRemoval, EnergyType type) {
		return convert(getValidRemoval_FE(maxRemoval, type), EnergyType.FE, type);
	}

	public final long toFE(long val, EnergyType from) {
		return FluxNetworks.TRANSFER_HANDLER.convert(val, from, EnergyType.FE);
	}

	public final long convert(long val, EnergyType from, EnergyType to) {
		return FluxNetworks.TRANSFER_HANDLER.convert(val, from, to);
	}

}
