package sonar.fluxnetworks.register;

import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

public abstract class Channel {

    /**
     * Note: Increment this if any packet is changed.
     */
    static final String PROTOCOL = "704";
    static Channel sChannel;

    @Nonnull
    static FriendlyByteBuf buffer(int index) {
        FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
        buffer.writeShort(index);
        return buffer;
    }

    public static Channel get() {
        return sChannel;
    }

    @OnlyIn(Dist.CLIENT)
    public abstract void sendToServer(@Nonnull FriendlyByteBuf payload);

    public final void sendToPlayer(@Nonnull FriendlyByteBuf payload, @Nonnull Player player) {
        sendToPlayer(payload, (ServerPlayer) player);
    }

    public abstract void sendToPlayer(@Nonnull FriendlyByteBuf payload, @Nonnull ServerPlayer player);

    public abstract void sendToAll(@Nonnull FriendlyByteBuf payload);

    public abstract void sendToTrackingChunk(@Nonnull FriendlyByteBuf payload, @Nonnull LevelChunk chunk);
}
