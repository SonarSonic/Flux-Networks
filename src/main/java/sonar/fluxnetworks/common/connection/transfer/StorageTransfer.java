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
    public void onServerStartTick() {
        tile.sendPacketIfNeeded();
    }

    @Override
    public long addToNetwork(long amount, boolean simulate) {
        return tile.removeEnergy(amount, simulate);
    }

    @Override
    public long removeFromNetwork(long amount, boolean simulate) {
        return tile.addEnergy(amount, simulate);
    }

    @Override
    public void addedToNetwork(long amount) {

    }

    @Override
    public void removedFromNetwork(long amount) {

    }

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
