package sonar.fluxnetworks.common.integration.energy;

import cofh.redstoneflux.api.IEnergyConnection;
import cofh.redstoneflux.api.IEnergyContainerItem;
import cofh.redstoneflux.api.IEnergyProvider;
import cofh.redstoneflux.api.IEnergyReceiver;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import sonar.fluxnetworks.api.energy.IItemEnergyHandler;
import sonar.fluxnetworks.api.energy.ITileEnergyHandler;

import javax.annotation.Nonnull;

public class RedstoneFluxHandler implements ITileEnergyHandler, IItemEnergyHandler {

    public static final RedstoneFluxHandler INSTANCE = new RedstoneFluxHandler();

    private RedstoneFluxHandler() {
    }

    @Override
    public boolean hasCapability(@Nonnull TileEntity tile, EnumFacing side) {
        return !tile.isInvalid() && tile instanceof IEnergyConnection;
    }

    @Override
    public boolean canAddEnergy(@Nonnull TileEntity tile, EnumFacing side) {
        if (hasCapability(tile, side)) {
            return tile instanceof IEnergyReceiver;
        }
        return false;
    }

    @Override
    public boolean canRemoveEnergy(@Nonnull TileEntity tile, EnumFacing side) {
        if (hasCapability(tile, side)) {
            return tile instanceof IEnergyProvider;
        }
        return false;
    }

    @Override
    public long addEnergy(long amount, @Nonnull TileEntity tile, EnumFacing side, boolean simulate) {
        if (tile instanceof IEnergyReceiver) {
            IEnergyReceiver receiver = (IEnergyReceiver) tile;
            return receiver.receiveEnergy(side, (int) Math.min(Integer.MAX_VALUE, amount), simulate);
        }
        return 0;
    }

    @Override
    public long removeEnergy(long amount, @Nonnull TileEntity tile, EnumFacing side) {
        if (tile instanceof IEnergyProvider) {
            IEnergyProvider receiver = (IEnergyProvider) tile;
            return receiver.extractEnergy(side, (int) Math.min(Integer.MAX_VALUE, amount), false);
        }
        return 0;
    }

    @Override
    public boolean hasCapability(@Nonnull ItemStack stack) {
        return !stack.isEmpty() && stack.getItem() instanceof IEnergyContainerItem;
    }

    @Override
    public boolean canAddEnergy(@Nonnull ItemStack stack) {
        return !stack.isEmpty() && stack.getItem() instanceof IEnergyContainerItem;
    }

    @Override
    public boolean canRemoveEnergy(@Nonnull ItemStack stack) {
        return !stack.isEmpty() && stack.getItem() instanceof IEnergyContainerItem;
    }

    @Override
    public long addEnergy(long amount, @Nonnull ItemStack stack, boolean simulate) {
        IEnergyContainerItem item = (IEnergyContainerItem) stack.getItem();
        int actualAdd = (int) Math.min(amount, Integer.MAX_VALUE);
        return item.receiveEnergy(stack, actualAdd, simulate);
    }

    @Override
    public long removeEnergy(long amount, @Nonnull ItemStack stack) {
        IEnergyContainerItem item = (IEnergyContainerItem) stack.getItem();
        int actualRemove = (int) Math.min(amount, Integer.MAX_VALUE);
        return item.extractEnergy(stack, actualRemove, false);
    }
}
