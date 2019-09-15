package fluxnetworks.common.network;

import fluxnetworks.FluxConfig;
import fluxnetworks.FluxNetworks;
import fluxnetworks.api.Coord4D;
import fluxnetworks.api.FeedbackInfo;
import fluxnetworks.api.network.FluxType;
import fluxnetworks.api.network.IFluxNetwork;
import fluxnetworks.api.tileentity.IFluxConnector;
import fluxnetworks.common.connection.FluxNetworkCache;
import fluxnetworks.common.core.FluxUtils;
import fluxnetworks.common.data.FluxChunkManager;
import fluxnetworks.common.handler.PacketHandler;
import fluxnetworks.common.item.ItemFluxConnector;
import fluxnetworks.common.tileentity.TileFluxCore;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.ChunkPos;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import scala.util.control.Exception;

import java.util.ArrayList;
import java.util.List;

public class PacketBatchEditing implements IMessageHandler<PacketBatchEditing.BatchEditingMessage, IMessage> {

    @Override
    public IMessage onMessage(BatchEditingMessage message, MessageContext ctx) {
        EntityPlayer player = PacketHandler.getPlayer(ctx);
        if(player != null) {
            IFluxNetwork network = FluxNetworkCache.instance.getNetwork(message.networkID);
            if(!network.isInvalid()) {
                if(network.getMemberPermission(player).canEdit()) {
                    boolean editName = message.editions[0];
                    boolean editPriority = message.editions[1];
                    boolean editLimit = message.editions[2];
                    boolean editSurge = message.editions[3];
                    boolean editUnlimited = message.editions[4];
                    boolean editChunkLoad = message.editions[5];
                    boolean disconnect = message.editions[6];
                    String name = message.tag.getString(ItemFluxConnector.CUSTOM_NAME);
                    int priority = message.tag.getInteger(ItemFluxConnector.PRIORITY);
                    long limit = message.tag.getLong(ItemFluxConnector.LIMIT);
                    boolean surge = message.tag.getBoolean(ItemFluxConnector.SURGE_MODE);
                    boolean unlimited = message.tag.getBoolean(ItemFluxConnector.DISABLE_LIMIT);
                    boolean load = message.tag.getBoolean("chunkLoad");
                    //noinspection unchecked
                    List<TileFluxCore> onlineConnectors = network.getConnections(FluxType.flux);
                    PacketHandler.handlePacket(() -> message.coord4DS.forEach(c -> onlineConnectors.stream().filter(f -> f.getCoords().equals(c)).findFirst().ifPresent(f -> {
                        if(disconnect) {
                            FluxUtils.removeConnection(f, false);
                            f.disconnect(network);
                        } else {
                            if(editName) {
                                f.customName = name;
                            }
                            if(editPriority) {
                                f.priority = priority;
                            }
                            if(editLimit) {
                                if(f.getConnectionType().isStorage()) {
                                    f.limit = f.getCurrentLimit(); // Always max transfer
                                    return; // Following settings are disabled for storage
                                }
                                f.limit = limit;
                            }
                            if(editSurge) {
                                f.surgeMode = surge;
                            }
                            if(editUnlimited) {
                                f.disableLimit = unlimited;
                            }
                            if(editChunkLoad) {
                                if (FluxConfig.enableChunkLoading) {
                                    if (load) {
                                        if(!f.chunkLoading) {
                                            f.chunkLoading = FluxChunkManager.forceChunk(f.getWorld(), new ChunkPos(f.getPos()));
                                        }
                                    } else {
                                        FluxChunkManager.releaseChunk(f.getWorld(), new ChunkPos(f.getPos()));
                                        f.chunkLoading = false;
                                    }
                                } else {
                                    f.chunkLoading = false;
                                }
                            }
                            f.sendPackets();
                        }
                    })), ctx.netHandler);
                    return disconnect ? new PacketFeedback.FeedbackMessage(FeedbackInfo.SUCCESS_2) : new PacketFeedback.FeedbackMessage(FeedbackInfo.SUCCESS);
                } else {
                    return new PacketFeedback.FeedbackMessage(FeedbackInfo.NO_ADMIN);
                }
            }
        }
        return null;
    }

    public static class BatchEditingMessage implements IMessage {

        public int networkID;
        public List<Coord4D> coord4DS = new ArrayList<>();
        public NBTTagCompound tag;
        public boolean[] editions = new boolean[7];

        public BatchEditingMessage() {}

        public BatchEditingMessage(int networkID, List<Coord4D> coord4DS, NBTTagCompound tag, boolean[] editions) {
            this.networkID = networkID;
            this.coord4DS = coord4DS;
            this.tag = tag;
            this.editions = editions;
        }

        @Override
        public void fromBytes(ByteBuf buf) {
            networkID = buf.readInt();
            for(int i = 0; i < 7; i++) {
                editions[i] = buf.readBoolean();
            }
            tag = ByteBufUtils.readTag(buf);
            int size = buf.readInt();
            for(int i = 0; i < size; i++) {
                coord4DS.add(new Coord4D(buf));
            }
        }

        @Override
        public void toBytes(ByteBuf buf) {
            buf.writeInt(networkID);
            for(Boolean b : editions) {
                buf.writeBoolean(b);
            }
            ByteBufUtils.writeTag(buf, tag);
            buf.writeInt(coord4DS.size());
            coord4DS.forEach(c -> c.write(buf));
        }
    }
}
