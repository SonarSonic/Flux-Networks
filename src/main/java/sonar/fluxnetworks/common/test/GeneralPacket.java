package sonar.fluxnetworks.common.test;

/*import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

*//** Gui operation packets.*//*
@Deprecated
public class GeneralPacket extends AbstractPacket {

    public GeneralPacketEnum handler;
    public CompoundNBT nbtTag;

    public GeneralPacket(PacketBuffer buf) {
        handler = GeneralPacketEnum.values()[buf.readInt()];
        nbtTag = buf.readCompoundTag();
    }

    public GeneralPacket(GeneralPacketEnum handler, CompoundNBT nbtTag) {
        super();
        this.handler = handler;
        this.nbtTag = nbtTag;
    }

    @Override
    public void encode(PacketBuffer buf) {
        buf.writeInt(handler.ordinal());
        buf.writeCompoundTag(nbtTag);
    }

    @Override
    public Object handle(NetworkEvent.Context ctx) {
        PlayerEntity player = PacketHandler.getPlayer(ctx);
        return handler.handler.handlePacket(player, nbtTag);
    }

}*/
