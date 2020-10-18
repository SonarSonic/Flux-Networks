package sonar.fluxnetworks.common.connection.handler;

import sonar.fluxnetworks.api.device.IFluxStorage;
import sonar.fluxnetworks.common.tileentity.TileFluxStorage;

public class FluxStorageHandler extends BasicTransferHandler<TileFluxStorage> {

    public FluxStorageHandler(TileFluxStorage fluxStorage) {
        super(fluxStorage);
    }

    @Override
    public void onCycleEnd() {
        super.onCycleEnd();
        //device.sendPacketIfNeeded();
    }

    @Override
    public long addToBuffer(long amount, boolean simulate) {
        long add = device.addEnergy(getMaxAdd(amount), simulate);
        if(!simulate) {
            this.addedToBuffer += add;
            this.change += add;
        }
        return add;
    }

    @Override
    public long removeFromBuffer(long energy, boolean simulate) {
        long renive = device.removeEnergy(getMaxRemove(energy), simulate);
        if(!simulate) {
            this.removedFromBuffer += renive;
            this.change -= renive;
        }
        return renive;
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
