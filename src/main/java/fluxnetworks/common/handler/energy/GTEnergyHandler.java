package fluxnetworks.common.handler.energy;

import fluxnetworks.FluxNetworks;
import fluxnetworks.api.energy.ITileEnergyHandler;
import gregtech.api.capability.GregtechCapabilities;
import gregtech.api.capability.IEnergyContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;

import javax.annotation.Nonnull;

public class GTEnergyHandler implements ITileEnergyHandler {

    @Override
    public boolean canRenderConnection(@Nonnull TileEntity tile, EnumFacing side) {
        return tile.hasCapability(GregtechCapabilities.CAPABILITY_ENERGY_CONTAINER, side);
    }

    @Override
    public boolean canAddEnergy(TileEntity tile, EnumFacing side) {
        if(canRenderConnection(tile, side)) {
            IEnergyContainer container = tile.getCapability(GregtechCapabilities.CAPABILITY_ENERGY_CONTAINER, side);
            return container.inputsEnergy(side);
        }
        return false;
    }

    @Override
    public boolean canRemoveEnergy(TileEntity tile, EnumFacing side) {
        if(canRenderConnection(tile, side)) {
            IEnergyContainer container = tile.getCapability(GregtechCapabilities.CAPABILITY_ENERGY_CONTAINER, side);
            return container.outputsEnergy(side);
        }
        return false;
    }

    @Override
    public long addEnergy(long amount, TileEntity tile, EnumFacing side, boolean simulate) {
        IEnergyContainer container = tile.getCapability(GregtechCapabilities.CAPABILITY_ENERGY_CONTAINER, side);
        long voltage = Math.min(container.getInputVoltage(), amount / 4);
        if(voltage == 0) {
            return 0;
        }
        long energy = voltage * container.acceptEnergyFromNetwork(side, voltage, amount / (voltage * 4));
        if(simulate) {
            container.removeEnergy(energy);
        }
        return energy * 4;
    }

    @Override
    public long removeEnergy(long amount, TileEntity tile, EnumFacing side) {
        IEnergyContainer container = tile.getCapability(GregtechCapabilities.CAPABILITY_ENERGY_CONTAINER, side);
        return container.removeEnergy(container.getOutputVoltage() * container.getOutputAmperage()) * 4;
    }
}
