package sonar.fluxnetworks.api.energy;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;

import javax.annotation.Nonnull;

public interface IBlockEnergyConnector {

    boolean hasCapability(@Nonnull BlockEntity target, @Nonnull Direction side);

    boolean canSendTo(@Nonnull BlockEntity target, @Nonnull Direction side);

    boolean canReceiveFrom(@Nonnull BlockEntity target, @Nonnull Direction side);

    long sendTo(long amount, @Nonnull BlockEntity target, @Nonnull Direction side, boolean simulate);

    long receiveFrom(long amount, @Nonnull BlockEntity target, @Nonnull Direction side, boolean simulate);
}
