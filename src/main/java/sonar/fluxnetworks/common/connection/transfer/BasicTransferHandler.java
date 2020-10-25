package sonar.fluxnetworks.common.connection.transfer;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Direction;
import sonar.fluxnetworks.api.device.IFluxDevice;
import sonar.fluxnetworks.api.misc.FluxConstants;
import sonar.fluxnetworks.api.network.ITransferHandler;

import javax.annotation.Nonnull;

public abstract class BasicTransferHandler<T extends IFluxDevice> implements ITransferHandler {

    protected final T device;

    protected long buffer;

    /**
     * the actual energy transfer change - has no relation to the buffer's usage
     */
    protected long change;

    public BasicTransferHandler(T device) {
        this.device = device;
    }

    @Override
    public void addToBuffer(long energy) {
        throw new UnsupportedOperationException();
    }

    @Override
    public long removeFromBuffer(long energy) {
        throw new UnsupportedOperationException();
    }

    @Override
    public long receiveFromSupplier(long amount, @Nonnull Direction side, boolean simulate) {
        throw new UnsupportedOperationException();
    }

    @Override
    public final long getBuffer() {
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
    public final long getChange() {
        return change;
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
        if (id == FluxConstants.S2C_GUI_SYNC) {
            buffer.writeLong(change);
            buffer.writeLong(this.buffer);
        }
    }

    @Override
    public void readPacket(@Nonnull PacketBuffer buffer, byte id) {
        if (id == FluxConstants.S2C_GUI_SYNC) {
            change = buffer.readLong();
            this.buffer = buffer.readLong();
        }
    }
}
