package sonar.fluxnetworks.common.connection.handler;

import sonar.fluxnetworks.common.tileentity.TileFluxStorage;

public class FluxStorageHandler extends AbstractTransferHandler<TileFluxStorage> {

    public FluxStorageHandler(TileFluxStorage fluxStorage) {
        super(fluxStorage);
    }

    @Override
    public void onEndCycle() {
        super.onEndCycle();
        device.sendPacketIfNeeded();
    }

    @Override
    public long addEnergyToBuffer(long energy, boolean simulate) {
        long amount = device.addEnergy(getMaxAdd(energy), simulate);
        if(!simulate) {
            this.addedToBuffer += amount;
            this.change += amount;
        }
        return amount;
    }

    @Override
    public long removeEnergyFromBuffer(long energy, boolean simulate) {
        long amount = device.removeEnergy(getMaxRemove(energy), simulate);
        if(!simulate) {
            this.removedFromBuffer += amount;
            this.change -= amount;
        }
        return amount;
    }

    @Override
    public long getBuffer() {
        return device.getEnergy();
    }

    @Override
    public long getRequest() {
        return Math.min(getAddLimit(), device.maxEnergyStorage - device.energyStored);
    }
}
