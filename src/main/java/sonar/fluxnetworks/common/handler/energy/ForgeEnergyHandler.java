package sonar.fluxnetworks.common.handler.energy;

import net.minecraft.util.Direction;
import sonar.fluxnetworks.api.energy.IItemEnergyHandler;
import sonar.fluxnetworks.api.energy.ITileEnergyHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

import javax.annotation.Nonnull;

public class ForgeEnergyHandler implements ITileEnergyHandler, IItemEnergyHandler {

    public static final ForgeEnergyHandler INSTANCE = new ForgeEnergyHandler();

    @Override
    public boolean canRenderConnection(@Nonnull TileEntity tile, Direction side) {
        return tile.getCapability(CapabilityEnergy.ENERGY, side).isPresent();
    }

    @Override
    public boolean canAddEnergy(TileEntity tile, Direction side) {
        if(canRenderConnection(tile, side)) {
            IEnergyStorage storage = tile.getCapability(CapabilityEnergy.ENERGY, side).orElse(null);
            return storage.canReceive();
        }
        return false;
    }

    @Override
    public boolean canRemoveEnergy(TileEntity tile, Direction side) {
        if(canRenderConnection(tile, side)) {
            IEnergyStorage storage = tile.getCapability(CapabilityEnergy.ENERGY, side).orElse(null);
            return storage.canExtract();
        }
        return false;
    }

    @Override
    public long addEnergy(long amount, TileEntity tile, Direction side, boolean simulate) {
        IEnergyStorage storage = tile.getCapability(CapabilityEnergy.ENERGY, side).orElse(null);
        return storage.receiveEnergy((int) Math.min(amount, Integer.MAX_VALUE), simulate);
    }

    @Override
    public long removeEnergy(long amount, TileEntity tile, Direction side) {
        IEnergyStorage storage = tile.getCapability(CapabilityEnergy.ENERGY, side).orElse(null);
        return storage.extractEnergy((int) Math.min(amount, Integer.MAX_VALUE), false);
    }

    @Override
    public boolean canAddEnergy(ItemStack stack) {
        IEnergyStorage storage = stack.getCapability(CapabilityEnergy.ENERGY, null).orElse(null);
        if(storage != null) {
            return storage.canReceive();
        }
        return false;
    }

    @Override
    public boolean canRemoveEnergy(ItemStack stack) {
        IEnergyStorage storage = stack.getCapability(CapabilityEnergy.ENERGY, null).orElse(null);
        if(storage != null) {
            return storage.canExtract();
        }
        return false;
    }

    @Override
    public long addEnergy(long amount, ItemStack stack, boolean simulate) {
        IEnergyStorage storage = stack.getCapability(CapabilityEnergy.ENERGY, null).orElse(null);
        return storage.receiveEnergy((int) Math.min(amount, Integer.MAX_VALUE), simulate);
    }

    @Override
    public long removeEnergy(long amount, ItemStack stack) {
        IEnergyStorage storage = stack.getCapability(CapabilityEnergy.ENERGY, null).orElse(null);
        return storage.extractEnergy((int) Math.min(amount, Integer.MAX_VALUE), false);
    }
}
