package sonar.fluxnetworks.common.network;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import sonar.fluxnetworks.api.network.IFluxNetwork;
import sonar.fluxnetworks.common.connection.FluxNetworkCache;
import sonar.fluxnetworks.api.misc.NBTType;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NetworkUpdatePacket extends AbstractPacket {

    public Map<Integer, CompoundNBT> updatedNetworks = new HashMap<>();
    public NBTType type;

    public NetworkUpdatePacket(PacketBuffer buf) {
        type = NBTType.values()[buf.readInt()];
        int size = buf.readInt();
        for(int i = 0; i < size; i++) {
            updatedNetworks.put(buf.readInt(), buf.readCompoundTag());
        }
    }

    public NetworkUpdatePacket(List<IFluxNetwork> toSend, NBTType type) {
        this.type = type;
        toSend.forEach(n -> {
            CompoundNBT tag = new CompoundNBT();
            n.writeNetworkNBT(tag, type);
            if(!tag.isEmpty()) {
                updatedNetworks.put(n.getNetworkID(), tag);
            }
        });
    }

    @Override
    public void encode(PacketBuffer buf) {
        buf.writeInt(type.ordinal());
        buf.writeInt(updatedNetworks.size());
        updatedNetworks.forEach((i, n) -> {
            buf.writeInt(i);
            buf.writeCompoundTag(n);
        });
    }

    @Override
    public Object handle(NetworkEvent.Context ctx) {
        FluxNetworkCache.INSTANCE.updateClientFromPacket(updatedNetworks, type);
        return null;
    }
}
