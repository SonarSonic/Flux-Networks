package sonar.fluxnetworks.common.network;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import sonar.fluxnetworks.api.misc.FeedbackInfo;
import sonar.fluxnetworks.api.misc.FluxConstants;
import sonar.fluxnetworks.api.misc.IMessage;
import sonar.fluxnetworks.api.network.IFluxNetwork;
import sonar.fluxnetworks.common.storage.FluxNetworkData;

import javax.annotation.Nonnull;

public class CEditWirelessMessage implements IMessage {

    private int networkID;
    private int wireless;

    public CEditWirelessMessage() {
    }

    public CEditWirelessMessage(int networkID, int wireless) {
        this.networkID = networkID;
        this.wireless = wireless;
    }

    @Override
    public void encode(@Nonnull PacketBuffer buffer) {
        buffer.writeVarInt(networkID);
        buffer.writeVarInt(wireless);
    }

    @Override
    public void handle(@Nonnull PacketBuffer buffer, @Nonnull NetworkEvent.Context context) {
        PlayerEntity player = NetworkHandler.getPlayer(context);
        if (player == null) {
            return;
        }
        int networkID = buffer.readVarInt();
        IFluxNetwork network = FluxNetworkData.getNetwork(networkID);
        if (!network.isValid()) {
            return;
        }
        if (network.getPlayerAccess(player).canEdit()) {
            network.setWirelessMode(buffer.readVarInt());
            NetworkHandler.INSTANCE.reply(new SNetworkUpdateMessage(network, FluxConstants.TYPE_NET_BASIC), context);
            NetworkHandler.INSTANCE.reply(new SFeedbackMessage(FeedbackInfo.SUCCESS), context);
        } else {
            NetworkHandler.INSTANCE.reply(new SFeedbackMessage(FeedbackInfo.NO_ADMIN), context);
        }
    }
}
