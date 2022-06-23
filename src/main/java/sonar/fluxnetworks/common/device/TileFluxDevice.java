package sonar.fluxnetworks.common.device;

import net.minecraft.Util;
import net.minecraft.core.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
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
import sonar.fluxnetworks.common.integration.MuiIntegration;
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
            (level, pos, state, entity) -> entity.onServerTick();

    public static final int INVALID_CLIENT_COLOR =
            FluxUtils.getModifiedColor(FluxConstants.INVALID_NETWORK_COLOR, 1.1f);

    public static final int MAX_CUSTOM_NAME_LENGTH = 24;

    static final int CONNECTION_MASK = 0x0000003F; // server
    static final int FLAG_FIRST_LOADED = 0x00000040; // server
    static final int FLAG_SETTING_CHANGED = 0x00000080; // server
    static final int FLAG_FORCED_LOADING = 0x00000100; // client and server
    static final int FLAG_ENERGY_CHANGED = 0x00000200; // server

    // the server player who is using this device
    @Nullable
    private Player mPlayerUsing;

    // leave empty to show a localized name
    private String mCustomName = "";
    private UUID mOwnerUUID = Util.NIL_UUID;

    private int mNetworkID = FluxConstants.INVALID_NETWORK_ID;

    /**
     * Tint color in 0xRRGGBB, this value is only valid on client for rendering, updated from server.
     * This color is brighter than actual network color.
     */
    public int mClientColor = INVALID_CLIENT_COLOR;

    // lower 6 bits represent sides connected
    protected int mFlags;

    /**
     * Lazy-loading since {@link #setLevel(Level)} is called later.
     */
    @Nullable
    private GlobalPos mGlobalPos;

    // server only
    @Nonnull
    protected FluxNetwork mNetwork = FluxNetwork.INVALID;

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
        if (!level.isClientSide && (mFlags & FLAG_FIRST_LOADED) == FLAG_FIRST_LOADED) {
            mNetwork.enqueueConnectionRemoval(this, false);
            if (isForcedLoading()) {
                //FluxChunkManager.removeChunkLoader(this);
                ForgeChunkManager.forceChunk((ServerLevel) level, FluxNetworks.MODID, worldPosition,
                        SectionPos.blockToSectionCoord(worldPosition.getX()),
                        SectionPos.blockToSectionCoord(worldPosition.getZ()),
                        false, true); //TODO ticking or not?
            }
            getTransferHandler().clearLocalStates();
            mFlags &= ~FLAG_FIRST_LOADED;
        }
    }

    @Override
    public void onChunkUnloaded() {
        super.onChunkUnloaded();
        if (!level.isClientSide && (mFlags & FLAG_FIRST_LOADED) == FLAG_FIRST_LOADED) {
            mNetwork.enqueueConnectionRemoval(this, true);
            getTransferHandler().clearLocalStates();
            mFlags &= ~FLAG_FIRST_LOADED;
        }
    }

    // server tick
    protected void onServerTick() {
        if ((mFlags & FLAG_FIRST_LOADED) == 0) {
            onFirstLoad();
            mFlags |= FLAG_FIRST_LOADED;
        }
        if ((mFlags & FLAG_SETTING_CHANGED) == FLAG_SETTING_CHANGED) {
            sendBlockUpdate();
            mFlags &= ~FLAG_SETTING_CHANGED;
        } else if (mPlayerUsing != null) {
            Channel.get().sendToPlayer(Messages.deviceBuffer(this, FluxConstants.DEVICE_S2C_GUI_SYNC), mPlayerUsing);
        }
    }

    protected void onFirstLoad() {
        connect(FluxNetworkData.getNetwork(mNetworkID));
    }

    /**
     * Called when a player interacts with this device.
     *
     * @param player the server player
     */
    public void onPlayerInteract(Player player) {
        if (mPlayerUsing != null) {
            player.displayClientMessage(FluxTranslate.ACCESS_OCCUPY, true);
        } else if (canPlayerAccess(player)) {
            Consumer<FriendlyByteBuf> writer = buf -> {
                buf.writeBoolean(true); // tell it's BlockEntity rather than Configurator
                buf.writeBlockPos(worldPosition);
                CompoundTag tag = new CompoundTag();
                writeCustomTag(tag, FluxConstants.NBT_TILE_UPDATE);
                buf.writeNbt(tag);
            };
            if (FluxConfig.enableGuiDebug && FluxNetworks.isModernUILoaded()) {
                MuiIntegration.openMenu(player, this, writer);
            } else {
                NetworkHooks.openGui((ServerPlayer) player, this, writer);
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
     * Check access first.
     *
     * @param network the server network to connect, can be invalid
     */
    public void connect(FluxNetwork network) {
        assert !level.isClientSide;
        if (mNetwork == network) {
            return;
        }
        if (network.enqueueConnectionAddition(this)) {
            mNetwork.enqueueConnectionRemoval(this, false);
            mNetwork = network;
            mNetworkID = network.getNetworkID();
            getTransferHandler().clearLocalStates();
            mFlags |= FLAG_SETTING_CHANGED;
        }
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
        level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), -1);
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
                if ((mFlags & FLAG_FIRST_LOADED) != 0) {
                    tag.putInt(FluxConstants.CLIENT_COLOR, mNetwork.getNetworkColor());
                }
                tag.putInt(FluxConstants.FLAGS, mFlags);
            }
            case FluxConstants.NBT_PHANTOM_UPDATE -> {
                // note the key may conflict when writing into the root tag
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
                mCustomName = tag.getString(FluxConstants.CUSTOM_NAME);
                if (mCustomName.length() > MAX_CUSTOM_NAME_LENGTH) {
                    throw new RuntimeException("Expected custom name length " + MAX_CUSTOM_NAME_LENGTH +
                            " is exceeded by " + mCustomName.length());
                }
            }
            boolean sort = getTransferHandler().changeSettings(tag);
            if (sort && mNetwork.isValid()) {
                ((ServerFluxNetwork) mNetwork).markSortConnections();
            }
            if (tag.contains(FluxConstants.FORCED_LOADING)) {
                if (FluxConfig.enableChunkLoading && !getDeviceType().isStorage()) {
                    boolean load = tag.getBoolean(FluxConstants.FORCED_LOADING);
                    ForgeChunkManager.forceChunk((ServerLevel) level, FluxNetworks.MODID, worldPosition,
                            SectionPos.blockToSectionCoord(worldPosition.getX()),
                            SectionPos.blockToSectionCoord(worldPosition.getZ()),
                            load, true);
                    setForcedLoading(load);
                } else {
                    setForcedLoading(false);
                }
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
        // devices without a network connection are not protected (e.g. abandoned).
        if (mNetwork.isValid()) {
            if (player.getUUID().equals(mOwnerUUID)) {
                return true;
            }
            return mNetwork.getPlayerAccess(player).canUse();
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
        level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), -1);
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

    public void writePacket(FriendlyByteBuf buf, byte type) {
        getTransferHandler().writePacket(buf, type);
    }

    public void readPacket(FriendlyByteBuf buf, byte type) {
        getTransferHandler().readPacket(buf, type);
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

    public void markChunkUnsaved() {
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

    public final void setOwnerUUID(UUID uuid) {
        mOwnerUUID = uuid;
    }

    /**
     * Called when the player started to interact with this connector.
     *
     * @param player the player
     */
    @Override
    public void onMenuOpened(Player player) {
        assert mPlayerUsing == null;
        mPlayerUsing = player;
    }

    /**
     * Called when the player stopped interacting with this connector.
     *
     * @param player the player
     */
    @Override
    public void onMenuClosed(Player player) {
        assert level.isClientSide || mPlayerUsing == player;
        mPlayerUsing = null;
    }

    @Override
    public final String getCustomName() {
        return mCustomName;
    }

    public final void setCustomName(String customName) {
        mCustomName = customName;
    }

    @Override
    public boolean isChunkLoaded() {
        return !isRemoved();
    }

    @Override
    public boolean isForcedLoading() {
        return (mFlags & FLAG_FORCED_LOADING) == FLAG_FORCED_LOADING;
    }

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
        return "TileFluxDevice{" +
                "customName='" + mCustomName + '\'' +
                ", networkID=" + mNetworkID +
                ", globalPos=" + mGlobalPos +
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
