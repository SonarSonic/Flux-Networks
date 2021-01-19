package sonar.fluxnetworks.common.network;

import sonar.fluxnetworks.FluxNetworks;
import io.netty.buffer.ByteBuf;
import net.minecraft.util.Tuple;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.HashMap;
import java.util.Map;

public class PacketColorCache implements IMessageHandler<PacketColorCache.ColorCacheMessage, IMessage> {

    @Override
    public IMessage onMessage(ColorCacheMessage message, MessageContext ctx) {
        FluxNetworks.proxy.receiveColorCache(message.cache);
        return null;
    }

    public static class ColorCacheMessage implements IMessage {

        public Map<Integer, Tuple<Integer, String>> cache;

        public ColorCacheMessage() {
        }

        public ColorCacheMessage(Map<Integer, Tuple<Integer, String>> cache) {
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
    }
}
