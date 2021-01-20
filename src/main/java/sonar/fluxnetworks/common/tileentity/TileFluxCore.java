package sonar.fluxnetworks.common.tileentity;

import io.netty.buffer.ByteBuf;
import li.cil.oc.api.machine.Arguments;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import sonar.fluxnetworks.FluxConfig;
import sonar.fluxnetworks.api.network.IFluxNetwork;
import sonar.fluxnetworks.api.network.NetworkFolder;
import sonar.fluxnetworks.api.network.NetworkSettings;
import sonar.fluxnetworks.api.tiles.IFluxConfigurable;
import sonar.fluxnetworks.api.tiles.IFluxConnector;
import sonar.fluxnetworks.api.tiles.ITileByteBuf;
import sonar.fluxnetworks.api.utils.Coord4D;
import sonar.fluxnetworks.api.utils.NBTType;
import sonar.fluxnetworks.common.connection.FluxNetworkInvalid;
import sonar.fluxnetworks.common.connection.FluxNetworkServer;
import sonar.fluxnetworks.common.connection.NetworkStatistics;
import sonar.fluxnetworks.common.core.FluxUtils;
import sonar.fluxnetworks.common.data.FluxChunkManager;
import sonar.fluxnetworks.common.data.FluxNetworkData;
import sonar.fluxnetworks.common.integration.oc.IOCPeripheral;
import sonar.fluxnetworks.common.item.ItemFluxConnector;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;

public abstract class TileFluxCore extends TileEntity implements IFluxConnector, IFluxConfigurable, ITickable, ITileByteBuf, IOCPeripheral {

    public HashSet<EntityPlayer> playerUsing = new HashSet<>();

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
    public byte[] connections = new byte[]{0, 0, 0, 0, 0, 0};

    public boolean chunkLoading = false;

    protected IFluxNetwork network = FluxNetworkInvalid.instance;

    protected boolean load = false;

    @Override
    public void invalidate() {
        super.invalidate();
        if (!world.isRemote && load) {
            FluxUtils.removeConnection(this, false);
            if (chunkLoading) {
                FluxChunkManager.releaseChunk(world, new ChunkPos(pos));
            }
            getTransferHandler().reset();
            load = false;
        }
    }

    @Override
    public void onChunkUnload() {
        super.onChunkUnload();
        if (!world.isRemote && load) {
            FluxUtils.removeConnection(this, true);
            getTransferHandler().reset();
            load = false;
        }
    }

