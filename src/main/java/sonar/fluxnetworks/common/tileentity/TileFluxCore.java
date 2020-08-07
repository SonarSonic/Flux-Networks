package sonar.fluxnetworks.common.tileentity;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.PacketDistributor;
import sonar.fluxnetworks.FluxConfig;
import sonar.fluxnetworks.api.network.IFluxNetwork;
import sonar.fluxnetworks.api.network.NetworkFolder;
import sonar.fluxnetworks.api.network.NetworkSettings;
import sonar.fluxnetworks.api.tiles.IFluxConfigurable;
import sonar.fluxnetworks.api.tiles.IFluxConnector;
import sonar.fluxnetworks.api.tiles.ITilePacketBuffer;
import sonar.fluxnetworks.api.utils.Coord4D;
import sonar.fluxnetworks.api.utils.NBTType;
import sonar.fluxnetworks.common.connection.FluxNetworkInvalid;
import sonar.fluxnetworks.common.connection.FluxNetworkServer;
import sonar.fluxnetworks.common.connection.handler.AbstractTransferHandler;
import sonar.fluxnetworks.common.core.ContainerConnector;
import sonar.fluxnetworks.common.core.FluxUtils;
import sonar.fluxnetworks.common.data.FluxChunkManager;
import sonar.fluxnetworks.common.data.FluxNetworkData;
import sonar.fluxnetworks.common.handler.PacketHandler;
import sonar.fluxnetworks.common.item.FluxConnectorBlockItem;
import sonar.fluxnetworks.common.network.TilePacketBufferPacket;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.UUID;

import static sonar.fluxnetworks.common.network.TilePacketBufferConstants.*;

@SuppressWarnings("ConstantConditions")
public abstract class TileFluxCore extends TileEntity implements IFluxConnector, IFluxConfigurable, ITickableTileEntity, ITilePacketBuffer, INamedContainerProvider {

    public HashSet<PlayerEntity> playerUsing = new HashSet<>();

    public String customName = "";
    public int    networkID  = -1;
    public UUID   playerUUID = FluxUtils.UUID_DEFAULT;
    public int    color      = -1;
    public int    folderID   = -1;

    public int  priority = 0;
    public long limit    = FluxConfig.defaultLimit;

    public boolean surgeMode    = false;
    public boolean disableLimit = false;

    public boolean connected   = false;
    public byte[]  connections = new byte[]{0, 0, 0, 0, 0, 0};

    public boolean chunkLoading = false;

    protected IFluxNetwork network = FluxNetworkInvalid.INSTANCE;

    protected boolean load = false;

    //// PACKET FLAGS \\\\
    public boolean settings_changed;

    public TileFluxCore(TileEntityType<?> tileEntityTypeIn) {
        super(tileEntityTypeIn);
    }

    @Override
    public void remove() {
        super.remove();
        if (!world.isRemote && load) {
            FluxUtils.removeConnection(this, false);
            if (chunkLoading) {
                FluxChunkManager.removeChunkLoader((ServerWorld) world, new ChunkPos(pos));
            }
            load = false;
        }
    }

    @Override
    public void onChunkUnloaded() {
        super.onChunkUnloaded();
        if (!world.isRemote && load) {
            FluxUtils.removeConnection(this, true);
            load = false;
        }
    }

    @Override
    public void tick() {
        if (!world.isRemote) {
            if (playerUsing.size() > 0) {
                sendTilePacketToUsing(FLUX_GUI_SYNC);
                settings_changed = false;
            }
            if (!load) {
                if (!FluxUtils.addConnection(this)) {
                    networkID = -1;
                    connected = false;
                    color = 0xb2b2b2;
                }
                updateTransfers(Direction.values());
                sendFullUpdatePacket();
                load = true;
            }
        }
    }

    @Override
    public void connect(@Nonnull IFluxNetwork network) {
        this.network = network;
        this.networkID = network.getNetworkID();
        this.color = network.getSetting(NetworkSettings.NETWORK_COLOR);
        connected = true;
        sendFullUpdatePacket();
    }

