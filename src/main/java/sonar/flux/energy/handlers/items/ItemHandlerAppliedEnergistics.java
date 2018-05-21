package sonar.flux.energy.handlers.items;

import appeng.api.implementations.items.IAEItemPowerStorage;
import net.minecraft.item.ItemStack;
import sonar.core.api.energy.EnergyType;
import sonar.core.api.utils.ActionType;
import sonar.core.integration.AE2Helper;
import sonar.flux.api.energy.IItemEnergyHandler;
import sonar.flux.api.energy.ItemEnergyHandler;

@ItemEnergyHandler(modid = "appliedenergistics2", priority = 4)
public class ItemHandlerAppliedEnergistics implements IItemEnergyHandler {

    @Override
    public EnergyType getEnergyType() {
        return EnergyType.AE;
    }

    @Override
    public boolean canAddEnergy(ItemStack stack) {
        return !stack.isEmpty() && stack.getItem() instanceof IAEItemPowerStorage;
    }

    @Override
    public boolean canRemoveEnergy(ItemStack stack) {
        return !stack.isEmpty() && stack.getItem() instanceof IAEItemPowerStorage;
    }

    @Override
    public long addEnergy(long add, ItemStack stack, ActionType actionType) {
        IAEItemPowerStorage storage = (IAEItemPowerStorage) stack.getItem();
        return add - (long)storage.injectAEPower(stack, add, AE2Helper.getActionable(actionType));
    }

    @Override
    public long removeEnergy(long remove, ItemStack stack, ActionType actionType) {
        IAEItemPowerStorage storage = (IAEItemPowerStorage) stack.getItem();
        return remove - (long)storage.extractAEPower(stack, remove, AE2Helper.getActionable(actionType));
    }
}
