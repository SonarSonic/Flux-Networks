package sonar.fluxnetworks.common.network;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import sonar.fluxnetworks.api.misc.FeedbackInfo;
import sonar.fluxnetworks.api.misc.IMessage;
import sonar.fluxnetworks.api.network.IFluxNetwork;
import sonar.fluxnetworks.common.misc.FluxUtils;
import sonar.fluxnetworks.common.storage.FluxNetworkData;

import javax.annotation.Nonnull;

public class CDeleteNetworkMessage implements IMessage {

    private int networkID;

    public CDeleteNetworkMessage() {
    }

    public CDeleteNetworkMessage(int networkID) {
        this.networkID = networkID;
    }

    @Override
    public void encode(@Nonnull PacketBuffer buffer) {
        buffer.writeVarInt(networkID);
    }

    @Override
    public final void handle(@Nonnull PacketBuffer buffer, @Nonnull NetworkEvent.Context context) {
        PlayerEntity player = FluxUtils.getPlayer(context);
        if (player == null) {
            return;
        }
        int id = buffer.readVarInt();
        IFluxNetwork network = FluxNetworkData.getNetwork(id);
        handle(buffer, context, player, network);
    }

    protected void handle(@Nonnull PacketBuffer buffer, @Nonnull NetworkEvent.Context context, @Nonnull PlayerEntity player,
                          @Nonnull IFluxNetwork network) {
        if (network.isValid()) {
            if (network.getPlayerAccess(player).canDelete()) {
                FluxNetworkData.get().deleteNetwork(network);
                NetworkHandler.INSTANCE.reply(new SFeedbackMessage(FeedbackInfo.SUCCESS), context);
            } else {
                NetworkHandler.INSTANCE.reply(new SFeedbackMessage(FeedbackInfo.NO_OWNER), context);
            }
        }
    }
}
