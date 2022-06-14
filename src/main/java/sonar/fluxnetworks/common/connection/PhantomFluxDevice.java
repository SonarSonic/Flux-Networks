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
 * This class represents a non-entity flux device (e.g. Network Connections tab).
 * These devices may not exist on client world, so there's no entity instance on the client.
 * They just are loaded on the server world, or used to record unloaded flux devices on server.
 * Logical operations are not allowed here.
 *
 * @see TileFluxDevice
 */
public class PhantomFluxDevice implements IFluxDevice {

    public static final int POWER_SURGE_MARKER = Integer.MAX_VALUE;
    public static final long BYPASS_LIMIT_MARKER = -1;

    private int mNetworkID;
    private String mCustomName;
    private int mPriority;
    private long mLimit;
    private UUID mPlayerUUID;
    private FluxDeviceType mDeviceType;
    private GlobalPos mGlobalPos;
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
    public static PhantomFluxDevice unload(@Nonnull TileFluxDevice device) {
        PhantomFluxDevice t = new PhantomFluxDevice();
        t.mNetworkID = device.getNetworkID();
        t.mCustomName = device.getCustomName();
        t.mPriority = device.getSurgeMode() ? POWER_SURGE_MARKER : device.getLiteralPriority();
        t.mLimit = device.getDisableLimit() ? BYPASS_LIMIT_MARKER : device.getLiteralLimit();
        t.mPlayerUUID = device.getOwnerUUID();
        t.mDeviceType = device.getDeviceType();
        t.mGlobalPos = device.getGlobalPos();
        t.mBuffer = device.getTransferBuffer();
        t.mDisplayStack = device.getDisplayStack();
        return t;
    }

    @Nonnull
    public static PhantomFluxDevice update(@Nonnull GlobalPos pos, @Nonnull CompoundTag tag) {
        PhantomFluxDevice t = new PhantomFluxDevice();
        t.mGlobalPos = pos;
        t.readCustomTag(tag, FluxConstants.NBT_PHANTOM_UPDATE);
        return t;
    }

    @Nonnull
    public static PhantomFluxDevice load(@Nonnull CompoundTag tag) {
        PhantomFluxDevice t = new PhantomFluxDevice();
        t.readCustomTag(tag, FluxConstants.NBT_SAVE_ALL);
        return t;
    }

    @Override
    public void writeCustomTag(@Nonnull CompoundTag tag, byte type) {
        if (type == FluxConstants.NBT_SAVE_ALL || type == FluxConstants.NBT_PHANTOM_UPDATE) {
            FluxUtils.writeGlobalPos(tag, mGlobalPos);
            tag.putByte(FluxConstants.DEVICE_TYPE, (byte) mDeviceType.ordinal());
            tag.putInt(FluxConstants.NETWORK_ID, mNetworkID);
            tag.putString(FluxConstants.CUSTOM_NAME, mCustomName);
            tag.putInt(FluxConstants.PRIORITY, mPriority);
            tag.putLong(FluxConstants.LIMIT, mLimit);
            /*tag.putBoolean(FluxConstants.SURGE_MODE, surgeMode);
            tag.putBoolean(FluxConstants.DISABLE_LIMIT, disableLimit);*/
            tag.putUUID(FluxConstants.PLAYER_UUID, mPlayerUUID);
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
            mDeviceType = FluxDeviceType.values()[tag.getByte(FluxConstants.DEVICE_TYPE)];
            mNetworkID = tag.getInt(FluxConstants.NETWORK_ID);
            mCustomName = tag.getString(FluxConstants.CUSTOM_NAME);
            mPriority = tag.getInt(FluxConstants.PRIORITY);
            mLimit = tag.getLong(FluxConstants.LIMIT);
            /*surgeMode = tag.getBoolean(FluxConstants.SURGE_MODE);
            disableLimit = tag.getBoolean(FluxConstants.DISABLE_LIMIT);*/
            mPlayerUUID = tag.getUUID(FluxConstants.PLAYER_UUID);
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
    public void onMenuOpened(@Nonnull Player player) {
        throw new IllegalStateException("Logic method cannot be invoked on phantom device");
    }

    @Override
    public void onMenuClosed(@Nonnull Player player) {
        throw new IllegalStateException("Logic method cannot be invoked on phantom device");
    }

    @Override
    public int getLiteralPriority() {
        return mPriority;
    }

    @Nonnull
    @Override
    public UUID getOwnerUUID() {
        return mPlayerUUID;
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
    public long getLiteralLimit() {
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

    @Override
    public String getCustomName() {
        return mCustomName;
    }

    @Override
    public boolean getDisableLimit() {
        return mLimit == -1;
    }

    @Override
    public boolean getSurgeMode() {
        return mPriority == Integer.MAX_VALUE;
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
