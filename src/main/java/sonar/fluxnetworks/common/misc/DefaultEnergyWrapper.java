package sonar.fluxnetworks.common.misc;

import net.minecraft.util.Direction;
import net.minecraftforge.energy.IEnergyStorage;
import sonar.fluxnetworks.api.energy.IFNEnergyStorage;
import sonar.fluxnetworks.api.device.IFluxDevice;
import sonar.fluxnetworks.common.tileentity.TileFluxPlug;

/**
 * Uses forge's own energy wrapper and also IExtendedEnergyStorage
 */
public class DefaultEnergyWrapper implements IEnergyStorage, IFNEnergyStorage {

    public IFluxDevice device;

    public Direction side;

    public DefaultEnergyWrapper(IFluxDevice device, Direction side) {
        this.device = device;
        this.side = side;
    }

    ///// FLUX EXTENDED \\\\\

    @Override
    public long receiveEnergyL(long maxReceive, boolean simulate) {
        //TODO
        return device instanceof TileFluxPlug ? ((TileFluxPlug) device).addPhantomEnergyToNetwork(side, maxReceive, simulate) : 0;
    }

    @Override
    public long extractEnergyL(long maxExtract, boolean simulate) {
        return 0;
    }

    @Override
    public long getEnergyStoredL() {
        return device.getDeviceType().isPoint() ? Long.MAX_VALUE : 0;
    }

    @Override
    public long getMaxEnergyStoredL() {
        return Long.MAX_VALUE;
    }

    @Override
    public boolean canExtractL() {
        return false;
    }

    @Override
    public boolean canReceiveL() {
        return device.getDeviceType().isPlug();
    }

    ///// FORGE \\\\\

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        return (int) receiveEnergyL(maxReceive, simulate);
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        return (int) extractEnergyL(maxExtract, simulate);
    }

    @Override
    public int getEnergyStored() {
        return (int) Math.min(getEnergyStoredL(), Integer.MAX_VALUE);
    }

    @Override
    public int getMaxEnergyStored() {
        return (int) Math.min(getMaxEnergyStoredL(), Integer.MAX_VALUE);
    }

    @Override
    public boolean canExtract() {
        return false;
    }

    @Override
    public boolean canReceive() {
        return canReceiveL();
    }
}
