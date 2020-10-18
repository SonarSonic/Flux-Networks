package sonar.fluxnetworks.api.energy;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;

import javax.annotation.Nonnull;

public interface ITileEnergyHandler {

    boolean hasCapability(@Nonnull TileEntity tile, @Nonnull Direction dir);

    boolean canAddEnergy(@Nonnull TileEntity tile, @Nonnull Direction dir);

    boolean canRemoveEnergy(@Nonnull TileEntity tile, @Nonnull Direction dir);

    long addEnergy(long amount, @Nonnull TileEntity tile, @Nonnull Direction dir, boolean simulate);

    long removeEnergy(long amount, @Nonnull TileEntity tile, @Nonnull Direction dir);
}
