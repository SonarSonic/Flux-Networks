package sonar.fluxnetworks.api.energy;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;

import javax.annotation.Nonnull;

public interface ITileEnergyHandler {

    boolean hasCapability(@Nonnull TileEntity tile, EnumFacing side);

    boolean canAddEnergy(@Nonnull TileEntity tile, EnumFacing side);

    boolean canRemoveEnergy(@Nonnull TileEntity tile, EnumFacing side);

    long addEnergy(long amount, @Nonnull TileEntity tile, EnumFacing side, boolean simulate);

    long removeEnergy(long amount, @Nonnull TileEntity tile, EnumFacing side);
}
