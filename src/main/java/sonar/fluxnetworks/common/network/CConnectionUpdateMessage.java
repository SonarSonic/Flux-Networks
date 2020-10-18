package sonar.fluxnetworks.common.network;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.GlobalPos;
import net.minecraftforge.fml.network.NetworkEvent;
import sonar.fluxnetworks.api.misc.IMessage;
import sonar.fluxnetworks.api.network.IFluxNetwork;
import sonar.fluxnetworks.common.misc.FluxUtils;
import sonar.fluxnetworks.common.storage.FluxNetworkData;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class CConnectionUpdateMessage implements IMessage {

    private int networkID;
    private List<GlobalPos> list;

    public CConnectionUpdateMessage() {
    }

    public CConnectionUpdateMessage(int networkID, List<GlobalPos> list) {
        this.networkID = networkID;
        this.list = list;
    }

    @Override
    public void encode(@Nonnull PacketBuffer buffer) {
        buffer.writeVarInt(networkID);
        buffer.writeVarInt(list.size());
        list.forEach(pos -> FluxUtils.writeGlobalPos(buffer, pos));
    }

    @Override
    public void handle(@Nonnull PacketBuffer buffer, @Nonnull NetworkEvent.Context context) {
        int networkID = buffer.readVarInt();
        IFluxNetwork network = FluxNetworkData.getNetwork(networkID);
        if (!network.isValid()) {
            return;
        }
        int size = buffer.readVarInt();
        List<CompoundNBT> tags = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            GlobalPos pos = FluxUtils.readGlobalPos(buffer);
            network.getConnectionByPos(pos).ifPresent(c -> {
                CompoundNBT tag = new CompoundNBT();
                c.writeCustomNBT(tag, 0);
                tags.add(tag);
            });
        }
        if (!tags.isEmpty()) {
            NetworkHandler.INSTANCE.reply(new SConnectionUpdateMessage(networkID, tags), context);
        }
    }
}
