package fluxnetworks.common.handler.energy;

import cofh.redstoneflux.api.IEnergyConnection;
import cofh.redstoneflux.api.IEnergyProvider;
import cofh.redstoneflux.api.IEnergyReceiver;
import fluxnetworks.api.energy.ITileEnergyHandler;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;

import javax.annotation.Nonnull;

public class RedstoneFluxHandler implements ITileEnergyHandler {

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
}
