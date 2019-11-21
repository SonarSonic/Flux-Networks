package icyllis.fluxnetworks.api.util;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;

public interface ITileEnergyHandler {

    boolean match(TileEntity tile, Direction side);

    long addEnergy(long amount, TileEntity tile, Direction side, boolean simulate);

    long removeEnergy(long amount, TileEntity tile, Direction side);
}
