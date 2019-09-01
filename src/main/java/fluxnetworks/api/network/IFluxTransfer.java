package fluxnetworks.api.network;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;

public interface IFluxTransfer {

    void onServerStartTick();

    long addToNetwork(long amount, boolean simulate);

    long removeFromNetwork(long amount, boolean simulate);

    void addedToNetwork(long amount);

    void removedFromNetwork(long amount);

    TileEntity getTile();

    ItemStack getDisplayStack();

    boolean isInvalid();
}
