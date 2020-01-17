package icyllis.fluxnetworks.system.network;

import icyllis.fluxnetworks.system.FluxNetworks;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public enum PacketHandler {
    INSTANCE;

    private SimpleChannel NETWORK = NetworkRegistry.ChannelBuilder
            .named(new ResourceLocation(FluxNetworks.MODID, "network"))
            .networkProtocolVersion(() -> "1")
            .clientAcceptedVersions(s -> true)
            .serverAcceptedVersions(s -> true)
            .simpleChannel();

    public void setup() {

    }

    public <M> void sendToServer(M message) {
        NETWORK.sendToServer(message);
    }

    public <M> void sendTo(M message, ServerPlayerEntity playerMP) {
        NETWORK.sendTo(message, playerMP.connection.netManager, NetworkDirection.PLAY_TO_CLIENT);
    }
}
