package sonar.fluxnetworks.common.device;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.Mth;
import sonar.fluxnetworks.api.FluxConstants;
import sonar.fluxnetworks.common.connection.TransferNode;

import javax.annotation.Nonnull;

/**
 * The energy transfer handler of a network device entity.
 * Any modification to this object should be invoked on
 * the device entity, since it has network listeners.
 */
public abstract class TransferHandler extends TransferNode {

    public static final int PRI_USER_MIN = -9999;
    public static final int PRI_USER_MAX = 9999;

    public static final int PRI_GAIN_MIN = 10000;
    public static final int PRI_GAIN_MAX = 100000;

    // to get the lowest priority across the network
    public static final int STORAGE_PRI_DECR = 1000000;

    /**
     * The internal buffer for this transfer handler.
     */
    protected long mBuffer;

    /**
     * The external energy transfer change in the last cycle, which has no relation
     * to the buffer's usage. This could be positive or negative.
     */
    protected long mChange;

    /**
     * The raw priority. Can be negative.
     */
    private int mPriority;

    /**
     * The power surge priority. When not zero, it replaces normal priority.
     */
    private int mSurge;

    /**
     * The raw transfer limit that is used to limit the maximum external energy transfer
     * in each cycle. Note that sign bit representing no limit set by user.
     */
    private long mLimit;

    protected TransferHandler(long limit) {
        setLimit(limit);
    }

    /**
     * @return the internal buffer for this transfer handler.
     */
    @Override
    public final long getBuffer() {
        return mBuffer;
    }

    /**
     * Get the energy change produced by externals in last tick.
     * For instance, a Plug may receive energy, but not transmit them across the network,
     * so energy change is the amount it received rather than 0, they just went to the buffer.
     * If a Point is requesting 1EU, but we can only provide 3FE, the 3FE will go to the
     * Point buffer, and the energy change of the Point is 0 rather than 3.
     *
     * @return energy change
     */
    public final long getChange() {
        return mChange;
    }

    /**
     * @return the requested energy for this transfer handler.
     */
    @Override
    public long getRequest() {
        throw new UnsupportedOperationException();
    }

    /**
     * Clear states.
     */
    public void clearLocalStates() {
        mChange = 0;
    }

    /**
     * Returns the logical priority across the network.
     * Storages always have the lowest priority in the network.
     *
     * @return the logical priority
     */
    @Override
    public int getPriority() {
        return mSurge > 0 ? mSurge : mPriority;
    }

    /**
     * @return the user-set priority without any gain
     */
    public int getRawPriority() {
        return mPriority;
    }

    /**
     * Set a raw priority to this device.
     * After calling, surge mode will be disabled.
     *
     * @param priority the priority to set
     */
    public void setPriority(int priority) {
        mPriority = Mth.clamp(priority, PRI_USER_MIN, PRI_USER_MAX);
        mSurge = 0;
    }

    /**
     * @return has surged
     */
    public boolean getSurgeMode() {
        return mSurge > 0;
    }

    /**
     * Set surge mode on this handler to get the highest priority in the network.
     *
     * @param surge whether to surge
     */
    public void setSurgeMode(boolean surge) {
        if (surge) {
            mSurge = PRI_GAIN_MAX;
        } else {
            mSurge = 0;
        }
    }

    /**
     * Called when another network device surged. So that other devices should
     * decrease its surge mode priority.
     */
    void onPowerSurge() {
        if (mSurge > PRI_GAIN_MIN) {
            mSurge--;
        }
    }

    /**
     * Get the logical transfer limit for this handler.
     *
     * @return the logical transfer limit
     */
    public long getLimit() {
        return mLimit >= 0 ? mLimit : Long.MAX_VALUE;
    }

    /**
     * @return the user-set limit without any bypass
     */
    public long getRawLimit() {
        return mLimit;
    }

    /**
     * Set a raw transfer limit for this handler.
     * After calling this method, bypass state will be reset to false.
     *
     * @param limit the limit to set
     */
    public void setLimit(long limit) {
        mLimit = Math.max(0, limit);
    }

    /**
     * Whether currently set limit should be bypassed.
     * <p>
     * This method is only used to obtain the bypass state.
     * If it's true, {@link #getLimit()} will return infinity.
     *
     * @return bypass limit
     */
    public boolean getDisableLimit() {
        return mLimit < 0;
    }

    /**
     * Set whether currently set limit should be bypassed.
     *
     * @param disable whether to bypass the limit
     */
    public void setDisableLimit(boolean disable) {
        if (disable) {
            if (mLimit >= 0) {
                mLimit += Long.MIN_VALUE;
            }
        } else if (mLimit < 0) {
            mLimit -= Long.MIN_VALUE;
        }
    }

    public void writeCustomTag(@Nonnull CompoundTag tag, byte type) {
        if (type == FluxConstants.TYPE_SAVE_ALL || type == FluxConstants.TYPE_TILE_DROP) {
            tag.putLong(FluxConstants.BUFFER, mBuffer);
        }
        if (type == FluxConstants.TYPE_TILE_UPDATE || type == FluxConstants.TYPE_PHANTOM_UPDATE) {
            tag.putLong(FluxConstants.BUFFER, mBuffer);
            tag.putLong(FluxConstants.CHANGE, mChange);
        }
    }

    public void readCustomTag(@Nonnull CompoundTag tag, byte type) {
        if (type == FluxConstants.TYPE_SAVE_ALL || type == FluxConstants.TYPE_TILE_DROP) {
            mBuffer = tag.getLong(FluxConstants.BUFFER);
        }
        if (type == FluxConstants.TYPE_TILE_UPDATE) {
            mBuffer = tag.getLong(FluxConstants.BUFFER);
            mChange = tag.getLong(FluxConstants.CHANGE);
        }
    }

    public void writePacket(@Nonnull FriendlyByteBuf buf, byte id) {
        switch (id) {
            case FluxConstants.C2S_PRIORITY -> buf.writeInt(mPriority);
            case FluxConstants.C2S_LIMIT -> buf.writeLong(mLimit);
            case FluxConstants.C2S_SURGE_MODE -> buf.writeBoolean(getSurgeMode());
            case FluxConstants.C2S_DISABLE_LIMIT -> buf.writeBoolean(getDisableLimit());
            case FluxConstants.S2C_GUI_SYNC -> {
                buf.writeLong(mChange);
                buf.writeLong(mBuffer);
            }
        }
    }

    public void readPacket(@Nonnull FriendlyByteBuf buf, byte id) {
        switch (id) {
            case FluxConstants.C2S_PRIORITY -> setPriority(buf.readVarInt());
            case FluxConstants.C2S_LIMIT -> setLimit(buf.readVarLong());
            case FluxConstants.S2C_GUI_SYNC -> {
                mChange = buf.readLong();
                mBuffer = buf.readLong();
            }
        }
    }
}
