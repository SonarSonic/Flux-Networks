package fluxnetworks.common.tileentity;

import com.google.common.collect.Lists;
import fluxnetworks.api.network.IFluxNetwork;
import fluxnetworks.api.tileentity.ITileByteBuf;
import fluxnetworks.api.tileentity.IFluxConnector;
import fluxnetworks.common.connection.FluxNetworkServer;
import fluxnetworks.common.connection.NetworkSettings;
import fluxnetworks.common.core.SyncType;
import fluxnetworks.common.handler.PacketHandler;
import fluxnetworks.common.connection.FluxNetworkInvalid;
import fluxnetworks.common.network.PacketNetworkUpdate;
import fluxnetworks.common.core.FluxUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.ByteBufUtils;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.UUID;

public abstract class TileFluxCore extends TileEntity implements IFluxConnector, ITickable, ITileByteBuf {

    public HashSet<EntityPlayer> playerUsing = new HashSet<>();

    public String customName = "";
    public int networkID = -1;
    public UUID playerUUID = new UUID(-1, -1);
    public int color = -1;

    public int priority = 0;
    public long limit = 800000;

    public boolean surge = false;
    public boolean unlimited = false;

    public boolean connected = false;
    public byte[] connections = new byte[]{0,0,0,0,0,0};

    public IFluxNetwork network = FluxNetworkInvalid.instance;

    public boolean load = false;

    @Override
    public void invalidate() {
        super.invalidate();
        if(!world.isRemote && load) {
            FluxUtils.removeConnection(this, false);
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
            if(playerUsing.size() > 0)
                sendPackets();
            if(!load) {
                FluxUtils.addConnection(this);
                updateTransfers(EnumFacing.VALUES);
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
            this.color = -1;
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
        return new SPacketUpdateTileEntity(pos, -1, writeSuperNBT(new NBTTagCompound()));
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        readSuperNBT(pkt.getNbtCompound());
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

    protected void sendPackets() {
        IBlockState state = world.getBlockState(pos);
        world.notifyBlockUpdate(pos, state, state, 3);
    }

    public NBTTagCompound writeUpdateNBT(NBTTagCompound tag) {
        tag.setBoolean("connected", connected);
        for(int i = 0; i < connections.length; i++) {
            tag.setByte(String.valueOf(i), connections[i]);
        }
        return tag;
    }

    public void readUpdateNBT(NBTTagCompound tag) {
        connected = tag.getBoolean("connected");
        for(int i = 0; i < connections.length; i++) {
            connections[i] = tag.getByte(String.valueOf(i));
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        writeCustomNBT(compound);
        writeUpdateNBT(compound);
        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        readCustomNBT(compound);
        readUpdateNBT(compound);
    }

    public NBTTagCompound writeCustomNBT(NBTTagCompound tag) {
        tag.setInteger("CachedColor", color);
        tag.setInteger("NetworkID", networkID);
        tag.setString("CustomName", customName);
        tag.setInteger("Priority", priority);
        tag.setBoolean("Surge", surge);
        tag.setLong("Limit", limit);
        tag.setBoolean("Unlimited", unlimited);
        return tag;
    }

    public void readCustomNBT(NBTTagCompound tag) {
        color = tag.getInteger("CachedColor");
        networkID = tag.getInteger("NetworkID");
        customName = tag.getString("CustomName");
        priority = tag.getInteger("Priority");
        surge = tag.getBoolean("Surge");
        limit = tag.getLong("Limit");
        unlimited = tag.getBoolean("Unlimited");
    }

    public NBTTagCompound writeSuperNBT(NBTTagCompound tag) {
        writeCustomNBT(tag);
        writeUpdateNBT(tag);
        if(getTransferHandler() != null)
            getTransferHandler().writeNetworkedNBT(tag);
        return tag;
    }

    public void readSuperNBT(NBTTagCompound tag) {
        readCustomNBT(tag);
        readUpdateNBT(tag);
        if(getTransferHandler() != null)
            getTransferHandler().readNetworkedNBT(tag);
    }

    public boolean canAccess(EntityPlayer player) {
        if(!network.isInvalid()) {
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
        }
    }

    @Override
    public void readPacket(ByteBuf buf, int id) {
        switch (id) {
            case 1:
                customName = ByteBufUtils.readUTF8String(buf);
                break;
            case 2:
                priority = buf.readInt();
                if (network instanceof FluxNetworkServer) {
                    FluxNetworkServer fluxNetworkServer = (FluxNetworkServer) network;
                    fluxNetworkServer.sortConnections = true;
                }
                break;
            case 3:
                limit = buf.readLong();
                break;
        }
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
        if(getTransferHandler() != null)
            getTransferHandler().updateTransfers(facings);
    }

    @Override
    public int getPriority() {
        return surge ? Integer.MAX_VALUE : priority;
    }

    public void open(EntityPlayer player) {
        playerUsing.add(player);
        if(!world.isRemote) {
            PacketHandler.network.sendTo(new PacketNetworkUpdate.NetworkUpdateMessage(Lists.newArrayList(getNetwork()), SyncType.ALL), (EntityPlayerMP) player);
            sendPackets();
        }
    }

    public void close(EntityPlayer player) {
        playerUsing.remove(player);
    }

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
    public long getCurrentLimit() {
        return unlimited ? Long.MAX_VALUE : limit;
    }
}
