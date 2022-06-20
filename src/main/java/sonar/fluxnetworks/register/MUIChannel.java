package sonar.fluxnetworks.register;

import icyllis.modernui.forge.NetworkHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.chunk.LevelChunk;
import sonar.fluxnetworks.FluxNetworks;

import javax.annotation.Nonnull;

public class MUIChannel extends Channel {

    private final NetworkHandler mNetwork;

    static {
        FluxNetworks.LOGGER.info("Use MUI network channel (high performance)");
    }

    MUIChannel() {
        mNetwork = new NetworkHandler(FluxNetworks.MODID, () -> Holder::msg, Messages::msg, PROTOCOL, false);
    }

    @Override
    public void sendToServer(@Nonnull FriendlyByteBuf payload) {
        mNetwork.sendToServer(payload);
    }

    @Override
    public void sendToPlayer(@Nonnull FriendlyByteBuf payload, @Nonnull ServerPlayer player) {
        mNetwork.sendToPlayer(payload, player);
    }

    @Override
    public void sendToAll(@Nonnull FriendlyByteBuf payload) {
        mNetwork.sendToAll(payload);
    }

    @Override
    public void sendToTrackingChunk(@Nonnull FriendlyByteBuf payload, @Nonnull LevelChunk chunk) {
        mNetwork.dispatch(payload).sendToTrackingChunk(chunk);
    }

    private static class Holder {

        @Nonnull
        static NetworkHandler.ClientListener msg() {
            return ClientMessages::msg;
        }
    }
}
