package sonar.fluxnetworks.common.integration.energy;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import sonar.fluxnetworks.api.energy.IFNEnergyStorage;
import sonar.fluxnetworks.api.energy.IItemEnergyHandler;
import sonar.fluxnetworks.api.energy.ITileEnergyHandler;
import sonar.fluxnetworks.api.misc.FluxCapabilities;
import sonar.fluxnetworks.common.misc.FluxUtils;

import javax.annotation.Nonnull;

public class FNEnergyHandler implements ITileEnergyHandler, IItemEnergyHandler {

    public static final FNEnergyHandler INSTANCE = new FNEnergyHandler();

    private FNEnergyHandler() {
    }

    @Override
    public boolean hasCapability(@Nonnull TileEntity tile, @Nonnull Direction side) {
        return !tile.isRemoved() && tile.getCapability(FluxCapabilities.FN_ENERGY_STORAGE, side).isPresent();
    }

    @Override
    public boolean canAddEnergy(@Nonnull TileEntity tile, @Nonnull Direction side) {
        if (!tile.isRemoved()) {
            IFNEnergyStorage storage = FluxUtils.get(tile.getCapability(FluxCapabilities.FN_ENERGY_STORAGE, side));
            if (storage != null) {
                return storage.canReceiveL();
            }
        }
        return false;
    }

    @Override
    public boolean canRemoveEnergy(@Nonnull TileEntity tile, @Nonnull Direction side) {
        if (!tile.isRemoved()) {
            IFNEnergyStorage storage = FluxUtils.get(tile.getCapability(FluxCapabilities.FN_ENERGY_STORAGE, side));
            if (storage != null) {
                return storage.canExtractL();
            }
        }
        return false;
    }

    @Override
    public long addEnergy(long amount, @Nonnull TileEntity tile, @Nonnull Direction side, boolean simulate) {
        IFNEnergyStorage storage = FluxUtils.get(tile.getCapability(FluxCapabilities.FN_ENERGY_STORAGE, side));
        return storage == null ? 0 : storage.receiveEnergyL(amount, simulate);
    }

    @Override
    public long removeEnergy(long amount, @Nonnull TileEntity tile, @Nonnull Direction side) {
        IFNEnergyStorage storage = FluxUtils.get(tile.getCapability(FluxCapabilities.FN_ENERGY_STORAGE, side));
        return storage == null ? 0 : storage.extractEnergyL(amount, false);
    }

    @Override
    public boolean hasCapability(@Nonnull ItemStack stack) {
        return stack.getCapability(FluxCapabilities.FN_ENERGY_STORAGE).isPresent();
    }

    @Override
    public boolean canAddEnergy(@Nonnull ItemStack stack) {
        IFNEnergyStorage storage = FluxUtils.get(stack.getCapability(FluxCapabilities.FN_ENERGY_STORAGE));
        return storage != null && storage.canReceiveL();
    }

    @Override
    public boolean canRemoveEnergy(@Nonnull ItemStack stack) {
        IFNEnergyStorage storage = FluxUtils.get(stack.getCapability(FluxCapabilities.FN_ENERGY_STORAGE));
        return storage != null && storage.canExtractL();
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
