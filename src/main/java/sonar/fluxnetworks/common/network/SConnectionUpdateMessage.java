package sonar.fluxnetworks.common.network;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import sonar.fluxnetworks.api.misc.IMessage;
import sonar.fluxnetworks.client.FluxClientCache;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class SConnectionUpdateMessage implements IMessage {

    private int networkID;
    private List<CompoundNBT> tags;

    public SConnectionUpdateMessage() {
    }

    public SConnectionUpdateMessage(int networkID, List<CompoundNBT> tags) {
        this.networkID = networkID;
        this.tags = tags;
    }

    @Override
    public void encode(@Nonnull PacketBuffer buffer) {
        buffer.writeVarInt(networkID);
        buffer.writeVarInt(tags.size());
        tags.forEach(buffer::writeCompoundTag);
    }

    @Override
    public void handle(@Nonnull PacketBuffer buffer, @Nonnull NetworkEvent.Context context) {
        int networkID = buffer.readVarInt();
        int size = buffer.readVarInt();
        List<CompoundNBT> tags = new ArrayList<>();
        for(int i = 0; i < size; i++) {
            tags.add(buffer.readCompoundTag());
        }
        FluxClientCache.updateConnections(networkID, tags);
        buffer.release();
    }
}
