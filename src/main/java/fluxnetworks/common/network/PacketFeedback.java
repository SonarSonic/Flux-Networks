package fluxnetworks.common.network;

import fluxnetworks.FluxNetworks;
import fluxnetworks.api.FeedbackInfo;
import fluxnetworks.common.handler.PacketHandler;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketFeedback implements IMessageHandler<PacketFeedback.FeedbackMessage, IMessage> {

    @Override
    public IMessage onMessage(FeedbackMessage message, MessageContext ctx) {
        PacketHandler.handlePacket(() -> {
            if(message.info == FeedbackInfo.SUCCESS || message.info == FeedbackInfo.SUCCESS_2 || message.info == FeedbackInfo.PASSWORD_REQUIRE) {
                FluxNetworks.proxy.setFeedback(message.info, true);
            } else {
                FluxNetworks.proxy.setFeedback(message.info, false);
            }
            }, ctx.netHandler);
        return null;
    }

    public static class FeedbackMessage implements IMessage {

        public FeedbackInfo info;

        public FeedbackMessage() {
        }

        public FeedbackMessage(FeedbackInfo info) {
            this.info = info;
        }

        @Override
        public void fromBytes(ByteBuf buf) {
            info = FeedbackInfo.values()[buf.readInt()];
        }

        @Override
        public void toBytes(ByteBuf buf) {
            buf.writeInt(info.ordinal());
        }
    }
}
