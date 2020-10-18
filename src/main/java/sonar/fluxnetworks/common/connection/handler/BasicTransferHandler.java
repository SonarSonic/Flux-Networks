package sonar.fluxnetworks.common.connection.handler;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import sonar.fluxnetworks.api.device.IFluxDevice;
import sonar.fluxnetworks.api.network.ITransferHandler;

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

    public void onCycleStart() {
        change = 0;
        addedToBuffer = 0;
        removedFromBuffer = 0;
    }

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

    public CompoundNBT writeNetworkedNBT(CompoundNBT tag) {
        tag.putLong("71", change);
        tag.putLong("72", buffer);
        return tag;
    }

    public void readNetworkedNBT(CompoundNBT tag) {
        change = tag.getLong("71");
        buffer = tag.getLong("72");
    }
}
