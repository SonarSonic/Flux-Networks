package sonar.fluxnetworks.common.device;

import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.world.ForgeChunkManager;
import net.minecraftforge.network.NetworkHooks;
import sonar.fluxnetworks.FluxConfig;
import sonar.fluxnetworks.FluxNetworks;
import sonar.fluxnetworks.api.FluxConstants;
import sonar.fluxnetworks.api.FluxTranslate;
import sonar.fluxnetworks.api.device.IFluxDevice;
import sonar.fluxnetworks.client.ClientCache;
import sonar.fluxnetworks.common.connection.*;
import sonar.fluxnetworks.common.integration.MUIIntegration;
import sonar.fluxnetworks.common.util.FluxUtils;
import sonar.fluxnetworks.register.Channel;
import sonar.fluxnetworks.register.Messages;

import javax.annotation.*;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * Represents a network device entity (on server) and a block entity.
 */
@SuppressWarnings("ConstantConditions")
@ParametersAreNonnullByDefault
public abstract class TileFluxDevice extends BlockEntity implements IFluxDevice {

    private static final BlockEntityTicker<? extends TileFluxDevice> sTickerServer =
            (level, pos, state, tile) -> tile.onServerTick();

    public static final int INVALID_CLIENT_COLOR =
            FluxUtils.getModifiedColor(FluxConstants.INVALID_NETWORK_COLOR, 1.1f);

    public static final int MAX_CUSTOM_NAME_LENGTH = 24;

    /**
     * The player who is using this device (GUI). Non-persisted value.
     */
    @Nullable
    private Player mPlayerUsing;

    /**
     * Leave empty to show a localized name. Persisted value.
     */
    @Nonnull
    private String mCustomName = "";
    /**
     * Player UUID of device's owner. Persisted value.
     */
    @Nonnull
    private UUID mOwnerUUID = Util.NIL_UUID;

    /**
     * The network ID that this device connects to. Persisted value.
     */
    private int mNetworkID = FluxConstants.INVALID_NETWORK_ID;

    /**
     * Tint color in 0xRRGGBB, this value is only valid on client for rendering, updated from server.
     * This color is brighter than actual network color. Non-persisted value.
     */
    public int mClientColor = INVALID_CLIENT_COLOR;

    protected static final int SIDES_CONNECTED_MASK = 0x3F; // server
    protected static final int FLAG_FORCED_LOADING = 0x40; // client and server
    protected static final int FLAG_FIRST_TICKED = 0x80; // server
    protected static final int FLAG_SETTING_CHANGED = 0x100; // server
    protected static final int FLAG_ENERGY_CHANGED = 0x200; // server

    /**
     * Lower 6 bits represent sides connected. Non-persisted value.
     */
    protected int mFlags;

    /**
     * Lazy-loading since {@link #setLevel(Level)} is called later. Non-persisted value.
     */
    @Nullable
    private GlobalPos mGlobalPos;

    // server only, read only by subclasses
    @Nonnull
    private FluxNetwork mNetwork = FluxNetwork.INVALID;

    protected TileFluxDevice(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Nullable
    @SuppressWarnings("unchecked")
    public static <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level) {
        return level.isClientSide ? null : (BlockEntityTicker<T>) sTickerServer;
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        if (!level.isClientSide && (mFlags & FLAG_FIRST_TICKED) != 0) {
            mNetwork.enqueueConnectionRemoval(this, false);
            if (isForcedLoading()) {
                //FluxChunkManager.removeChunkLoader(this);
                long chunkPos = ChunkPos.asLong(worldPosition);
                ForgeChunkManager.forceChunk((ServerLevel) level, FluxNetworks.MODID, worldPosition,
                        ChunkPos.getX(chunkPos), ChunkPos.getZ(chunkPos), false, true);
            }
            getTransferHandler().onNetworkChanged();
            mFlags &= ~FLAG_FIRST_TICKED;
        }
    }

    @Override
    public void onChunkUnloaded() {
        super.onChunkUnloaded();
        if (!level.isClientSide && (mFlags & FLAG_FIRST_TICKED) != 0) {
            mNetwork.enqueueConnectionRemoval(this, true);
            getTransferHandler().onNetworkChanged();
            mFlags &= ~FLAG_FIRST_TICKED;
        }
    }

    // server tick, before network tick
    protected void onServerTick() {
        if ((mFlags & FLAG_FIRST_TICKED) == 0) {
            onFirstTick();
            mFlags |= FLAG_FIRST_TICKED;
        }
        if ((mFlags & FLAG_SETTING_CHANGED) != 0) {
            sendBlockUpdate();
            mFlags &= ~FLAG_SETTING_CHANGED;
        } else if (mPlayerUsing != null) {
            Channel.get().sendToPlayer(
                    Messages.makeDeviceBuffer(this, FluxConstants.DEVICE_S2C_GUI_SYNC), mPlayerUsing);
        }
    }

