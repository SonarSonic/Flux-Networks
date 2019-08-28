package fluxnetworks.api.network;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;

public interface IFluxTransfer {

    void onServerStartTick();

    long addToNetwork(long amount);

    long removeFromNetwork(long amount, boolean simulate);

    void addedToNetwork(long amount);

    void removedFromNetwork(long amount);

    TileEntity getTile();

    EnumFacing getSide();

    ItemStack getDisplayStack();

    boolean isInvalid();
}
