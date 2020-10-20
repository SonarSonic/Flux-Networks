package sonar.fluxnetworks.common.connection.transfer;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Direction;
import sonar.fluxnetworks.api.device.IFluxDevice;
import sonar.fluxnetworks.api.misc.FluxConstants;
import sonar.fluxnetworks.api.network.ITransferHandler;
import sonar.fluxnetworks.common.network.FluxTileMessage;

import javax.annotation.Nonnull;

public abstract class BasicTransferHandler<T extends IFluxDevice> implements ITransferHandler {

    protected final T device;

    protected long buffer;

    protected long addedToBuffer;
    protected long removedFromBuffer;

    /**
     * the actual energy transfer change - has no relation to the buffer's usage
     */
    protected long change;

    public BasicTransferHandler(T device) {
        this.device = device;
    }

    @Override
    public void onCycleStart() {
        change = 0;
        addedToBuffer = 0;
        removedFromBuffer = 0;
    }

    @Override
    public void onCycleEnd() {

    }

    @Override
    public long addToBuffer(long energy, boolean simulate) {
        long add = getMaxAdd(energy);
        if (add > 0) {
            if (!simulate) {
                buffer += add;
                addedToBuffer += add;
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
                buffer -= remove;
                removedFromBuffer += remove;
            }
            return remove;
        }
        return 0;
    }

    @Override
    public long receiveFromAdjacency(long amount, @Nonnull Direction side, boolean simulate) {
        return 0;
    }

    @Override
    public void setBuffer(long buffer) {
        this.buffer = buffer;
    }

    @Override
    public long getBuffer() {
        return buffer;
    }

    @Override
    public long getRequest() {
        return 0;
    }

    /**
     * Get the energy change produced by externals in last tick.
     * For instance, a Plug may receive energy, but not transmit them across the network,
     * so energy change is the amount it received rather than 0, they just went to the buffer.
     * If a Point is requesting 1EU but we can only provide 3FE, the 3FE will go to the
     * Point buffer, and the energy change of the Point is 0 rather than 3.
     *
     * @return energy change
     */
    @Override
    public long getChange() {
        return change;
    }

    protected long getAddLimit() {
        return device.getLogicLimit();
    }

    protected long getRemoveLimit() {
        return device.getLogicLimit();
    }

    protected long getMaxAdd(long toAdd) {
        return Math.max(Math.min(getAddLimit() - addedToBuffer, toAdd), 0);
    }

    protected long getMaxRemove(long toRemove) {
        return Math.max(Math.min(getRemoveLimit() - removedFromBuffer, Math.min(toRemove, getBuffer())), 0);
    }

    @Override
    public void writeCustomNBT(CompoundNBT tag, int type) {
        if (type == FluxConstants.TYPE_SAVE_ALL || type == FluxConstants.TYPE_TILE_DROP) {
            tag.putLong(FluxConstants.BUFFER, buffer);
        }
        if (type == FluxConstants.TYPE_TILE_UPDATE || type == FluxConstants.TYPE_CONNECTION_UPDATE) {
            tag.putLong(FluxConstants.BUFFER, buffer);
            tag.putLong(FluxConstants.CHANGE, change);
        }
    }

    @Override
    public void readCustomNBT(CompoundNBT tag, int type) {
        if (type == FluxConstants.TYPE_SAVE_ALL || type == FluxConstants.TYPE_TILE_DROP) {
            buffer = tag.getLong(FluxConstants.BUFFER);
        }
        if (type == FluxConstants.TYPE_TILE_UPDATE) {
            buffer = tag.getLong(FluxConstants.BUFFER);
            change = tag.getLong(FluxConstants.CHANGE);
        }
    }

    @Override
    public void writePacket(@Nonnull PacketBuffer buffer, byte id) {
        if (id == FluxTileMessage.S2C_GUI_SYNC) {
            buffer.writeLong(change);
            buffer.writeLong(this.buffer);
        }
        if (id == FluxTileMessage.S2C_STORAGE_ENERGY) {
            buffer.writeLong(this.buffer);
        }
    }

    @Override
    public void readPacket(@Nonnull PacketBuffer buffer, byte id) {
        if (id == FluxTileMessage.S2C_GUI_SYNC) {
            change = buffer.readLong();
            this.buffer = buffer.readLong();
        }
        if (id == FluxTileMessage.S2C_STORAGE_ENERGY) {
            this.buffer = buffer.readLong();
        }
    }
}
