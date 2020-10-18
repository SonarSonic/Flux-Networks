package sonar.fluxnetworks.common.network;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import sonar.fluxnetworks.api.misc.IMessage;
import sonar.fluxnetworks.api.network.IFluxNetwork;
import sonar.fluxnetworks.common.storage.FluxNetworkData;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.IntConsumer;

public class CNetworkUpdateMessage implements IMessage {

    private IntList list;
    private int flags;

    public CNetworkUpdateMessage() {
    }

    public CNetworkUpdateMessage(int networkID, int flags) {
        list = new IntArrayList();
        list.add(networkID);
        this.flags = flags;
    }

    public CNetworkUpdateMessage(@Nonnull Collection<IFluxNetwork> networks, int flags) {
        list = new IntArrayList();
        networks.forEach(n -> list.add(n.getNetworkID()));
        this.flags = flags;
    }

    @Override
    public void encode(@Nonnull PacketBuffer buffer) {
        buffer.writeVarInt(flags);
        buffer.writeVarInt(list.size());
        list.forEach((IntConsumer) buffer::writeVarInt);
    }

    @Override
    public void handle(@Nonnull PacketBuffer buffer, @Nonnull NetworkEvent.Context context) {
        int flags = buffer.readVarInt();
        int size = buffer.readVarInt();
        List<IFluxNetwork> networks = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            IFluxNetwork network = FluxNetworkData.getNetwork(i);
            if (network.isValid()) {
                networks.add(network);
            }
        }
        if (!networks.isEmpty()) {
            NetworkHandler.INSTANCE.reply(new SNetworkUpdateMessage(networks, flags), context);
        }
    }
}
