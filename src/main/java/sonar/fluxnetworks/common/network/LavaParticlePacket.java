package sonar.fluxnetworks.common.network;

import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;

public class LavaParticlePacket extends AbstractPacket {

    private BlockPos pos;
    private int count;

    public LavaParticlePacket(PacketBuffer buf) {
        pos = buf.readBlockPos();
        count = buf.readVarInt();
    }

    public LavaParticlePacket(BlockPos pos, int count) {
        this.pos = pos;
        this.count = count;
    }

    @Override
    public void encode(PacketBuffer buf) {
        buf.writeBlockPos(pos);
        buf.writeVarInt(count);
    }

    @Override
    public Object handle(NetworkEvent.Context ctx) {
        if (Minecraft.getInstance().world != null) {
            for (int i = 0; i < count; i++) {
                Minecraft.getInstance().world.addParticle(ParticleTypes.LAVA, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, 0, 0, 0);
            }
        }
        return null;
    }
}
