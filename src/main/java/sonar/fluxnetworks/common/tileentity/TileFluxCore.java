package sonar.fluxnetworks.common.tileentity;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import sonar.fluxnetworks.FluxConfig;
import sonar.fluxnetworks.api.network.NetworkFolder;
import sonar.fluxnetworks.api.network.NetworkSettings;
import sonar.fluxnetworks.api.utils.Coord4D;
import sonar.fluxnetworks.api.network.IFluxNetwork;
import sonar.fluxnetworks.api.tiles.IFluxConfigurable;
import sonar.fluxnetworks.api.tiles.ITileByteBuf;
import sonar.fluxnetworks.api.tiles.IFluxConnector;
import sonar.fluxnetworks.api.utils.NBTType;
import sonar.fluxnetworks.common.block.FluxConnectorBlock;
import sonar.fluxnetworks.common.core.ContainerCore;
import sonar.fluxnetworks.common.data.FluxChunkManager;
import sonar.fluxnetworks.common.data.FluxNetworkData;
import sonar.fluxnetworks.common.item.FluxConnectorBlockItem;
import sonar.fluxnetworks.common.core.FluxUtils;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import sonar.fluxnetworks.common.connection.FluxNetworkInvalid;
import sonar.fluxnetworks.common.connection.FluxNetworkServer;
import sonar.fluxnetworks.common.connection.FluxTransferHandler;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.UUID;

public abstract class TileFluxCore extends TileEntity implements IFluxConnector, IFluxConfigurable, ITickableTileEntity, ITileByteBuf, INamedContainerProvider {

    public HashSet<PlayerEntity> playerUsing = new HashSet<>();

    public String customName = "";
    public int networkID = -1;
    public UUID playerUUID = FluxUtils.UUID_DEFAULT;
    public int color = -1;
    public int folderID = -1;

    public int priority = 0;
    public long limit = FluxConfig.defaultLimit;

    public boolean surgeMode = false;
    public boolean disableLimit = false;

    public boolean connected = false;
    public byte[] connections = new byte[]{0,0,0,0,0,0};

    public boolean chunkLoading = false;

    protected IFluxNetwork network = FluxNetworkInvalid.instance;

    protected boolean load = false;

    public TileFluxCore(TileEntityType<?> tileEntityTypeIn) {
        super(tileEntityTypeIn);
    }

    @Override
    public void remove() {
        super.remove();
        if(!world.isRemote && load) {
            FluxUtils.removeConnection(this, false);
            if(chunkLoading) {
                FluxChunkManager.releaseChunk(world, new ChunkPos(pos));
            }
            load = false;
        }
    }

    @Override
    public void onChunkUnloaded() {
        super.onChunkUnloaded();
        if(!world.isRemote && load) {
            FluxUtils.removeConnection(this, true);
            load = false;
        }
    }

    @Override
    public void tick() {
        if(!world.isRemote) {
            if(playerUsing.size() > 0) {
                //sendPackets();
            }
            if(!load) {
                if(!FluxUtils.addConnection(this)) {
                    networkID = -1;
                    connected = false;
                    color = 0xb2b2b2;
                }
                updateTransfers(Direction.values());
                sendPackets();
                load = true;
            }
        }
    }

    @Override
    public void connect(IFluxNetwork network) {
        this.network = network;
        this.networkID = network.getNetworkID();
        this.color = network.getSetting(NetworkSettings.NETWORK_COLOR);
        connected = true;
        sendPackets();
    }

    @Override
    public void disconnect(IFluxNetwork network) {
        if(network.getNetworkID() == getNetworkID()) {
            this.network = FluxNetworkInvalid.instance;
            this.networkID = -1;
            this.color = 0xb2b2b2;
            connected = false;
            sendPackets();
        }
    }

    @Override
    public IFluxNetwork getNetwork() {
        return network;
    }

    @Override
    public int getNetworkID() {
        return networkID;
    }

