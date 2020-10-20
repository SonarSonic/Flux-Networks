package sonar.fluxnetworks.common.connection;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.world.World;
import sonar.fluxnetworks.api.device.IFluxDevice;
import sonar.fluxnetworks.api.misc.FluxConstants;
import sonar.fluxnetworks.api.network.FluxDeviceType;
import sonar.fluxnetworks.api.network.IFluxNetwork;
import sonar.fluxnetworks.api.network.ITransferHandler;
import sonar.fluxnetworks.common.misc.FluxUtils;

import javax.annotation.Nonnull;
import java.util.UUID;

/**
 * A POJO class holds values updated from server for GUI display (via Network Connections tab,
 * because these devices may not exist on client world so there's no TileFluxDevice instance on client,
 * they just are loaded on server world for other players), or records unloaded flux devices on server.
 * Logical operations are not allowed here.
 *
 * @see sonar.fluxnetworks.common.tileentity.TileFluxDevice
 */
public class PhantomFluxDevice implements IFluxDevice {

    private int networkID;
    private String customName;
    private int priority;
    private long limit;
    private UUID playerUUID;
    private FluxDeviceType deviceType;
    private GlobalPos globalPos;
    private boolean surgeMode;
    private boolean disableLimit;
    private boolean chunkLoaded;
    private boolean forcedLoading;
    private long buffer;
    private long change;
    private ItemStack stack;

    public PhantomFluxDevice() {
    }

    /**
     * Copy data from TileFluxDevice on server
     *
     * @param device loaded device
     */
    public PhantomFluxDevice(@Nonnull IFluxDevice device) {
        if (device instanceof PhantomFluxDevice) {
            throw new IllegalArgumentException();
        }
        this.networkID = device.getNetworkID();
        this.customName = device.getCustomName();
        this.priority = device.getRawPriority();
        this.limit = device.getRawLimit();
        this.playerUUID = device.getConnectionOwner();
        this.deviceType = device.getDeviceType();
        this.globalPos = device.getGlobalPos();
        this.surgeMode = device.getSurgeMode();
        this.disableLimit = device.getDisableLimit();
        this.buffer = device.getTransferBuffer();
        this.stack = device.getDisplayStack();
    }

    // client update only
    public PhantomFluxDevice(@Nonnull GlobalPos globalPos, CompoundNBT tag) {
        this.globalPos = globalPos;
        readCustomNBT(tag, FluxConstants.TYPE_CONNECTION_UPDATE);
    }

    @Override
    public void writeCustomNBT(CompoundNBT tag, int type) {
        if (type == FluxConstants.TYPE_SAVE_ALL || type == FluxConstants.TYPE_CONNECTION_UPDATE) {
            FluxUtils.writeGlobalPos(tag, globalPos);
            tag.putInt(FluxConstants.NETWORK_ID, networkID);
            tag.putByte(FluxConstants.DEVICE_TYPE, (byte) deviceType.ordinal());
            tag.putString(FluxConstants.CUSTOM_NAME, customName);
            tag.putInt(FluxConstants.PRIORITY, priority);
            tag.putLong(FluxConstants.LIMIT, limit);
            tag.putUniqueId(FluxConstants.PLAYER_UUID, playerUUID);
            tag.putBoolean(FluxConstants.SURGE_MODE, surgeMode);
            tag.putBoolean(FluxConstants.DISABLE_LIMIT, disableLimit);
            tag.putLong(FluxConstants.BUFFER, buffer);
            stack.write(tag);
        }
    }

    @Override
    public void readCustomNBT(CompoundNBT tag, int type) {
        if (type == FluxConstants.TYPE_SAVE_ALL) {
            globalPos = FluxUtils.readGlobalPos(tag);
        }
        if (type == FluxConstants.TYPE_SAVE_ALL || type == FluxConstants.TYPE_CONNECTION_UPDATE) {
            networkID = tag.getInt(FluxConstants.NETWORK_ID);
            deviceType = FluxDeviceType.values()[tag.getByte(FluxConstants.DEVICE_TYPE)];
            customName = tag.getString(FluxConstants.CUSTOM_NAME);
            priority = tag.getInt(FluxConstants.PRIORITY);
            limit = tag.getLong(FluxConstants.LIMIT);
            playerUUID = tag.getUniqueId(FluxConstants.PLAYER_UUID);
            surgeMode = tag.getBoolean(FluxConstants.SURGE_MODE);
            disableLimit = tag.getBoolean(FluxConstants.DISABLE_LIMIT);
            buffer = tag.getLong(FluxConstants.BUFFER);
            stack = ItemStack.read(tag);
        }
        if (type == FluxConstants.TYPE_CONNECTION_UPDATE) {
            forcedLoading = tag.getBoolean(FluxConstants.FORCED_LOADING);
            chunkLoaded = tag.getBoolean(FluxConstants.CHUNK_LOADED);
            change = tag.getLong(FluxConstants.CHANGE);
        }
    }

