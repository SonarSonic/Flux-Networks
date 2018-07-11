package sonar.flux.network;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import sonar.flux.FluxNetworks;
import sonar.flux.api.network.IFluxNetwork;
import sonar.flux.connection.NetworkSettings;

public class PacketConnectionsRefresh implements IMessage {

    public int networkID;

    public PacketConnectionsRefresh(){}

    public PacketConnectionsRefresh(int networkID){
        this.networkID=networkID;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        networkID = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(networkID);
    }

    public static class Handler implements IMessageHandler<PacketConnectionsRefresh, IMessage> {

        @Override
        public IMessage onMessage(PacketConnectionsRefresh message, MessageContext ctx) {
            IFluxNetwork network = FluxNetworks.getServerCache().getNetwork(message.networkID);
            if(!network.isFakeNetwork()) {
                network.getSyncSetting(NetworkSettings.UNLOADED_CONNECTIONS).setDirty(false);
                return new PacketConnectionsClientList(network);
            }
            return null;
        }

    }
}
