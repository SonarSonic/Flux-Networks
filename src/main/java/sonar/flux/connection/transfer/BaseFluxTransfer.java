package sonar.flux.connection.transfer;

import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import sonar.core.api.energy.EnergyType;
import sonar.core.helpers.NBTHelper.SyncType;
import sonar.flux.api.energy.IFluxTransfer;

public abstract class BaseFluxTransfer implements IFluxTransfer {

	public EnergyType energy_type;
	public long added = 0;
	public long removed = 0;
	//public final List<Long> old_transfers = Lists.newArrayList();

	public BaseFluxTransfer(EnergyType type) {
		this.energy_type = type;
	}

	public void onStartServerTick() {
		added = 0;
		removed = 0;
	}

	public void onEndWorldTick() {
		/*
		old_transfers.add(transfer);
		if (old_transfers.size() > 10) {
			old_transfers.remove(0);
		}
		*/
	}

	@Override
	public void readData(NBTTagCompound nbt, SyncType type) {
		nbt.setByte("ET", (byte) this.energy_type.ordinal());
	}

	@Override
	public NBTTagCompound writeData(NBTTagCompound nbt, SyncType type) {
		energy_type = EnergyType.values()[nbt.getByte("ET")];
		return nbt;
	}

	@Override
	public final void addedToNetwork(long add) {
		added += add;
		onTransferAdded(add);
	}

	@Override
	public final void removedFromNetwork(long remove) {
		removed += remove;
		onTransferRemoved(remove);
	}

	public void onTransferAdded(long add) {}

	public void onTransferRemoved(long remove) {}

	@Override
	public final EnergyType getEnergyType() {
		return energy_type;
	}

}
