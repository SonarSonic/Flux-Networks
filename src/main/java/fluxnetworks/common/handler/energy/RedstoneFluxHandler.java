package fluxnetworks.common.handler.energy;

import cofh.redstoneflux.api.IEnergyConnection;
import cofh.redstoneflux.api.IEnergyContainerItem;
import cofh.redstoneflux.api.IEnergyProvider;
import cofh.redstoneflux.api.IEnergyReceiver;
import fluxnetworks.api.energy.IItemEnergyHandler;
import fluxnetworks.api.energy.ITileEnergyHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;

import javax.annotation.Nonnull;

public class RedstoneFluxHandler implements ITileEnergyHandler, IItemEnergyHandler {

    public static final RedstoneFluxHandler INSTANCE = new RedstoneFluxHandler();

    @Override
    public boolean canRenderConnection(@Nonnull TileEntity tile, EnumFacing side) {
        return tile instanceof IEnergyConnection;
    }

    @Override
    public boolean canAddEnergy(TileEntity tile, EnumFacing side) {
        if (canRenderConnection(tile, side)) {
            return tile instanceof IEnergyReceiver;
        }
        return false;
    }

    @Override
    public boolean canRemoveEnergy(TileEntity tile, EnumFacing side) {
        if (canRenderConnection(tile, side)) {
            return tile instanceof IEnergyProvider;
        }
        return false;
    }

    @Override
    public long addEnergy(long amount, TileEntity tile, EnumFacing side, boolean simulate) {
        IEnergyReceiver receiver = (IEnergyReceiver) tile;
        return receiver.receiveEnergy(side, (int) Math.min(Integer.MAX_VALUE, amount), simulate);
    }

    @Override
    public long removeEnergy(long amount, TileEntity tile, EnumFacing side) {
        IEnergyProvider receiver = (IEnergyProvider) tile;
        return receiver.extractEnergy(side, (int) Math.min(Integer.MAX_VALUE, amount), false);
    }

    @Override
    public boolean canAddEnergy(ItemStack stack) {
        return !stack.isEmpty() && stack.getItem() instanceof IEnergyContainerItem;
    }

    @Override
    public boolean canRemoveEnergy(ItemStack stack) {
        return !stack.isEmpty() && stack.getItem() instanceof IEnergyContainerItem;
    }

    @Override
    public long addEnergy(long amount, ItemStack stack, boolean simulate) {
        IEnergyContainerItem item = (IEnergyContainerItem)stack.getItem();
        int actualAdd = (int) Math.min(amount, Integer.MAX_VALUE);
        return item.receiveEnergy(stack, actualAdd, simulate);
    }

    @Override
    public long removeEnergy(long amount, ItemStack stack) {
        IEnergyContainerItem item = (IEnergyContainerItem)stack.getItem();
        int actualRemove = (int) Math.min(amount, Integer.MAX_VALUE);
        return item.extractEnergy(stack, actualRemove, false);
    }
}
