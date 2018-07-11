package sonar.flux.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.util.Tuple;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import sonar.flux.FluxNetworks;

import java.util.HashMap;
import java.util.Map;

public class PacketColourCache implements IMessage {

    public Map<Integer, Tuple<Integer, String>> cache;

    public PacketColourCache() {}

    public PacketColourCache(Map<Integer, Tuple<Integer, String>> cache) {
        this.cache = cache;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        cache = new HashMap<>();
        int size = buf.readInt();
        for (int i = 0; i < size; i++) {
            int id = buf.readInt();
            int colour = buf.readInt();
            String name = ByteBufUtils.readUTF8String(buf);
            cache.put(id, new Tuple<>(colour, name));
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(cache.size());
        cache.forEach((ID,DETAILS)->{
            buf.writeInt(ID);
            buf.writeInt(DETAILS.getFirst());
            ByteBufUtils.writeUTF8String(buf, DETAILS.getSecond());
        });
    }

    public static class Handler implements IMessageHandler<PacketColourCache, IMessage> {

        @Override
        public IMessage onMessage(PacketColourCache message, MessageContext ctx) {
            FluxNetworks.proxy.receiveColourCache(message.cache);
            return null;
        }
    }

}
