package sonar.flux.common.tileentity;

import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import sonar.core.SonarCore;
import sonar.core.helpers.NBTHelper.SyncType;
import sonar.core.network.sync.IDirtyPart;
import sonar.core.network.sync.SyncEnergyStorage;
import sonar.flux.FluxConfig;
import sonar.flux.FluxNetworks;
import sonar.flux.api.energy.internal.ITransferHandler;
import sonar.flux.api.network.FluxCache;
import sonar.flux.api.tiles.IFluxStorage;
import sonar.flux.client.GuiTab;
import sonar.flux.client.tabs.GuiTabStorageIndex;
import sonar.flux.connection.transfer.StorageTransfer;
import sonar.flux.connection.transfer.handlers.SingleTransferHandler;

import javax.annotation.Nonnull;
import java.util.List;

public abstract class TileStorage extends TileFluxConnector implements IFluxStorage {

	public final SingleTransferHandler handler = new SingleTransferHandler(this, new StorageTransfer(this));
	public final SyncEnergyStorage storage;
	public int maxTransfer;

	public static class Basic extends TileStorage {
		public Basic() {
			super(FluxConfig.basicCapacity, FluxConfig.basicTransfer);
			customName.setDefault("Basic Storage");
		}

		@Override
		public ItemStack getDisplayStack() {
			return new ItemStack(FluxNetworks.fluxStorage, 1);
		}
	}

	public static class Herculean extends TileStorage {
		public Herculean() {
			super(FluxConfig.herculeanCapacity, FluxConfig.herculeanTransfer);
			customName.setDefault("Herculean Storage");
		}

		@Override
		public ItemStack getDisplayStack() {
			return new ItemStack(FluxNetworks.largeFluxStorage, 1);
		}
	}

	public static class Gargantuan extends TileStorage {
		public Gargantuan() {
			super(FluxConfig.gargantuanCapacity, FluxConfig.gargantuanTransfer);
			customName.setDefault("Gargantuan Storage");
		}

		@Override
		public ItemStack getDisplayStack() {
			return new ItemStack(FluxNetworks.massiveFluxStorage, 1);
		}
	}

	public TileStorage(int capacity, int transfer) {
		super(ConnectionType.STORAGE);
		maxTransfer = transfer;
		storage = new SyncEnergyStorage(capacity, maxTransfer);
		syncList.addPart(storage);
	}

	public long lastStorageUpdate;
	public boolean updateStorage;
	public int targetEnergy;

	public void update() {
		super.update();		
		if (isServer()) {
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
	public void markChanged(IDirtyPart part) {
		super.markChanged(part);
        if (this.world != null && !world.isRemote) {
			if (part == storage) {
				network.markTypeDirty(FluxCache.storage);
				updateStorage = true;
			} else if (part == colour) {
				SonarCore.sendPacketAround(this, 128, 11);
			}
		}
	}

	@Override
	public long getMaxEnergyStored() {
		return storage.getFullCapacity();
	}

	@Override
	public long getEnergyStored() {
		return storage.getEnergyLevel();
	}

	public boolean hasTransfers() {
		return true;
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
		if (type.isType(SyncType.DROP))
			this.storage.setEnergyStored(nbt.getInteger("energy"));
	}

	public NBTTagCompound writeData(NBTTagCompound nbt, SyncType type) {
		super.writeData(nbt, type);
		if (type.isType(SyncType.DROP)) {
			nbt.setInteger("energy", this.storage.getEnergyStored());
		}
		return nbt;
	}

	@Override
	public void writePacket(ByteBuf buf, int id) {
		super.writePacket(buf, id);
		switch (id) {
		case 10:
			buf.writeInt(storage.getEnergyStored());
			break;
		case 11:
			colour.writeToBuf(buf);
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
			updateStorage = true;
			break;
		case 11:
			colour.readFromBuf(buf);
			break;
		}
	}

	@Override
	public ITransferHandler getTransferHandler() {
		return handler;
	}

	@Nonnull
	public Object getIndexScreen(List<GuiTab> tabs){
		return new GuiTabStorageIndex(this, tabs);
	}
}