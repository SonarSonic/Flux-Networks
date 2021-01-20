package sonar.fluxnetworks.common.connection.transfer;

import net.minecraft.util.EnumFacing;
import sonar.fluxnetworks.api.network.IFluxNetwork;
import sonar.fluxnetworks.api.network.ITransferHandler;
import sonar.fluxnetworks.api.tiles.IFluxConnector;
import net.minecraft.nbt.NBTTagCompound;
import sonar.fluxnetworks.api.utils.NBTType;
import sonar.fluxnetworks.common.connection.FluxNetworkServer;

import javax.annotation.Nonnull;

public abstract class BasicTransferHandler<T extends IFluxConnector> implements ITransferHandler {

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
    public long receiveFromSupplier(long amount, @Nonnull EnumFacing side, boolean simulate) {
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

    /**
     * Send data to client tile entity for gui, this is always last tick energy change.
     */
    @Override
    public void writeCustomNBT(NBTTagCompound tag, NBTType type) {
        if (type == NBTType.ALL_SAVE || type == NBTType.TILE_DROP) {
            tag.setLong("buffer", buffer);
        }
        if (type == NBTType.TILE_UPDATE) {
            tag.setLong("buffer", buffer);
            tag.setLong("71", change);
        }
    }

    @Override
    public void readCustomNBT(NBTTagCompound tag, NBTType type) {
        if (type == NBTType.ALL_SAVE || type == NBTType.TILE_DROP) {
            buffer = tag.getLong("buffer");
        }
        if (type == NBTType.TILE_UPDATE) {
            buffer = tag.getLong("buffer");
            change = tag.getLong("71");
        }
    }

    @Override
    public void reset() {
        change = 0;
    }
}
