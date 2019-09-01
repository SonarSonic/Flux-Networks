package fluxnetworks.common.connection;

import fluxnetworks.api.Coord4D;
import fluxnetworks.api.tileentity.IFluxConnector;
import fluxnetworks.api.tileentity.ILiteConnector;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import java.util.UUID;

/**
 * A lite connector for GUI display, also unloaded connector.
 */
public class FluxLiteConnector implements ILiteConnector {

    public int networkID;
    public int priority;
    public UUID playerUUID;
    public IFluxConnector.ConnectionType connectionType;
    public long limit;
    public Coord4D coord4D;
    public boolean isChunkLoaded;
    public int folderID;
    public String customName;
    public boolean surgeMode;
    public boolean disableLimit;
    public ItemStack stack;

    public boolean isDirty = true;

    public FluxLiteConnector(IFluxConnector tile) {
        this.networkID = tile.getNetworkID();
        this.priority = tile.getPriority();
        this.playerUUID = tile.getConnectionOwner();
        this.connectionType = tile.getConnectionType();
        this.limit = tile.getCurrentLimit();
        this.coord4D = tile.getCoords();
        this.isChunkLoaded = tile.isChunkLoaded();
        this.folderID = tile.getFolderID();
        this.customName = tile.getCustomName();
        this.surgeMode = tile.getSurgeMode();
        this.disableLimit = tile.getDisableLimit();
        this.stack = tile.getDisplayStack();
    }

    public FluxLiteConnector(int networkID, int priority, UUID playerUUID, IFluxConnector.ConnectionType connectionType, long limit, Coord4D coord4D, boolean isChunkLoaded, int folderID, String customName, boolean surgeMode, boolean disableLimit, ItemStack stack, boolean isDirty) {
        this.networkID = networkID;
        this.priority = priority;
        this.playerUUID = playerUUID;
        this.connectionType = connectionType;
        this.limit = limit;
        this.coord4D = coord4D;
        this.isChunkLoaded = isChunkLoaded;
        this.folderID = folderID;
        this.customName = customName;
        this.surgeMode = surgeMode;
        this.disableLimit = disableLimit;
        this.stack = stack;
        this.isDirty = isDirty;
    }

    public FluxLiteConnector(NBTTagCompound tag) {
        readNetworkData(tag);
    }

    public NBTTagCompound writeNetworkData(NBTTagCompound tag) {
        coord4D.write(tag);
        tag.setInteger("type", connectionType.ordinal());
        tag.setInteger("n_id", networkID);
        tag.setInteger("priority", priority);
        tag.setInteger("folder_id", folderID);
        tag.setLong("limit", limit);
        tag.setString("name", customName);
        tag.setBoolean("isChunkLoaded", isChunkLoaded);
        tag.setBoolean("dLimit", disableLimit);
        tag.setBoolean("surge", surgeMode);
        stack.writeToNBT(tag);
        isDirty = false;
        return tag;
    }

    public void readNetworkData(NBTTagCompound tag) {
        coord4D.read(tag);
        connectionType = IFluxConnector.ConnectionType.values()[tag.getInteger("type")];
        networkID = tag.getInteger("n_id");
        priority = tag.getInteger("priority");
        folderID = tag.getInteger("folder_id");
        limit = tag.getLong("limit");
        customName = tag.getString("name");
        isChunkLoaded = tag.getBoolean("isChunkLoaded");
        disableLimit = tag.getBoolean("dLimit");
        surgeMode = tag.getBoolean("surge");
        stack = new ItemStack(tag);
        isDirty = false;
    }

    public void updateData(IFluxConnector tile) {
        this.networkID = tile.getNetworkID();
        this.priority = tile.getPriority();
        this.playerUUID = tile.getConnectionOwner();
        this.connectionType = tile.getConnectionType();
        this.limit = tile.getCurrentLimit();
        this.isChunkLoaded = tile.isChunkLoaded();
        this.folderID = tile.getFolderID();
        this.customName = tile.getCustomName();
        this.surgeMode = tile.getSurgeMode();
        this.disableLimit = tile.getDisableLimit();
        this.stack = tile.getDisplayStack();
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
    public UUID getConnectionOwner() {
        return playerUUID;
    }

    @Override
    public IFluxConnector.ConnectionType getConnectionType() {
        return connectionType;
    }

    @Override
    public boolean isChunkLoaded() {
        return isChunkLoaded;
    }

    @Override
    public long getCurrentLimit() {
        return limit;
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
    public boolean isDirty() {
        return isDirty;
    }

    @Override
    public void setChunkLoaded(boolean b) {
        isChunkLoaded = b;
    }

    @Override
    public ItemStack getDisplayStack() {
        return stack;
    }
}
