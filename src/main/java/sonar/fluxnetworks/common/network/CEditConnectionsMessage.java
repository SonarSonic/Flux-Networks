package sonar.fluxnetworks.common.network;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkEvent;
import sonar.fluxnetworks.FluxConfig;
import sonar.fluxnetworks.api.device.IFluxDevice;
import sonar.fluxnetworks.api.misc.FeedbackInfo;
import sonar.fluxnetworks.api.misc.FluxConstants;
import sonar.fluxnetworks.api.misc.IMessage;
import sonar.fluxnetworks.api.network.IFluxNetwork;
import sonar.fluxnetworks.common.misc.FluxUtils;
import sonar.fluxnetworks.common.storage.FluxChunkManager;
import sonar.fluxnetworks.common.storage.FluxNetworkData;
import sonar.fluxnetworks.common.tileentity.TileFluxDevice;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class CEditConnectionsMessage implements IMessage {

    private int networkID;
    private List<GlobalPos> list;
    private int flags;
    private String name;
    private int priority;
    private long limit;
    private boolean surgeMode;
    private boolean disableLimit;
    private boolean chunkLoading;

    public CEditConnectionsMessage() {
    }

    public CEditConnectionsMessage(int networkID, List<GlobalPos> list, int flags) {
        this.networkID = networkID;
        this.list = list;
        this.flags = flags;
    }

    public CEditConnectionsMessage(int networkID, List<GlobalPos> list, int flags, String name, int priority,
                                   long limit, boolean surgeMode, boolean disableLimit, boolean chunkLoading) {
        this.networkID = networkID;
        this.list = list;
        this.flags = flags;
        this.name = name;
        this.priority = priority;
        this.limit = limit;
        this.surgeMode = surgeMode;
        this.disableLimit = disableLimit;
        this.chunkLoading = chunkLoading;
    }

    @Override
    public void encode(@Nonnull PacketBuffer buffer) {
        buffer.writeVarInt(networkID);
        buffer.writeVarInt(flags);
        buffer.writeVarInt(list.size());
        list.forEach(pos -> FluxUtils.writeGlobalPos(buffer, pos));
        if ((flags & FluxConstants.FLAG_EDIT_NAME) != 0) {
            buffer.writeString(name, 0x100);
        }
        if ((flags & FluxConstants.FLAG_EDIT_PRIORITY) != 0) {
            buffer.writeInt(priority);
        }
        if ((flags & FluxConstants.FLAG_EDIT_LIMIT) != 0) {
            buffer.writeLong(limit);
        }
        if ((flags & FluxConstants.FLAG_EDIT_SURGE_MODE) != 0) {
            buffer.writeBoolean(surgeMode);
        }
        if ((flags & FluxConstants.FLAG_EDIT_DISABLE_LIMIT) != 0) {
            buffer.writeBoolean(disableLimit);
        }
        if ((flags & FluxConstants.FLAG_EDIT_CHUNK_LOADING) != 0) {
            buffer.writeBoolean(chunkLoading);
        }
    }

    @Override
    public void handle(@Nonnull PacketBuffer buffer, @Nonnull NetworkEvent.Context context) {
        PlayerEntity player = NetworkHandler.getPlayer(context);
        if (player == null) {
            return;
        }
        IFluxNetwork network = FluxNetworkData.getNetwork(buffer.readVarInt());
        if (network.getPlayerAccess(player).canEdit()) {
            NetworkHandler.INSTANCE.reply(new SFeedbackMessage(FeedbackInfo.NO_ADMIN), context);
            return;
        }
        int flags = buffer.readVarInt();
        int size = buffer.readVarInt();
        if (size == 0) {
            return;
        }
        List<IFluxDevice> toEdit = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            network.getConnectionByPos(FluxUtils.readGlobalPos(buffer)).ifPresent(toEdit::add);
        }
        if (toEdit.isEmpty()) {
            return;
        }
        if ((flags & FluxConstants.FLAG_EDIT_DISCONNECT) != 0) {
            toEdit.forEach(d -> d.getNetwork().enqueueConnectionRemoval(d, false));
            NetworkHandler.INSTANCE.reply(new SFeedbackMessage(FeedbackInfo.SUCCESS_2), context);
        } else {
            boolean editName = (flags & FluxConstants.FLAG_EDIT_NAME) != 0;
            boolean editPriority = (flags & FluxConstants.FLAG_EDIT_PRIORITY) != 0;
            boolean editLimit = (flags & FluxConstants.FLAG_EDIT_LIMIT) != 0;
            boolean editSurgeMode = (flags & FluxConstants.FLAG_EDIT_SURGE_MODE) != 0;
            boolean editDisableLimit = (flags & FluxConstants.FLAG_EDIT_DISABLE_LIMIT) != 0;
            boolean editChunkLoading = (flags & FluxConstants.FLAG_EDIT_CHUNK_LOADING) != 0;
            String name = null;
            int priority = 0;
            long limit = 0;
            boolean surgeMode = false;
            boolean disableLimit = false;
            boolean chunkLoading = false;
            if (editName) {
                name = buffer.readString(0x100);
            }
            if (editPriority) {
                priority = buffer.readInt();
            }
            if (editLimit) {
                limit = buffer.readLong();
            }
            if (editSurgeMode) {
                surgeMode = buffer.readBoolean();
            }
            if (editDisableLimit) {
                disableLimit = buffer.readBoolean();
            }
            if (editChunkLoading) {
                chunkLoading = buffer.readBoolean();
            }
            boolean sendBannedLoading = false;
            for (IFluxDevice d : toEdit) {
                if (!(d instanceof TileFluxDevice)) {
                    continue;
                }
                TileFluxDevice t = (TileFluxDevice) d;
                if (editName) {
                    t.setCustomName(name);
                }
                if (editPriority) {
                    t.setPriority(priority);
                }
                if (editLimit) {
                    t.setTransferLimit(limit);
                }
                if (editSurgeMode) {
                    t.setSurgeMode(surgeMode);
                }
                if (editDisableLimit) {
                    t.setDisableLimit(disableLimit);
                }
                if (editChunkLoading) {
                    if (FluxConfig.enableChunkLoading) {
                        if (chunkLoading && !t.isForcedLoading()) {
                            FluxChunkManager.addChunkLoader((ServerWorld) t.getFluxWorld(), t);
                            t.setForcedLoading(true);
                        } else if (!chunkLoading && t.isForcedLoading()) {
                            FluxChunkManager.removeChunkLoader((ServerWorld) t.getFluxWorld(), t);
                            t.setForcedLoading(true);
                        }
                    } else {
                        t.setForcedLoading(true);
                        sendBannedLoading = true;
                    }
                }
                t.sendFullUpdatePacket();
            }
            NetworkHandler.INSTANCE.reply(new SFeedbackMessage(FeedbackInfo.SUCCESS), context);
            if (sendBannedLoading) {
                NetworkHandler.INSTANCE.reply(new SFeedbackMessage(FeedbackInfo.BANNED_LOADING), context);
            }
        }
    }
}
