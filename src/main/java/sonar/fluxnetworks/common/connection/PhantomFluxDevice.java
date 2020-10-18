package sonar.fluxnetworks.common.connection;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.world.World;
import sonar.fluxnetworks.api.device.IFluxDevice;
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
 */
public class PhantomFluxDevice implements IFluxDevice {

    public int networkID;
    public int priority;
    public UUID playerUUID;
    public FluxDeviceType connectionType;
    public long limit;
    public GlobalPos globalPos;
    public String customName;
    public boolean surgeMode;
    public boolean disableLimit;
    public boolean chunkLoaded;
    public boolean forcedLoading;
    public long buffer;
    public long change;
    public ItemStack stack;

    public PhantomFluxDevice(@Nonnull IFluxDevice device) {
        if (device instanceof PhantomFluxDevice) {
            throw new IllegalArgumentException();
        }
        this.networkID = device.getNetworkID();
        this.priority = device.getRawPriority();
        this.playerUUID = device.getConnectionOwner();
        this.connectionType = device.getDeviceType();
        this.limit = device.getRawLimit();
        this.globalPos = device.getGlobalPos();
        this.customName = device.getCustomName();
        this.surgeMode = device.getSurgeMode();
        this.disableLimit = device.getDisableLimit();
        this.chunkLoaded = device.isChunkLoaded();
        this.buffer = device.getTransferHandler().getBuffer();
        this.change = device.getTransferHandler().getChange();
        this.forcedLoading = device.isForcedLoading();
        this.stack = device.getDisplayStack();
    }

    public PhantomFluxDevice(@Nonnull GlobalPos globalPos, CompoundNBT tag) {
        this.globalPos = globalPos;
        readExcludePos(tag);
    }

    /*public static CompoundNBT writeCustomNBT(IFluxDevice tile, CompoundNBT tag) {
        FluxUtils.writeGlobalPos(tag, tile.getGlobalPos());
        tag.putInt("type", tile.getDeviceType().ordinal());
        tag.putInt("n_id", tile.getNetworkID());
        tag.putInt("priority", tile.getRawPriority());
        //tag.putInt("folder_id", tile.getFolderID());
        tag.putLong("limit", tile.getRawLimit());
        tag.putString("name", tile.getCustomName());
        tag.putBoolean("dLimit", tile.getDisableLimit());
        tag.putBoolean("surge", tile.getSurgeMode());
        tag.putBoolean("isChunkLoaded", tile.isChunkLoaded());
        tag.putLong("buffer", tile.getBuffer());
        tag.putLong("change", tile.getChange());
        tag.putBoolean("forcedChunk", tile.isForcedLoading());
        tile.getDisplayStack().write(tag);
        return tag;
    }*/

    @Override
    public void writeCustomNBT(CompoundNBT tag, int type) {
        FluxUtils.writeGlobalPos(tag, globalPos);
        tag.putInt("type", connectionType.ordinal());
        tag.putInt("n_id", networkID);
        tag.putInt("priority", priority);
        //tag.putInt("folder_id", folderID);
        tag.putLong("limit", limit);
        tag.putString("name", customName);
        tag.putBoolean("dLimit", disableLimit);
        tag.putBoolean("surge", surgeMode);
        tag.putBoolean("chunkLoaded", chunkLoaded);
        tag.putLong("buffer", buffer);
        tag.putLong("change", change);
        tag.putBoolean("forcedChunk", forcedLoading);
        stack.write(tag);
    }

    @Override
    public void readCustomNBT(CompoundNBT tag, int type) {
        globalPos = FluxUtils.readGlobalPos(tag);
        readExcludePos(tag);
    }

    private void readExcludePos(@Nonnull CompoundNBT tag) {
        connectionType = FluxDeviceType.values()[tag.getInt("type")];
        networkID = tag.getInt("n_id");
        priority = tag.getInt("priority");
        //folderID = tag.getInt("folder_id");
        limit = tag.getLong("limit");
        customName = tag.getString("name");
        disableLimit = tag.getBoolean("dLimit");
        surgeMode = tag.getBoolean("surge");
        chunkLoaded = tag.getBoolean("chunkLoaded");
        buffer = tag.getLong("buffer");
        change = tag.getLong("change");
        forcedLoading = tag.getBoolean("forcedChunk");
        stack = ItemStack.read(tag);
    }

    @Override
    public int getNetworkID() {
        return networkID;
    }

    @Override
    public int getLogicPriority() {
        throw new IllegalStateException("Client or unloaded device");
    }

    @Override
    public int getRawPriority() {
        return priority;
    }

    @Override
    public void setPriority(int priority) {
        throw new IllegalStateException("Client or unloaded device");
    }

    @Override
    public IFluxNetwork getNetwork() {
        throw new IllegalStateException("Client or unloaded device");
    }

    @Override
    public void onContainerOpened(PlayerEntity player) {
        throw new IllegalStateException("Client or unloaded device");
    }

    @Override
    public void onContainerClosed(PlayerEntity player) {
        throw new IllegalStateException("Client or unloaded device");
    }

    @Override
    public UUID getConnectionOwner() {
        return playerUUID;
    }

    @Override
    public void setConnectionOwner(UUID uuid) {
        throw new IllegalStateException("Client or unloaded device");
    }

    @Override
    public FluxDeviceType getDeviceType() {
        return connectionType;
    }

    @Override
    public boolean canPlayerAccess(PlayerEntity player) {
        throw new IllegalStateException("Client or unloaded device");
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
        throw new IllegalStateException("Client or unloaded device");
    }

    @Override
    public void onConnect(IFluxNetwork network) {
        throw new IllegalStateException("Client or unloaded device");
    }

    @Override
    public void onDisconnect() {
        throw new IllegalStateException("Client or unloaded device");
    }

    @Nonnull
    @Override
    public ITransferHandler getTransferHandler() {
        throw new IllegalStateException("Client or unloaded device");
    }

    @Nonnull
    @Override
    public World getFluxWorld() {
        throw new IllegalStateException("Client or unloaded device");
    }

    @Override
    public long getLogicLimit() {
        throw new IllegalStateException("Client or unloaded device");
    }

    @Override
    public long getRawLimit() {
        return limit;
    }

    @Override
    public void setTransferLimit(long limit) {
        throw new IllegalStateException("Client or unloaded device");
    }

    @Override
    public long getMaxTransferLimit() {
        throw new IllegalStateException("Client or unloaded device");
    }

    @Override
    public boolean isActive() {
        return false;
    }

    /*@Override
    public Coord4D getCoords() {
        return coord4D;
    }*/

    @Nonnull
    @Override
    public GlobalPos getGlobalPos() {
        return globalPos;
    }

    /*@Override
    public int getFolderID() {
        return folderID;
    }*/

    @Override
    public String getCustomName() {
        return customName;
    }

    @Override
    public void setCustomName(String customName) {
        throw new IllegalStateException("Client or unloaded device");
    }

    @Override
    public boolean getDisableLimit() {
        return disableLimit;
    }

    @Override
    public void setDisableLimit(boolean disableLimit) {
        throw new IllegalStateException("Client or unloaded device");
    }

    @Override
    public boolean getSurgeMode() {
        return surgeMode;
    }

    @Override
    public void setSurgeMode(boolean surgeMode) {
        throw new IllegalStateException("Client or unloaded device");
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
