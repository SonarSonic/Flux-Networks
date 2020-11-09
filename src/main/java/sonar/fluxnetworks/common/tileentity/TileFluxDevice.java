package sonar.fluxnetworks.common.tileentity;

import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import mcjty.lib.api.power.IBigPower;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
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
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import sonar.fluxnetworks.FluxConfig;
import sonar.fluxnetworks.api.device.IFluxDevice;
import sonar.fluxnetworks.api.misc.FluxConstants;
import sonar.fluxnetworks.api.network.FluxLogicType;
import sonar.fluxnetworks.api.network.IFluxNetwork;
import sonar.fluxnetworks.client.FluxClientCache;
import sonar.fluxnetworks.common.connection.FluxNetworkInvalid;
import sonar.fluxnetworks.common.misc.FluxMenu;
import sonar.fluxnetworks.common.misc.FluxUtils;
import sonar.fluxnetworks.common.network.FluxTileMessage;
import sonar.fluxnetworks.common.network.NetworkHandler;
import sonar.fluxnetworks.common.storage.FluxChunkManager;
import sonar.fluxnetworks.common.storage.FluxNetworkData;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Set;
import java.util.UUID;

@SuppressWarnings("ConstantConditions")
public abstract class TileFluxDevice extends TileEntity implements IFluxDevice, ITickableTileEntity,
        INamedContainerProvider, IBigPower {

    private static final int FLAG_LOAD = 1 << 6; // server
    private static final int FLAG_SETTING_CHANGED = 1 << 7; // server
    private static final int FLAG_FORCED_LOADING = 1 << 8; // client and server

    // server, players who are accessing this tile with container
    public final Set<PlayerEntity> playerUsing = new ObjectArraySet<>();

    //TODO keep empty when created and client can use translated name as default, waiting for new UI framework
    private String customName;
    private UUID playerUUID = Util.DUMMY_UUID;

    private int networkID;

    // 0xRRGGBB, this value only available on client for rendering, updated from server data
    // this color is brighter than network color
    public int brColor = FluxConstants.INVALID_NETWORK_COLOR;

    protected int priority;
    protected boolean surgeMode;
    private long limit; // transfer limit
    private boolean disableLimit;

    // bit 0~5 side connected, server
    protected int flags;

    // cached value, pos may changed so still needs to test
    @Nullable
    private GlobalPos globalPos;

    protected IFluxNetwork network = FluxNetworkInvalid.INSTANCE;

    public TileFluxDevice(TileEntityType<? extends TileFluxDevice> tileEntityTypeIn, String customName, long limit) {
        super(tileEntityTypeIn);
        this.customName = customName;
        this.limit = limit;
    }

    @Override
    public void remove() {
        super.remove();
        if (!world.isRemote && (flags & FLAG_LOAD) == FLAG_LOAD) {
            network.enqueueConnectionRemoval(this, false);
            if (isForcedLoading()) {
                FluxChunkManager.removeChunkLoader(this);
            }
            flags &= ~FLAG_LOAD;
        }
    }

    @Override
    public void onChunkUnloaded() {
        super.onChunkUnloaded();
        if (!world.isRemote && (flags & FLAG_LOAD) == FLAG_LOAD) {
            network.enqueueConnectionRemoval(this, true);
            flags &= ~FLAG_LOAD;
        }
    }

    @Override
    public final void tick() {
        if (!world.isRemote) {
            sTick();
        }
    }

    // server tick
    protected void sTick() {
        if (!playerUsing.isEmpty()) {
            NetworkHandler.INSTANCE.sendToPlayers(new FluxTileMessage(this, FluxConstants.S2C_GUI_SYNC), playerUsing);
        }
        if ((flags & FLAG_LOAD) == 0) {
            if (networkID > 0) {
                IFluxNetwork network = FluxNetworkData.getNetwork(networkID);
                if (network.isValid() && !(getDeviceType().isController() &&
                        !network.getConnections(FluxLogicType.CONTROLLER).isEmpty())) {
                    network.enqueueConnectionAddition(this);
                } else {
                    networkID = FluxConstants.INVALID_NETWORK_ID;
                }
            }
            updateTransfers(Direction.values());
            flags |= FLAG_LOAD;
        }
    }

    @Override
    public void onConnected(@Nonnull IFluxNetwork network) {
        this.network = network;
        this.networkID = network.getNetworkID();
        sendFullUpdatePacket();
    }

    @Override
    public void onDisconnected() {
        if (network.isValid()) {
            network = FluxNetworkInvalid.INSTANCE;
            networkID = network.getNetworkID();
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
        CompoundNBT nbt = new CompoundNBT();
        writeCustomNBT(nbt, FluxConstants.TYPE_TILE_UPDATE);
        return new SUpdateTileEntityPacket(pos, -1, nbt);
    }

    @Override
    public final void onDataPacket(NetworkManager net, @Nonnull SUpdateTileEntityPacket pkt) {
        // Client side, read block update data
        readCustomNBT(pkt.getNbtCompound(), FluxConstants.TYPE_TILE_UPDATE);
        world.notifyBlockUpdate(pos, getBlockState(), getBlockState(), -1);
    }

    @Nonnull
    @Override
    public final CompoundNBT getUpdateTag() {
        // Server side, write NBT when updating chunk data
        CompoundNBT tag = new CompoundNBT();
        tag.putInt(FluxConstants.CLIENT_COLOR, network.getNetworkColor());
        return write(tag);
    }

    @Override
    public final void handleUpdateTag(BlockState state, @Nonnull CompoundNBT tag) {
        // Client side, read NBT when updating chunk data
        brColor = FluxUtils.getBrighterColor(tag.getInt(FluxConstants.CLIENT_COLOR), 1.2f);
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
    public final CompoundNBT write(@Nonnull CompoundNBT compound) {
        writeCustomNBT(super.write(compound), FluxConstants.TYPE_SAVE_ALL);
        return compound;
    }

    @Override
    public final void read(@Nonnull BlockState state, @Nonnull CompoundNBT compound) {
        super.read(state, compound);
        readCustomNBT(compound, FluxConstants.TYPE_SAVE_ALL);
    }

    @Override
    public void setPos(@Nonnull BlockPos posIn) {
        super.setPos(posIn);
        if (globalPos != null) {
            globalPos = FluxUtils.getGlobalPos(this);
        }
    }

    @Override
    public void setWorldAndPos(@Nonnull World world, @Nonnull BlockPos pos) {
        super.setWorldAndPos(world, pos);
        if (globalPos != null) {
            globalPos = FluxUtils.getGlobalPos(this);
        }
    }

    @Override
    public void writeCustomNBT(CompoundNBT tag, int type) {
        if (type <= FluxConstants.TYPE_CONNECTION_UPDATE) {
            // TYPE_SAVE_ALL, TYPE_TILE_UPDATE, TYPE_TILE_DROP, TYPE_CONNECTION_UPDATE
            tag.putInt(FluxConstants.NETWORK_ID, networkID);
            tag.putString(FluxConstants.CUSTOM_NAME, customName);
            tag.putInt(FluxConstants.PRIORITY, priority);
            tag.putLong(FluxConstants.LIMIT, limit);
            tag.putBoolean(FluxConstants.SURGE_MODE, surgeMode);
            tag.putBoolean(FluxConstants.DISABLE_LIMIT, disableLimit);
        }
        if (type <= FluxConstants.TYPE_TILE_UPDATE) {
            // TYPE_SAVE_ALL, TYPE_TILE_UPDATE
            tag.putUniqueId(FluxConstants.PLAYER_UUID, playerUUID);
        }
        if (type == FluxConstants.TYPE_TILE_UPDATE) {
            tag.putInt(FluxConstants.CLIENT_COLOR, network.getNetworkColor());
            tag.putInt(FluxConstants.FLAGS, flags);
        }
        if (type == FluxConstants.TYPE_CONNECTION_UPDATE) {
            FluxUtils.writeGlobalPos(tag, getGlobalPos());
            tag.putByte(FluxConstants.DEVICE_TYPE, (byte) getDeviceType().ordinal());
            tag.putUniqueId(FluxConstants.PLAYER_UUID, playerUUID);
            tag.putBoolean(FluxConstants.FORCED_LOADING, isForcedLoading());
            tag.putBoolean(FluxConstants.CHUNK_LOADED, isChunkLoaded());
            getDisplayStack().write(tag);
        }
        getTransferHandler().writeCustomNBT(tag, type);
    }

    @Override
    public void readCustomNBT(CompoundNBT tag, int type) {
        if (type <= FluxConstants.TYPE_TILE_DROP) {
            networkID = tag.getInt(FluxConstants.NETWORK_ID);
            customName = tag.getString(FluxConstants.CUSTOM_NAME);
            setPriority(tag.getInt(FluxConstants.PRIORITY));
            setTransferLimit(tag.getLong(FluxConstants.LIMIT));
            surgeMode = tag.getBoolean(FluxConstants.SURGE_MODE);
            disableLimit = tag.getBoolean(FluxConstants.DISABLE_LIMIT);
        }
        if (type <= FluxConstants.TYPE_TILE_UPDATE) {
            playerUUID = tag.getUniqueId(FluxConstants.PLAYER_UUID);
        }
        if (type == FluxConstants.TYPE_TILE_UPDATE) {
            brColor = FluxUtils.getBrighterColor(tag.getInt(FluxConstants.CLIENT_COLOR), 1.2f);
            flags = tag.getInt(FluxConstants.FLAGS);
        }
        if (type == FluxConstants.TYPE_TILE_DROP) {
            if (world.isRemote) {
                brColor = FluxUtils.getBrighterColor(FluxClientCache.getNetwork(networkID).getNetworkColor(), 1.2f);
            }
        }
        getTransferHandler().readCustomNBT(tag, type);
    }

    @Override
    public boolean canPlayerAccess(@Nonnull PlayerEntity player) {
        if (network.isValid()) {
            if (PlayerEntity.getUUID(player.getGameProfile()).equals(playerUUID)) {
                return true;
            }
            return network.getPlayerAccess(player).canUse();
        }
        return true;
    }

    //// PACKETS\\\\

    /**
     * Sends a block update
     */
    public void sendFullUpdatePacket() {
        if (!world.isRemote) {
            world.notifyBlockUpdate(pos, getBlockState(), getBlockState(), -1);
        }
    }

    /*@Deprecated
    private void sendTilePacketToUsing(byte packetID) {
        if (!world.isRemote) {
            for (PlayerEntity playerEntity : playerUsing) {
                PacketHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) playerEntity), new TilePacketBufferPacket(this, pos, packetID));
            }
        }
    }

    @Deprecated
    private void sendTilePacketToNearby(byte packetID) {
        if (!world.isRemote) {
            PacketHandler.CHANNEL.send(PacketDistributor.TRACKING_CHUNK.with(() -> world.getChunkAt(pos)), new TilePacketBufferPacket(this, pos, packetID));
        }
    }

    @Deprecated
    private void sendTilePacketToServer(byte packetID) {
        PacketHandler.CHANNEL.sendToServer(new TilePacketBufferPacket(this, pos, packetID));
    }*/

    public void writePacket(PacketBuffer buffer, byte id) {
        getTransferHandler().writePacket(buffer, id);
        switch (id) {
            case FluxConstants.C2S_CUSTOM_NAME:
                buffer.writeString(customName, 256);
                break;
            case FluxConstants.C2S_PRIORITY:
                buffer.writeInt(priority);
                break;
            case FluxConstants.C2S_LIMIT:
                buffer.writeLong(limit);
                break;
            case FluxConstants.C2S_SURGE_MODE:
                buffer.writeBoolean(surgeMode);
                break;
            case FluxConstants.C2S_DISABLE_LIMIT:
                buffer.writeBoolean(disableLimit);
                break;
            case FluxConstants.C2S_CHUNK_LOADING:
                buffer.writeBoolean(isForcedLoading());
                break;
            case FluxConstants.S2C_GUI_SYNC:
                boolean s = (flags & FLAG_SETTING_CHANGED) == FLAG_SETTING_CHANGED;
                buffer.writeBoolean(s);
                if (s) {
                    buffer.writeString(customName, 256);
                    buffer.writeInt(priority);
                    buffer.writeLong(limit);
                    buffer.writeByte(flags >> 6);
                }
                flags &= ~FLAG_SETTING_CHANGED;
                break;
        }
    }

    public void readPacket(PacketBuffer buffer, byte id) {
        getTransferHandler().readPacket(buffer, id);
        switch (id) {
            case FluxConstants.C2S_CUSTOM_NAME:
                customName = buffer.readString(256);
                break;
            case FluxConstants.C2S_PRIORITY:
                setPriority(buffer.readInt());
                network.markSortConnections();
                break;
            case FluxConstants.C2S_LIMIT:
                setTransferLimit(buffer.readLong());
                break;
            case FluxConstants.C2S_SURGE_MODE:
                surgeMode = buffer.readBoolean();
                network.markSortConnections();
                break;
            case FluxConstants.C2S_DISABLE_LIMIT:
                disableLimit = buffer.readBoolean();
                break;
            case FluxConstants.C2S_CHUNK_LOADING:
                boolean toLoad = buffer.readBoolean();
                if (FluxConfig.enableChunkLoading && !getDeviceType().isStorage()) {
                    if (toLoad && !isForcedLoading()) {
                        FluxChunkManager.addChunkLoader(this);
                    } else if (!toLoad && isForcedLoading()) {
                        FluxChunkManager.removeChunkLoader(this);
                    }
                    setForcedLoading(FluxChunkManager.isChunkLoader(this));
                } else {
                    setForcedLoading(false);
                }
                break;
            case FluxConstants.S2C_GUI_SYNC:
                if (buffer.readBoolean()) {
                    customName = buffer.readString(256);
                    priority = buffer.readInt();
                    limit = buffer.readLong();
                    flags = (flags & 0x3f) | buffer.readByte() << 6;
                }
                break;
        }
        // C2S
        if (id > 0) {
            flags |= FLAG_SETTING_CHANGED;
        }
    }

    /*@Override
    public CompoundNBT copyConfiguration(CompoundNBT config) {
        return FluxUtils.copyConfiguration(this, config);
    }

    @Override
    public void pasteConfiguration(CompoundNBT config) {
        FluxUtils.pasteConfiguration(this, config);
    }*/

    @Override
    public UUID getConnectionOwner() {
        return playerUUID;
    }

    public final void setConnectionOwner(UUID uuid) {
        playerUUID = uuid;
    }

    public void updateTransfers(Direction... dirs) {
        getTransferHandler().updateTransfers(dirs);
    }

    @Override
    public void onMenuOpened(PlayerEntity player) {
        if (!world.isRemote) {
            playerUsing.add(player);
            sendFullUpdatePacket();
        }
    }

    @Override
    public void onMenuClosed(PlayerEntity player) {
        if (!world.isRemote) {
            playerUsing.remove(player);
        }
    }

    @Override
    public String getCustomName() {
        return customName;
    }

    @Override
    public void setCustomName(String customName) {
        this.customName = customName;
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
        return (flags & FLAG_FORCED_LOADING) == FLAG_FORCED_LOADING;
    }

    @Override
    public void setForcedLoading(boolean forcedLoading) {
        if (forcedLoading) {
            flags |= FLAG_FORCED_LOADING;
        } else {
            flags &= ~FLAG_FORCED_LOADING;
        }
    }

    @Override
    public final int getRawPriority() {
        return priority;
    }

    @Override
    public int getLogicPriority() {
        return surgeMode ? Integer.MAX_VALUE : priority;
    }

    @Override
    public void setPriority(int priority) {
        this.priority = MathHelper.clamp(priority, TileFluxStorage.PRI_UPPER + 1, Integer.MAX_VALUE - 1);
    }

    @Override
    public final long getRawLimit() {
        return limit;
    }

    @Override
    public final long getLogicLimit() {
        return disableLimit ? getMaxTransferLimit() : limit;
    }

    @Override
    public final void setTransferLimit(long limit) {
        this.limit = Math.min(limit, getMaxTransferLimit());
    }

    @Override
    public long getMaxTransferLimit() {
        return Long.MAX_VALUE;
    }

    @Override
    public final long getTransferBuffer() {
        return getTransferHandler().getBuffer();
    }

    @Override
    public final long getTransferChange() {
        return getTransferHandler().getChange();
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
    public final boolean getDisableLimit() {
        return disableLimit;
    }

    @Override
    public final void setDisableLimit(boolean disableLimit) {
        this.disableLimit = disableLimit;
    }

    @Override
    public final boolean getSurgeMode() {
        return surgeMode;
    }

    @Override
    public final void setSurgeMode(boolean surgeMode) {
        this.surgeMode = surgeMode;
    }

    @Override
    public final long getStoredPower() {
        return getTransferBuffer();
    }

    @Override
    public final long getCapacity() {
        return getMaxTransferLimit();
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
    public final ITextComponent getDisplayName() {
        return StringTextComponent.EMPTY;
    }

    @Nullable
    public final Container createMenu(int windowID, @Nonnull PlayerInventory inventory, @Nonnull PlayerEntity player) {
        return new FluxMenu(windowID, inventory, this);
    }
}
