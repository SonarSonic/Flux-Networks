package fluxnetworks.common.handler.energy;

import fluxnetworks.api.energy.ITileEnergyHandler;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

import javax.annotation.Nonnull;

public class ForgeEnergyHandler implements ITileEnergyHandler {

    @Override
    public boolean canRenderConnection(@Nonnull TileEntity tile, EnumFacing side) {
        return tile.hasCapability(CapabilityEnergy.ENERGY, side);
    }

    @Override
    public boolean canAddEnergy(TileEntity tile, EnumFacing side) {
        if(canRenderConnection(tile, side)) {
            IEnergyStorage storage = tile.getCapability(CapabilityEnergy.ENERGY, side);
            return storage.canReceive();
        }
        return false;
    }

    @Override
    public boolean canRemoveEnergy(TileEntity tile, EnumFacing side) {
        if(canRenderConnection(tile, side)) {
            IEnergyStorage storage = tile.getCapability(CapabilityEnergy.ENERGY, side);
            return storage.canExtract();
        }
        return false;
    }

    @Override
    public long addEnergy(long amount, TileEntity tile, EnumFacing side, boolean simulate) {
        IEnergyStorage storage = tile.getCapability(CapabilityEnergy.ENERGY, side);
        return storage.receiveEnergy((int) Math.min(amount, Integer.MAX_VALUE), simulate);
    }

    @Override
    public long removeEnergy(long amount, TileEntity tile, EnumFacing side) {
        IEnergyStorage storage = tile.getCapability(CapabilityEnergy.ENERGY, side);
        return storage.extractEnergy((int) Math.min(amount, Integer.MAX_VALUE), false);
    }
}
