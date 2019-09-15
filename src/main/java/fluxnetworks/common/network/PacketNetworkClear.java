package fluxnetworks.common.network;

import fluxnetworks.api.network.IFluxNetwork;
import fluxnetworks.common.connection.FluxNetworkCache;
import fluxnetworks.common.handler.PacketHandler;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

import java.util.ArrayList;
import java.util.List;

public class PacketNetworkClear implements IMessageHandler<PacketNetworkClear.NetworkClearMessage, IMessage> {

    @Override
    public IMessage onMessage(PacketNetworkClear.NetworkClearMessage message, MessageContext ctx) {
        if(ctx.side == Side.CLIENT) {
            PacketHandler.handlePacket(() -> FluxNetworkCache.instance.clearClientNetworksFromPacket(message.clearedNetworks), ctx.netHandler);
        }
        return null;
    }

    public static class NetworkClearMessage implements IMessage {

        public List<Integer> clearedNetworks = new ArrayList<>();

        public NetworkClearMessage() {}

        public NetworkClearMessage(List<IFluxNetwork> toSend) {
            toSend.forEach(n -> clearedNetworks.add(n.getNetworkID()));
        }

        @Override
        public void fromBytes(ByteBuf buf) {
            int size = buf.readInt();
            for(int i = 0; i < size; i++) {
                clearedNetworks.add(buf.readInt());
            }
        }

        @Override
        public void toBytes(ByteBuf buf) {
            buf.writeInt(clearedNetworks.size());
            clearedNetworks.forEach(buf::writeInt);
        }
    }
}
