package fluxnetworks.common.handler;

import fluxnetworks.FluxNetworks;
import fluxnetworks.common.network.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetHandler;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

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
        network.registerMessage(PacketNetworkClear.class, PacketNetworkClear.NetworkClearMessage.class, 13, Side.CLIENT);
    }

    public static EntityPlayer getPlayer(MessageContext ctx) {
        return FluxNetworks.proxy.getPlayer(ctx);
    }

    public static void handlePacket(Runnable runnable, INetHandler netHandler) {
        FMLCommonHandler.instance().getWorldThread(netHandler).addScheduledTask(runnable);
    }

}
