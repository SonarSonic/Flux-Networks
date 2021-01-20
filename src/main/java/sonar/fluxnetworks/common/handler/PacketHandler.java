package sonar.fluxnetworks.common.handler;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetHandler;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import sonar.fluxnetworks.FluxNetworks;
import sonar.fluxnetworks.common.network.*;

public class PacketHandler {

    public static SimpleNetworkWrapper network = NetworkRegistry.INSTANCE.newSimpleChannel(FluxNetworks.MODID);

    public static void registerMessages() {
        network.registerMessage(PacketGeneral.class, PacketGeneral.GeneralMessage.class, 1, Side.SERVER);
        network.registerMessage(PacketNetworkUpdate.class, PacketNetworkUpdate.NetworkUpdateMessage.class, 2, Side.CLIENT);
        network.registerMessage(PacketByteBuf.class, PacketByteBuf.ByteBufMessage.class, 3, Side.SERVER);
        network.registerMessage(PacketByteBuf.class, PacketByteBuf.ByteBufMessage.class, 4, Side.CLIENT);
        network.registerMessage(PacketTile.class, PacketTile.TileMessage.class, 5, Side.SERVER);
        network.registerMessage(PacketFeedback.class, PacketFeedback.FeedbackMessage.class, 6, Side.CLIENT);
        network.registerMessage(PacketColorRequest.class, PacketColorRequest.ColorRequestMessage.class, 7, Side.SERVER);
        network.registerMessage(PacketColorCache.class, PacketColorCache.ColorCacheMessage.class, 8, Side.CLIENT);
        network.registerMessage(PacketNetworkUpdateRequest.class, PacketNetworkUpdateRequest.UpdateRequestMessage.class, 9, Side.SERVER);
        network.registerMessage(PacketConnectionUpdate.class, PacketConnectionUpdate.NetworkConnectionMessage.class, 10, Side.CLIENT);
        network.registerMessage(PacketConnectionUpdateRequest.class, PacketConnectionUpdateRequest.ConnectionRequestMessage.class, 11, Side.SERVER);
        network.registerMessage(PacketBatchEditing.class, PacketBatchEditing.BatchEditingMessage.class, 12, Side.SERVER);
        network.registerMessage(PacketPermissionRequest.class, PacketPermissionRequest.PermissionRequestMessage.class, 13, Side.SERVER);
        network.registerMessage(PacketGUIPermission.class, PacketGUIPermission.GUIPermissionMessage.class, 14, Side.CLIENT);
        network.registerMessage(PacketSuperAdmin.class, PacketSuperAdmin.SuperAdminMessage.class, 15, Side.CLIENT);
        network.registerMessage(PacketActivateSuperAdmin.class, PacketActivateSuperAdmin.ActivateSuperAdminMessage.class, 16, Side.SERVER);
        network.registerMessage(PacketConfiguratorSettings.class, PacketConfiguratorSettings.ConfiguratorSettingsMessage.class, 17, Side.SERVER);
        network.registerMessage(PacketSetConfiguratorNetwork.class, PacketSetConfiguratorNetwork.SetConfiguratorNetworkMessage.class, 18, Side.SERVER);
    }

    public static EntityPlayer getPlayer(MessageContext ctx) {
        return FluxNetworks.proxy.getPlayer(ctx);
    }

    public static void handlePacket(Runnable runnable, INetHandler netHandler) {
        FMLCommonHandler.instance().getWorldThread(netHandler).addScheduledTask(runnable);
    }

}
