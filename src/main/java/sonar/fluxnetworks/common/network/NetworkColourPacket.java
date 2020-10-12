package sonar.fluxnetworks.common.network;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import sonar.fluxnetworks.FluxNetworks;
import net.minecraft.util.Tuple;

import java.util.HashMap;
import java.util.Map;

@Deprecated
public class NetworkColourPacket extends AbstractPacket {

    public Map<Integer, Tuple<Integer, String>> cache;

    public NetworkColourPacket(PacketBuffer buf) {
        cache = new HashMap<>();
        int size = buf.readInt();
        for (int i = 0; i < size; i++) {
            int id = buf.readInt();
            int colour = buf.readInt();
            String name = buf.readString(256);
            cache.put(id, new Tuple<>(colour, name));
        }
    }

    public NetworkColourPacket(Map<Integer, Tuple<Integer, String>> cache) {
        this.cache = cache;
    }

    @Override
    public void encode(PacketBuffer buf) {
        buf.writeInt(cache.size());
        cache.forEach((ID,DETAILS)->{
            buf.writeInt(ID);
            buf.writeInt(DETAILS.getA());
            buf.writeString(DETAILS.getB(), 256);
        });
    }

    @Override
    public Object handle(NetworkEvent.Context ctx) {
        FluxNetworks.PROXY.receiveColorCache(cache);
        return null;
    }

}
