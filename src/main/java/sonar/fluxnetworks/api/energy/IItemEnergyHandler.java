package sonar.fluxnetworks.api.energy;

import net.minecraft.item.ItemStack;

public interface IItemEnergyHandler {

    boolean canAddEnergy(ItemStack stack);

    boolean canRemoveEnergy(ItemStack stack);

    long addEnergy(long amount, ItemStack stack, boolean simulate);

    long removeEnergy(long amount, ItemStack stack);
}
