package sonar.fluxnetworks.common.connection.transfer;

import sonar.fluxnetworks.api.network.IFluxTransfer;
import sonar.fluxnetworks.common.tileentity.TileFluxStorage;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

public class StorageTransfer implements IFluxTransfer {

    public final TileFluxStorage tile;

    public StorageTransfer(TileFluxStorage tile) {
        this.tile = tile;
    }

    @Override
    public void onStartCycle() {
        tile.sendPacketIfNeeded();
    }

    @Override
    public void onEndCycle() {}

    @Override
    public long addEnergy(long amount, boolean simulate) {
        return tile.addEnergy(amount, simulate);
    }

    @Override
    public long removeEnergy(long amount, boolean simulate) {
        return tile.removeEnergy(amount, simulate);
    }

    @Override
    public void onEnergyAdded(long amount) {}

    @Override
    public void onEnergyRemoved(long amount) {}

    @Override
    public TileEntity getTile() {
        return tile;
    }

    @Override
    public ItemStack getDisplayStack() {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean isInvalid() {
        return tile.isRemoved();
    }
}
