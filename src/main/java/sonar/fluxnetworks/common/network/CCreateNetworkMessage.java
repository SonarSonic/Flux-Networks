package sonar.fluxnetworks.common.network;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import sonar.fluxnetworks.api.gui.EnumFeedbackInfo;
import sonar.fluxnetworks.api.misc.IMessage;
import sonar.fluxnetworks.api.network.NetworkSecurity;
import sonar.fluxnetworks.common.handler.NetworkHandler;
import sonar.fluxnetworks.common.misc.FluxUtils;
import sonar.fluxnetworks.common.storage.FluxNetworkData;

import javax.annotation.Nonnull;

public class CCreateNetworkMessage implements IMessage {

    private String name;
    private int color;
    private NetworkSecurity.Type security;
    private String password;

    public CCreateNetworkMessage() {
    }

    public CCreateNetworkMessage(String name, int color, NetworkSecurity.Type security, String password) {
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
        PlayerEntity player = NetworkHandler.getPlayer(context);
        if (player == null) {
            return;
        }
        String name = buffer.readString(256);
        int color = buffer.readInt();
        NetworkSecurity.Type security = NetworkSecurity.Type.values()[buffer.readVarInt()];
        String password = buffer.readString(256);
        if (FluxUtils.checkPassword(password)) {
            handle(buffer, context, player, name, color, security, password);
        } else {
            NetworkHandler.INSTANCE.reply(new SFeedbackMessage(EnumFeedbackInfo.ILLEGAL_PASSWORD), context);
        }
    }

    protected void handle(@Nonnull PacketBuffer buffer, @Nonnull NetworkEvent.Context context, PlayerEntity player,
                          String name, int color, NetworkSecurity.Type security, String password) {
        if (FluxNetworkData.get().createNetwork(player, name, color, security, password) != null) {
            NetworkHandler.INSTANCE.reply(new SFeedbackMessage(EnumFeedbackInfo.SUCCESS), context);
        } else {
            NetworkHandler.INSTANCE.reply(new SFeedbackMessage(EnumFeedbackInfo.NO_SPACE), context);
        }
    }
}
