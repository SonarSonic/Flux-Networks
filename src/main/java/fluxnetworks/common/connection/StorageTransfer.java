package fluxnetworks.common.connection;

import fluxnetworks.FluxNetworks;
import fluxnetworks.api.network.IFluxTransfer;
import fluxnetworks.common.core.FluxUtils;
import fluxnetworks.common.tileentity.TileFluxStorage;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;

public class StorageTransfer implements IFluxTransfer {

    public final TileFluxStorage tile;

    public StorageTransfer(TileFluxStorage tile) {
        this.tile = tile;
    }

    @Override
    public void onServerStartTick() {

    }

    @Override
    public long addToNetwork(long amount, boolean simulate) {
        long remove = tile.removeEnergy(amount, simulate);
        return remove;
    }

    @Override
    public long removeFromNetwork(long amount, boolean simulate) {
        long add = tile.addEnergy(amount, simulate);
        return add;
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
        return tile.isInvalid();
    }
}
