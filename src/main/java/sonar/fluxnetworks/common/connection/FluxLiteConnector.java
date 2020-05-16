package sonar.fluxnetworks.common.connection;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import sonar.fluxnetworks.api.network.EnumConnectionType;
import sonar.fluxnetworks.api.network.ITransferHandler;
import sonar.fluxnetworks.api.utils.Coord4D;
import sonar.fluxnetworks.api.network.IFluxNetwork;
import sonar.fluxnetworks.api.tiles.IFluxConnector;
import sonar.fluxnetworks.api.utils.NBTType;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import java.util.UUID;

public class FluxLiteConnector implements IFluxConnector {

    public int networkID;
    public int priority;
    public UUID playerUUID;
    public EnumConnectionType connectionType;
    public long limit;
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

    public FluxLiteConnector(IFluxConnector tile) {
        this.networkID = tile.getNetworkID();
        this.priority = tile.getActualPriority();
        this.playerUUID = tile.getConnectionOwner();
        this.connectionType = tile.getConnectionType();
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

    public FluxLiteConnector(CompoundNBT tag) {
        readCustomNBT(tag, NBTType.ALL_SAVE);
    }

    public static CompoundNBT writeCustomNBT(IFluxConnector tile, CompoundNBT tag) {
        tile.getCoords().write(tag);
        tag.putInt("type", tile.getConnectionType().ordinal());
        tag.putInt("n_id", tile.getNetworkID());
        tag.putInt("priority", tile.getActualPriority());
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
        connectionType = EnumConnectionType.values()[tag.getInt("type")];
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
    public int getPriority() {
        return priority;
    }

    @Override
    public int getActualPriority() {
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
    public EnumConnectionType getConnectionType() {
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

    @Override
    public World getWorld0() {
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
