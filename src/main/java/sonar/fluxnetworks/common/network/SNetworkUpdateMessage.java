package sonar.fluxnetworks.common.network;

import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import sonar.fluxnetworks.api.misc.IMessage;
import sonar.fluxnetworks.api.network.IFluxNetwork;
import sonar.fluxnetworks.client.FluxClientCache;

import javax.annotation.Nonnull;
import java.util.Collection;

public class SNetworkUpdateMessage implements IMessage {

    private final Int2ObjectMap<CompoundNBT> updatedNetworks = new Int2ObjectArrayMap<>();
    private int type;

    public SNetworkUpdateMessage() {
    }

    public SNetworkUpdateMessage(@Nonnull IFluxNetwork toSend, int type) {
        this.type = type;
        CompoundNBT tag = new CompoundNBT();
        toSend.writeCustomNBT(tag, type);
        if (!tag.isEmpty()) {
            updatedNetworks.put(toSend.getNetworkID(), tag);
        }
    }

    public SNetworkUpdateMessage(@Nonnull Collection<IFluxNetwork> toSend, int type) {
        this.type = type;
        toSend.forEach(network -> {
            CompoundNBT tag = new CompoundNBT();
            network.writeCustomNBT(tag, type);
            if (!tag.isEmpty()) {
                updatedNetworks.put(network.getNetworkID(), tag);
            }
        });
    }

    @Override
    public void encode(@Nonnull PacketBuffer buffer) {
        buffer.writeVarInt(type);
        Int2ObjectMap<CompoundNBT> updatedNetworks = this.updatedNetworks;
        buffer.writeVarInt(updatedNetworks.size());
        updatedNetworks.forEach((i, n) -> {
            buffer.writeVarInt(i);
            buffer.writeCompoundTag(n);
        });
    }

    @Override
    public void handle(@Nonnull PacketBuffer buffer, @Nonnull NetworkEvent.Context context) {
        int type = buffer.readVarInt();
        Int2ObjectMap<CompoundNBT> updatedNetworks = this.updatedNetworks;
        final int size = buffer.readVarInt();
        for (int i = 0; i < size; i++) {
            updatedNetworks.put(buffer.readVarInt(), buffer.readCompoundTag());
        }
        FluxClientCache.updateNetworks(updatedNetworks, type);
        buffer.release();
    }
}
