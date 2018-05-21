package sonar.flux.energy.handlers.items;

import cofh.redstoneflux.api.IEnergyContainerItem;
import net.minecraft.item.ItemStack;
import sonar.core.api.energy.EnergyType;
import sonar.core.api.utils.ActionType;
import sonar.flux.api.energy.IItemEnergyHandler;
import sonar.flux.api.energy.ItemEnergyHandler;

@ItemEnergyHandler(modid = "redstoneflux", priority = 3)
public class ItemHandlerRedstoneFlux implements IItemEnergyHandler {

    @Override
    public EnergyType getEnergyType() {
        return EnergyType.RF;
    }

    @Override
    public boolean canAddEnergy(ItemStack stack) {
        return !stack.isEmpty() && stack.getItem() instanceof IEnergyContainerItem;
    }

    @Override
    public boolean canRemoveEnergy(ItemStack stack) {
        return !stack.isEmpty() && stack.getItem() instanceof IEnergyContainerItem;
    }

    @Override
    public long addEnergy(long add, ItemStack stack, ActionType actionType) {
        IEnergyContainerItem item = (IEnergyContainerItem)stack.getItem();
        int actualAdd = (int)Math.min(add, Integer.MAX_VALUE);
        return item.receiveEnergy(stack, actualAdd, actionType.shouldSimulate());
    }

    @Override
    public long removeEnergy(long remove, ItemStack stack, ActionType actionType) {
        IEnergyContainerItem item = (IEnergyContainerItem)stack.getItem();
        int actualRemove = (int)Math.min(remove, Integer.MAX_VALUE);
        return item.extractEnergy(stack, actualRemove, actionType.shouldSimulate());
    }
}