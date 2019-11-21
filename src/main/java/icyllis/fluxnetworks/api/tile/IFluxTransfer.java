package icyllis.fluxnetworks.api.tile;

import net.minecraft.tileentity.TileEntity;

public interface IFluxTransfer {

    long addToNetwork(long amount, boolean simulate);

    long removeFromNetwork(long amount, boolean simulate);

    TileEntity getTile();

    boolean isInvalid();
}
