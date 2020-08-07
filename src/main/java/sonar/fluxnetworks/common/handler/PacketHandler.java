package sonar.fluxnetworks.common.handler;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import sonar.fluxnetworks.FluxNetworks;
import sonar.fluxnetworks.common.network.*;

public class PacketHandler {

    private static final String PROTOCOL_VERSION = "1";

    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(new ResourceLocation(FluxNetworks.MODID, "main"), () -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals);

    public static int packetID = 0;

    public static void registerMessages() {

        CHANNEL.registerMessage(packetID++, GeneralPacket.class, GeneralPacket::encode, GeneralPacket::new, GeneralPacket::handle);
        CHANNEL.registerMessage(packetID++, NetworkUpdatePacket.class, NetworkUpdatePacket::encode, NetworkUpdatePacket::new, NetworkUpdatePacket::handle);
        CHANNEL.registerMessage(packetID++, TilePacketBufferPacket.class, TilePacketBufferPacket::encode, TilePacketBufferPacket::new, TilePacketBufferPacket::handle);
        CHANNEL.registerMessage(packetID++, TilePacket.class, TilePacket::encode, TilePacket::new, TilePacket::handle);
        CHANNEL.registerMessage(packetID++, FeedbackPacket.class, FeedbackPacket::encode, FeedbackPacket::new, FeedbackPacket::handle);
        CHANNEL.registerMessage(packetID++, NetworkColourRequestPacket.class, NetworkColourRequestPacket::encode, NetworkColourRequestPacket::new, NetworkColourRequestPacket::handle);
        CHANNEL.registerMessage(packetID++, NetworkColourPacket.class, NetworkColourPacket::encode, NetworkColourPacket::new, NetworkColourPacket::handle);
        CHANNEL.registerMessage(packetID++, NetworkUpdateRequestPacket.class, NetworkUpdateRequestPacket::encode, NetworkUpdateRequestPacket::new, NetworkUpdateRequestPacket::handle);
        CHANNEL.registerMessage(packetID++, ConnectionUpdatePacket.class, ConnectionUpdatePacket::encode, ConnectionUpdatePacket::new, ConnectionUpdatePacket::handle);
        CHANNEL.registerMessage(packetID++, ConnectionUpdateRequestPacket.class, ConnectionUpdateRequestPacket::encode, ConnectionUpdateRequestPacket::new, ConnectionUpdateRequestPacket::handle);
        CHANNEL.registerMessage(packetID++, BatchEditingPacket.class, BatchEditingPacket::encode, BatchEditingPacket::new, BatchEditingPacket::handle);
        CHANNEL.registerMessage(packetID++, GUIPermissionRequestPacket.class, GUIPermissionRequestPacket::encode, GUIPermissionRequestPacket::new, GUIPermissionRequestPacket::handle);
        CHANNEL.registerMessage(packetID++, GUIPermissionPacket.class, GUIPermissionPacket::encode, GUIPermissionPacket::new, GUIPermissionPacket::handle);
        CHANNEL.registerMessage(packetID++, SuperAdminPacket.class, SuperAdminPacket::encode, SuperAdminPacket::new, SuperAdminPacket::handle);
        CHANNEL.registerMessage(packetID++, SuperAdminRequestPacket.class, SuperAdminRequestPacket::encode, SuperAdminRequestPacket::new, SuperAdminRequestPacket::handle);
        CHANNEL.registerMessage(packetID++, ConfiguratorUpdateSettingsPacket.class, ConfiguratorUpdateSettingsPacket::encode, ConfiguratorUpdateSettingsPacket::new, ConfiguratorUpdateSettingsPacket::handle);
        CHANNEL.registerMessage(packetID++, ConfiguratorNetworkConnectPacket.class, ConfiguratorNetworkConnectPacket::encode, ConfiguratorNetworkConnectPacket::new, ConfiguratorNetworkConnectPacket::handle);
        CHANNEL.registerMessage(packetID++, LavaParticlePacket.class, LavaParticlePacket::encode, LavaParticlePacket::new, LavaParticlePacket::handle);
    }

    public static World getWorld(NetworkEvent.Context context) {
        return getPlayer(context).getEntityWorld();
    }

    public static PlayerEntity getPlayer(NetworkEvent.Context context) {
        if (context.getDirection() == NetworkDirection.PLAY_TO_SERVER) {
            return context.getSender();
        }
        return FluxNetworks.PROXY.getClientPlayer();
    }

}
