package sonar.fluxnetworks.common.integration.energy;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import sonar.fluxnetworks.api.energy.IItemEnergyHandler;
import sonar.fluxnetworks.api.energy.ITileEnergyHandler;

import javax.annotation.Nonnull;

public class ForgeEnergyHandler implements ITileEnergyHandler, IItemEnergyHandler {

    public static final ForgeEnergyHandler INSTANCE = new ForgeEnergyHandler();

    private ForgeEnergyHandler() {
    }

    @Override
    public boolean hasCapability(@Nonnull TileEntity tile, EnumFacing side) {
        return !tile.isInvalid() && tile.hasCapability(CapabilityEnergy.ENERGY, side);
    }

    @Override
    public boolean canAddEnergy(@Nonnull TileEntity tile, EnumFacing side) {
        if (hasCapability(tile, side)) {
            IEnergyStorage storage = tile.getCapability(CapabilityEnergy.ENERGY, side);
            if (storage != null) {
                return storage.canReceive();
            }
        }
        return false;
    }

    @Override
    public boolean canRemoveEnergy(@Nonnull TileEntity tile, EnumFacing side) {
        if (hasCapability(tile, side)) {
            IEnergyStorage storage = tile.getCapability(CapabilityEnergy.ENERGY, side);
            if (storage != null) {
                return storage.canExtract();
            }
        }
        return false;
    }

    @Override
    public long addEnergy(long amount, @Nonnull TileEntity tile, EnumFacing side, boolean simulate) {
        IEnergyStorage storage = tile.getCapability(CapabilityEnergy.ENERGY, side);
        return storage == null ? 0 : storage.receiveEnergy((int) Math.min(amount, Integer.MAX_VALUE), simulate);
    }

    @Override
    public long removeEnergy(long amount, @Nonnull TileEntity tile, EnumFacing side) {
        IEnergyStorage storage = tile.getCapability(CapabilityEnergy.ENERGY, side);
        return storage == null ? 0 : storage.extractEnergy((int) Math.min(amount, Integer.MAX_VALUE), false);
    }

    @Override
    public boolean hasCapability(@Nonnull ItemStack stack) {
        return stack.hasCapability(CapabilityEnergy.ENERGY, null);
    }

    @Override
    public boolean canAddEnergy(@Nonnull ItemStack stack) {
        IEnergyStorage storage = stack.getCapability(CapabilityEnergy.ENERGY, null);
        return storage != null && storage.canReceive();
    }

    @Override
    public boolean canRemoveEnergy(@Nonnull ItemStack stack) {
        IEnergyStorage storage = stack.getCapability(CapabilityEnergy.ENERGY, null);
        return storage != null && storage.canExtract();
    }

    @Override
    public long addEnergy(long amount, @Nonnull ItemStack stack, boolean simulate) {
        IEnergyStorage storage = stack.getCapability(CapabilityEnergy.ENERGY, null);
        return storage == null ? 0 : storage.receiveEnergy((int) Math.min(amount, Integer.MAX_VALUE), simulate);
    }

    @Override
    public long removeEnergy(long amount, @Nonnull ItemStack stack) {
        IEnergyStorage storage = stack.getCapability(CapabilityEnergy.ENERGY, null);
        return storage == null ? 0 : storage.extractEnergy((int) Math.min(amount, Integer.MAX_VALUE), false);
    }
}
