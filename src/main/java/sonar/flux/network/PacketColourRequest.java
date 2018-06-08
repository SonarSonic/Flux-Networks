package sonar.flux.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.util.Tuple;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import sonar.flux.api.network.IFluxNetwork;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PacketColourRequest implements IMessage {

    public List<Integer> requests;

    public PacketColourRequest(){}

    public PacketColourRequest(List<Integer> requests){
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


    public static class Handler implements IMessageHandler<PacketColourRequest, IMessage> {

        @Override
        public IMessage onMessage(PacketColourRequest message, MessageContext ctx) {
            Map<Integer, Tuple<Integer, String>> cache = new HashMap();
            if(!message.requests.isEmpty()){
                for(int id : message.requests){
                    IFluxNetwork network = FluxNetworkCache.instance().getNetwork(id);
                    cache.put(id, new Tuple<>(network.getNetworkColour().getRGB(), network.isFakeNetwork() ? "NONE": network.getNetworkName()));
                    return new PacketColourCache(cache);
                }
            }
            return null;
        }
    }
}
