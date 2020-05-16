package sonar.fluxnetworks.common.network;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import sonar.fluxnetworks.api.network.IFluxNetwork;
import sonar.fluxnetworks.common.connection.FluxNetworkCache;
import sonar.fluxnetworks.api.network.NetworkSettings;
import net.minecraft.util.Tuple;
import sonar.fluxnetworks.common.handler.PacketHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NetworkColourRequestPacket extends AbstractPacket {

    List<Integer> requests;

    public NetworkColourRequestPacket(List<Integer> requests) {
        this.requests = requests;
    }

    public NetworkColourRequestPacket(PacketBuffer buf) {
        requests = new ArrayList<>();
        int size = buf.readInt();
        for(int i = 0; i < size; i++){
            requests.add(buf.readInt());
        }
    }

    @Override
    public void encode(PacketBuffer buf) {
        buf.writeInt(requests.size());
        requests.forEach(buf::writeInt);
    }

    @Override
    public Object handle(NetworkEvent.Context ctx) {
        PlayerEntity player = PacketHandler.getPlayer(ctx);
        Map<Integer, Tuple<Integer, String>> cache = new HashMap<>();
        if(!requests.isEmpty()){
            for(int id : requests){
                IFluxNetwork network = FluxNetworkCache.INSTANCE.getNetwork(id);
                cache.put(id, new Tuple<>(network.getSetting(NetworkSettings.NETWORK_COLOR) | 0xff000000, network.isInvalid() ? "NONE": network.getSetting(NetworkSettings.NETWORK_NAME)));
            } // More than one
            reply(player, new NetworkColourPacket(cache));

        }
        return null;
    }
}
