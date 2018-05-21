package sonar.flux.api.energy;

import net.minecraft.item.ItemStack;
import sonar.core.api.energy.EnergyType;
import sonar.core.api.utils.ActionType;

/**the handler Flux Networks uses to transfer energy*/
public interface IItemEnergyHandler {
	
	EnergyType getEnergyType();

	/**if flux networks can add energy to the stack*/
    boolean canAddEnergy(ItemStack stack);

	/**if flux networks can remove energy from the stack*/
    boolean canRemoveEnergy(ItemStack stack);
	
	/**returns how much energy was added to the item depending on the TransferType called, this will always be called after canAddEnergy*/
    long addEnergy(long add, ItemStack stack, ActionType actionType);

	/**returns how much energy was removed from the item depending on the TransferType called, this will always be called after canRemoveEnergy*/
    long removeEnergy(long remove, ItemStack stack, ActionType actionType);
	
}
