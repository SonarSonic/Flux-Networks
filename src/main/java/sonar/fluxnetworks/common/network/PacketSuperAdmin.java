package sonar.fluxnetworks.common.network;

import sonar.fluxnetworks.client.gui.basic.GuiFluxCore;
import sonar.fluxnetworks.common.connection.FluxNetworkCache;
import sonar.fluxnetworks.common.handler.PacketHandler;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketSuperAdmin implements IMessageHandler<PacketSuperAdmin.SuperAdminMessage, IMessage> {

    @SuppressWarnings("ConstantConditions")
    @Override
    public IMessage onMessage(SuperAdminMessage message, MessageContext ctx) {
        FluxNetworkCache.instance.superAdminClient = message.superAdmin;
        EntityPlayer player = PacketHandler.getPlayer(ctx);
        if(player != null) {
            Gui gui = Minecraft.getMinecraft().currentScreen;
            if (gui instanceof GuiFluxCore) {
                GuiFluxCore guiFluxCore = (GuiFluxCore) gui;
                guiFluxCore.onSuperAdminChanged();
            }
        }
        return null;
    }

    public static class SuperAdminMessage implements IMessage {

        public boolean superAdmin;

        public SuperAdminMessage() {}

        public SuperAdminMessage(boolean superAdmin) {
            this.superAdmin = superAdmin;
        }

        @Override
        public void fromBytes(ByteBuf buf) {
            superAdmin = buf.readBoolean();
        }

        @Override
        public void toBytes(ByteBuf buf) {
            buf.writeBoolean(superAdmin);
        }
    }
}
