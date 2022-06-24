package sonar.fluxnetworks.common.connection;

import net.minecraft.core.GlobalPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import sonar.fluxnetworks.api.FluxConstants;
import sonar.fluxnetworks.api.device.FluxDeviceType;
import sonar.fluxnetworks.api.device.IFluxDevice;
import sonar.fluxnetworks.common.device.TileFluxDevice;
import sonar.fluxnetworks.common.util.FluxUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.UUID;

/**
 * This class represents a non-entity flux device (e.g. in Network Connections tab).
 * These devices may not exist on client world, so there's no entity instance on the client.
 * They just are loaded on the server world, or used to record unloaded flux devices on server.
 * Logical operations are not allowed here.
 *
 * @see TileFluxDevice
 */
public class PhantomFluxDevice implements IFluxDevice {

    private int mNetworkID;
    private String mCustomName;
    private int mPriority;
    private long mLimit;
    private UUID mOwnerUUID;
    private FluxDeviceType mDeviceType;
    private GlobalPos mGlobalPos;
    private boolean mSurgeMode;
    private boolean mDisableLimit;
    private boolean mChunkLoaded;
    private boolean mForcedLoading;
    private long mBuffer;
    private long mChange;
    private ItemStack mDisplayStack;

    public PhantomFluxDevice() {
    }

    /**
     * Copy data from TileFluxDevice on server.
     *
     * @param device the loaded device entity
     */
    @Nonnull
    public static PhantomFluxDevice makeUnloaded(@Nonnull TileFluxDevice device) {
        PhantomFluxDevice t = new PhantomFluxDevice();
        t.mNetworkID = device.getNetworkID();
        t.mCustomName = device.getCustomName();
        t.mPriority = device.getRawPriority();
        t.mLimit = device.getRawLimit();
        t.mOwnerUUID = device.getOwnerUUID();
        t.mDeviceType = device.getDeviceType();
        t.mGlobalPos = device.getGlobalPos();
        t.mSurgeMode = device.getSurgeMode();
        t.mDisableLimit = device.getDisableLimit();
        t.mBuffer = device.getTransferBuffer();
        t.mDisplayStack = device.getDisplayStack();
        return t;
    }

    @Nonnull
    public static PhantomFluxDevice makeUpdated(@Nonnull GlobalPos pos, @Nonnull CompoundTag tag) {
        PhantomFluxDevice t = new PhantomFluxDevice();
        t.mGlobalPos = pos;
        t.readCustomTag(tag, FluxConstants.NBT_PHANTOM_UPDATE);
        return t;
    }

    @Nonnull
    public static PhantomFluxDevice make(@Nonnull CompoundTag tag) {
        PhantomFluxDevice t = new PhantomFluxDevice();
        t.readCustomTag(tag, FluxConstants.NBT_SAVE_ALL);
        return t;
    }

    @Override
    public void writeCustomTag(@Nonnull CompoundTag tag, byte type) {
        if (type == FluxConstants.NBT_SAVE_ALL || type == FluxConstants.NBT_PHANTOM_UPDATE) {
            FluxUtils.writeGlobalPos(tag, mGlobalPos);
            tag.putByte(FluxConstants.DEVICE_TYPE, mDeviceType.getId());
            tag.putInt(FluxConstants.NETWORK_ID, mNetworkID);
            tag.putString(FluxConstants.CUSTOM_NAME, mCustomName);
            tag.putInt(FluxConstants.PRIORITY, mPriority);
            tag.putLong(FluxConstants.LIMIT, mLimit);
            tag.putBoolean(FluxConstants.SURGE_MODE, mSurgeMode);
            tag.putBoolean(FluxConstants.DISABLE_LIMIT, mDisableLimit);
            tag.putUUID(FluxConstants.PLAYER_UUID, mOwnerUUID);
            tag.putLong(FluxConstants.BUFFER, mBuffer);
            mDisplayStack.save(tag);
        }
    }

    @Override
    public void readCustomTag(@Nonnull CompoundTag tag, byte type) {
        if (type == FluxConstants.NBT_SAVE_ALL) {
            mGlobalPos = FluxUtils.readGlobalPos(tag);
        }
        if (type == FluxConstants.NBT_SAVE_ALL || type == FluxConstants.NBT_PHANTOM_UPDATE) {
            mDeviceType = FluxDeviceType.fromId(tag.getByte(FluxConstants.DEVICE_TYPE));
            mNetworkID = tag.getInt(FluxConstants.NETWORK_ID);
            mCustomName = tag.getString(FluxConstants.CUSTOM_NAME);
            mPriority = tag.getInt(FluxConstants.PRIORITY);
            mLimit = tag.getLong(FluxConstants.LIMIT);
            mSurgeMode = tag.getBoolean(FluxConstants.SURGE_MODE);
            mDisableLimit = tag.getBoolean(FluxConstants.DISABLE_LIMIT);
            mOwnerUUID = tag.getUUID(FluxConstants.PLAYER_UUID);
            mBuffer = tag.getLong(FluxConstants.BUFFER);
            mDisplayStack = ItemStack.of(tag);
        }
        if (type == FluxConstants.NBT_PHANTOM_UPDATE) {
            mForcedLoading = tag.getBoolean(FluxConstants.FORCED_LOADING);
            mChunkLoaded = tag.getBoolean(FluxConstants.CHUNK_LOADED);
            mChange = tag.getLong(FluxConstants.CHANGE);
        }
    }

    @Override
    public int getNetworkID() {
        return mNetworkID;
    }

    @Override
    public void onPlayerOpened(@Nonnull Player player) {
        throw new IllegalStateException("Logic method cannot be invoked on phantom device");
    }

    @Override
    public void onPlayerClosed(@Nonnull Player player) {
        throw new IllegalStateException("Logic method cannot be invoked on phantom device");
    }

    @Override
    public int getRawPriority() {
        return mPriority;
    }

    @Nonnull
    @Override
    public UUID getOwnerUUID() {
        return mOwnerUUID;
    }

    @Nonnull
    @Override
    public FluxDeviceType getDeviceType() {
        return mDeviceType;
    }

    @Override
    public boolean isChunkLoaded() {
        return mChunkLoaded;
    }

    @Override
    public boolean isForcedLoading() {
        return mForcedLoading;
    }

    @Override
    public long getRawLimit() {
        return mLimit;
    }

    @Override
    public long getMaxTransferLimit() {
        throw new IllegalStateException("Logic method cannot be invoked on phantom device");
    }

    @Nonnull
    @Override
    public GlobalPos getGlobalPos() {
        return mGlobalPos;
    }

    @Nonnull
    @Override
    public String getCustomName() {
        return mCustomName;
    }

    @Override
    public boolean getDisableLimit() {
        return mDisableLimit;
    }

    @Override
    public boolean getSurgeMode() {
        return mSurgeMode;
    }

    @Nonnull
    @Override
    public ItemStack getDisplayStack() {
        return mDisplayStack;
    }

    @Override
    public long getTransferBuffer() {
        return mBuffer;
    }

    @Override
    public long getTransferChange() {
        return mChange;
    }

    @Nullable
    @Override
    public FluxMenu createMenu(int containerId, @Nonnull Inventory inventory, @Nonnull Player player) {
        throw new IllegalStateException("Logic method cannot be invoked on phantom device");
    }
}