    protected void onFirstTick() {
        connect(FluxNetworkData.getNetwork(mNetworkID));
    }

    /**
     * Called when a player interacts with this device.
     *
     * @param player the server player
     */
    public void onPlayerInteract(Player player) {
        assert !level.isClientSide;
        if (mPlayerUsing != null) {
            player.displayClientMessage(FluxTranslate.ACCESS_OCCUPY, true);
        } else if (canPlayerAccess(player)) {
            final Consumer<FriendlyByteBuf> writer = buf -> {
                buf.writeBoolean(true); // tell it's BlockEntity rather than Configurator
                buf.writeBlockPos(worldPosition);
                CompoundTag tag = new CompoundTag();
                writeCustomTag(tag, FluxConstants.NBT_TILE_UPDATE);
                buf.writeNbt(tag);
            };
            //TODO check if client player wants to use Modern UI or not
            if (FluxConfig.enableGuiDebug && FluxNetworks.isModernUILoaded()) {
                MUIIntegration.openMenu(player, this, writer);
            } else {
                NetworkHooks.openScreen((ServerPlayer) player, this, writer);
            }
        } else {
            player.displayClientMessage(FluxTranslate.ACCESS_DENIED, true);
        }
    }

    @Nonnull
    @Override
    public final FluxMenu createMenu(int containerId, Inventory inventory, Player player) {
        return new FluxMenu(containerId, inventory, this);
    }

    /**
     * Connect this device to a flux network. Server only.
     * Check access first. Called outside network ticking cycle.
     *
     * @param network the server network to connect, can be invalid
     * @return true if successfully connected to the network
     */
    public boolean connect(FluxNetwork network) {
        assert !level.isClientSide;
        if (mNetwork == network) {
            return true;
        }
        if (network.enqueueConnectionAddition(this)) {
            mNetwork.enqueueConnectionRemoval(this, false);
            mNetwork = network;
            mNetworkID = mNetwork.getNetworkID();
            getTransferHandler().onNetworkChanged();
            // notify listeners
            mFlags |= FLAG_SETTING_CHANGED;
            markChunkUnsaved();
            return true;
        }
        return false;
    }

    /**
     * Connect this device to an invalid network (i.e. disconnect).
     */
    public final void disconnect() {
        connect(FluxNetwork.INVALID);
    }

    @Override
    public final int getNetworkID() {
        return mNetworkID;
    }

    @Nonnull
    public final FluxNetwork getNetwork() {
        return mNetwork;
    }

    @Nonnull
    public abstract TransferHandler getTransferHandler();

    @Nonnull
    @Override
    public final ClientboundBlockEntityDataPacket getUpdatePacket() {
        // Server side, write block update data
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public final void onDataPacket(Connection net, ClientboundBlockEntityDataPacket packet) {
        // Client side, read block update data
        readCustomTag(packet.getTag(), FluxConstants.NBT_TILE_UPDATE);
        // update chunk render whether state changed or not
        level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL_IMMEDIATE);
    }

    @Nonnull
    @Override
    public final CompoundTag getUpdateTag() {
        // Server side, read NBT when updating chunk data
        CompoundTag tag = super.getUpdateTag();
        writeCustomTag(tag, FluxConstants.NBT_TILE_UPDATE);
        return tag;
    }

    @Override
    public final void handleUpdateTag(CompoundTag tag) {
        // Client side, read NBT when updating chunk data
        super.load(tag);
        readCustomTag(tag, FluxConstants.NBT_TILE_UPDATE);
    }

    @Override
    protected final void saveAdditional(@Nonnull CompoundTag tag) {
        super.saveAdditional(tag);
        writeCustomTag(tag, FluxConstants.NBT_SAVE_ALL);
    }

    @Override
    public final void load(@Nonnull CompoundTag tag) {
        super.load(tag);
        readCustomTag(tag, FluxConstants.NBT_SAVE_ALL);
    }

    @Override
    public void setLevel(@Nonnull Level level) {
        super.setLevel(level);
        mGlobalPos = GlobalPos.of(level.dimension(), worldPosition);
    }

