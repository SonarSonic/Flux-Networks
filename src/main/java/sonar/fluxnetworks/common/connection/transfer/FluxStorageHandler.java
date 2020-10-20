package sonar.fluxnetworks.common.connection.transfer;

import net.minecraft.nbt.CompoundNBT;
import sonar.fluxnetworks.api.misc.FluxConstants;
import sonar.fluxnetworks.common.tileentity.TileFluxStorage;

public class FluxStorageHandler extends BasicTransferHandler<TileFluxStorage> {

    public FluxStorageHandler(TileFluxStorage fluxStorage) {
        super(fluxStorage);
    }

    @Override
    public long addToBuffer(long amount, boolean simulate) {
        long add = getMaxAdd(amount);
        if (add > 0) {
            if (!simulate) {
                buffer += add;
                addedToBuffer += add;
                change += add;
                device.serverEnergyChanged = true;
            }
            return add;
        }
        return 0;
    }

    @Override
    public long removeFromBuffer(long energy, boolean simulate) {
        long remove = getMaxRemove(energy);
        if (remove > 0) {
            if (!simulate) {
                buffer += remove;
                removedFromBuffer += remove;
                change -= remove;
                device.serverEnergyChanged = true;
            }
            return remove;
        }
        return 0;
    }

    @Override
    public void writeCustomNBT(CompoundNBT tag, int type) {
        if (type == FluxConstants.TYPE_SAVE_ALL || type == FluxConstants.TYPE_TILE_DROP) {
            tag.putLong(FluxConstants.ENERGY, buffer);
        }
        if (type == FluxConstants.TYPE_TILE_UPDATE || type == FluxConstants.TYPE_CONNECTION_UPDATE) {
            tag.putLong(FluxConstants.ENERGY, buffer);
            tag.putLong(FluxConstants.CHANGE, change);
        }
    }

    @Override
    public void readCustomNBT(CompoundNBT tag, int type) {
        if (type == FluxConstants.TYPE_SAVE_ALL || type == FluxConstants.TYPE_TILE_DROP) {
            buffer = tag.getLong(FluxConstants.ENERGY);
        }
        if (type == FluxConstants.TYPE_TILE_UPDATE) {
            buffer = tag.getLong(FluxConstants.ENERGY);
            change = tag.getLong(FluxConstants.CHANGE);
        }
    }

    @Override
    public long getRequest() {
        return Math.min(getAddLimit(), device.getMaxTransferLimit() - buffer);
    }
}
