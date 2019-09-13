package fluxnetworks.common.handler.energy;

import fluxnetworks.FluxNetworks;
import fluxnetworks.api.energy.ITileEnergyHandler;
import ic2.api.energy.EnergyNet;
import ic2.api.energy.tile.IEnergySink;
import ic2.api.energy.tile.IEnergyTile;
import ic2.api.tile.IEnergyStorage;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;

import javax.annotation.Nonnull;

public class IC2EnergyHandler implements ITileEnergyHandler {

    @Override
    public boolean canRenderConnection(@Nonnull TileEntity tile, EnumFacing side) {
        return tile instanceof IEnergyTile || tile instanceof IEnergyStorage;
    }

    @Override
    public boolean canAddEnergy(TileEntity tile, EnumFacing side) {
        return tile instanceof IEnergySink || tile instanceof IEnergyStorage;
    }

    @Override
    public boolean canRemoveEnergy(TileEntity tile, EnumFacing side) {
        return false;
    }

    @Override
    public long addEnergy(long amount, TileEntity tile, EnumFacing side, boolean simulate) {
        if (tile instanceof IEnergyStorage) {
            IEnergyStorage sink = (IEnergyStorage) tile;
            int before = sink.getStored();
            if (!simulate) {
                return sink.addEnergy((int) Math.min(sink.getCapacity() - before, amount >> 2)) << 2;
            } else {
                return Math.min(sink.getCapacity() - before, amount >> 2) << 2;
            }
        } else if (tile instanceof IEnergySink) {
            IEnergySink sink = (IEnergySink) tile;
            double voltage = EnergyNet.instance.getPowerFromTier(sink.getSinkTier());
            double a = Math.min(amount >> 2, voltage);
            if (simulate) {
                return (long) Math.min(a, sink.getDemandedEnergy()) << 2;
            } else {
                return (long) Math.floor(a - sink.injectEnergy(side, a, voltage)) << 2;
            }
        }
        return 0;
    }

    @Override
    public long removeEnergy(long amount, TileEntity tile, EnumFacing side) {
        return 0;
    }
}
