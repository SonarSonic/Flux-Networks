package sonar.flux.api;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import sonar.core.api.energy.EnergyType;
import sonar.core.api.nbt.INBTSyncable;
import sonar.core.helpers.NBTHelper.SyncType;
import sonar.flux.api.energy.internal.IFluxTransfer;
import sonar.flux.connection.transfer.BaseFluxTransfer;
import sonar.flux.connection.transfer.ISidedTransfer;
import sonar.flux.connection.transfer.PhantomTransfer;
import sonar.flux.network.FluxNetworkData;

public class ClientTransfer implements INBTSyncable, IFluxTransfer {

	public EnergyType energyType;
	public EnumFacing direction;
	public boolean isPhantomPower;
	public long added;
	public long removed;
	public ItemStack stack;
	public ClientTransferHandler handler;

	public ClientTransfer(ClientTransferHandler handler) {
		this.handler = handler;
	}

	@Override
	public void readData(NBTTagCompound nbt, SyncType type) {
		energyType = EnergyType.readFromNBT(nbt, FluxNetworkData.ENERGY_TYPE);
		byte direction_byte = nbt.getByte("d");
		direction = direction_byte == -1 ? null : EnumFacing.VALUES[direction_byte];
		isPhantomPower = nbt.getBoolean("p");
		added = nbt.getLong("a");
		removed = nbt.getLong("r");
		stack = new ItemStack(nbt);
	}

	@Override
	public NBTTagCompound writeData(NBTTagCompound nbt, SyncType type) {
		EnergyType.writeToNBT(energyType, nbt, FluxNetworkData.ENERGY_TYPE);
		nbt.setByte("d", direction == null ? -1 : (byte) direction.ordinal());
		if (isPhantomPower)
			nbt.setBoolean("p", isPhantomPower);
		if (added != 0)
			nbt.setLong("a", added);
		if (removed != 0)
			nbt.setLong("r", removed);
		stack.writeToNBT(nbt);
		return nbt;
	}

	public static ClientTransfer getInstanceFromHandler(ClientTransferHandler handler, IFluxTransfer transfer) {
		ClientTransfer clientTransfer = new ClientTransfer(handler);
		clientTransfer.energyType = transfer.getEnergyType();
		if (transfer instanceof BaseFluxTransfer) {
			BaseFluxTransfer baseTransfer = (BaseFluxTransfer) transfer;
			clientTransfer.added = baseTransfer.added;
			clientTransfer.removed = baseTransfer.removed;
		}
		if (transfer instanceof ISidedTransfer) {
			ISidedTransfer sided = (ISidedTransfer) transfer;
			clientTransfer.direction = sided.getDirection();
		}
		clientTransfer.isPhantomPower = transfer instanceof PhantomTransfer;
		clientTransfer.stack = transfer.getDisplayStack();
		return clientTransfer;
	}

	@Override
	public void onStartServerTick() {}

	@Override
	public void onEndWorldTick() {}

	@Override
	public void addedToNetwork(long add, EnergyType energyType) {}

	@Override
	public void removedFromNetwork(long remove, EnergyType energyType) {}

	@Override
	public EnergyType getEnergyType() {
		return this.energyType;
	}

	@Override
	public ItemStack getDisplayStack() {
		return this.stack;
	}

}
