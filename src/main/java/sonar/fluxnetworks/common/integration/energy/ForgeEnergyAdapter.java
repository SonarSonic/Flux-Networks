package sonar.fluxnetworks.common.integration.energy;

import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.energy.IEnergyStorage;
import sonar.fluxnetworks.api.energy.IBlockEnergyAdapter;
import sonar.fluxnetworks.api.energy.IItemEnergyAdapter;
import sonar.fluxnetworks.common.util.FluxUtils;

import javax.annotation.Nonnull;

public class ForgeEnergyAdapter implements IBlockEnergyAdapter, IItemEnergyAdapter {

    public static final ForgeEnergyAdapter INSTANCE = new ForgeEnergyAdapter();

    private ForgeEnergyAdapter() {
    }

    @Override
    public boolean hasCapability(@Nonnull BlockEntity target, @Nonnull Direction side) {
        return !target.isRemoved() && target.getCapability(ForgeCapabilities.ENERGY, side).isPresent();
    }

    @Override
    public boolean canSendTo(@Nonnull BlockEntity target, @Nonnull Direction side) {
        if (!target.isRemoved()) {
            IEnergyStorage storage = FluxUtils.get(target, ForgeCapabilities.ENERGY, side);
            return storage != null && storage.canReceive();
        }
        return false;
    }

    @Override
    public boolean canReceiveFrom(@Nonnull BlockEntity target, @Nonnull Direction side) {
        if (!target.isRemoved()) {
            IEnergyStorage storage = FluxUtils.get(target, ForgeCapabilities.ENERGY, side);
            return storage != null && storage.canExtract();
        }
        return false;
    }

    @Override
    public long sendTo(long amount, @Nonnull BlockEntity target, @Nonnull Direction side, boolean simulate) {
        IEnergyStorage storage = FluxUtils.get(target, ForgeCapabilities.ENERGY, side);
        return storage == null ? 0 : storage.receiveEnergy((int) Math.min(amount, Integer.MAX_VALUE), simulate);
    }

    @Override
    public long receiveFrom(long amount, @Nonnull BlockEntity target, @Nonnull Direction side, boolean simulate) {
        IEnergyStorage storage = FluxUtils.get(target, ForgeCapabilities.ENERGY, side);
        return storage == null ? 0 : storage.extractEnergy((int) Math.min(amount, Integer.MAX_VALUE), simulate);
    }

    @Override
    public boolean hasCapability(@Nonnull ItemStack stack) {
        return !stack.isEmpty() && stack.getCapability(ForgeCapabilities.ENERGY).isPresent();
    }

    @Override
    public boolean canSendTo(@Nonnull ItemStack stack) {
        if (!stack.isEmpty()) {
            IEnergyStorage storage = FluxUtils.get(stack, ForgeCapabilities.ENERGY);
            return storage != null && storage.canReceive();
        }
        return false;
    }

    @Override
    public boolean canReceiveFrom(@Nonnull ItemStack stack) {
        if (!stack.isEmpty()) {
            IEnergyStorage storage = FluxUtils.get(stack, ForgeCapabilities.ENERGY);
            return storage != null && storage.canExtract();
        }
        return false;
    }

    @Override
    public long sendTo(long amount, @Nonnull ItemStack stack, boolean simulate) {
        IEnergyStorage storage = FluxUtils.get(stack, ForgeCapabilities.ENERGY);
        return storage == null ? 0 : storage.receiveEnergy((int) Math.min(amount, Integer.MAX_VALUE), simulate);
    }

    @Override
    public long receiveFrom(long amount, @Nonnull ItemStack stack, boolean simulate) {
        IEnergyStorage storage = FluxUtils.get(stack, ForgeCapabilities.ENERGY);
        return storage == null ? 0 : storage.extractEnergy((int) Math.min(amount, Integer.MAX_VALUE), simulate);
    }
}
