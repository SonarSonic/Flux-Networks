package fluxnetworks.common.network;

import fluxnetworks.api.network.IFluxNetwork;
import fluxnetworks.common.connection.FluxNetworkCache;
import fluxnetworks.common.connection.NetworkSettings;
import io.netty.buffer.ByteBuf;
import net.minecraft.util.Tuple;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PacketColorRequest implements IMessageHandler<PacketColorRequest.ColorRequestMessage, IMessage> {

    @Override
    public IMessage onMessage(ColorRequestMessage message, MessageContext ctx) {
        Map<Integer, Tuple<Integer, String>> cache = new HashMap();
        if(!message.requests.isEmpty()){
            for(int id : message.requests){
                IFluxNetwork network = FluxNetworkCache.instance.getNetwork(id);
                cache.put(id, new Tuple<>(network.getSetting(NetworkSettings.NETWORK_COLOR) | 0xff000000, network.isInvalid() ? "NONE": network.getSetting(NetworkSettings.NETWORK_NAME)));
            } // More than one
            return new PacketColorCache.ColorCacheMessage(cache);
        }
        return null;
    }

    public static class ColorRequestMessage implements IMessage {

        List<Integer> requests;

        public ColorRequestMessage() {
        }

        public ColorRequestMessage(List<Integer> requests) {
            this.requests = requests;
        }

        @Override
        public void fromBytes(ByteBuf buf) {
            requests = new ArrayList<>();
            int size = buf.readInt();
            for(int i = 0; i < size; i++){
                requests.add(buf.readInt());
            }
        }

        @Override
        public void toBytes(ByteBuf buf) {
            buf.writeInt(requests.size());
            requests.forEach(buf::writeInt);
        }
    }
}