    @Override
    public void update() {
        if (!world.isRemote) {
            if (playerUsing.size() > 0) {
                sendPackets();
            }
            if (!load) {
                if (!FluxUtils.addConnection(this)) {
                    networkID = -1;
                    connected = false;
                    color = 0xb2b2b2;
                }
                updateTransfers(EnumFacing.VALUES);
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
        getTransferHandler().reset();
        sendPackets();
    }

    @Override
    public void disconnect(IFluxNetwork network) {
        if (network.getNetworkID() == getNetworkID()) {
            this.network = FluxNetworkInvalid.instance;
            this.networkID = -1;
            this.color = 0xb2b2b2;
            connected = false;
            getTransferHandler().reset();
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
    public SPacketUpdateTileEntity getUpdatePacket() {
        return new SPacketUpdateTileEntity(pos, -1, writeCustomNBT(new NBTTagCompound(), NBTType.TILE_UPDATE));
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        readCustomNBT(pkt.getNbtCompound(), NBTType.TILE_UPDATE);
        world.markBlockRangeForRenderUpdate(pos, pos);
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        return writeToNBT(super.getUpdateTag());
    }

    @Override
    public void handleUpdateTag(NBTTagCompound tag) {
        readFromNBT(tag);
    }

    public void sendPackets() {
        IBlockState state = world.getBlockState(pos);
        world.notifyBlockUpdate(pos, state, state, 3);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        writeCustomNBT(compound, NBTType.ALL_SAVE);
        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        readCustomNBT(compound, NBTType.ALL_SAVE);
    }

    public NBTTagCompound writeCustomNBT(NBTTagCompound tag, NBTType type) {
        if (type == NBTType.ALL_SAVE || type == NBTType.TILE_UPDATE) {
            tag.setInteger("0", priority);
            tag.setLong("1", limit);
            tag.setBoolean("2", disableLimit);
            tag.setBoolean("3", surgeMode);
            tag.setInteger("4", networkID);
            tag.setUniqueId("5", playerUUID);
            tag.setString("6", customName);
            tag.setInteger("7", color);
            tag.setBoolean("8", connected);
            tag.setInteger("9", folderID);
            for (int i = 0; i < connections.length; i++) {
                tag.setByte('c' + String.valueOf(i), connections[i]);
            }
            tag.setBoolean("l", chunkLoading);
        }
        if (type == NBTType.TILE_DROP) {
            tag.setInteger(ItemFluxConnector.PRIORITY, priority);
            tag.setLong(ItemFluxConnector.LIMIT, limit);
            tag.setBoolean(ItemFluxConnector.DISABLE_LIMIT, disableLimit);
            tag.setBoolean(ItemFluxConnector.SURGE_MODE, surgeMode);
            tag.setInteger(FluxNetworkData.NETWORK_ID, networkID);
            tag.setString(ItemFluxConnector.CUSTOM_NAME, customName);
            tag.setInteger(NetworkFolder.FOLDER_ID, folderID);
        }
        getTransferHandler().writeCustomNBT(tag, type);
        return tag;
    }

    public void readCustomNBT(NBTTagCompound tag, NBTType type) {
        if (type == NBTType.ALL_SAVE || type == NBTType.TILE_UPDATE) {
            priority = tag.getInteger("0");
            limit = tag.getLong("1");
            disableLimit = tag.getBoolean("2");
            surgeMode = tag.getBoolean("3");
            networkID = tag.getInteger("4");
            playerUUID = tag.getUniqueId("5");
            customName = tag.getString("6");
            color = tag.getInteger("7");
            connected = tag.getBoolean("8");
            folderID = tag.getInteger("9");
            for (int i = 0; i < connections.length; i++) {
                connections[i] = tag.getByte('c' + String.valueOf(i));
            }
            chunkLoading = tag.getBoolean("l");
        }
        if (type == NBTType.TILE_DROP) {
            priority = tag.getInteger(ItemFluxConnector.PRIORITY);
            long l;
            limit = (l = tag.getLong(ItemFluxConnector.LIMIT)) > 0 ? l : limit;
            disableLimit = tag.getBoolean(ItemFluxConnector.DISABLE_LIMIT);
            surgeMode = tag.getBoolean(ItemFluxConnector.SURGE_MODE);
            int i;
            networkID = (i = tag.getInteger(FluxNetworkData.NETWORK_ID)) > 0 ? i : networkID;
            String name;
            customName = (name = tag.getString(ItemFluxConnector.CUSTOM_NAME)).isEmpty() ? customName : name;
            folderID = tag.getInteger(NetworkFolder.FOLDER_ID);
        }
        getTransferHandler().readCustomNBT(tag, type);
    }

    public boolean canAccess(EntityPlayer player) {
        if (!network.isInvalid()) {
            if (EntityPlayer.getUUID(player.getGameProfile()).equals(playerUUID)) {
                return true;
            }
            return network.getMemberPermission(player).canAccess();
        }
        return true;
    }

    @Override
    public void writePacket(ByteBuf buf, int id) {
        switch (id) {
            case 1:
                ByteBufUtils.writeUTF8String(buf, customName);
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
    public void readPacket(ByteBuf buf, int id) {
        switch (id) {
            case 1:
                customName = ByteBufUtils.readUTF8String(buf);
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
    public NBTTagCompound copyConfiguration(NBTTagCompound config) {
        return FluxUtils.copyConfiguration(this, config);
    }

    @Override
    public void pasteConfiguration(NBTTagCompound config) {
        FluxUtils.pasteConfiguration(this, config);
    }

    @Override
    public final World getFluxWorld() {
        return world;
    }

    @Override
    public UUID getConnectionOwner() {
        return playerUUID;
    }

    public void updateTransfers(EnumFacing... facings) {
        getTransferHandler().updateTransfers(facings);
    }

    @Override
    public int getLogicPriority() {
        return surgeMode ? Integer.MAX_VALUE : priority;
    }

    @Override
    public void open(EntityPlayer player) {
        if (!world.isRemote) {
            playerUsing.add(player);
            sendPackets();
        }
    }

    @Override
    public void close(EntityPlayer player) {
        if (!world.isRemote) {
            playerUsing.remove(player);
        }
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

    @Override
    public String getCustomName() {
        return customName;
    }

    @Override
    public boolean isChunkLoaded() {
        return !isInvalid();
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
    public final long getLogicLimit() {
        return disableLimit ? Long.MAX_VALUE : limit;
    }

    @Override
    public final int getRawPriority() {
        return priority;
    }

    @Override
    public final long getRawLimit() {
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
                map.put("averageTick", network.getSetting(NetworkSettings.NETWORK_STATISTICS).averageTickMicro);
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
                map.put("buffer", getTransferHandler().getBuffer());
                return new Object[]{map};
            }
        }
        return new Object[0];
    }
}
