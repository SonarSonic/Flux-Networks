package sonar.fluxnetworks.api.energy;

import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public interface IItemEnergyHandler {

    boolean hasCapability(@Nonnull ItemStack stack);

    boolean canAddEnergy(@Nonnull ItemStack stack);

    boolean canRemoveEnergy(@Nonnull ItemStack stack);

    long addEnergy(long amount, @Nonnull ItemStack stack, boolean simulate);

    long removeEnergy(long amount, @Nonnull ItemStack stack);
}
