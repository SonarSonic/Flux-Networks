package sonar.fluxnetworks.common.tileentity;

import net.minecraft.block.BlockState;
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
import net.minecraft.util.math.GlobalPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.PacketDistributor;
import sonar.fluxnetworks.FluxConfig;
import sonar.fluxnetworks.api.device.IFluxConfigurable;
import sonar.fluxnetworks.api.device.IFluxDevice;
import sonar.fluxnetworks.api.device.ITilePacketBuffer;
import sonar.fluxnetworks.api.misc.FluxConstants;
import sonar.fluxnetworks.api.misc.NBTType;
import sonar.fluxnetworks.api.network.FluxLogicType;
import sonar.fluxnetworks.api.network.IFluxNetwork;
import sonar.fluxnetworks.api.network.NetworkFolder;
import sonar.fluxnetworks.common.connection.FluxNetworkCache;
import sonar.fluxnetworks.common.connection.FluxNetworkInvalid;
import sonar.fluxnetworks.common.connection.FluxNetworkServer;
import sonar.fluxnetworks.common.handler.PacketHandler;
import sonar.fluxnetworks.common.item.ItemFluxDevice;
import sonar.fluxnetworks.common.misc.ContainerConnector;
import sonar.fluxnetworks.common.misc.FluxUtils;
import sonar.fluxnetworks.common.network.TilePacketBufferPacket;
import sonar.fluxnetworks.common.storage.FluxChunkManager;
import sonar.fluxnetworks.common.storage.FluxNetworkData;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.UUID;

import static sonar.fluxnetworks.common.network.TilePacketBufferConstants.*;

