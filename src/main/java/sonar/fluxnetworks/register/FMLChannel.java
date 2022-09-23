package sonar.fluxnetworks.register;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundCustomPayloadPacket;
import net.minecraft.network.protocol.game.ServerboundCustomPayloadPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.server.ServerLifecycleHooks;
import sonar.fluxnetworks.FluxNetworks;

import javax.annotation.Nonnull;

public class FMLChannel extends Channel {

    private static final ResourceLocation CHANNEL_NAME = FluxNetworks.rl("network");

    FMLChannel() {
        NetworkRegistry.newEventChannel(CHANNEL_NAME, () -> PROTOCOL, PROTOCOL::equals, PROTOCOL::equals)
                .registerObject(this);
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    void onCustomPayload(@Nonnull NetworkEvent.ServerCustomPayloadEvent e) {
        FriendlyByteBuf payload = e.getPayload();
        ClientMessages.msg(payload.readShort(), payload, () -> e.getSource().get().getNetworkManager().isConnected() ?
                Minecraft.getInstance().player : null);
        e.getSource().get().setPacketHandled(true);
    }

    @SubscribeEvent
    void onCustomPayload(@Nonnull NetworkEvent.ClientCustomPayloadEvent e) {
        FriendlyByteBuf payload = e.getPayload();
        Messages.msg(payload.readShort(), payload, () -> e.getSource().get().getNetworkManager().isConnected() ?
                e.getSource().get().getSender() : null);
        e.getSource().get().setPacketHandled(true);
    }

    @Override
    public void sendToServer(@Nonnull FriendlyByteBuf payload) {
        ClientPacketListener connection = Minecraft.getInstance().getConnection();
        if (connection != null) {
            connection.send(new ServerboundCustomPayloadPacket(CHANNEL_NAME, payload));
        } else {
            payload.release();
        }
    }

    @Override
    public void sendToPlayer(@Nonnull FriendlyByteBuf payload, @Nonnull ServerPlayer player) {
        player.connection.send(new ClientboundCustomPayloadPacket(CHANNEL_NAME, payload));
    }

    @Override
    public void sendToAll(@Nonnull FriendlyByteBuf payload) {
        ServerLifecycleHooks.getCurrentServer().getPlayerList()
                .broadcastAll(new ClientboundCustomPayloadPacket(CHANNEL_NAME, payload));
    }

    @Override
    public void sendToTrackingChunk(@Nonnull FriendlyByteBuf payload, @Nonnull LevelChunk chunk) {
        final ClientboundCustomPayloadPacket packet = new ClientboundCustomPayloadPacket(CHANNEL_NAME, payload);
        ((ServerLevel) chunk.getLevel()).getChunkSource().chunkMap.getPlayers(
                chunk.getPos(), /* boundaryOnly */ false).forEach(p -> p.connection.send(packet));
    }
}