    @Override
    public void disconnect(@Nonnull IFluxNetwork network) {
        if (network.getNetworkID() == getNetworkID()) {
            this.network = FluxNetworkInvalid.INSTANCE;
            networkID = -1;
            color = 0xb2b2b2;
            connected = false;
            sendFullUpdatePacket();
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

    @Override
    public World getFluxWorld() {
        return world;
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
        if (type == NBTType.ALL_SAVE || type == NBTType.TILE_UPDATE) {
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
            for (int i = 0; i < connections.length; i++) {
                tag.putByte('c' + String.valueOf(i), connections[i]);
            }
            tag.putLong("buf", getTransferHandler().getBuffer());
            tag.putBoolean("l", chunkLoading);
        }
        if (type == NBTType.TILE_UPDATE) {
            getTransferHandler().writeNetworkedNBT(tag);
        }
        if (type == NBTType.TILE_DROP) {
            tag.putLong("buffer", getTransferHandler().getBuffer());
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
        if (type == NBTType.ALL_SAVE || type == NBTType.TILE_UPDATE) {
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
            for (int i = 0; i < connections.length; i++) {
                connections[i] = tag.getByte('c' + String.valueOf(i));
            }
            ((AbstractTransferHandler<?>) getTransferHandler()).buffer = tag.getLong("buf");
            chunkLoading = tag.getBoolean("l");
        }
        if (type == NBTType.TILE_UPDATE) {
            getTransferHandler().readNetworkedNBT(tag);
        }
        if (type == NBTType.TILE_DROP) {
            long k;
            ((AbstractTransferHandler<?>) getTransferHandler()).buffer = (k = tag.getLong("buffer")) > 0 ? k : ((AbstractTransferHandler<?>) getTransferHandler()).buffer;
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
        if (!network.isInvalid()) {
            if (PlayerEntity.getUUID(player.getGameProfile()).equals(playerUUID)) {
                return true;
            }
            return network.getMemberPermission(player).canAccess();
        }
        return true;
    }

    //// PACKETS\\\\

    /**
     * sends a block update
     */
    public void sendFullUpdatePacket() {
        if (!world.isRemote) {
            world.notifyBlockUpdate(pos, getBlockState(), getBlockState(), 3);
        }
    }

    /**
     * for all simple GUI updates
     */
    public void sendTilePacketToUsing(byte packetID) {
        if (!world.isRemote) {
            for (PlayerEntity playerEntity : playerUsing) {
                PacketHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) playerEntity), new TilePacketBufferPacket(this, pos, packetID));
            }
        }
    }

    /**
     * for any visual updates - colour / energy storage
     */
    public void sendTilePacketToNearby(byte packetID) {
        if (!world.isRemote) {
            PacketHandler.CHANNEL.send(PacketDistributor.TRACKING_CHUNK.with(() -> world.getChunkAt(pos)), new TilePacketBufferPacket(this, pos, packetID));
        }
    }


    public void sendTilePacketToServer(byte packetID) {
        PacketHandler.CHANNEL.sendToServer(new TilePacketBufferPacket(this, pos, packetID));
    }

    @Override
    public void writePacket(PacketBuffer buf, byte id) {
        switch (id) {
            case FLUX_CUSTOM_NAME:
                buf.writeString(customName, 256);
                break;
            case FLUX_PRIORITY:
                buf.writeInt(priority);
                break;
            case FLUX_LIMIT:
                buf.writeLong(limit);
                break;
            case FLUX_SURGE_MODE:
                buf.writeBoolean(surgeMode);
                break;
            case FLUX_DISABLE_LIMIT:
                buf.writeBoolean(disableLimit);
                break;
            case FLUX_GUI_SYNC:
                buf.writeBoolean(settings_changed);
                if (settings_changed) {
                    buf.writeString(customName, 256);
                    buf.writeInt(priority);
                    buf.writeLong(limit);
                    buf.writeBoolean(surgeMode);
                    buf.writeBoolean(disableLimit);
                    buf.writeBoolean(chunkLoading);
                }
                buf.writeCompoundTag(getTransferHandler().writeNetworkedNBT(new CompoundNBT()));
                break;
        }
    }

    @Override
    public void readPacket(PacketBuffer buf, byte id) {
        switch (id) {
            case FLUX_CUSTOM_NAME:
                customName = buf.readString(256);
                markLiteSettingChanged();
                break;
            case FLUX_PRIORITY:
                priority = buf.readInt();
                sortNetworkConnections();
                break;
            case FLUX_LIMIT:
                limit = buf.readLong();
                markLiteSettingChanged();
                break;
            case FLUX_SURGE_MODE:
                surgeMode = buf.readBoolean();
                sortNetworkConnections();
                break;
            case FLUX_DISABLE_LIMIT:
                disableLimit = buf.readBoolean();
                markLiteSettingChanged();
                break;
            case FLUX_GUI_SYNC:
                if (buf.readBoolean()) {
                    customName = buf.readString(256);
                    priority = buf.readInt();
                    limit = buf.readLong();
                    surgeMode = buf.readBoolean();
                    disableLimit = buf.readBoolean();
                    chunkLoading = buf.readBoolean();
                    settings_changed = true;
                    ///NEED TO SEND TO GUI?
                }
                getTransferHandler().readNetworkedNBT(buf.readCompoundTag());
                break;
        }
    }

    protected void sortNetworkConnections() {
        if (network instanceof FluxNetworkServer) {
            FluxNetworkServer fluxNetworkServer = (FluxNetworkServer) network;
            fluxNetworkServer.needSortConnections = true;
            markLiteSettingChanged();
        }
    }

    protected void markLiteSettingChanged() {
        if (network instanceof FluxNetworkServer) {
            FluxNetworkServer fluxNetworkServer = (FluxNetworkServer) network;
            fluxNetworkServer.markLiteSettingChanged(this);
            settings_changed = true;
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
        if (!world.isRemote) {
            playerUsing.add(player);
            sendFullUpdatePacket();
        }
    }

    public void close(PlayerEntity player) {
        if (!world.isRemote) {
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
        if (coord4D == null)
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

    @Nonnull
    @Override
    public ITextComponent getDisplayName() {
        return new StringTextComponent(getCustomName());
    }

    @Nullable
    public Container createMenu(int windowID, @Nonnull PlayerInventory playerInventory, @Nonnull PlayerEntity entity) {
        return new ContainerConnector<>(windowID, playerInventory, this);
    }
}