@SuppressWarnings("ConstantConditions")
public abstract class TileFluxDevice extends TileEntity implements IFluxDevice,
        IFluxConfigurable, ITickableTileEntity, ITilePacketBuffer, INamedContainerProvider {

    public HashSet<PlayerEntity> playerUsing = new HashSet<>();

    public String customName = "";
    public UUID playerUUID = FluxConstants.DEFAULT_UUID;

    private int networkID;

    public int color;
    //public int folderID  = -1;

    public int priority = 0;
    public long limit = FluxConfig.defaultLimit;

    public boolean surgeMode = false;
    public boolean disableLimit = false;

    public boolean connected = false;
    public byte connections = 0;

    public boolean chunkLoading = false;

    private GlobalPos globalPos;

    protected IFluxNetwork network = FluxNetworkInvalid.INSTANCE;

    protected boolean load = false;

    //// PACKET FLAGS \\\\
    public boolean settings_changed;

    public TileFluxDevice(TileEntityType<?> tileEntityTypeIn) {
        super(tileEntityTypeIn);
    }

    @Override
    public void remove() {
        super.remove();
        if (!world.isRemote && load) {
            network.enqueueConnectionRemoval(this, false);
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
            network.enqueueConnectionRemoval(this, true);
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
                if (networkID > 0) {
                    IFluxNetwork network = FluxNetworkData.getNetwork(networkID);
                    if (network.isValid() && !(getDeviceType().isController() &&
                            network.getConnections(FluxLogicType.CONTROLLER).size() > 0)) {
                        network.enqueueConnectionAddition(this);
                    }
                } else {
                    networkID = FluxConstants.INVALID_NETWORK_ID;
                    connected = false;
                    color = FluxConstants.INVALID_NETWORK_COLOR;
                }
                updateTransfers(Direction.values());
                sendFullUpdatePacket();
                load = true;
            }
        }
    }

    @Override
    public void onConnect(@Nonnull IFluxNetwork network) {
        this.network = network;
        this.networkID = network.getNetworkID();
        this.color = network.getNetworkColor();
        connected = true;
        sendFullUpdatePacket();
    }

    @Override
    public void onDisconnect() {
        if (network.isValid()) {
            network = FluxNetworkInvalid.INSTANCE;
            networkID = FluxConstants.INVALID_NETWORK_ID;
            color = FluxConstants.INVALID_NETWORK_COLOR;
            connected = false;
            sendFullUpdatePacket();
        }
    }

    @Override
    public int getNetworkID() {
        return networkID;
    }

    @Override
    public IFluxNetwork getNetwork() {
        return network;
    }

    @Nonnull
    @Override
    public final SUpdateTileEntityPacket getUpdatePacket() {
        // Server side, write block update data
        return new SUpdateTileEntityPacket(pos, -1, writeCustomNBT(new CompoundNBT(), NBTType.TILE_UPDATE));
    }

    @Override
    public final void onDataPacket(NetworkManager net, @Nonnull SUpdateTileEntityPacket pkt) {
        // Client side, read block update data
        readCustomNBT(pkt.getNbtCompound(), NBTType.TILE_UPDATE);
        world.notifyBlockUpdate(pos, getBlockState(), getBlockState(), 3);
    }

    @Nonnull
    @Override
    public final CompoundNBT getUpdateTag() {
        // Server side, write NBT when updating chunk data
        return write(new CompoundNBT());
    }

    @Override
    public final void handleUpdateTag(BlockState state, CompoundNBT tag) {
        // Client side, read NBT when updating chunk data
        read(state, tag);
    }

    @Nonnull
    @Override
    public final World getFluxWorld() {
        // Access world with interface
        return world;
    }

    @Nonnull
    @Override
    public final CompoundNBT write(CompoundNBT compound) {
        super.write(compound);
        writeCustomNBT(compound, NBTType.ALL_SAVE);
        return compound;
    }

    @Override
    public final void read(BlockState state, CompoundNBT compound) {
        super.read(state, compound);
        readCustomNBT(compound, NBTType.ALL_SAVE);
    }

    public CompoundNBT writeCustomNBT(CompoundNBT tag, NBTType type) {
        if (type == NBTType.ALL_SAVE || type == NBTType.TILE_UPDATE) {
            tag.putInt("0", priority);
            tag.putLong("1", limit);
            tag.putBoolean("2", disableLimit);
            tag.putBoolean("3", surgeMode);
            tag.putInt("4", getNetworkID());
            tag.putUniqueId("5", playerUUID);
            tag.putString("6", customName);
            tag.putInt("7", color);
            tag.putBoolean("8", connected);
            //tag.putInt("9", folderID);
            tag.putByte("a", connections);
            tag.putLong("b", getTransferHandler().getBuffer());
            tag.putBoolean("c", chunkLoading);
        }
        if (type == NBTType.TILE_UPDATE) {
            getTransferHandler().writeNetworkedNBT(tag);
        }
        if (type == NBTType.TILE_DROP) {
            tag.putLong("buffer", getTransferHandler().getBuffer());
            tag.putInt(ItemFluxDevice.PRIORITY, priority);
            tag.putLong(ItemFluxDevice.LIMIT, limit);
            tag.putBoolean(ItemFluxDevice.DISABLE_LIMIT, disableLimit);
            tag.putBoolean(ItemFluxDevice.SURGE_MODE, surgeMode);
            tag.putInt(FluxNetworkData.NETWORK_ID, getNetworkID());
            tag.putString(ItemFluxDevice.CUSTOM_NAME, customName);
            //tag.putInt(NetworkFolder.FOLDER_ID, folderID);
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
            //folderID = tag.getInt("9");
            connections = tag.getByte("a");
            getTransferHandler().setBuffer(tag.getLong("b"));
            chunkLoading = tag.getBoolean("c");
        }
        if (type == NBTType.TILE_UPDATE) {
            getTransferHandler().readNetworkedNBT(tag);
        }
        if (type == NBTType.TILE_DROP) {
            long l;
            if ((l = tag.getLong("buffer")) > 0) {
                getTransferHandler().setBuffer(l);
            }
            priority = tag.getInt(ItemFluxDevice.PRIORITY);
            limit = (l = tag.getLong(ItemFluxDevice.LIMIT)) > 0 ? l : limit;
            disableLimit = tag.getBoolean(ItemFluxDevice.DISABLE_LIMIT);
            surgeMode = tag.getBoolean(ItemFluxDevice.SURGE_MODE);
            int i;
            networkID = (i = tag.getInt(FluxNetworkData.NETWORK_ID)) > 0 ? i : networkID;
            String name;
            customName = (name = tag.getString(ItemFluxDevice.CUSTOM_NAME)).isEmpty() ? customName : name;
            //folderID = tag.getInt(NetworkFolder.FOLDER_ID);
        }
    }

    public boolean canPlayerAccess(PlayerEntity player) {
        if (network.isValid()) {
            if (PlayerEntity.getUUID(player.getGameProfile()).equals(playerUUID)) {
                return true;
            }
            return network.getPlayerAccess(player).canAccess();
        }
        return true;
    }

    //// PACKETS\\\\

    /**
     * sends a block update
     */
    public void sendFullUpdatePacket() {
        if (!world.isRemote) {
            world.notifyBlockUpdate(pos, getBlockState(), getBlockState(), 1 | 2);
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
            fluxNetworkServer.sortConnections = true;
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
    public int getLogicPriority() {
        return surgeMode ? Integer.MAX_VALUE : priority;
    }

    public void onContainerOpened(PlayerEntity player) {
        if (!world.isRemote) {
            playerUsing.add(player);
            sendFullUpdatePacket();
        }
    }

    public void onContainerClosed(PlayerEntity player) {
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
    public long getLogicLimit() {
        return disableLimit ? Long.MAX_VALUE : limit;
    }

    @Override
    public int getRawPriority() {
        return priority;
    }

    @Override
    public long getRawLimit() {
        return limit;
    }

    /*@Override
    public int getFolderID() {
        return folderID;
    }*/

    /*public Coord4D coord4D;

    @Override
    public Coord4D getCoords() {
        if (coord4D == null)
            coord4D = new Coord4D(this);
        return coord4D;
    }*/

    @Nonnull
    @Override
    public final GlobalPos getGlobalPos() {
        if (globalPos == null)
            globalPos = FluxUtils.getGlobalPos(this);
        return globalPos;
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
        return new StringTextComponent("");
    }

    @Nullable
    public Container createMenu(int windowID, @Nonnull PlayerInventory playerInventory, @Nonnull PlayerEntity entity) {
        return new ContainerConnector<>(windowID, playerInventory, this);
    }
}
