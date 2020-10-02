package sonar.fluxnetworks.common.connection;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.GlobalPos;
import sonar.fluxnetworks.api.network.FluxDeviceType;
import sonar.fluxnetworks.api.network.ITransferHandler;
import sonar.fluxnetworks.api.device.IFluxDevice;
import sonar.fluxnetworks.api.misc.Coord4D;
import sonar.fluxnetworks.api.network.IFluxNetwork;
import sonar.fluxnetworks.api.misc.NBTType;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.UUID;

/**
 * A POJO class holds values updated from server for GUI display on client,
 * or defines unloaded flux devices on both sides
 */
public class SimpleFluxDevice implements IFluxDevice {

    public int networkID;
    public int priority;
    public UUID           playerUUID;
    public FluxDeviceType connectionType;
    public long           limit;
    public Coord4D coord4D;
    public int folderID;
    public String customName;
    public boolean surgeMode;
    public boolean disableLimit;
    public boolean isChunkLoaded;
    public boolean chunkLoading;
    public long buffer;
    public long change;
    public ItemStack stack;

    public SimpleFluxDevice(IFluxDevice tile) {
        this.networkID = tile.getNetworkID();
        this.priority = tile.getRawPriority();
        this.playerUUID = tile.getConnectionOwner();
        this.connectionType = tile.getDeviceType();
        this.limit = tile.getActualLimit();
        this.coord4D = tile.getCoords();
        this.folderID = tile.getFolderID();
        this.customName = tile.getCustomName();
        this.surgeMode = tile.getSurgeMode();
        this.disableLimit = tile.getDisableLimit();
        this.isChunkLoaded = tile.isChunkLoaded();
        this.buffer = tile.getTransferHandler().getBuffer();
        this.change = tile.getTransferHandler().getChange();
        this.chunkLoading = tile.isForcedLoading();
        this.stack = tile.getDisplayStack();
    }

    public SimpleFluxDevice(CompoundNBT tag) {
        readCustomNBT(tag, NBTType.ALL_SAVE);
    }

    public static CompoundNBT writeCustomNBT(IFluxDevice tile, CompoundNBT tag) {
        tile.getCoords().write(tag);
        tag.putInt("type", tile.getDeviceType().ordinal());
        tag.putInt("n_id", tile.getNetworkID());
        tag.putInt("priority", tile.getRawPriority());
        tag.putInt("folder_id", tile.getFolderID());
        tag.putLong("limit", tile.getActualLimit());
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
        coord4D.write(tag);
        tag.putInt("type", connectionType.ordinal());
        tag.putInt("n_id", networkID);
        tag.putInt("priority", priority);
        tag.putInt("folder_id", folderID);
        tag.putLong("limit", limit);
        tag.putString("name", customName);
        tag.putBoolean("dLimit", disableLimit);
        tag.putBoolean("surge", surgeMode);
        tag.putBoolean("isChunkLoaded", isChunkLoaded);
        tag.putLong("buffer", buffer);
        tag.putLong("change", change);
        tag.putBoolean("forcedChunk", chunkLoading);
        stack.write(tag);
        return tag;
    }

    @Override
    public void readCustomNBT(CompoundNBT tag, NBTType type) {
        coord4D = new Coord4D(tag);
        connectionType = FluxDeviceType.values()[tag.getInt("type")];
        networkID = tag.getInt("n_id");
        priority = tag.getInt("priority");
        folderID = tag.getInt("folder_id");
        limit = tag.getLong("limit");
        customName = tag.getString("name");
        disableLimit = tag.getBoolean("dLimit");
        surgeMode = tag.getBoolean("surge");
        isChunkLoaded = tag.getBoolean("isChunkLoaded");
        buffer = tag.getLong("buffer");
        change = tag.getLong("change");
        chunkLoading = tag.getBoolean("forcedChunk");
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
    public IFluxNetwork getNetwork() {
        return FluxNetworkInvalid.INSTANCE;
    }

    @Override
    public void open(PlayerEntity player) {}

    @Override
    public void close(PlayerEntity player) {}

    @Override
    public UUID getConnectionOwner() {
        return playerUUID;
    }

    @Override
    public FluxDeviceType getDeviceType() {
        return connectionType;
    }

    @Override
    public boolean canAccess(PlayerEntity player) {
        return false;
    }

    @Override
    public boolean isChunkLoaded() {
        return isChunkLoaded;
    }

    @Override
    public boolean isForcedLoading() {
        return chunkLoading;
    }

    @Override
    public void connect(IFluxNetwork network) {}

    @Override
    public void disconnect(IFluxNetwork network) {}

    @Override
    public ITransferHandler getTransferHandler() {
        return null;
    }

    @Nonnull
    @Override
    public World getFluxWorld() {
        return null;
    }

    @Override
    public long getCurrentLimit() {
        return limit;
    }

    @Override
    public long getActualLimit() {
        return limit;
    }

    @Override
    public boolean isActive() {
        return false;
    }

    @Override
    public Coord4D getCoords() {
        return coord4D;
    }

    @Nonnull
    @Override
    public GlobalPos getGlobalPos() {
        return null;
    }

    @Override
    public int getFolderID() {
        return folderID;
    }

    @Override
    public String getCustomName() {
        return customName;
    }

    @Override
    public boolean getDisableLimit() {
        return disableLimit;
    }

    @Override
    public boolean getSurgeMode() {
        return surgeMode;
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
