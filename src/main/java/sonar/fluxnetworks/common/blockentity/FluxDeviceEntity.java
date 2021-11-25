package sonar.fluxnetworks.common.blockentity;

import icyllis.modernui.forge.MuiForgeBridge;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuConstructor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import sonar.fluxnetworks.api.FluxTranslate;
import sonar.fluxnetworks.api.device.IFluxDevice;
import sonar.fluxnetworks.api.misc.FluxConstants;
import sonar.fluxnetworks.api.network.IFluxNetwork;
import sonar.fluxnetworks.client.FluxClientCache;
import sonar.fluxnetworks.common.connection.FluxNetworkInvalid;
import sonar.fluxnetworks.common.connection.FluxNetworkManager;
import sonar.fluxnetworks.common.connection.TransferHandler;
import sonar.fluxnetworks.common.util.FluxContainerMenu;
import sonar.fluxnetworks.common.util.FluxUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.UUID;

@SuppressWarnings("ConstantConditions")
@ParametersAreNonnullByDefault
public abstract class FluxDeviceEntity extends BlockEntity implements IFluxDevice, MenuConstructor {

    private static final BlockEntityTicker<? extends FluxDeviceEntity> sTickerServer =
            (level, pos, state, entity) -> entity.onServerTick();

    static final int CONNECTION_MASK = 0x3F;
    static final int FLAG_FIRST_LOADED = 0x40; // server
    static final int FLAG_SETTING_CHANGED = 0x80; // server
    static final int FLAG_FORCED_LOADING = 0x100; // client and server
    static final int FLAG_ENERGY_CHANGED = 0x200; // server

    // the server player who is using this device
    @Nullable
    private Player mUsingPlayer;

    //TODO keep empty when created and client can use translated name as default, waiting for new UI framework
    private String mCustomName = "";
    private UUID mPlayerUUID = Util.NIL_UUID;

    private int mNetworkID;

    /**
     * Tint color in 0xRRGGBB, this value is only valid on client for rendering, updated from server.
     * This color is brighter than actual network color.
     */
    public int mBlockTint = FluxConstants.INVALID_NETWORK_COLOR;

    // lower 6 bits represent sides connected
    protected int mFlags;

    /**
     * Lazy-loading since {@link #setLevel(Level)} is called later.
     */
    @Nullable
    private GlobalPos mGlobalPos;

    @Nonnull
    protected IFluxNetwork mNetwork = FluxNetworkInvalid.INSTANCE;

