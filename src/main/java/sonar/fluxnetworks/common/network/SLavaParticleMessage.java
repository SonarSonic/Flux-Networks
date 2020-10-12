package sonar.fluxnetworks.common.network;

import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;
import sonar.fluxnetworks.api.misc.IMessage;

import javax.annotation.Nonnull;

public class SLavaParticleMessage implements IMessage {

    private BlockPos pos;
    private int count;

    public SLavaParticleMessage() {
    }

    public SLavaParticleMessage(BlockPos pos, int count) {
        this.pos = pos;
        this.count = count;
    }

    @Override
    public void encode(@Nonnull PacketBuffer buffer) {
        buffer.writeBlockPos(pos);
        buffer.writeVarInt(count);
    }

    @Override
    public void handle(@Nonnull PacketBuffer buffer, @Nonnull NetworkEvent.Context context) {
        BlockPos pos = buffer.readBlockPos();
        int count = buffer.readVarInt();
        if (Minecraft.getInstance().world != null) {
            for (int i = 0; i < count; i++) {
                Minecraft.getInstance().world.addParticle(ParticleTypes.LAVA,
                        pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, 0, 0, 0);
            }
        }
    }
}
