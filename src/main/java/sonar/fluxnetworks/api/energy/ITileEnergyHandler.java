package sonar.fluxnetworks.api.energy;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;

import javax.annotation.Nonnull;

public interface ITileEnergyHandler {

    boolean canRenderConnection(@Nonnull TileEntity tile, EnumFacing side);

    boolean canAddEnergy(TileEntity tile, EnumFacing side);

    boolean canRemoveEnergy(TileEntity tile, EnumFacing side);

    long addEnergy(long amount, TileEntity tile, EnumFacing side, boolean simulate);

    long removeEnergy(long amount, TileEntity tile, EnumFacing side);
}