    @Override
    public void writeCustomTag(@Nonnull CompoundTag tag, byte type) {
        // the two most basic data, regardless of type
        tag.putInt(FluxConstants.NETWORK_ID, mNetworkID);
        tag.putString(FluxConstants.CUSTOM_NAME, mCustomName);
        getTransferHandler().writeCustomTag(tag, type);
        switch (type) {
            case FluxConstants.NBT_SAVE_ALL -> tag.putUUID(FluxConstants.PLAYER_UUID, mOwnerUUID);
            case FluxConstants.NBT_TILE_UPDATE -> {
                tag.putUUID(FluxConstants.PLAYER_UUID, mOwnerUUID);
                if ((mFlags & FLAG_FIRST_TICKED) != 0) {
                    tag.putInt(FluxConstants.CLIENT_COLOR, mNetwork.getNetworkColor());
                }
                tag.putInt(FluxConstants.FLAGS, mFlags);
            }
            case FluxConstants.NBT_PHANTOM_UPDATE -> {
                // XXX: the key may conflict when writing into the root tag
                FluxUtils.writeGlobalPos(tag, getGlobalPos());
                tag.putByte(FluxConstants.DEVICE_TYPE, getDeviceType().getId());
                tag.putUUID(FluxConstants.PLAYER_UUID, mOwnerUUID);
                tag.putBoolean(FluxConstants.FORCED_LOADING, isForcedLoading());
                tag.putBoolean(FluxConstants.CHUNK_LOADED, isChunkLoaded());
                getDisplayStack().save(tag);
            }
        }
    }

    @Override
    public void readCustomTag(@Nonnull CompoundTag tag, byte type) {
        if (type == FluxConstants.NBT_TILE_SETTINGS) {
            assert !level.isClientSide;
            if (tag.isEmpty()) {
                return;
            }
            if (tag.contains(FluxConstants.CUSTOM_NAME)) {
                String name = tag.getString(FluxConstants.CUSTOM_NAME);
                if (name.length() <= MAX_CUSTOM_NAME_LENGTH) {
                    mCustomName = name;
                }
            }
            boolean sort = getTransferHandler().changeSettings(tag);
            if (sort && mNetwork.isValid()) {
                ((ServerFluxNetwork) mNetwork).markSortConnections();
            }
            if (tag.contains(FluxConstants.FORCED_LOADING)) {
                boolean load = tag.getBoolean(FluxConstants.FORCED_LOADING) &&
                        FluxConfig.enableChunkLoading && !getDeviceType().isStorage();
                long chunkPos = ChunkPos.asLong(worldPosition);
                ForgeChunkManager.forceChunk((ServerLevel) level, FluxNetworks.MODID, worldPosition,
                        ChunkPos.getX(chunkPos), ChunkPos.getZ(chunkPos), load, true);
                setForcedLoading(load);
            }
            // notify listeners
            mFlags |= FLAG_SETTING_CHANGED;
            markChunkUnsaved();
            return;
        }
        mNetworkID = tag.getInt(FluxConstants.NETWORK_ID);
        mCustomName = tag.getString(FluxConstants.CUSTOM_NAME);
        getTransferHandler().readCustomTag(tag, type);
        switch (type) {
            case FluxConstants.NBT_SAVE_ALL -> mOwnerUUID = tag.getUUID(FluxConstants.PLAYER_UUID);
            case FluxConstants.NBT_TILE_UPDATE -> {
                mOwnerUUID = tag.getUUID(FluxConstants.PLAYER_UUID);
                if (tag.contains(FluxConstants.CLIENT_COLOR)) {
                    mClientColor = FluxUtils.getModifiedColor(tag.getInt(FluxConstants.CLIENT_COLOR), 1.1f);
                }
                mFlags = tag.getInt(FluxConstants.FLAGS);
            }
            case FluxConstants.NBT_TILE_DROP -> {
                if (level.isClientSide) {
                    mClientColor = FluxUtils.getModifiedColor(
                            ClientCache.getNetwork(mNetworkID).getNetworkColor(), 1.1f);
                }
            }
        }
    }

    /**
     * Server-only.
     *
     * @param player the player to access this
     * @return should access
     */
    public boolean canPlayerAccess(@Nonnull Player player) {
        assert level != null && !level.isClientSide;
        // devices without a network connection are not protected (e.g. abandoned).
        if (mNetwork.isValid()) {
            if (player.getUUID().equals(mOwnerUUID)) {
                return true;
            }
            return mNetwork.canPlayerAccess(player);
        }
        return true;
    }

    //// PACKETS\\\\

