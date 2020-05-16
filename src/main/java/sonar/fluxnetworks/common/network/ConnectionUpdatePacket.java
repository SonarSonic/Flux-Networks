package sonar.fluxnetworks.common.network;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import sonar.fluxnetworks.common.connection.FluxNetworkCache;

import java.util.ArrayList;
import java.util.List;

public class ConnectionUpdatePacket extends AbstractPacket{

    public int networkID;
    public List<CompoundNBT> tags = new ArrayList<>();

    public ConnectionUpdatePacket(int networkID, List<CompoundNBT> tags) {
        this.networkID = networkID;
        this.tags = tags;
    }

    public ConnectionUpdatePacket(PacketBuffer buf) {
        networkID = buf.readInt();
        int size = buf.readInt();
        for(int i = 0; i < size; i++) {
            tags.add(buf.readCompoundTag());
        }
    }

    @Override
    public void encode(PacketBuffer buf) {
        buf.writeInt(networkID);
        buf.writeInt(tags.size());
        tags.forEach(buf::writeCompoundTag);
    }

    @Override
    public Object handle(NetworkEvent.Context ctx) {
        FluxNetworkCache.INSTANCE.updateClientConnections(networkID, tags);
        return null;
    }
}
