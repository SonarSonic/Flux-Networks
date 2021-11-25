package sonar.fluxnetworks.common.test;

/*import sonar.fluxnetworks.common.blockentity.FluxStorageEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

@Deprecated
public class StorageTransfer implements IFluxTransfer {

    public final FluxStorageEntity tile;

    public StorageTransfer(FluxStorageEntity tile) {
        this.tile = tile;
    }

    @Override
    public void onCycleStart() {
        //tile.sendPacketIfNeeded();
    }

    @Override
    public void onCycleEnd() {}

    @Override
    public long sendToTile(long amount, boolean simulate) {
        return 0;
    }

    *//*@Override
    public long removeEnergy(long amount, boolean simulate) {
        return tile.removeEnergy(amount, simulate);
    }

    @Override
    public void onEnergyAdded(long amount) {}*//*

    @Override
    public void onEnergyReceived(long amount) {}

    @Override
    public TileEntity getTile() {
        return tile;
    }

    @Override
    public ItemStack getDisplayStack() {
        return ItemStack.EMPTY;
    }

    *//*@Override
    public boolean isInvalid() {
        return tile.isRemoved();
    }*//*
}*/
