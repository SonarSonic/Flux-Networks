package sonar.flux.energy.handlers.items;

import net.minecraft.item.ItemStack;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import sonar.core.api.energy.EnergyType;
import sonar.core.api.utils.ActionType;
import sonar.flux.api.energy.IItemEnergyHandler;
import sonar.flux.api.energy.ItemEnergyHandler;

@ItemEnergyHandler(modid = "sonarcore", priority = 0)
public class ItemHandlerForge implements IItemEnergyHandler {

    @Override
    public EnergyType getEnergyType() {
        return EnergyType.FE;
    }

    @Override
    public boolean canAddEnergy(ItemStack stack) {
        return !stack.isEmpty() && stack.hasCapability(CapabilityEnergy.ENERGY, null) && stack.getCapability(CapabilityEnergy.ENERGY, null).canReceive();
    }

    @Override
    public boolean canRemoveEnergy(ItemStack stack) {
        return !stack.isEmpty() && stack.hasCapability(CapabilityEnergy.ENERGY, null) && stack.getCapability(CapabilityEnergy.ENERGY, null).canExtract();
    }

    @Override
    public long addEnergy(long add, ItemStack stack, ActionType actionType) {
        IEnergyStorage storage = stack.getCapability(CapabilityEnergy.ENERGY, null);
        return storage.receiveEnergy((int) Math.min(Integer.MAX_VALUE, add), actionType.shouldSimulate());
    }

    @Override
    public long removeEnergy(long remove, ItemStack stack, ActionType actionType) {
        IEnergyStorage storage = stack.getCapability(CapabilityEnergy.ENERGY, null);
        return storage.extractEnergy((int) Math.min(Integer.MAX_VALUE, remove), actionType.shouldSimulate());
    }
}