    protected FluxDeviceEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
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
        if (mUsingPlayer != null) {
            //S2CNetMsg.tileEntity(this, FluxConstants.S2C_GUI_SYNC).sendToPlayers(mUsingPlayer);
        }
        if ((mFlags & FLAG_FIRST_LOADED) == 0) {
            onFirstLoad();
            mFlags |= FLAG_FIRST_LOADED;
        }
    }

    // first load called from server tick
    protected void onFirstLoad() {
        connect(FluxNetworkManager.getNetwork(mNetworkID));
    }

    /**
     * Called when a player interacts with this device.
     *
     * @param player the server player
     */
    public void interact(Player player) {
        if (mUsingPlayer != null) {
            player.displayClientMessage(FluxTranslate.error(FluxTranslate.ACCESS_OCCUPY), true);
        } else if (canPlayerAccess(player)) {
            MuiForgeBridge.openMenu(player, this, worldPosition);
        } else {
            player.displayClientMessage(FluxTranslate.error(FluxTranslate.ACCESS_DENIED), true);
        }
    }

    @Nullable
    public final AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
        return new FluxContainerMenu(containerId, inventory, this);
    }

    /**
     * Connect this device to a flux network.
     *
     * @param network the server network to connect, can be invalid
     */
    public void connect(IFluxNetwork network) {
        if (mNetwork == network) {
            return;
        }
        if (network.enqueueConnectionAddition(this)) {
            mNetwork.enqueueConnectionRemoval(this, false);
            mNetwork = network;
            mNetworkID = network.getNetworkID();
            getTransferHandler().clearLocalStates();
            sendBlockUpdate();
        }
    }

    @Override
    public final int getNetworkID() {
        return mNetworkID;
    }

    @Nonnull
    public final IFluxNetwork getNetwork() {
        return mNetwork;
    }

    @Nonnull
    public abstract TransferHandler getTransferHandler();

    @Nonnull
    @Override
    public final ClientboundBlockEntityDataPacket getUpdatePacket() {
        // Server side, write block update data
        CompoundTag nbt = new CompoundTag();
        writeCustomTag(nbt, FluxConstants.TYPE_TILE_UPDATE);
        return new ClientboundBlockEntityDataPacket(worldPosition, -1, nbt);
    }

    @Override
    public final void onDataPacket(Connection net, ClientboundBlockEntityDataPacket packet) {
        // Client side, read block update data
        readCustomTag(packet.getTag(), FluxConstants.TYPE_TILE_UPDATE);
        // update chunk render whether state changed or not
        //world.notifyBlockUpdate(pos, getBlockState(), getBlockState(), -1);
    }

    @Nonnull
    @Override
    public final CompoundTag getUpdateTag() {
        // Server side, read NBT when updating chunk data
        CompoundTag tag = super.getUpdateTag();
        writeCustomTag(tag, FluxConstants.TYPE_TILE_UPDATE);
        return tag;
    }

    @Override
    public final void handleUpdateTag(CompoundTag tag) {
        // Client side, read NBT when updating chunk data
        super.load(tag);
        readCustomTag(tag, FluxConstants.TYPE_TILE_UPDATE);
    }

    @Nonnull
    @Override
    public final Level getFluxWorld() {
        // Access world with interface
        return level;
    }

    @Nonnull
    @Override
    public final CompoundTag save(@Nonnull CompoundTag tag) {
        super.save(tag);
        writeCustomTag(tag, FluxConstants.TYPE_SAVE_ALL);
        return tag;
    }

    @Override
    public final void load(@Nonnull CompoundTag tag) {
        super.load(tag);
        readCustomTag(tag, FluxConstants.TYPE_SAVE_ALL);
    }

    @Override
    public void setLevel(@Nonnull Level level) {
        super.setLevel(level);
        mGlobalPos = GlobalPos.of(level.dimension(), worldPosition);
    }

    @Override
    public void writeCustomTag(@Nonnull CompoundTag tag, int type) {
        if (type <= FluxConstants.TYPE_CONNECTION_UPDATE) {
            // TYPE_SAVE_ALL, TYPE_TILE_UPDATE, TYPE_TILE_DROP, TYPE_CONNECTION_UPDATE
            tag.putInt(FluxConstants.NETWORK_ID, mNetworkID);
            tag.putString(FluxConstants.CUSTOM_NAME, mCustomName);
            /*tag.putInt(FluxConstants.PRIORITY, priority);
            tag.putLong(FluxConstants.LIMIT, limit);
            tag.putBoolean(FluxConstants.SURGE_MODE, surgeMode);
            tag.putBoolean(FluxConstants.DISABLE_LIMIT, disableLimit);*/
        }
        if (type <= FluxConstants.TYPE_TILE_UPDATE) {
            // TYPE_SAVE_ALL, TYPE_TILE_UPDATE
            tag.putUUID(FluxConstants.PLAYER_UUID, mPlayerUUID);
        }
        if (type == FluxConstants.TYPE_TILE_UPDATE) {
            tag.putInt(FluxConstants.CLIENT_COLOR, mNetwork.getNetworkColor());
            tag.putInt(FluxConstants.FLAGS, mFlags);
        }
        if (type == FluxConstants.TYPE_CONNECTION_UPDATE) {
            FluxUtils.writeGlobalPos(tag, getGlobalPos());
            tag.putByte(FluxConstants.DEVICE_TYPE, (byte) getDeviceType().ordinal());
            tag.putUUID(FluxConstants.PLAYER_UUID, mPlayerUUID);
            tag.putBoolean(FluxConstants.FORCED_LOADING, isForcedLoading());
            tag.putBoolean(FluxConstants.CHUNK_LOADED, isChunkLoaded());
            getDisplayStack().save(tag);
        }
        getTransferHandler().writeCustomTag(tag, type);
    }

    @Override
    public void readCustomTag(@Nonnull CompoundTag tag, int type) {
        if (type <= FluxConstants.TYPE_TILE_DROP) {
            mNetworkID = tag.getInt(FluxConstants.NETWORK_ID);
            mCustomName = tag.getString(FluxConstants.CUSTOM_NAME);
            /*setPriority(tag.getInt(FluxConstants.PRIORITY));
            setTransferLimit(tag.getLong(FluxConstants.LIMIT));
            surgeMode = tag.getBoolean(FluxConstants.SURGE_MODE);
            disableLimit = tag.getBoolean(FluxConstants.DISABLE_LIMIT);*/
        }
        if (type <= FluxConstants.TYPE_TILE_UPDATE) {
            mPlayerUUID = tag.getUUID(FluxConstants.PLAYER_UUID);
        }
        if (type == FluxConstants.TYPE_TILE_UPDATE) {
            mBlockTint = FluxUtils.getBrighterColor(tag.getInt(FluxConstants.CLIENT_COLOR), 1.2f);
            mFlags = tag.getInt(FluxConstants.FLAGS);
        }
        if (type == FluxConstants.TYPE_TILE_DROP) {
            if (level.isClientSide) {
                mBlockTint = FluxUtils.getBrighterColor(FluxClientCache.getNetwork(mNetworkID).getNetworkColor(), 1.2f);
            }
        }
        getTransferHandler().readCustomTag(tag, type);
    }

    public boolean canPlayerAccess(@Nonnull Player player) {
        if (mNetwork.isValid()) {
            if (player.getUUID().equals(mPlayerUUID)) {
                return true;
            }
            return mNetwork.getPlayerAccess(player).canUse();
        }
        return true;
    }

    //// PACKETS\\\\

    /**
     * Sends a block update
     */
    public void sendBlockUpdate() {
        assert level != null && !level.isClientSide;
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

    public void writePacket(FriendlyByteBuf buffer, byte id) {
        getTransferHandler().writePacket(buffer, id);
        /*switch (id) {
            case FluxConstants.C2S_CUSTOM_NAME:
                buffer.writeString(mCustomName, 256);
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
                boolean s = (mFlags & FLAG_SETTING_CHANGED) == FLAG_SETTING_CHANGED;
                buffer.writeBoolean(s);
                if (s) {
                    buffer.writeString(mCustomName, 256);
                    buffer.writeInt(priority);
                    buffer.writeLong(limit);
                    buffer.writeByte(mFlags >> 6);
                }
                mFlags &= ~FLAG_SETTING_CHANGED;
                break;
        }*/
    }

    public void readPacket(FriendlyByteBuf buffer, byte id) {
        getTransferHandler().readPacket(buffer, id);
        /*switch (id) {
            case FluxConstants.C2S_CUSTOM_NAME:
                mCustomName = buffer.readString(256);
                break;
            case FluxConstants.C2S_PRIORITY:
                setPriority(buffer.readInt());
                mNetwork.markSortConnections();
                break;
            case FluxConstants.C2S_LIMIT:
                setTransferLimit(buffer.readLong());
                break;
            case FluxConstants.C2S_SURGE_MODE:
                surgeMode = buffer.readBoolean();
                mNetwork.markSortConnections();
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
                    mCustomName = buffer.readUtf(256);
                    priority = buffer.readInt();
                    limit = buffer.readLong();
                    mFlags = (mFlags & 0x3f) | buffer.readByte() << 6;
                }
                break;
        }*/
        // C2S
        if (id > 0) {
            mFlags |= FLAG_SETTING_CHANGED;
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

    @Nonnull
    @Override
    public final UUID getOwnerUUID() {
        return mPlayerUUID;
    }

    public final void setConnectionOwner(UUID uuid) {
        mPlayerUUID = uuid;
    }

    @Override
    public void onPlayerOpen(Player player) {
        mUsingPlayer = player;
        if (!level.isClientSide) {
            sendBlockUpdate();
        }
    }

    @Override
    public void onPlayerClose(Player player) {
        mUsingPlayer = null;
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
        return getTransferHandler().getLimit();
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
