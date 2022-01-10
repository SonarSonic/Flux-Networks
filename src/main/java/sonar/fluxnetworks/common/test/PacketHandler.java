package sonar.fluxnetworks.common.test;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import sonar.fluxnetworks.FluxNetworks;
import sonar.fluxnetworks.register.NetworkHandler;

/**
 * @deprecated use {@link NetworkHandler} instead
 */
@Deprecated
public class PacketHandler {

    private static final String PROTOCOL_VERSION = "1";

    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(new ResourceLocation(FluxNetworks.MODID, "main"), () -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals);

    public static int packetID = 0;

    public static void registerMessages() {
        //CHANNEL.registerMessage(packetID++, GeneralPacket.class, GeneralPacket::encode, GeneralPacket::new, GeneralPacket::handle);
        //CHANNEL.registerMessage(packetID++, TilePacketBufferPacket.class, TilePacketBufferPacket::encode, TilePacketBufferPacket::new, TilePacketBufferPacket::handle);
        //CHANNEL.registerMessage(packetID++, TilePacket.class, TilePacket::encode, TilePacket::new, TilePacket::handle);
        //CHANNEL.registerMessage(packetID++, FeedbackPacket.class, FeedbackPacket::encode, FeedbackPacket::new, FeedbackPacket::handle);
        //CHANNEL.registerMessage(packetID++, NetworkColourRequestPacket.class, NetworkColourRequestPacket::encode, NetworkColourRequestPacket::new, NetworkColourRequestPacket::handle);
        //CHANNEL.registerMessage(packetID++, NetworkColourPacket.class, NetworkColourPacket::encode, NetworkColourPacket::new, NetworkColourPacket::handle);
        //CHANNEL.registerMessage(packetID++, CNetworkUpdateMessage.class, CNetworkUpdateMessage::encode, CNetworkUpdateMessage::new, CNetworkUpdateMessage::handle);
        //CHANNEL.registerMessage(packetID++, SConnectionUpdateMessage.class, SConnectionUpdateMessage::encode, SConnectionUpdateMessage::new, SConnectionUpdateMessage::handle);
        //CHANNEL.registerMessage(packetID++, CConnectionUpdateMessage.class, CConnectionUpdateMessage::encode, CConnectionUpdateMessage::new, CConnectionUpdateMessage::handle);
        //CHANNEL.registerMessage(packetID++, CEditConnectionsMessage.class, CEditConnectionsMessage::encode, CEditConnectionsMessage::new, CEditConnectionsMessage::handle);
        //CHANNEL.registerMessage(packetID++, CGuiPermissionMessage.class, CGuiPermissionMessage::encode, CGuiPermissionMessage::new, CGuiPermissionMessage::handle);
        //CHANNEL.registerMessage(packetID++, SGuiPermissionMessage.class, SGuiPermissionMessage::encode, SGuiPermissionMessage::new, SGuiPermissionMessage::handle);
        //CHANNEL.registerMessage(packetID++, SSuperAdminMessage.class, SSuperAdminMessage::encode, SSuperAdminMessage::new, SSuperAdminMessage::handle);
        //CHANNEL.registerMessage(packetID++, CSuperAdminMessage.class, CSuperAdminMessage::encode, CSuperAdminMessage::new, CSuperAdminMessage::handle);
        //CHANNEL.registerMessage(packetID++, CConfiguratorSettingsMsg.class, CConfiguratorSettingsMsg::encode, CConfiguratorSettingsMsg::new, CConfiguratorSettingsMsg::handle);
        //CHANNEL.registerMessage(packetID++, CConfiguratorConnectMsg.class, CConfiguratorConnectMsg::encode, CConfiguratorConnectMsg::new, CConfiguratorConnectMsg::handle);
    }

    @Deprecated
    public static World getWorld(NetworkEvent.Context context) {
        return getPlayer(context).getEntityWorld();
    }

    @Deprecated
    public static PlayerEntity getPlayer(NetworkEvent.Context context) {
        if (context.getDirection() == NetworkDirection.PLAY_TO_SERVER) {
            return context.getSender();
        }
        return Minecraft.getInstance().player;
    }

}
