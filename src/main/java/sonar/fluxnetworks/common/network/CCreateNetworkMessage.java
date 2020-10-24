package sonar.fluxnetworks.common.network;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import sonar.fluxnetworks.api.misc.FeedbackInfo;
import sonar.fluxnetworks.api.misc.IMessage;
import sonar.fluxnetworks.api.network.SecurityType;
import sonar.fluxnetworks.common.misc.FluxUtils;
import sonar.fluxnetworks.common.storage.FluxNetworkData;

import javax.annotation.Nonnull;

public class CCreateNetworkMessage implements IMessage {

    protected String name;
    protected int color;
    protected SecurityType security;
    protected String password;

    public CCreateNetworkMessage() {
    }

    public CCreateNetworkMessage(String name, int color, SecurityType security, String password) {
        this.name = name;
        this.color = color;
        this.security = security;
        this.password = password;
    }

    @Override
    public void encode(@Nonnull PacketBuffer buffer) {
        buffer.writeString(name, 256);
        buffer.writeInt(color);
        buffer.writeVarInt(security.ordinal());
        buffer.writeString(password, 256);
    }

    @Override
    public final void handle(@Nonnull PacketBuffer buffer, @Nonnull NetworkEvent.Context context) {
        PlayerEntity player = FluxUtils.getPlayer(context);
        if (player == null) {
            return;
        }
        name = buffer.readString(256);
        color = buffer.readInt();
        security = SecurityType.values()[buffer.readVarInt()];
        password = buffer.readString(256);
        if (FluxUtils.checkPassword(password)) {
            handle(buffer, context, player);
        } else {
            NetworkHandler.INSTANCE.reply(new SFeedbackMessage(FeedbackInfo.ILLEGAL_PASSWORD), context);
        }
    }

    protected void handle(@Nonnull PacketBuffer buffer, @Nonnull NetworkEvent.Context context, PlayerEntity player) {
        if (FluxNetworkData.get().createNetwork(player, name, color, security, password) != null) {
            NetworkHandler.INSTANCE.reply(new SFeedbackMessage(FeedbackInfo.SUCCESS), context);
        } else {
            NetworkHandler.INSTANCE.reply(new SFeedbackMessage(FeedbackInfo.NO_SPACE), context);
        }
    }
}
