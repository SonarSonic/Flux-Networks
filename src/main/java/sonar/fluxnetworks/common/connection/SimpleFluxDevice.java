package sonar.fluxnetworks.common.connection;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.world.World;
import sonar.fluxnetworks.api.device.IFluxDevice;
import sonar.fluxnetworks.api.misc.NBTType;
import sonar.fluxnetworks.api.network.FluxDeviceType;
import sonar.fluxnetworks.api.network.IFluxNetwork;
import sonar.fluxnetworks.api.network.ITransferHandler;
import sonar.fluxnetworks.common.misc.FluxUtils;

import javax.annotation.Nonnull;
import java.util.UUID;

/**
 * A POJO class holds values updated from server for GUI display (via Network Connections tab,
 * because these devices may not exist on client world so there's no TileFluxDevice instance on client,
 * they just are loaded on server world for other players),
 * or records unloaded flux devices on server
 */
public class SimpleFluxDevice implements IFluxDevice {

    public int networkID;
    public int priority;
    public UUID playerUUID;
    public FluxDeviceType connectionType;
    public long limit;
    public GlobalPos globalPos;
    //public int folderID;
    public String customName;
    public boolean surgeMode;
    public boolean disableLimit;
    public boolean isChunkLoaded;
    public boolean forcedLoading;
    public long buffer;
    public long change;
    public ItemStack stack;

    public SimpleFluxDevice(@Nonnull IFluxDevice device) {
        this.networkID = device.getNetworkID();
        this.priority = device.getRawPriority();
        this.playerUUID = device.getConnectionOwner();
        this.connectionType = device.getDeviceType();
        this.limit = device.getRawLimit();
        //this.coord4D = tile.getCoords();
        this.globalPos = device.getGlobalPos();
        //this.folderID = tile.getFolderID();
        this.customName = device.getCustomName();
        this.surgeMode = device.getSurgeMode();
        this.disableLimit = device.getDisableLimit();
        this.isChunkLoaded = device.isChunkLoaded();
        this.buffer = device.getTransferHandler().getBuffer();
        this.change = device.getTransferHandler().getChange();
        this.forcedLoading = device.isForcedLoading();
        this.stack = device.getDisplayStack();
    }

    public SimpleFluxDevice(CompoundNBT tag) {
        readCustomNBT(tag, NBTType.ALL_SAVE);
    }

    public static CompoundNBT writeCustomNBT(IFluxDevice tile, CompoundNBT tag) {
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
    }

    @Override
    public CompoundNBT writeCustomNBT(CompoundNBT tag, NBTType type) {
        FluxUtils.writeGlobalPos(tag, globalPos);
        tag.putInt("type", connectionType.ordinal());
        tag.putInt("n_id", networkID);
        tag.putInt("priority", priority);
        //tag.putInt("folder_id", folderID);
        tag.putLong("limit", limit);
        tag.putString("name", customName);
        tag.putBoolean("dLimit", disableLimit);
        tag.putBoolean("surge", surgeMode);
        tag.putBoolean("isChunkLoaded", isChunkLoaded);
        tag.putLong("buffer", buffer);
        tag.putLong("change", change);
        tag.putBoolean("forcedChunk", forcedLoading);
        stack.write(tag);
        return tag;
    }

    @Override
    public void readCustomNBT(CompoundNBT tag, NBTType type) {
        globalPos = FluxUtils.readGlobalPos(tag);
        connectionType = FluxDeviceType.values()[tag.getInt("type")];
        networkID = tag.getInt("n_id");
        priority = tag.getInt("priority");
        //folderID = tag.getInt("folder_id");
        limit = tag.getLong("limit");
        customName = tag.getString("name");
        disableLimit = tag.getBoolean("dLimit");
        surgeMode = tag.getBoolean("surge");
        isChunkLoaded = tag.getBoolean("isChunkLoaded");
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
        return priority;
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
        return FluxNetworkInvalid.INSTANCE;
    }

    @Override
    public void onContainerOpened(PlayerEntity player) {

    }

    @Override
    public void onContainerClosed(PlayerEntity player) {

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
        return false;
    }

    @Override
    public boolean isChunkLoaded() {
        return isChunkLoaded;
    }

    @Override
    public boolean isForcedLoading() {
        return forcedLoading;
    }

    @Override
    public void onConnect(IFluxNetwork network) {
    }

    @Override
    public void onDisconnect() {
    }

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
    public void setLimit(long limit) {
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
    public long getBuffer() {
        return buffer;
    }

    @Override
    public long getChange() {
        return change;
    }

    @Override
    public void setChunkLoaded(boolean chunkLoaded) {
        isChunkLoaded = chunkLoaded;
    }
}
