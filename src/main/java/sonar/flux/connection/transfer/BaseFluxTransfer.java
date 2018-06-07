package sonar.flux.connection.transfer;

import net.minecraft.nbt.NBTTagCompound;
import sonar.core.api.energy.EnergyType;
import sonar.core.helpers.NBTHelper.SyncType;
import sonar.flux.api.energy.internal.IFluxTransfer;

public abstract class BaseFluxTransfer implements IFluxTransfer {

	public EnergyType energy_type;
	public long added = 0;
	public long removed = 0;

	public BaseFluxTransfer(EnergyType type) {
		this.energy_type = type;
	}

	public void onStartServerTick() {
		added = 0;
		removed = 0;
	}

	public void onEndWorldTick() {
		/* old_transfers.add(transfer); if (old_transfers.size() > 10) { old_transfers.remove(0); } */
	}

	@Override
	public void readData(NBTTagCompound nbt, SyncType type) {
		setEnergyType(EnergyType.readFromNBT(nbt, "ET"));
	}

	@Override
	public NBTTagCompound writeData(NBTTagCompound nbt, SyncType type) {
		EnergyType.writeToNBT(getEnergyType(), nbt, "ET");
		return nbt;
	}

	@Override
	public final void addedToNetwork(long add, EnergyType energyType) {
		added += add;
		onTransferAdded(add);
	}

	@Override
	public final void removedFromNetwork(long remove, EnergyType energyType) {
		removed += remove;
		onTransferRemoved(remove);
	}

	public void onTransferAdded(long add) {}

	public void onTransferRemoved(long remove) {}

	@Override
	public EnergyType getEnergyType() {
		return energy_type;
	}

	public void setEnergyType(EnergyType type) {
		energy_type = type;
	}

}
