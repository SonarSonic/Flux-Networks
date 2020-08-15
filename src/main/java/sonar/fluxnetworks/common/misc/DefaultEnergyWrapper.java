package sonar.fluxnetworks.common.misc;

import net.minecraft.util.Direction;
import sonar.fluxnetworks.api.energy.IFNEnergyStorage;
import sonar.fluxnetworks.api.tiles.IFluxDevice;
import sonar.fluxnetworks.api.tiles.IFluxPoint;
import net.minecraftforge.energy.IEnergyStorage;
import sonar.fluxnetworks.common.tileentity.TileFluxPlug;

/**uses forge's own energy wrapper and also IExtendedEnergyStorage*/
public class DefaultEnergyWrapper implements IEnergyStorage, IFNEnergyStorage {

    public IFluxDevice tileEntity;
    public Direction   side;

    public DefaultEnergyWrapper(IFluxDevice tileEntity, Direction side) {
        this.tileEntity = tileEntity;
        this.side = side;
    }

    ///// FLUX EXTENDED \\\\\

    @Override
    public long receiveEnergyL(long maxReceive, boolean simulate) {
        return tileEntity instanceof TileFluxPlug ? ((TileFluxPlug) tileEntity).addPhantomEnergyToNetwork(side, maxReceive, simulate) : 0;
    }

    @Override
    public long extractEnergyL(long maxExtract, boolean simulate) {
        return 0;
    }

    @Override
    public long getEnergyStoredL() {
        return tileEntity instanceof IFluxPoint ? Long.MAX_VALUE : 0;
    }

    @Override
    public long getMaxEnergyStoredL() {
        return Long.MAX_VALUE;
    }

    @Override
    public boolean canExtractL() {
        return tileEntity.getConnectionType().canRemoveEnergy();
    }

    @Override
    public boolean canReceiveL() {
        return tileEntity.getConnectionType().canAddEnergy();
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
        return (int)Math.min(getEnergyStoredL(), Integer.MAX_VALUE);
    }

    @Override
    public int getMaxEnergyStored() {
        return (int)Math.min(getMaxEnergyStoredL(), Integer.MAX_VALUE);
    }

    @Override
    public boolean canExtract() {
        return canExtractL();
    }

    @Override
    public boolean canReceive() {
        return canReceiveL();
    }
}
