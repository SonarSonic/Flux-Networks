package sonar.fluxnetworks.common.connection.handler;

import sonar.fluxnetworks.common.tileentity.TileFluxStorage;

public class FluxStorageHandler extends AbstractTransferHandler<TileFluxStorage> {

    public FluxStorageHandler(TileFluxStorage fluxStorage) {
        super(fluxStorage);
    }

    @Override
    public void onEndCycle() {
        super.onEndCycle();
        fluxConnector.sendPacketIfNeeded();
    }

    @Override
    public long addEnergyToBuffer(long energy, boolean simulate) {
        long amount = fluxConnector.addEnergy(getMaxAdd(energy), simulate);
        if(!simulate) {
            this.addedToBuffer += amount;
            this.change += amount;
        }
        return amount;
    }

    @Override
    public long removeEnergyFromBuffer(long energy, boolean simulate) {
        long amount = fluxConnector.removeEnergy(getMaxRemove(energy), simulate);
        if(!simulate) {
            this.removedFromBuffer += amount;
            this.change -= amount;
        }
        return amount;
    }

    @Override
    public long getBuffer() {
        return fluxConnector.getEnergy();
    }

    @Override
    public long getRequest() {
        return Math.min(getAddLimit(), fluxConnector.maxEnergyStorage - fluxConnector.energyStored);
    }
}
