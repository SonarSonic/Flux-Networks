package sonar.flux.network;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import sonar.flux.FluxNetworks;
import sonar.flux.api.network.IFluxNetwork;
import sonar.flux.connection.NetworkSettings;

public class PacketNetworkDeleted implements IMessage {

    public int networkID;

    public PacketNetworkDeleted(){}

    public PacketNetworkDeleted(IFluxNetwork network){
        this.networkID = network.getSetting(NetworkSettings.NETWORK_ID);
    }

    public PacketNetworkDeleted(int networkID){
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

    public static class Handler implements IMessageHandler<PacketNetworkDeleted, IMessage> {

        @Override
        public IMessage onMessage(PacketNetworkDeleted message, MessageContext ctx) {
            FluxNetworks.proxy.clearNetwork(message.networkID);
            return null;
        }
    }
}
