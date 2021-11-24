package sonar.fluxnetworks.api.energy;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;

import javax.annotation.Nonnull;

public interface IBlockEnergyBridge {

    boolean hasCapability(@Nonnull BlockEntity target, @Nonnull Direction side);

    boolean canAddEnergy(@Nonnull BlockEntity target, @Nonnull Direction side);

    boolean canRemoveEnergy(@Nonnull BlockEntity target, @Nonnull Direction side);

    long addEnergy(long energy, @Nonnull BlockEntity target, @Nonnull Direction side, boolean simulate);

    long removeEnergy(long energy, @Nonnull BlockEntity target, @Nonnull Direction side);
}
