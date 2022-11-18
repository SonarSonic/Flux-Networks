package sonar.fluxnetworks.api.energy;

import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;

public interface IItemEnergyAdapter {

    boolean hasCapability(@Nonnull ItemStack stack);

    boolean canSendTo(@Nonnull ItemStack stack);

    boolean canReceiveFrom(@Nonnull ItemStack stack);

    long sendTo(long amount, @Nonnull ItemStack stack, boolean simulate);

    long receiveFrom(long amount, @Nonnull ItemStack stack, boolean simulate);
}
