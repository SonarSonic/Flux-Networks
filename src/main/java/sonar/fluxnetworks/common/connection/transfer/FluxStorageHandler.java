package sonar.fluxnetworks.common.connection.transfer;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import sonar.fluxnetworks.api.misc.FluxConstants;
import sonar.fluxnetworks.common.network.FluxTileMessage;
import sonar.fluxnetworks.common.tileentity.TileFluxStorage;

import javax.annotation.Nonnull;

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
                device.markServerEnergyChanged();
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
                device.markServerEnergyChanged();
            }
            return remove;
        }
        return 0;
    }

    @Override
    public long getRequest() {
        return Math.min(getAddLimit(), device.getMaxTransferLimit() - buffer);
    }

    @Override
    public void writeCustomNBT(CompoundNBT tag, int type) {
        if (type == FluxConstants.TYPE_TILE_UPDATE || type == FluxConstants.TYPE_CONNECTION_UPDATE) {
            super.writeCustomNBT(tag, type); // read by PhantomFluxDevice
        }
        if (type == FluxConstants.TYPE_SAVE_ALL || type == FluxConstants.TYPE_TILE_DROP) {
            tag.putLong(FluxConstants.ENERGY, buffer);
        }
    }

    @Override
    public void readCustomNBT(CompoundNBT tag, int type) {
        if (type == FluxConstants.TYPE_TILE_UPDATE) {
            super.readCustomNBT(tag, type);
        }
        if (type == FluxConstants.TYPE_SAVE_ALL || type == FluxConstants.TYPE_TILE_DROP) {
            buffer = tag.getLong(FluxConstants.ENERGY);
        }
    }

    @Override
    public void writePacket(@Nonnull PacketBuffer buffer, byte id) {
        if (id == FluxTileMessage.S2C_STORAGE_ENERGY) {
            buffer.writeLong(this.buffer);
        } else {
            super.writePacket(buffer, id);
        }
    }

    @Override
    public void readPacket(@Nonnull PacketBuffer buffer, byte id) {
        if (id == FluxTileMessage.S2C_STORAGE_ENERGY) {
            this.buffer = buffer.readLong();
        } else {
            super.readPacket(buffer, id);
        }
    }
}
