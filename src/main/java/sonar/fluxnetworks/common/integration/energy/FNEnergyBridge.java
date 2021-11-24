package sonar.fluxnetworks.common.integration.energy;

import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import sonar.fluxnetworks.api.energy.IBlockEnergyBridge;
import sonar.fluxnetworks.api.energy.IFNEnergyStorage;
import sonar.fluxnetworks.api.energy.IItemEnergyBridge;
import sonar.fluxnetworks.api.misc.FluxCapabilities;
import sonar.fluxnetworks.common.util.FluxUtils;

import javax.annotation.Nonnull;

public class FNEnergyBridge implements IBlockEnergyBridge, IItemEnergyBridge {

    public static final FNEnergyBridge INSTANCE = new FNEnergyBridge();

    private FNEnergyBridge() {
    }

    @Override
    public boolean hasCapability(@Nonnull BlockEntity target, @Nonnull Direction side) {
        return !target.isRemoved() && target.getCapability(FluxCapabilities.FN_ENERGY_STORAGE, side).isPresent();
    }

    @Override
    public boolean canAddEnergy(@Nonnull BlockEntity target, @Nonnull Direction side) {
        if (!target.isRemoved()) {
            IFNEnergyStorage storage = FluxUtils.get(target.getCapability(FluxCapabilities.FN_ENERGY_STORAGE, side));
            if (storage != null) {
                return storage.canReceive();
            }
        }
        return false;
    }

    @Override
    public boolean canRemoveEnergy(@Nonnull BlockEntity target, @Nonnull Direction side) {
        if (!target.isRemoved()) {
            IFNEnergyStorage storage = FluxUtils.get(target.getCapability(FluxCapabilities.FN_ENERGY_STORAGE, side));
            if (storage != null) {
                return storage.canExtract();
            }
        }
        return false;
    }

    @Override
    public long addEnergy(long amount, @Nonnull BlockEntity target, @Nonnull Direction side, boolean simulate) {
        IFNEnergyStorage storage = FluxUtils.get(target.getCapability(FluxCapabilities.FN_ENERGY_STORAGE, side));
        return storage == null ? 0 : storage.receiveEnergyL(amount, simulate);
    }

    @Override
    public long removeEnergy(long amount, @Nonnull BlockEntity target, @Nonnull Direction side) {
        IFNEnergyStorage storage = FluxUtils.get(target.getCapability(FluxCapabilities.FN_ENERGY_STORAGE, side));
        return storage == null ? 0 : storage.extractEnergyL(amount, false);
    }

    @Override
    public boolean hasCapability(@Nonnull ItemStack stack) {
        return stack.getCapability(FluxCapabilities.FN_ENERGY_STORAGE).isPresent();
    }

    @Override
    public boolean canAddEnergy(@Nonnull ItemStack stack) {
        IFNEnergyStorage storage = FluxUtils.get(stack.getCapability(FluxCapabilities.FN_ENERGY_STORAGE));
        return storage != null && storage.canReceive();
    }

    @Override
    public boolean canRemoveEnergy(@Nonnull ItemStack stack) {
        IFNEnergyStorage storage = FluxUtils.get(stack.getCapability(FluxCapabilities.FN_ENERGY_STORAGE));
        return storage != null && storage.canExtract();
    }

    @Override
    public long addEnergy(long amount, @Nonnull ItemStack stack, boolean simulate) {
        IFNEnergyStorage storage = FluxUtils.get(stack.getCapability(FluxCapabilities.FN_ENERGY_STORAGE));
        return storage == null ? 0 : storage.receiveEnergyL(amount, simulate);
    }

    @Override
    public long removeEnergy(long amount, @Nonnull ItemStack stack) {
        IFNEnergyStorage storage = FluxUtils.get(stack.getCapability(FluxCapabilities.FN_ENERGY_STORAGE));
        return storage == null ? 0 : storage.extractEnergyL(amount, false);
    }
}
