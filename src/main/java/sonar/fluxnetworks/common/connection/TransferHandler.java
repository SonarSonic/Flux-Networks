package sonar.fluxnetworks.common.connection;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.Mth;
import sonar.fluxnetworks.api.FluxConstants;
import sonar.fluxnetworks.common.device.TileFluxDevice;

import javax.annotation.Nonnull;

/**
 * A transfer handler is associated with a logical entity in a network.
 * Any modification to this object should be invoked on the device entity.
 *
 * @see TileFluxDevice#getTransferHandler()
 */
public abstract class TransferHandler {

    public static final int PRI_USER_MIN = -9999;
    public static final int PRI_USER_MAX = 9999;

    public static final int PRI_GAIN_MIN = 10000;
    public static final int PRI_GAIN_MAX = 100000;

    // to get the lowest priority across the network
    // contrast: STORAGE_PRI_DECR > PRI_USER_MAX + PRI_GAIN_MAX
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
     * The user-set priority. Can be negative.
     */
    private int mPriority;
    private boolean mSurgeMode;

    /**
     * The user-set transfer limit that is used to limit the maximum external energy transfer
     * in each cycle. Note that sign bit representing no limit set by user.
     */
    private long mLimit;
    private boolean mDisableLimit;

    /**
     * @param limit the initial limit
     */
    protected TransferHandler(long limit) {
        setLimit(limit);
    }

    /**
     * Called before the start of the internal transfer cycle.
     * In this time, external energy transfer should be simulated.
     */
    protected abstract void onCycleStart();

    /**
     * Called after the end of the internal transfer cycle.
     * In this time, external energy transfer should be performed.
     */
    protected abstract void onCycleEnd();

    /**
     * Insert energy to the internal buffer.
     *
     * @param energy the amount
     */
    protected void addToBuffer(long energy) {
        throw new UnsupportedOperationException();
    }

    /**
     * Extract energy from the internal buffer.
     *
     * @param energy the desired amount
     * @return the actual energy in Flux Energy units
     */
    protected long removeFromBuffer(long energy) {
        throw new UnsupportedOperationException();
    }

    /**
     * @return the internal buffer for this transfer handler.
     */
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
    public long getRequest() {
        return 0;
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
    public int getPriority() {
        return mSurgeMode ? PRI_GAIN_MAX : mPriority;
    }

    /**
     * @return the user-set priority without any gain
     */
    public int getRawPriority() {
        return mPriority;
    }

    /**
     * Set a raw priority to this device.
     *
     * @param priority the priority to set
     */
    public boolean setPriority(int priority) {
        priority = Mth.clamp(priority, PRI_USER_MIN, PRI_USER_MAX);
        if (mPriority != priority) {
            mPriority = priority;
            return true;
        }
        return false;
    }

    /**
     * @return has surged
     */
    public boolean getSurgeMode() {
        return mSurgeMode;
    }

    /**
     * Set surge mode on this handler to get the highest priority in the network.
     *
     * @param surgeMode whether to surge
     */
    public boolean setSurgeMode(boolean surgeMode) {
        if (mSurgeMode != surgeMode) {
            mSurgeMode = surgeMode;
            return true;
        }
        return false;
    }

    /**
     * Get the logical transfer limit for this handler.
     *
     * @return the logical transfer limit
     */
    public long getLimit() {
        return mDisableLimit ? Long.MAX_VALUE : mLimit;
    }

    /**
     * @return the user-set limit without any gain
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
        return mDisableLimit;
    }

    /**
     * Set whether currently set limit should be bypassed.
     *
     * @param disableLimit whether to bypass the limit
     */
    public void setDisableLimit(boolean disableLimit) {
        mDisableLimit = disableLimit;
    }

    public void writeCustomTag(@Nonnull CompoundTag tag, byte type) {
        switch (type) {
            case FluxConstants.NBT_SAVE_ALL, FluxConstants.NBT_TILE_DROP -> {
                tag.putInt(FluxConstants.PRIORITY, mPriority);
                tag.putBoolean(FluxConstants.SURGE_MODE, mSurgeMode);
                tag.putLong(FluxConstants.LIMIT, mLimit);
                tag.putBoolean(FluxConstants.DISABLE_LIMIT, mDisableLimit);
            }
            case FluxConstants.NBT_TILE_UPDATE, FluxConstants.NBT_PHANTOM_UPDATE -> {
                tag.putLong(FluxConstants.CHANGE, mChange);
                tag.putInt(FluxConstants.PRIORITY, mPriority);
                tag.putBoolean(FluxConstants.SURGE_MODE, mSurgeMode);
                tag.putLong(FluxConstants.LIMIT, mLimit);
                tag.putBoolean(FluxConstants.DISABLE_LIMIT, mDisableLimit);
            }
        }
    }

    public void readCustomTag(@Nonnull CompoundTag tag, byte type) {
        if (type == FluxConstants.NBT_TILE_SETTINGS) {
            // use changeSettings()
            throw new IllegalArgumentException();
        }
        if (tag.contains(FluxConstants.BUFFER)) {
            mBuffer = tag.getLong(FluxConstants.BUFFER);
        } else {
            mBuffer = tag.getLong(FluxConstants.ENERGY);
        }
        switch (type) {
            case FluxConstants.NBT_SAVE_ALL, FluxConstants.NBT_TILE_DROP -> {
                mPriority = tag.getInt(FluxConstants.PRIORITY);
                mSurgeMode = tag.getBoolean(FluxConstants.SURGE_MODE);
                mLimit = tag.getLong(FluxConstants.LIMIT);
                mDisableLimit = tag.getBoolean(FluxConstants.DISABLE_LIMIT);
            }
            case FluxConstants.NBT_TILE_UPDATE -> {
                mChange = tag.getLong(FluxConstants.CHANGE);
                mPriority = tag.getInt(FluxConstants.PRIORITY);
                mSurgeMode = tag.getBoolean(FluxConstants.SURGE_MODE);
                mLimit = tag.getLong(FluxConstants.LIMIT);
                mDisableLimit = tag.getBoolean(FluxConstants.DISABLE_LIMIT);
            }
        }
    }

    /**
     * @return true if sorting is required
     */
    public boolean changeSettings(@Nonnull CompoundTag tag) {
        boolean sort = false;
        if (tag.contains(FluxConstants.SURGE_MODE)) {
            sort = setSurgeMode(tag.getBoolean(FluxConstants.SURGE_MODE));
        }
        if (tag.contains(FluxConstants.PRIORITY)) {
            sort |= setPriority(tag.getInt(FluxConstants.PRIORITY));
        }
        if (tag.contains(FluxConstants.DISABLE_LIMIT)) {
            setDisableLimit(tag.getBoolean(FluxConstants.DISABLE_LIMIT));
        }
        if (tag.contains(FluxConstants.LIMIT)) {
            setLimit(tag.getLong(FluxConstants.LIMIT));
        }
        return sort;
    }

    public void writePacket(@Nonnull FriendlyByteBuf buf, byte type) {
        if (type == FluxConstants.DEVICE_S2C_GUI_SYNC) {
            buf.writeLong(mChange);
            buf.writeLong(mBuffer);
        }
    }

    public void readPacket(@Nonnull FriendlyByteBuf buf, byte type) {
        if (type == FluxConstants.DEVICE_S2C_GUI_SYNC) {
            mChange = buf.readLong();
            mBuffer = buf.readLong();
        }
    }
}
