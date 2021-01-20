package sonar.fluxnetworks.common.network;

import sonar.fluxnetworks.FluxConfig;
import sonar.fluxnetworks.api.utils.Coord4D;
import sonar.fluxnetworks.api.gui.EnumFeedbackInfo;
import sonar.fluxnetworks.api.network.FluxLogicType;
import sonar.fluxnetworks.api.network.IFluxNetwork;
import sonar.fluxnetworks.common.connection.FluxNetworkCache;
import sonar.fluxnetworks.common.core.FluxUtils;
import sonar.fluxnetworks.common.data.FluxChunkManager;
import sonar.fluxnetworks.common.handler.PacketHandler;
import sonar.fluxnetworks.common.item.ItemFluxConnector;
import sonar.fluxnetworks.common.tileentity.TileFluxCore;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.ChunkPos;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

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
                    List<TileFluxCore> onlineConnectors = network.getConnections(FluxLogicType.ANY);
                    AtomicBoolean reject = new AtomicBoolean(false);
                    PacketHandler.handlePacket(() -> {
                        message.coord4DS.forEach(c -> onlineConnectors.stream().filter(f -> f.getCoords().equals(c)).findFirst().ifPresent(f -> {
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
                                f.limit = Math.min(limit, f.getMaxTransferLimit());
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
                                        if(f.getConnectionType().isStorage()) {
                                            reject.set(true);
                                            return;
                                        }
                                        if(!f.chunkLoading) {
                                            f.chunkLoading = FluxChunkManager.forceChunk(f.getWorld(), new ChunkPos(f.getPos()));
                                            if(!f.chunkLoading) {
                                                reject.set(true);
                                            }
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
                        }));
                        if(reject.get()) {
                            PacketHandler.network.sendTo(new PacketFeedback.FeedbackMessage(EnumFeedbackInfo.REJECT_SOME), (EntityPlayerMP) player);
                        }
                    }, ctx.netHandler);
                    return disconnect ? new PacketFeedback.FeedbackMessage(EnumFeedbackInfo.SUCCESS_2) : new PacketFeedback.FeedbackMessage(EnumFeedbackInfo.SUCCESS);
                } else {
                    return new PacketFeedback.FeedbackMessage(EnumFeedbackInfo.NO_ADMIN);
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
