package sonar.fluxnetworks.common.integration.energy;

import com.gregtechceu.gtceu.api.capability.IElectricItem;
import com.gregtechceu.gtceu.api.capability.IEnergyContainer;
import com.gregtechceu.gtceu.api.capability.forge.GTCapability;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import sonar.fluxnetworks.api.energy.IBlockEnergyConnector;
import sonar.fluxnetworks.api.energy.IItemEnergyConnector;
import sonar.fluxnetworks.common.util.FluxUtils;

import javax.annotation.Nonnull;

public class GTCEUEnergyConnector implements IBlockEnergyConnector, IItemEnergyConnector {

    public static final GTCEUEnergyConnector INSTANCE = new GTCEUEnergyConnector();

    @Override
    public boolean hasCapability(@Nonnull BlockEntity target, @Nonnull Direction side) {
        return !target.isRemoved() && target.getCapability(GTCapability.CAPABILITY_ENERGY_CONTAINER, side).isPresent();
    }

    @Override
    public boolean canSendTo(@Nonnull BlockEntity target, @Nonnull Direction side) {
        if (!target.isRemoved()) {
            IEnergyContainer container = FluxUtils.get(target, GTCapability.CAPABILITY_ENERGY_CONTAINER, side);
            return container != null && container.inputsEnergy(side);
        }
        return false;
    }

    @Override
    public boolean canReceiveFrom(@Nonnull BlockEntity target, @Nonnull Direction side) {
        if (!target.isRemoved()) {
            IEnergyContainer container = FluxUtils.get(target, GTCapability.CAPABILITY_ENERGY_CONTAINER, side);
            return container != null && container.outputsEnergy(side);
        }
        return false;
    }

    @Override
    public long sendTo(long amount, @Nonnull BlockEntity target, @Nonnull Direction side, boolean simulate) {
        IEnergyContainer container = FluxUtils.get(target, GTCapability.CAPABILITY_ENERGY_CONTAINER, side);
        if (container == null) {
            return 0;
        }
        long demand = container.getEnergyCanBeInserted();
        if (demand == 0) {
            return 0;
        }
        long voltage = Math.min(container.getInputVoltage(), demand);
        if (simulate) {
            return Math.min(voltage << 2, amount);
        }
        voltage = Math.min(voltage, amount >> 2);
        if (voltage == 0) {
            return 0;
        }
        long energy = voltage * container.acceptEnergyFromNetwork(side, voltage, 1);
        return energy << 2;
    }

    @Override
    public long receiveFrom(long amount, @Nonnull BlockEntity target, @Nonnull Direction side, boolean simulate) {
        IEnergyContainer container = FluxUtils.get(target, GTCapability.CAPABILITY_ENERGY_CONTAINER, side);
        if (container == null) {
            return 0;
        }
        return container.removeEnergy(container.getOutputVoltage() * container.getOutputAmperage()) << 2;
    }

    @Override
    public boolean hasCapability(@Nonnull ItemStack stack) {
        return !stack.isEmpty() && stack.getCapability(GTCapability.CAPABILITY_ELECTRIC_ITEM).isPresent();
    }

    @Override
    public boolean canSendTo(@Nonnull ItemStack stack) {
        return hasCapability(stack);
    }

    @Override
    public boolean canReceiveFrom(@Nonnull ItemStack stack) {
        return hasCapability(stack);
    }

    @Override
    public long sendTo(long amount, @Nonnull ItemStack stack, boolean simulate) {
        IElectricItem electricItem = FluxUtils.get(stack, GTCapability.CAPABILITY_ELECTRIC_ITEM);
        if (electricItem != null) {
            return electricItem.charge(amount >> 2, electricItem.getTier(), false, simulate) << 2;
        }
        return 0;
    }

    @Override
    public long receiveFrom(long amount, @Nonnull ItemStack stack, boolean simulate) {
        IElectricItem electricItem = FluxUtils.get(stack, GTCapability.CAPABILITY_ELECTRIC_ITEM);
        if (electricItem != null) {
            return electricItem.discharge(amount >> 2, electricItem.getTier(), false, true, false) << 2;
        }
        return 0;
    }
}
