package sonar.flux.network;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import sonar.flux.FluxNetworks;

public class PacketClearNetwork implements IMessage {

    public int networkID;

    public PacketClearNetwork(){}

    public PacketClearNetwork(int networkID){
        this.networkID = networkID;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        networkID = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(networkID);
    }

    public static class Handler implements IMessageHandler<PacketClearNetwork, IMessage> {

        @Override
        public IMessage onMessage(PacketClearNetwork message, MessageContext ctx) {
            FluxNetworks.proxy.clearNetwork(message.networkID);
            return null;
        }
    }
}
