package sonar.fluxnetworks.common.integration.energy;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import sonar.fluxnetworks.api.energy.IItemEnergyHandler;
import sonar.fluxnetworks.api.energy.ITileEnergyHandler;
import sonar.fluxnetworks.common.misc.FluxUtils;

import javax.annotation.Nonnull;

public class ForgeEnergyHandler implements ITileEnergyHandler, IItemEnergyHandler {

    public static final ForgeEnergyHandler INSTANCE = new ForgeEnergyHandler();

    private ForgeEnergyHandler() {
    }

    @Override
    public boolean hasCapability(@Nonnull TileEntity tile, @Nonnull Direction side) {
        return !tile.isRemoved() && tile.getCapability(CapabilityEnergy.ENERGY, side).isPresent();
    }

    @Override
    public boolean canAddEnergy(@Nonnull TileEntity tile, @Nonnull Direction side) {
        if (!tile.isRemoved()) {
            IEnergyStorage storage = FluxUtils.getCap(tile.getCapability(CapabilityEnergy.ENERGY, side));
            if (storage != null) {
                return storage.canReceive();
            }
        }
        return false;
    }

    @Override
    public boolean canRemoveEnergy(@Nonnull TileEntity tile, @Nonnull Direction side) {
        if (!tile.isRemoved()) {
            IEnergyStorage storage = FluxUtils.getCap(tile.getCapability(CapabilityEnergy.ENERGY, side));
            if (storage != null) {
                return storage.canExtract();
            }
        }
        return false;
    }

    @Override
    public long addEnergy(long amount, @Nonnull TileEntity tile, @Nonnull Direction side, boolean simulate) {
        IEnergyStorage storage = FluxUtils.getCap(tile.getCapability(CapabilityEnergy.ENERGY, side));
        return storage == null ? 0 : storage.receiveEnergy((int) Math.min(amount, Integer.MAX_VALUE), simulate);
    }

    @Override
    public long removeEnergy(long amount, @Nonnull TileEntity tile, @Nonnull Direction side) {
        IEnergyStorage storage = FluxUtils.getCap(tile.getCapability(CapabilityEnergy.ENERGY, side));
        return storage == null ? 0 : storage.extractEnergy((int) Math.min(amount, Integer.MAX_VALUE), false);
    }

    @Override
    public boolean hasCapability(@Nonnull ItemStack stack) {
        return stack.getCapability(CapabilityEnergy.ENERGY).isPresent();
    }

    @Override
    public boolean canAddEnergy(@Nonnull ItemStack stack) {
        IEnergyStorage storage = FluxUtils.getCap(stack.getCapability(CapabilityEnergy.ENERGY));
        return storage != null && storage.canReceive();
    }

    @Override
    public boolean canRemoveEnergy(@Nonnull ItemStack stack) {
        IEnergyStorage storage = FluxUtils.getCap(stack.getCapability(CapabilityEnergy.ENERGY));
        return storage != null && storage.canExtract();
    }

    @Override
    public long addEnergy(long amount, @Nonnull ItemStack stack, boolean simulate) {
        IEnergyStorage storage = FluxUtils.getCap(stack.getCapability(CapabilityEnergy.ENERGY));
        return storage == null ? 0 : storage.receiveEnergy((int) Math.min(amount, Integer.MAX_VALUE), simulate);
    }

    @Override
    public long removeEnergy(long amount, @Nonnull ItemStack stack) {
        IEnergyStorage storage = FluxUtils.getCap(stack.getCapability(CapabilityEnergy.ENERGY));
        return storage == null ? 0 : storage.extractEnergy((int) Math.min(amount, Integer.MAX_VALUE), false);
    }
}
