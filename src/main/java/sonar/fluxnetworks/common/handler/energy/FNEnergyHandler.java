package sonar.fluxnetworks.common.handler.energy;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import sonar.fluxnetworks.api.energy.IFNEnergyStorage;
import sonar.fluxnetworks.api.energy.IItemEnergyHandler;
import sonar.fluxnetworks.api.energy.ITileEnergyHandler;
import sonar.fluxnetworks.api.misc.FluxCapabilities;

import javax.annotation.Nonnull;

public class FNEnergyHandler implements ITileEnergyHandler, IItemEnergyHandler {

    public static final FNEnergyHandler INSTANCE = new FNEnergyHandler();

    @Override
    public boolean canRenderConnection(@Nonnull TileEntity tile, Direction side) {
        return !tile.isRemoved() && tile.getCapability(FluxCapabilities.FN_ENERGY_STORAGE, side).isPresent();
    }

    @Override
    public boolean canAddEnergy(TileEntity tile, Direction side) {
        if(canRenderConnection(tile, side)) {
            IFNEnergyStorage storage = tile.getCapability(FluxCapabilities.FN_ENERGY_STORAGE, side).orElse(null);
            return storage.canReceiveL();
        }
        return false;
    }

    @Override
    public boolean canRemoveEnergy(TileEntity tile, Direction side) {
        if(canRenderConnection(tile, side)) {
            IFNEnergyStorage storage = tile.getCapability(FluxCapabilities.FN_ENERGY_STORAGE, side).orElse(null);
            return storage.canExtractL();
        }
        return false;
    }

    @Override
    public long addEnergy(long amount, TileEntity tile, Direction side, boolean simulate) {
        IFNEnergyStorage storage = tile.getCapability(FluxCapabilities.FN_ENERGY_STORAGE, side).orElse(null);
        return storage.receiveEnergyL(Math.min(amount, Long.MAX_VALUE), simulate);
    }

    @Override
    public long removeEnergy(long amount, TileEntity tile, Direction side) {
        IFNEnergyStorage storage = tile.getCapability(FluxCapabilities.FN_ENERGY_STORAGE, side).orElse(null);
        return storage.extractEnergyL(Math.min(amount, Long.MAX_VALUE), false);
    }

    @Override
    public boolean canAddEnergy(ItemStack stack) {
        IFNEnergyStorage storage = stack.getCapability(FluxCapabilities.FN_ENERGY_STORAGE, null).orElse(null);
        if(storage != null) {
            return storage.canReceiveL();
        }
        return false;
    }

    @Override
    public boolean canRemoveEnergy(ItemStack stack) {
        IFNEnergyStorage storage = stack.getCapability(FluxCapabilities.FN_ENERGY_STORAGE, null).orElse(null);
        if(storage != null) {
            return storage.canExtractL();
        }
        return false;
    }

    @Override
    public long addEnergy(long amount, ItemStack stack, boolean simulate) {
        IFNEnergyStorage storage = stack.getCapability(FluxCapabilities.FN_ENERGY_STORAGE, null).orElse(null);
        return storage.receiveEnergyL(Math.min(amount, Long.MAX_VALUE), simulate);
    }

    @Override
    public long removeEnergy(long amount, ItemStack stack) {
        IFNEnergyStorage storage = stack.getCapability(FluxCapabilities.FN_ENERGY_STORAGE, null).orElse(null);
        return storage.extractEnergyL(Math.min(amount, Long.MAX_VALUE), false);
    }
}
