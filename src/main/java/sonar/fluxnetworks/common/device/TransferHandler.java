package sonar.fluxnetworks.common.device;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.Mth;
import sonar.fluxnetworks.api.FluxConstants;
import sonar.fluxnetworks.common.connection.PhantomFluxDevice;
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
    @Override
    public int getPriority() {
        return mSurge > 0 ? mSurge : mPriority;
    }

    /**
     * @return the user-set priority without any gain
     */
    public int getUserPriority() {
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
    public boolean hasPowerSurge() {
        return mSurge > 0;
    }

    /**
     * Set surge mode on this handler to get the highest priority in the network.
     *
     * @param enable whether to surge
     */
    public void setPowerSurge(boolean enable) {
        if (enable) {
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
     * @return the user-set limit without any gain
     */
    public long getUserLimit() {
        return mLimit >= 0 ? mLimit : mLimit - Long.MIN_VALUE;
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
    public boolean canBypassLimit() {
        return mLimit < 0;
    }

    /**
     * Set whether currently set limit should be bypassed.
     *
     * @param enable whether to bypass the limit
     */
    public void setBypassLimit(boolean enable) {
        if (enable) {
            if (mLimit >= 0) {
                mLimit += Long.MIN_VALUE;
            }
        } else if (mLimit < 0) {
            mLimit -= Long.MIN_VALUE;
        }
    }

    public void writeCustomTag(@Nonnull CompoundTag tag, byte type) {
        switch (type) {
            case FluxConstants.TYPE_SAVE_ALL -> {
                tag.putInt(FluxConstants.PRIORITY, mPriority);
                tag.putInt(FluxConstants.SURGE_MODE, mSurge);
                tag.putLong(FluxConstants.LIMIT, mLimit);
            }
            case FluxConstants.TYPE_TILE_UPDATE -> {
                tag.putLong(FluxConstants.CHANGE, mChange);
                tag.putInt(FluxConstants.PRIORITY, mPriority);
                tag.putInt(FluxConstants.SURGE_MODE, mSurge);
                tag.putLong(FluxConstants.LIMIT, mLimit);
            }
            case FluxConstants.TYPE_TILE_DROP -> {
                tag.putInt(FluxConstants.PRIORITY, mPriority);
                tag.putBoolean(FluxConstants.SURGE_MODE, hasPowerSurge());
                tag.putLong(FluxConstants.LIMIT, mLimit);
            }
            case FluxConstants.TYPE_PHANTOM_UPDATE -> {
                tag.putLong(FluxConstants.CHANGE, mChange);
                tag.putInt(FluxConstants.PRIORITY, hasPowerSurge() ? PhantomFluxDevice.POWER_SURGE_MARKER : mPriority);
                tag.putLong(FluxConstants.LIMIT, canBypassLimit() ? PhantomFluxDevice.BYPASS_LIMIT_MARKER : mLimit);
            }
        }
    }

    public void readCustomTag(@Nonnull CompoundTag tag, byte type) {
        if (type == FluxConstants.TYPE_TILE_SETTING) {
            if (tag.contains(FluxConstants.SURGE_MODE)) {
                setPowerSurge(tag.getBoolean(FluxConstants.SURGE_MODE));
            } else if (tag.contains(FluxConstants.PRIORITY)) {
                setPriority(tag.getInt(FluxConstants.PRIORITY));
            }
            if (tag.contains(FluxConstants.DISABLE_LIMIT)) {
                setBypassLimit(tag.getBoolean(FluxConstants.DISABLE_LIMIT));
            } else if (tag.contains(FluxConstants.LIMIT)) {
                setLimit(tag.getLong(FluxConstants.LIMIT));
            }
            return;
        }
        if (tag.contains(FluxConstants.BUFFER)) {
            mBuffer = tag.getLong(FluxConstants.BUFFER);
        } else {
            mBuffer = tag.getLong(FluxConstants.ENERGY);
        }
        switch (type) {
            case FluxConstants.TYPE_SAVE_ALL -> {
                mPriority = tag.getInt(FluxConstants.PRIORITY);
                mSurge = tag.getInt(FluxConstants.SURGE_MODE);
                mLimit = tag.getLong(FluxConstants.LIMIT);
            }
            case FluxConstants.TYPE_TILE_UPDATE -> {
                mChange = tag.getLong(FluxConstants.CHANGE);
                mPriority = tag.getInt(FluxConstants.PRIORITY);
                mSurge = tag.getInt(FluxConstants.SURGE_MODE);
                mLimit = tag.getLong(FluxConstants.LIMIT);
            }
            case FluxConstants.TYPE_TILE_DROP -> {
                mPriority = tag.getInt(FluxConstants.PRIORITY);
                setPowerSurge(tag.getBoolean(FluxConstants.SURGE_MODE));
                mLimit = tag.getLong(FluxConstants.LIMIT);
            }
        }
    }

    public void writePacket(@Nonnull FriendlyByteBuf buf, byte id) {
        if (id == FluxConstants.DEVICE_BUFFER_S2C_GUI_SYNC) {
            buf.writeLong(mChange);
            buf.writeLong(mBuffer);
        }
    }

    public void readPacket(@Nonnull FriendlyByteBuf buf, byte id) {
        if (id == FluxConstants.DEVICE_BUFFER_S2C_GUI_SYNC) {
            mChange = buf.readLong();
            mBuffer = buf.readLong();
        }
    }
}