    @Nullable
    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        return new SUpdateTileEntityPacket(pos, -1, writeCustomNBT(new CompoundNBT(), NBTType.TILE_UPDATE));
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        readCustomNBT(pkt.getNbtCompound(), NBTType.TILE_UPDATE);
        world.notifyBlockUpdate(pos, getBlockState(), getBlockState(), 3);

    }

    @Override
    public CompoundNBT getUpdateTag() {
        return write(super.getUpdateTag());
    }

    @Override
    public void handleUpdateTag(CompoundNBT tag) {
        read(tag);
    }

    public void sendPackets() {
        if(!world.isRemote){
            world.notifyBlockUpdate(pos, getBlockState(), getBlockState(), 3);
        }
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        super.write(compound);
        writeCustomNBT(compound, NBTType.ALL_SAVE);
        return compound;
    }

    @Override
    public void read(CompoundNBT compound) {
        super.read(compound);
        readCustomNBT(compound, NBTType.ALL_SAVE);
    }

    public CompoundNBT writeCustomNBT(CompoundNBT tag, NBTType type) {
        if(type == NBTType.ALL_SAVE || type == NBTType.TILE_UPDATE) {
            tag.putInt("0", priority);
            tag.putLong("1", limit);
            tag.putBoolean("2", disableLimit);
            tag.putBoolean("3", surgeMode);
            tag.putInt("4", networkID);
            tag.putUniqueId("5", playerUUID);
            tag.putString("6", customName);
            tag.putInt("7", color);
            tag.putBoolean("8", connected);
            tag.putInt("9", folderID);
            for(int i = 0; i < connections.length; i++) {
                tag.putByte('c' + String.valueOf(i), connections[i]);
            }
            tag.putLong("buf", ((FluxTransferHandler) getTransferHandler()).buffer);
            tag.putBoolean("l", chunkLoading);
        }
        if(type == NBTType.TILE_UPDATE) {
            getTransferHandler().writeNetworkedNBT(tag);
        }
        if(type == NBTType.TILE_DROP) {
            tag.putLong("buffer", ((FluxTransferHandler) getTransferHandler()).buffer);
            tag.putInt(FluxConnectorBlockItem.PRIORITY, priority);
            tag.putLong(FluxConnectorBlockItem.LIMIT, limit);
            tag.putBoolean(FluxConnectorBlockItem.DISABLE_LIMIT, disableLimit);
            tag.putBoolean(FluxConnectorBlockItem.SURGE_MODE, surgeMode);
            tag.putInt(FluxNetworkData.NETWORK_ID, networkID);
            tag.putString(FluxConnectorBlockItem.CUSTOM_NAME, customName);
            tag.putInt(NetworkFolder.FOLDER_ID, folderID);
        }

        return tag;
    }

    public void readCustomNBT(CompoundNBT tag, NBTType type) {
        if(type == NBTType.ALL_SAVE || type == NBTType.TILE_UPDATE) {
            priority = tag.getInt("0");
            limit = tag.getLong("1");
            disableLimit = tag.getBoolean("2");
            surgeMode = tag.getBoolean("3");
            networkID = tag.getInt("4");
            playerUUID = tag.getUniqueId("5");
            customName = tag.getString("6");
            color = tag.getInt("7");
            connected = tag.getBoolean("8");
            folderID = tag.getInt("9");
            for(int i = 0; i < connections.length; i++) {
                connections[i] = tag.getByte('c' + String.valueOf(i));
            }
            ((FluxTransferHandler) getTransferHandler()).buffer = tag.getLong("buf");
            chunkLoading = tag.getBoolean("l");
        }
        if(type == NBTType.TILE_UPDATE) {
            getTransferHandler().readNetworkedNBT(tag);
        }
        if(type == NBTType.TILE_DROP) {
            long k;
            ((FluxTransferHandler) getTransferHandler()).buffer = (k = tag.getLong("buffer")) > 0 ? k : ((FluxTransferHandler) getTransferHandler()).buffer;
            priority = tag.getInt(FluxConnectorBlockItem.PRIORITY);
            long l;
            limit = (l = tag.getLong(FluxConnectorBlockItem.LIMIT)) > 0 ? l : limit;
            disableLimit = tag.getBoolean(FluxConnectorBlockItem.DISABLE_LIMIT);
            surgeMode = tag.getBoolean(FluxConnectorBlockItem.SURGE_MODE);
            int i;
            networkID = (i = tag.getInt(FluxNetworkData.NETWORK_ID)) > 0 ? i : networkID;
            String name;
            customName = (name = tag.getString(FluxConnectorBlockItem.CUSTOM_NAME)).isEmpty() ? customName : name;
            folderID = tag.getInt(NetworkFolder.FOLDER_ID);
        }
    }

    public boolean canAccess(PlayerEntity player) {
        if(!network.isInvalid()) {
            if(PlayerEntity.getUUID(player.getGameProfile()).equals(playerUUID)) {
                return true;
            }
            return network.getMemberPermission(player).canAccess();
        }
        return true;
    }

    @Override
    public void writePacket(PacketBuffer buf, int id) {
        switch (id) {
            case 1:
                buf.writeString(customName);
                break;
            case 2:
                buf.writeInt(priority);
                break;
            case 3:
                buf.writeLong(limit);
                break;
            case 4:
                buf.writeBoolean(surgeMode);
                break;
            case 5:
                buf.writeBoolean(disableLimit);
                break;
        }
    }

    @Override
    public void readPacket(PacketBuffer buf, int id) {
        switch (id) {
            case 1:
                customName = buf.readString();
                markLiteSettingChanged();
                break;
            case 2:
                priority = buf.readInt();
                sortNetworkConnections();
                break;
            case 3:
                limit = buf.readLong();
                markLiteSettingChanged();
                break;
            case 4:
                surgeMode = buf.readBoolean();
                sortNetworkConnections();
                break;
            case 5:
                disableLimit = buf.readBoolean();
                markLiteSettingChanged();
                break;
        }
    }

    protected void sortNetworkConnections() {
        if (network instanceof FluxNetworkServer) {
            FluxNetworkServer fluxNetworkServer = (FluxNetworkServer) network;
            fluxNetworkServer.sortConnections = true;
            markLiteSettingChanged();
        }
    }

    protected void markLiteSettingChanged() {
        if (network instanceof FluxNetworkServer) {
            FluxNetworkServer fluxNetworkServer = (FluxNetworkServer) network;
            fluxNetworkServer.markLiteSettingChanged(this);
        }
    }

    @Override
    public CompoundNBT copyConfiguration(CompoundNBT config) {
        return FluxUtils.copyConfiguration(this, config);
    }

    @Override
    public void pasteConfiguration(CompoundNBT config) {
        FluxUtils.pasteConfiguration(this, config);
    }

    @Override
    public World getDimension() {
        return world;
    }

    @Override
    public UUID getConnectionOwner() {
        return playerUUID;
    }

    public void updateTransfers(Direction... dirs) {
        getTransferHandler().updateTransfers(dirs);
    }

    @Override
    public int getPriority() {
        return surgeMode ? Integer.MAX_VALUE : priority;
    }

    public void open(PlayerEntity player) {
        if(!world.isRemote) {
            playerUsing.add(player);
            sendPackets();
        }
    }

    public void close(PlayerEntity player) {
        if(!world.isRemote) {
            playerUsing.remove(player);
        }
    }

    @Override
    public String getCustomName() {
        return customName;
    }

    @Override
    public boolean isChunkLoaded() {
        return !isRemoved();
    }

    @Override
    public boolean isActive() {
        return true;
    }

    @Override
    public boolean isForcedLoading() {
        return chunkLoading;
    }

    @Override
    public long getCurrentLimit() {
        return disableLimit ? Long.MAX_VALUE : limit;
    }

    @Override
    public int getActualPriority() {
        return priority;
    }

    @Override
    public long getActualLimit() {
        return limit;
    }

    @Override
    public int getFolderID() {
        return folderID;
    }

    public Coord4D coord4D;

    @Override
    public Coord4D getCoords() {
        if(coord4D == null)
            coord4D = new Coord4D(this);
        return coord4D;
    }

    @Override
    public boolean getDisableLimit() {
        return disableLimit;
    }

    @Override
    public boolean getSurgeMode() {
        return surgeMode;
    }

    /* TODO - FIX OPEN COMPUTERS INTEGRATION
    @Override
    public String[] getOCMethods() {
        return new String[]{"getNetworkInfo", "getCountInfo", "getEnergyInfo", "getFluxInfo"};
    }

    @Override
    public Object[] invokeMethods(String method, Arguments arguments) {
        switch (method) {
            case "getNetworkInfo": {
                Map<Object, Object> map = new HashMap<>();
                map.put("id", network.getNetworkID());
                map.put("name", network.getNetworkName());
                map.put("ownerUUID", network.getSetting(NetworkSettings.NETWORK_OWNER).toString());
                map.put("securityType", network.getSetting(NetworkSettings.NETWORK_SECURITY).name().toLowerCase());
                map.put("energyType", network.getSetting(NetworkSettings.NETWORK_ENERGY).getStorageSuffix());
                map.put("averageTick", network.getSetting(NetworkSettings.NETWORK_STATISTICS).average_tick_micro);
                return new Object[]{map};
            }
            case "getCountInfo": {
                Map<Object, Object> map = new HashMap<>();
                NetworkStatistics stats = network.getSetting(NetworkSettings.NETWORK_STATISTICS);
                map.put("plugCount", stats.fluxPlugCount);
                map.put("pointCount", stats.fluxPointCount);
                map.put("controllerCount", stats.fluxControllerCount);
                map.put("storageCount", stats.fluxStorageCount);
                return new Object[]{map};
            }
            case "getEnergyInfo": {
                Map<Object, Object> map = new HashMap<>();
                NetworkStatistics stats = network.getSetting(NetworkSettings.NETWORK_STATISTICS);
                map.put("energyInput", stats.energyInput);
                map.put("energyOutput", stats.energyOutput);
                map.put("totalBuffer", stats.totalBuffer);
                map.put("totalEnergy", stats.totalEnergy);
                return new Object[]{map};
            }
            case "getFluxInfo": {
                Map<Object, Object> map = new HashMap<>();
                map.put("customName", customName);
                map.put("priority", priority);
                map.put("transferLimit", limit);
                map.put("surgeMode", surgeMode);
                map.put("unlimited", disableLimit);
                map.put("buffer", getTransferHandler().getEnergyStored());
                return new Object[]{map};
            }
        }
        return new Object[0];
    }
    */

    @Override
    public ITextComponent getDisplayName() {
        return new StringTextComponent(getCustomName());
    }

    @Nullable
    public Container createMenu(int windowID, PlayerInventory playerInventory, PlayerEntity entity){
        return new ContainerCore(windowID, playerInventory, this);
    }
}
