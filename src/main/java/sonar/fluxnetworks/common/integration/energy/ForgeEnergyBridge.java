package sonar.fluxnetworks.common.integration.energy;

import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import sonar.fluxnetworks.api.energy.IBlockEnergyBridge;
import sonar.fluxnetworks.api.energy.IItemEnergyBridge;
import sonar.fluxnetworks.common.util.FluxUtils;

import javax.annotation.Nonnull;

public class ForgeEnergyBridge implements IBlockEnergyBridge, IItemEnergyBridge {

    public static final ForgeEnergyBridge INSTANCE = new ForgeEnergyBridge();

    private ForgeEnergyBridge() {
    }

    @Override
    public boolean hasCapability(@Nonnull BlockEntity target, @Nonnull Direction side) {
        return !target.isRemoved() && target.getCapability(CapabilityEnergy.ENERGY, side).isPresent();
    }

    @Override
    public boolean canAddEnergy(@Nonnull BlockEntity target, @Nonnull Direction side) {
        if (!target.isRemoved()) {
            IEnergyStorage storage = FluxUtils.get(target.getCapability(CapabilityEnergy.ENERGY, side));
            if (storage != null) {
                return storage.canReceive();
            }
        }
        return false;
    }

    @Override
    public boolean canRemoveEnergy(@Nonnull BlockEntity target, @Nonnull Direction side) {
        if (!target.isRemoved()) {
            IEnergyStorage storage = FluxUtils.get(target.getCapability(CapabilityEnergy.ENERGY, side));
            if (storage != null) {
                return storage.canExtract();
            }
        }
        return false;
    }

    @Override
    public long addEnergy(long amount, @Nonnull BlockEntity target, @Nonnull Direction side, boolean simulate) {
        IEnergyStorage storage = FluxUtils.get(target.getCapability(CapabilityEnergy.ENERGY, side));
        return storage == null ? 0 : storage.receiveEnergy((int) Math.min(amount, Integer.MAX_VALUE), simulate);
    }

    @Override
    public long removeEnergy(long amount, @Nonnull BlockEntity target, @Nonnull Direction side, boolean simulate) {
        IEnergyStorage storage = FluxUtils.get(target.getCapability(CapabilityEnergy.ENERGY, side));
        return storage == null ? 0 : storage.extractEnergy((int) Math.min(amount, Integer.MAX_VALUE), simulate);
    }

    @Override
    public boolean hasCapability(@Nonnull ItemStack stack) {
        return stack.getCapability(CapabilityEnergy.ENERGY).isPresent();
    }

    @Override
    public boolean canAddEnergy(@Nonnull ItemStack stack) {
        IEnergyStorage storage = FluxUtils.get(stack.getCapability(CapabilityEnergy.ENERGY));
        return storage != null && storage.canReceive();
    }

    @Override
    public boolean canRemoveEnergy(@Nonnull ItemStack stack) {
        IEnergyStorage storage = FluxUtils.get(stack.getCapability(CapabilityEnergy.ENERGY));
        return storage != null && storage.canExtract();
    }

    @Override
    public long addEnergy(long amount, @Nonnull ItemStack stack, boolean simulate) {
        IEnergyStorage storage = FluxUtils.get(stack.getCapability(CapabilityEnergy.ENERGY));
        return storage == null ? 0 : storage.receiveEnergy((int) Math.min(amount, Integer.MAX_VALUE), simulate);
    }

    @Override
    public long removeEnergy(long amount, @Nonnull ItemStack stack, boolean simulate) {
        IEnergyStorage storage = FluxUtils.get(stack.getCapability(CapabilityEnergy.ENERGY));
        return storage == null ? 0 : storage.extractEnergy((int) Math.min(amount, Integer.MAX_VALUE), simulate);
    }
}
