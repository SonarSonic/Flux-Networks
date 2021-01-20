package sonar.fluxnetworks.common.integration.energy;

import gregtech.api.capability.GregtechCapabilities;
import gregtech.api.capability.IElectricItem;
import gregtech.api.capability.IEnergyContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import sonar.fluxnetworks.api.energy.IItemEnergyHandler;
import sonar.fluxnetworks.api.energy.ITileEnergyHandler;

import javax.annotation.Nonnull;

/**
 * Reworked on 2021-01-20, using new transfer system from 1.16.5
 */
public class GTEnergyHandler implements ITileEnergyHandler, IItemEnergyHandler {

    public static final GTEnergyHandler INSTANCE = new GTEnergyHandler();

    private GTEnergyHandler() {
    }

    @Override
    public boolean hasCapability(@Nonnull TileEntity tile, EnumFacing side) {
        return !tile.isInvalid() && tile.hasCapability(GregtechCapabilities.CAPABILITY_ENERGY_CONTAINER, side);
    }

    @Override
    public boolean canAddEnergy(@Nonnull TileEntity tile, EnumFacing side) {
        if (hasCapability(tile, side)) {
            IEnergyContainer container = tile.getCapability(GregtechCapabilities.CAPABILITY_ENERGY_CONTAINER, side);
            if (container != null) {
                return container.inputsEnergy(side);
            }
        }
        return false;
    }

    @Override
    public boolean canRemoveEnergy(@Nonnull TileEntity tile, EnumFacing side) {
        return false;
    }

    @Override
    public long addEnergy(long amount, @Nonnull TileEntity tile, EnumFacing side, boolean simulate) {
        IEnergyContainer container = tile.getCapability(GregtechCapabilities.CAPABILITY_ENERGY_CONTAINER, side);
        if (container == null) return 0;
        long demand = container.getEnergyCanBeInserted();
        if (demand <= 0) return 0;
        if (simulate)
            return Math.min(Math.min(demand,
                    container.getInputVoltage() * container.getInputAmperage()) << 2, amount);
        long vol = Math.min(Math.min(container.getInputVoltage(), demand), amount >> 2);
        if (vol <= 0) return 0;
        long amp = Math.min(container.getInputAmperage(), amount / vol >> 2);
        if (amp <= 0) return 0;
        return vol * container.acceptEnergyFromNetwork(side, vol, amp) << 2;
    }

    @Override
    public long removeEnergy(long amount, @Nonnull TileEntity tile, EnumFacing side) {
        return 0;
    }

    @Override
    public boolean hasCapability(@Nonnull ItemStack stack) {
        return !stack.isEmpty() && stack.hasCapability(GregtechCapabilities.CAPABILITY_ELECTRIC_ITEM, null);
    }

    @Override
    public boolean canAddEnergy(@Nonnull ItemStack stack) {
        return !stack.isEmpty() && stack.hasCapability(GregtechCapabilities.CAPABILITY_ELECTRIC_ITEM, null);
    }

    @Override
    public boolean canRemoveEnergy(@Nonnull ItemStack stack) {
        return !stack.isEmpty() && stack.hasCapability(GregtechCapabilities.CAPABILITY_ELECTRIC_ITEM, null);
    }

    @Override
    public long addEnergy(long amount, @Nonnull ItemStack stack, boolean simulate) {
        IElectricItem electricItem = stack.getCapability(GregtechCapabilities.CAPABILITY_ELECTRIC_ITEM, null);
        return electricItem == null ? 0 : electricItem.charge(amount >> 2, electricItem.getTier(), false, simulate) << 2;
    }

    @Override
    public long removeEnergy(long amount, @Nonnull ItemStack stack) {
        IElectricItem electricItem = stack.getCapability(GregtechCapabilities.CAPABILITY_ELECTRIC_ITEM, null);
        return electricItem == null ? 0 : electricItem.discharge(amount >> 2, electricItem.getTier(), false, true, false) << 2;
    }
}
