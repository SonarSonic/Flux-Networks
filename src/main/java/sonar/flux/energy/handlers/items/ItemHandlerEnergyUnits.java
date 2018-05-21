package sonar.flux.energy.handlers.items;

import ic2.api.item.ElectricItem;
import ic2.api.item.IElectricItem;
import ic2.api.item.IElectricItemManager;
import ic2.api.item.ISpecialElectricItem;
import net.minecraft.item.ItemStack;
import sonar.core.api.energy.EnergyType;
import sonar.core.api.utils.ActionType;
import sonar.flux.api.energy.IItemEnergyHandler;
import sonar.flux.api.energy.ItemEnergyHandler;

@ItemEnergyHandler(modid = "ic2", priority = 4)
public class ItemHandlerEnergyUnits implements IItemEnergyHandler {

    @Override
    public EnergyType getEnergyType() {
        return EnergyType.EU;
    }

    @Override
    public boolean canAddEnergy(ItemStack stack) {
        return !stack.isEmpty() && (stack.getItem() instanceof IElectricItem || stack.getItem() instanceof ISpecialElectricItem);
    }

    @Override
    public boolean canRemoveEnergy(ItemStack stack) {
        return !stack.isEmpty() && (stack.getItem() instanceof IElectricItem || stack.getItem() instanceof ISpecialElectricItem);
    }

    @Override
    public long addEnergy(long add, ItemStack stack, ActionType actionType) {
        IElectricItemManager manager = getManager(stack);
        return (long) manager.charge(stack, add, 4, false, actionType.shouldSimulate());
    }

    @Override
    public long removeEnergy(long remove, ItemStack stack, ActionType actionType) {
        IElectricItemManager manager = getManager(stack);
        return (long) manager.discharge(stack, remove, 4, false, true, actionType.shouldSimulate());
    }

    public static IElectricItemManager getManager(ItemStack stack) {
        if (stack.getItem() instanceof ISpecialElectricItem) {
            IElectricItemManager manager = ((ISpecialElectricItem) stack.getItem()).getManager(stack);
            if(manager == null){
                manager = ElectricItem.getBackupManager(stack);
            }
            return manager;
        }
        return ElectricItem.manager;
    }
}
