package fluxnetworks.common.connection;

import fluxnetworks.api.Coord4D;
import fluxnetworks.api.network.IFluxNetwork;
import fluxnetworks.api.network.ITransferHandler;
import fluxnetworks.api.tileentity.IFluxConnector;
import fluxnetworks.api.tileentity.ILiteConnector;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import java.util.UUID;

/**
 * For unloaded connectors.
 */
public class FluxLiteConnector implements ILiteConnector {

    public int networkID;
    public int priority;
    public UUID playerUUID;
    public IFluxConnector.ConnectionType connectionType;
    public long limit;
    public Coord4D coord4D;
    public int folderID;
    public String customName;
    public boolean surgeMode;
    public boolean disableLimit;
    public ItemStack stack;

    public FluxLiteConnector(IFluxConnector tile) {
        this.networkID = tile.getNetworkID();
        this.priority = tile.getPriority();
        this.playerUUID = tile.getConnectionOwner();
        this.connectionType = tile.getConnectionType();
        this.limit = tile.getCurrentLimit();
        this.coord4D = tile.getCoords();
        this.folderID = tile.getFolderID();
        this.customName = tile.getCustomName();
        this.surgeMode = tile.getSurgeMode();
        this.disableLimit = tile.getDisableLimit();
        this.stack = tile.getDisplayStack();
    }

    public FluxLiteConnector(int networkID, int priority, UUID playerUUID, IFluxConnector.ConnectionType connectionType, long limit, Coord4D coord4D, int folderID, String customName, boolean surgeMode, boolean disableLimit, ItemStack stack) {
        this.networkID = networkID;
        this.priority = priority;
        this.playerUUID = playerUUID;
        this.connectionType = connectionType;
        this.limit = limit;
        this.coord4D = coord4D;
        this.folderID = folderID;
        this.customName = customName;
        this.surgeMode = surgeMode;
        this.disableLimit = disableLimit;
        this.stack = stack;
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
        tag.setBoolean("dLimit", disableLimit);
        tag.setBoolean("surge", surgeMode);
        stack.writeToNBT(tag);
        return tag;
    }

    public void readNetworkData(NBTTagCompound tag) {
        coord4D = new Coord4D(tag);
        connectionType = IFluxConnector.ConnectionType.values()[tag.getInteger("type")];
        networkID = tag.getInteger("n_id");
        priority = tag.getInteger("priority");
        folderID = tag.getInteger("folder_id");
        limit = tag.getLong("limit");
        customName = tag.getString("name");
        disableLimit = tag.getBoolean("dLimit");
        surgeMode = tag.getBoolean("surge");
        stack = new ItemStack(tag);
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
    public IFluxNetwork getNetwork() {
        return FluxNetworkInvalid.instance;
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
    public boolean canAccess(EntityPlayer player) {
        return false;
    }

    @Override
    public boolean isChunkLoaded() {
        return false;
    }

    @Override
    public void connect(IFluxNetwork network) {

    }

    @Override
    public void disconnect(IFluxNetwork network) {

    }

    @Override
    public ITransferHandler getTransferHandler() {
        return null;
    }

    @Override
    public World getDimension() {
        return null;
    }

    @Override
    public long getCurrentLimit() {
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
}
