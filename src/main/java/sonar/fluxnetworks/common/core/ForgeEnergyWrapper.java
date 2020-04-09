package sonar.fluxnetworks.common.core;

import net.minecraft.util.Direction;
import sonar.fluxnetworks.api.tiles.IFluxPhantomEnergy;
import sonar.fluxnetworks.api.tiles.IFluxPoint;
import net.minecraftforge.energy.IEnergyStorage;

public class ForgeEnergyWrapper implements IEnergyStorage {

    public IFluxPhantomEnergy tileEntity;
    public Direction side;

    public ForgeEnergyWrapper(IFluxPhantomEnergy tileEntity, Direction side) {
        this.tileEntity = tileEntity;
        this.side = side;
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        return (int) tileEntity.addPhantomEnergyToNetwork(side, maxReceive, simulate);
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        return 0;
    }

    @Override
    public int getEnergyStored() {
        return tileEntity instanceof IFluxPoint ? Integer.MAX_VALUE : 0;
    }

    @Override
    public int getMaxEnergyStored() {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean canExtract() {
        return tileEntity.getConnectionType().canRemoveEnergy();
    }

    @Override
    public boolean canReceive() {
        return tileEntity.getConnectionType().canAddEnergy();
    }
}
