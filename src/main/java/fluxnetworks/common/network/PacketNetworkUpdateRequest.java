package fluxnetworks.common.network;

import com.google.common.collect.Lists;
import fluxnetworks.api.network.IFluxNetwork;
import fluxnetworks.common.connection.FluxNetworkCache;
import fluxnetworks.common.core.NBTType;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketNetworkUpdateRequest implements IMessageHandler<PacketNetworkUpdateRequest.UpdateRequestMessage, IMessage> {

    @Override
    public IMessage onMessage(UpdateRequestMessage message, MessageContext ctx) {
        IFluxNetwork network = FluxNetworkCache.instance.getNetwork(message.networkID);
        if(!network.isInvalid()) {
            return new PacketNetworkUpdate.NetworkUpdateMessage(Lists.newArrayList(network), message.type);
        }
        return null;
    }

    public static class UpdateRequestMessage implements IMessage {

        public int networkID;
        public NBTType type;

        public UpdateRequestMessage() {
        }

        public UpdateRequestMessage(int networkID, NBTType type) {
            this.networkID = networkID;
            this.type = type;
        }

        @Override
        public void fromBytes(ByteBuf buf) {
            networkID = buf.readInt();
            type = NBTType.values()[buf.readInt()];
        }

        @Override
        public void toBytes(ByteBuf buf) {
            buf.writeInt(networkID);
            buf.writeInt(type.ordinal());
        }
    }
}