    @Override
    public int getNetworkID() {
        return networkID;
    }

    @Override
    public int getLogicPriority() {
        throw new IllegalStateException("Logic method cannot be invoked on phantom device");
    }

    @Override
    public int getRawPriority() {
        return priority;
    }

    @Override
    public void setPriority(int priority) {
        throw new IllegalStateException("Logic method cannot be invoked on phantom device");
    }

    @Override
    public IFluxNetwork getNetwork() {
        throw new IllegalStateException("Logic method cannot be invoked on phantom device");
    }

    @Override
    public void onContainerOpened(PlayerEntity player) {
        throw new IllegalStateException("Logic method cannot be invoked on phantom device");
    }

    @Override
    public void onContainerClosed(PlayerEntity player) {
        throw new IllegalStateException("Logic method cannot be invoked on phantom device");
    }

    @Override
    public UUID getConnectionOwner() {
        return playerUUID;
    }

    @Override
    public void setConnectionOwner(UUID uuid) {
        throw new IllegalStateException("Logic method cannot be invoked on phantom device");
    }

    @Override
    public FluxDeviceType getDeviceType() {
        return deviceType;
    }

    @Override
    public boolean canPlayerAccess(PlayerEntity player) {
        throw new IllegalStateException("Logic method cannot be invoked on phantom device");
    }

    @Override
    public boolean isChunkLoaded() {
        return chunkLoaded;
    }

    @Override
    public boolean isForcedLoading() {
        return forcedLoading;
    }

    @Override
    public void setForcedLoading(boolean forcedLoading) {
        throw new IllegalStateException("Logic method cannot be invoked on phantom device");
    }

    @Override
    public void onConnect(IFluxNetwork network) {
        throw new IllegalStateException("Logic method cannot be invoked on phantom device");
    }

    @Override
    public void onDisconnect() {
        throw new IllegalStateException("Logic method cannot be invoked on phantom device");
    }

    @Nonnull
    @Override
    public ITransferHandler getTransferHandler() {
        throw new IllegalStateException("Logic method cannot be invoked on phantom device");
    }

    @Nonnull
    @Override
    public World getFluxWorld() {
        throw new IllegalStateException("Logic method cannot be invoked on phantom device");
    }

    @Override
    public long getLogicLimit() {
        throw new IllegalStateException("Logic method cannot be invoked on phantom device");
    }

    @Override
    public long getRawLimit() {
        return limit;
    }

    @Override
    public void setTransferLimit(long limit) {
        throw new IllegalStateException("Logic method cannot be invoked on phantom device");
    }

    @Override
    public long getMaxTransferLimit() {
        throw new IllegalStateException("Logic method cannot be invoked on phantom device");
    }

    @Override
    public boolean isActive() {
        return false;
    }

    @Nonnull
    @Override
    public GlobalPos getGlobalPos() {
        return globalPos;
    }

    @Override
    public String getCustomName() {
        return customName;
    }

    @Override
    public void setCustomName(String customName) {
        throw new IllegalStateException("Logic method cannot be invoked on phantom device");
    }

    @Override
    public boolean getDisableLimit() {
        return disableLimit;
    }

    @Override
    public void setDisableLimit(boolean disableLimit) {
        throw new IllegalStateException("Logic method cannot be invoked on phantom device");
    }

    @Override
    public boolean getSurgeMode() {
        return surgeMode;
    }

    @Override
    public void setSurgeMode(boolean surgeMode) {
        throw new IllegalStateException("Logic method cannot be invoked on phantom device");
    }

    @Override
    public ItemStack getDisplayStack() {
        return stack;
    }

    @Override
    public long getTransferBuffer() {
        return buffer;
    }

    @Override
    public long getTransferChange() {
        return change;
    }
}