    /**
     * Sends a block update. Including a {@link #getUpdatePacket()} and {@link Level#setBlock}
     * with Flags 0b10011 on the client. Server only.
     */
    public void sendBlockUpdate() {
        assert level != null && !level.isClientSide;
        // last arg has no usage on server side
        level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL_IMMEDIATE);
    }

    /*@Deprecated
    private void sendTilePacketToUsing(byte packetID) {
        if (!world.isRemote) {
            for (PlayerEntity playerEntity : playerUsing) {
                PacketHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) playerEntity),
                new TilePacketBufferPacket(this, pos, packetID));
            }
        }
    }

    @Deprecated
    private void sendTilePacketToNearby(byte packetID) {
        if (!world.isRemote) {
            PacketHandler.CHANNEL.send(PacketDistributor.TRACKING_CHUNK.with(() -> world.getChunkAt(pos)), new
            TilePacketBufferPacket(this, pos, packetID));
        }
    }

    @Deprecated
    private void sendTilePacketToServer(byte packetID) {
        PacketHandler.CHANNEL.sendToServer(new TilePacketBufferPacket(this, pos, packetID));
    }*/

    /**
     * Write hot data to a byte buffer. Hot data is what's updated almost every tick,
     * such as energy changes.
     *
     * @param buf  the byte buf
     * @param type the type id
     */
    public void writePacketBuffer(FriendlyByteBuf buf, byte type) {
        getTransferHandler().writePacketBuffer(buf, type);
    }

    /**
     * Read hot data from a byte buffer. Hot data is what's updated almost every tick,
     * such as energy changes.
     *
     * @param buf  the byte buf
     * @param type the type id
     */
    public void readPacketBuffer(FriendlyByteBuf buf, byte type) {
        getTransferHandler().readPacketBuffer(buf, type);
        /*switch (id) {
            case FluxConstants.C2S_CUSTOM_NAME:
                mCustomName = buf.readUtf(96);
                break;*/
            /*case FluxConstants.C2S_SURGE_MODE:
                surgeMode = buf.readBoolean();
                mNetwork.markSortConnections();
                break;
            case FluxConstants.C2S_DISABLE_LIMIT:
                disableLimit = buf.readBoolean();
                break;
            case FluxConstants.C2S_CHUNK_LOADING:
                boolean toLoad = buf.readBoolean();
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
                if (buf.readBoolean()) {
                    mCustomName = buf.readUtf(256);
                    priority = buf.readInt();
                    limit = buf.readLong();
                    mFlags = (mFlags & 0x3f) | buf.readByte() << 6;
                }
                break;*/
        //}
    }

    /**
     * Mark this chunk dirty, so it will be saved to disk later.
     * This should be called at the end of the tick if any NBT data changed (buffer, settings).
     * This should be called only if this block entity is loaded, not removed, and ticking on the server side.
     * This will not trigger redstone comparator updates, because we have no item containers.
     */
    public void markChunkUnsaved() {
        assert level != null;
        level.getChunkAt(worldPosition).setUnsaved(true);
    }

    /*@Override
    public CompoundNBT copyConfiguration(CompoundNBT config) {
        return FluxUtils.copyConfiguration(this, config);
    }

    @Override
    public void pasteConfiguration(CompoundNBT config) {
        FluxUtils.pasteConfiguration(this, config);
    }*/

    @Nonnull
    @Override
    public final UUID getOwnerUUID() {
        return mOwnerUUID;
    }

    public final void setOwnerUUID(@Nonnull UUID uuid) {
        if (!mOwnerUUID.equals(uuid)) {
            mOwnerUUID = uuid;
            // notify listeners
            mFlags |= FLAG_SETTING_CHANGED;
            markChunkUnsaved();
        }
    }

    /**
     * Called when the player started to interact with this connector.
     *
     * @param player the player
     */
    @Override
    public void onPlayerOpened(Player player) {
        assert mPlayerUsing == null;
        mPlayerUsing = player;
    }

    /**
     * Called when the player stopped interacting with this connector.
     *
     * @param player the player
     */
    @Override
    public void onPlayerClosed(Player player) {
        assert level.isClientSide || mPlayerUsing == player;
        mPlayerUsing = null;
    }

    @Nonnull
    @Override
    public final String getCustomName() {
        return mCustomName;
    }

    @Override
    public boolean isChunkLoaded() {
        return !isRemoved();
    }

    @Override
    public boolean isForcedLoading() {
        return (mFlags & FLAG_FORCED_LOADING) != 0;
    }

    // just a marker
    public void setForcedLoading(boolean forcedLoading) {
        if (forcedLoading) {
            mFlags |= FLAG_FORCED_LOADING;
        } else {
            mFlags &= ~FLAG_FORCED_LOADING;
        }
    }

    @Override
    public final int getRawPriority() {
        return getTransferHandler().getRawPriority();
    }

    @Override
    public final long getRawLimit() {
        return getTransferHandler().getRawLimit();
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
        if (mGlobalPos == null) {
            mGlobalPos = GlobalPos.of(level.dimension(), worldPosition);
        }
        return mGlobalPos;
    }

    @Override
    public final boolean getDisableLimit() {
        return getTransferHandler().getDisableLimit();
    }

    @Override
    public final boolean getSurgeMode() {
        return getTransferHandler().getSurgeMode();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() +
                '{' +
                "mNetworkID=" + mNetworkID +
                ", mGlobalPos=" + mGlobalPos +
                '}';
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
}
