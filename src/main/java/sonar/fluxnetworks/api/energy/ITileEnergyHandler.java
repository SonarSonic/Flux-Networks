package sonar.fluxnetworks.api.energy;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;

import javax.annotation.Nonnull;

public interface ITileEnergyHandler {

    boolean canRenderConnection(@Nonnull TileEntity tile, Direction dir);

    boolean canAddEnergy(TileEntity tile, Direction dir);

    boolean canRemoveEnergy(TileEntity tile, Direction dir);

    long addEnergy(long amount, TileEntity tile, Direction dir, boolean simulate);

    long removeEnergy(long amount, TileEntity tile, Direction dir);
}
