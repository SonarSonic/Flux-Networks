package sonar.fluxnetworks.register;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.chunk.LevelChunk;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import sonar.fluxnetworks.FluxNetworks;

import javax.annotation.Nonnull;

public class Channel {

    /**
     * Note: Increment this if any packet is changed.
     */
    static final String PROTOCOL = "800";
    static final Channel sChannel = new Channel();

    @Nonnull
    static FriendlyByteBuf buffer(int index) {
        ByteBuf buf = Unpooled.buffer();
        buf.writeShort(index);
        return new FriendlyByteBuf(buf);
    }

    public static Channel get() {
        return sChannel;
    }

    public static final StreamCodec<FriendlyByteBuf, FriendlyByteBuf> BYTE_BUFFER_CODEC = new StreamCodec<>() {
        @Nonnull
        @Override
        public FriendlyByteBuf decode(@Nonnull FriendlyByteBuf source) {
            return new FriendlyByteBuf(source.readRetainedSlice(source.readableBytes()));
        }

        @Override
        public void encode(@Nonnull FriendlyByteBuf target, @Nonnull FriendlyByteBuf source) {
            target.writeBytes(source.slice());
        }
    };

    // A 🩼 to use plain byte buffers for Client<=>Server communication
    // Define a custom payload with a type of byte buffer
    public record Message(FriendlyByteBuf data) implements CustomPacketPayload {
        public static final Type<Message> TYPE = new Type<>(FluxNetworks.location("message"));
        public static final StreamCodec<FriendlyByteBuf, Message> CODEC = StreamCodec.composite(
                BYTE_BUFFER_CODEC, Message::data, Message::new
        );

        @Nonnull
        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }

        public void handle(@Nonnull IPayloadContext context) {
            int index = data.readUnsignedShort();
            if (context.flow() == PacketFlow.CLIENTBOUND) {
                Channel.get().mS2CMessageHandler.handle(index, data, context);
            } else {
                Channel.get().mC2SMessageHandler.handle(index, data, context);
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    public void sendToServer(@Nonnull FriendlyByteBuf payload) {
        ClientPacketListener connection = Minecraft.getInstance().getConnection();
        if (connection != null) {
            connection.send(new Message(payload));
        } else {
            payload.release();
        }
    }

    public final void sendToPlayer(@Nonnull FriendlyByteBuf payload, @Nonnull Player player) {
        sendToPlayer(payload, (ServerPlayer) player);
    }

    public void sendToPlayer(@Nonnull FriendlyByteBuf payload, @Nonnull ServerPlayer player) {
        player.connection.send(new Message(payload));
    }

    public void sendToAll(@Nonnull FriendlyByteBuf payload) {
        final var packet = new ClientboundCustomPayloadPacket(new Message(payload));
        ServerLifecycleHooks.getCurrentServer().getPlayerList().broadcastAll(packet);
    }

    public void sendToTrackingChunk(@Nonnull FriendlyByteBuf payload, @Nonnull LevelChunk chunk) {
        final var packet = new ClientboundCustomPayloadPacket(new Message(payload));
        ((ServerLevel) chunk.getLevel()).getChunkSource().chunkMap.getPlayers(
                chunk.getPos(), /* boundaryOnly */ false).forEach(p -> p.connection.send(packet));
    }

    public void setS2CMessageHandler(@Nonnull MessageHandler s2cMessageHandler) {
        mS2CMessageHandler = s2cMessageHandler;
    }

    public void setC2SMessageHandler(@Nonnull MessageHandler c2sMessageHandler) {
        mC2SMessageHandler = c2sMessageHandler;
    }

    @FunctionalInterface
    public interface MessageHandler {

        void handle(int index,
                    @Nonnull FriendlyByteBuf payload,
                    @Nonnull IPayloadContext context);
    }

    private MessageHandler mS2CMessageHandler;
    private MessageHandler mC2SMessageHandler;
}
