package sonar.fluxnetworks.common.connection.handler;

import sonar.fluxnetworks.api.device.IFluxStorage;
import sonar.fluxnetworks.common.tileentity.TileFluxStorage;

public class FluxStorageHandler extends AbstractTransferHandler<IFluxStorage> {

    public FluxStorageHandler(TileFluxStorage fluxStorage) {
        super(fluxStorage);
    }

    @Override
    public void onEndCycle() {
        super.onEndCycle();
        //device.sendPacketIfNeeded();
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
        return device.getEnergyStored();
    }

    @Override
    public long getRequest() {
        return Math.min(getAddLimit(), device.getMaxEnergyStorage() - device.getEnergyStored());
    }
}
