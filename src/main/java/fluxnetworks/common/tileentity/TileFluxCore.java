package fluxnetworks.common.tileentity;

import fluxnetworks.FluxConfig;
import fluxnetworks.api.network.NetworkFolder;
import fluxnetworks.api.network.NetworkSettings;
import fluxnetworks.api.utils.Coord4D;
import fluxnetworks.api.network.IFluxNetwork;
import fluxnetworks.api.tiles.IFluxConfigurable;
import fluxnetworks.api.tiles.ITileByteBuf;
import fluxnetworks.api.tiles.IFluxConnector;
import fluxnetworks.common.connection.*;
import fluxnetworks.api.utils.NBTType;
import fluxnetworks.common.data.FluxChunkManager;
import fluxnetworks.common.data.FluxNetworkData;
import fluxnetworks.common.integration.oc.IOCPeripheral;
import fluxnetworks.common.item.ItemFluxConnector;
import fluxnetworks.common.core.FluxUtils;
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

import javax.annotation.Nullable;
import java.util.*;

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
    public byte[] connections = new byte[]{0,0,0,0,0,0};

    public boolean chunkLoading = false;

    protected IFluxNetwork network = FluxNetworkInvalid.instance;

    protected boolean load = false;

    @Override
    public void invalidate() {
        super.invalidate();
        if(!world.isRemote && load) {
            FluxUtils.removeConnection(this, false);
            if(chunkLoading) {
                FluxChunkManager.releaseChunk(world, new ChunkPos(pos));
            }
            load = false;
        }
    }

    @Override
    public void onChunkUnload() {
        super.onChunkUnload();
        if(!world.isRemote && load) {
            FluxUtils.removeConnection(this, true);
            load = false;
        }
    }

    @Override
    public void update() {
        if(!world.isRemote) {
            if(playerUsing.size() > 0) {
                sendPackets();
            }
            if(!load) {
                if(!FluxUtils.addConnection(this)) {
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
        if(type == NBTType.ALL_SAVE || type == NBTType.TILE_UPDATE) {
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
            for(int i = 0; i < connections.length; i++) {
                tag.setByte('c' + String.valueOf(i), connections[i]);
            }
            tag.setLong("buf", ((FluxTransferHandler) getTransferHandler()).buffer);
            tag.setBoolean("l", chunkLoading);
        }
        if(type == NBTType.TILE_UPDATE) {
            getTransferHandler().writeNetworkedNBT(tag);
        }
        if(type == NBTType.TILE_DROP) {
            tag.setLong("buffer", ((FluxTransferHandler) getTransferHandler()).buffer);
            tag.setInteger(ItemFluxConnector.PRIORITY, priority);
            tag.setLong(ItemFluxConnector.LIMIT, limit);
            tag.setBoolean(ItemFluxConnector.DISABLE_LIMIT, disableLimit);
            tag.setBoolean(ItemFluxConnector.SURGE_MODE, surgeMode);
            tag.setInteger(FluxNetworkData.NETWORK_ID, networkID);
            tag.setString(ItemFluxConnector.CUSTOM_NAME, customName);
            tag.setInteger(NetworkFolder.FOLDER_ID, folderID);
        }

        return tag;
    }

    public void readCustomNBT(NBTTagCompound tag, NBTType type) {
        if(type == NBTType.ALL_SAVE || type == NBTType.TILE_UPDATE) {
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
    }

    public boolean canAccess(EntityPlayer player) {
        if(!network.isInvalid()) {
            if(EntityPlayer.getUUID(player.getGameProfile()).equals(playerUUID)) {
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
    public World getDimension() {
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
    public int getPriority() {
        return surgeMode ? Integer.MAX_VALUE : priority;
    }

    public void open(EntityPlayer player) {
        if(!world.isRemote) {
            playerUsing.add(player);
            sendPackets();
        }
    }

    public void close(EntityPlayer player) {
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

    @Override
    public String[] getOCMethods() {
        return new String[]{"getNetworkInfo", "getCountInfo", "getEnergyInfo", "getFluxInfo", "getNetworkDevices"};
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
            case "getNetworkDevices": {
                List<IFluxConnector> connectors = network.getSetting(NetworkSettings.ALL_CONNECTORS);
                Map<Object, Object> connections = new HashMap<>();

                for(int i = 0; i < connectors.size(); i++) {
                    IFluxConnector connector = connectors.get(i);

                    Map<Object, Object> mapItem = new HashMap<>();

                    mapItem.put("connectionType", connector.getConnectionType());
                    mapItem.put("transfer", connector.getChange());
                    mapItem.put("priority", connector.getPriority());
                    mapItem.put("active", connector.isActive());
                    mapItem.put("chunkLoaded", connector.isChunkLoaded());
                    mapItem.put("customName", connector.getCustomName());
                    mapItem.put("transferLimit", connector.getCurrentLimit());
                    mapItem.put("surgeMode", connector.getSurgeMode());
                    mapItem.put("unlimited", connector.getDisableLimit());
                    mapItem.put("owner", connector.getConnectionOwner().toString());
                    mapItem.put("dimension", connector.getDimension().getProviderName());

                    connections.put(i, mapItem);
                }

                return new Object[]{connections};
            }
        }
        return new Object[0];
    }
}
