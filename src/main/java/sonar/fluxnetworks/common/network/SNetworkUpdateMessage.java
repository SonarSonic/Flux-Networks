package sonar.fluxnetworks.common.network;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import sonar.fluxnetworks.api.misc.IMessage;
import sonar.fluxnetworks.api.network.IFluxNetwork;
import sonar.fluxnetworks.client.FluxClientCache;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class SNetworkUpdateMessage implements IMessage {

    private Map<Integer, CompoundNBT> updatedNetworks = new HashMap<>();
    private int flags;

    public SNetworkUpdateMessage(@Nonnull List<IFluxNetwork> toSend, int flags) {
        this.flags = flags;
        toSend.forEach(network -> {
            CompoundNBT tag = new CompoundNBT();
            network.writeCustomNBT(tag, flags);
            if (!tag.isEmpty()) {
                updatedNetworks.put(network.getNetworkID(), tag);
            }
        });
    }

    @Override
    public void encode(@Nonnull PacketBuffer buffer) {
        buffer.writeVarInt(flags);
        buffer.writeVarInt(updatedNetworks.size());
        updatedNetworks.forEach((i, n) -> {
            buffer.writeVarInt(i);
            buffer.writeCompoundTag(n);
        });
    }

    @Override
    public void decode(@Nonnull PacketBuffer buffer) {
        flags = buffer.readVarInt();
        int size = buffer.readVarInt();
        for (int i = 0; i < size; i++) {
            updatedNetworks.put(buffer.readVarInt(), buffer.readCompoundTag());
        }
    }

    @Override
    public void handle(@Nonnull Supplier<NetworkEvent.Context> context) {
        FluxClientCache.INSTANCE.updateNetworks(updatedNetworks, flags);
    }
}
