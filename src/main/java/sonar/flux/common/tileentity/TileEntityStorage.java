package sonar.flux.common.tileentity;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import sonar.core.SonarCore;
import sonar.core.api.utils.ActionType;
import sonar.core.helpers.NBTHelper.SyncType;
import sonar.core.network.sync.IDirtyPart;
import sonar.core.network.sync.SyncEnergyStorage;
import sonar.core.utils.IGuiTile;
import sonar.flux.FluxConfig;
import sonar.flux.client.GuiFlux;
import sonar.flux.common.ContainerFlux;

public class TileEntityStorage extends TileEntityFlux implements IGuiTile {

	public final SyncEnergyStorage storage;
	public int maxTransfer;

	public static class Basic extends TileEntityStorage {
		public Basic() {
			super(FluxConfig.basicCapacity, FluxConfig.basicTransfer);
			customName.setDefault("Basic Storage");
		}
	}

	public static class Advanced extends TileEntityStorage {
		public Advanced() {
			super(FluxConfig.herculeanCapacity, FluxConfig.herculeanTransfer);
			customName.setDefault("Herculean Storage");
		}
	}

	public static class Massive extends TileEntityStorage {
		public Massive() {
			super(FluxConfig.gargantuanCapacity, FluxConfig.gargantuanTransfer);
			customName.setDefault("Gargantuan Storage");
		}
	}

	public TileEntityStorage(int capacity, int transfer) {
		super(ConnectionType.STORAGE);
		maxTransfer = transfer;
		storage = new SyncEnergyStorage(capacity, maxTransfer);
		syncList.addPart(storage);
	}

	@Override
	public void markChanged(IDirtyPart part) {
		super.markChanged(part);
		if (this.worldObj != null) {
			if (part == storage) {
				SonarCore.sendPacketAround(this, 128, 10);
			}
			if (part == colour) {
				SonarCore.sendPacketAround(this, 128, 11);
			}
		}
	}

	public boolean canTransfer() {
		return true;
	}

	@Override
	public Object getGuiContainer(EntityPlayer player) {
		return new ContainerFlux(player, this, false);
	}

	@Override
	public Object getGuiScreen(EntityPlayer player) {
		return new GuiFlux((Container) getGuiContainer(player), this, player);
	}

	@Override
	public long getTransferLimit() {
		return storage.getMaxExtract();
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
		if (type.isType(SyncType.DROP)) {
			this.storage.setEnergyStored(nbt.getInteger("energy"));
		}
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
			storage.writeToBuf(buf);
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
			storage.readFromBuf(buf);
			break;
		case 11:
			colour.readFromBuf(buf);
			break;
		}
	}
}