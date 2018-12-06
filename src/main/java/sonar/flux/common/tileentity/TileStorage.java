package sonar.flux.common.tileentity;

import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import sonar.core.SonarCore;
import sonar.core.common.block.SonarBlock;
import sonar.core.helpers.NBTHelper;
import sonar.core.helpers.NBTHelper.SyncType;
import sonar.core.sync.ISonarValue;
import sonar.core.sync.SyncValueEnergyStorage;
import sonar.flux.FluxConfig;
import sonar.flux.FluxNetworks;
import sonar.flux.api.energy.internal.ITransferHandler;
import sonar.flux.api.tiles.IFluxStorage;
import sonar.flux.client.gui.EnumGuiTab;
import sonar.flux.client.gui.tabs.GuiTabIndexStorage;
import sonar.flux.connection.transfer.StorageTransfer;
import sonar.flux.connection.transfer.handlers.SingleTransferHandler;

import javax.annotation.Nonnull;
import java.util.List;

public abstract class TileStorage extends TileFluxConnector implements IFluxStorage {

	public final SingleTransferHandler handler = new SingleTransferHandler(this, new StorageTransfer(this));
	public final SyncValueEnergyStorage storage = new SyncValueEnergyStorage(value_watcher);
	public int maxTransfer;

	public static class Basic extends TileStorage {
		public Basic() {
			super(FluxConfig.basicCapacity, FluxConfig.basicTransfer);
			customName.setValueInternal("Basic Storage");
		}

		@Override
		public ItemStack getDisplayStack() {
			return writeStorageToDisplayStack(new ItemStack(FluxNetworks.fluxStorage, 1));
		}
	}

	public static class Herculean extends TileStorage {
		public Herculean() {
			super(FluxConfig.herculeanCapacity, FluxConfig.herculeanTransfer);
			customName.setValueInternal("Herculean Storage");
		}

		@Override
		public ItemStack getDisplayStack() {
			return writeStorageToDisplayStack(new ItemStack(FluxNetworks.largeFluxStorage, 1));
		}
	}

	public static class Gargantuan extends TileStorage {
		public Gargantuan() {
			super(FluxConfig.gargantuanCapacity, FluxConfig.gargantuanTransfer);
			customName.setValueInternal("Gargantuan Storage");
		}

		@Override
		public ItemStack getDisplayStack() {
			return writeStorageToDisplayStack(new ItemStack(FluxNetworks.massiveFluxStorage, 1));
		}
	}

	public TileStorage(int capacity, int transfer) {
		super(ConnectionType.STORAGE);
		maxTransfer = transfer;
		storage.setCapacity(capacity);
		storage.setMaxTransfer(transfer);
	}

	public long lastStorageUpdate;
	public boolean updateStorage;
	public int targetEnergy;

	public void update() {
		super.update();
		if (!world.isRemote) {
			if (updateStorage && lastStorageUpdate == 0) {
				lastStorageUpdate = getWorld().getWorldTime(); //stops it jumping on first receive
			} else if (updateStorage && getWorld().getWorldTime() > lastStorageUpdate + 20) {
				SonarCore.sendPacketAround(this, 128, 10);
				lastStorageUpdate = getWorld().getWorldTime();
				updateStorage = false;
			}
		} else if (updateStorage && storage.getEnergyStored() != targetEnergy) {
			int inc = storage.getMaxEnergyStored() / 50;
			int dif = Math.abs(storage.getEnergyStored() - targetEnergy);
			if (dif < inc * 2) {
				inc = inc / 4; // slows when it gets closer
			}
			if (storage.getEnergyStored() < targetEnergy) {
				storage.setEnergyStored(Math.min(storage.getEnergyStored() + inc, targetEnergy));
			} else {
				storage.setEnergyStored(Math.max(storage.getEnergyStored() - inc, targetEnergy));
			}
		} else {
			updateStorage = false;
		}
		
	}
	public EnumFacing[] getValidFaces() {
		return new EnumFacing[]{EnumFacing.UP, EnumFacing.DOWN};
	}

	@Override
	public void onInternalValueChanged(ISonarValue value){
		super.onInternalValueChanged(value);
		if (world != null && !world.isRemote && storage == value) {
			updateStorage = true;
		}
	}

	@Override
	public long getMaxEnergyStored() {
		return storage.getMaxEnergyStored();
	}

	@Override
	public long getEnergyStored() {
		return storage.getEnergyStored();
	}

	@Override
	public long getTransferLimit() {
		return Math.min(super.getTransferLimit(), storage.getMaxExtract());
	}

	@Override
	public boolean canConnectEnergy(EnumFacing from) {
		return true;
	}

	@Override
	public int getEnergyStored(EnumFacing from) {
		return storage.getEnergyStored();
	}

	@Override
	public int getMaxEnergyStored(EnumFacing from) {
		return storage.getMaxEnergyStored();
	}

	public void readData(NBTTagCompound nbt, SyncType type) {
		super.readData(nbt, type);
		if (type.isType(SyncType.DROP, SyncType.SPECIAL))
			this.storage.setEnergyStored(nbt.getInteger("energy"));
	}

	public NBTTagCompound writeData(NBTTagCompound nbt, SyncType type) {
		super.writeData(nbt, type);
		if (type.isType(SyncType.DROP, SyncType.SPECIAL)) {
			nbt.setInteger("energy", this.storage.getEnergyStored());
		}
		return nbt;
	}

	public ItemStack writeStorageToDisplayStack(ItemStack stack){
		NBTTagCompound tag = stack.getOrCreateSubCompound(SonarBlock.DROP_TAG_NAME);
		writeData(tag, NBTHelper.SyncType.DROP);
		return stack;
	}

	@Override
	public void writePacket(ByteBuf buf, int id) {
		super.writePacket(buf, id);
		switch (id) {
		case 10:
			buf.writeInt(storage.getEnergyStored());
			break;
		}
	}

	@Override
	public void readPacket(ByteBuf buf, int id) {
		super.readPacket(buf, id);
		switch (id) {
		case 10:
			//FIXME - if they are in the gui, update power instantly
			targetEnergy = buf.readInt();
			updateStorage = targetEnergy != storage.getEnergyStored();
			break;
		}
	}

	@Override
	public ITransferHandler getTransferHandler() {
		return handler;
	}

	@Nonnull
	public Object getIndexScreen(List<EnumGuiTab> tabs){
		return new GuiTabIndexStorage(tabs);
	}
}