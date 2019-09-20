package fluxnetworks.common.network;

import fluxnetworks.FluxNetworks;
import fluxnetworks.api.AccessPermission;
import fluxnetworks.client.gui.basic.GuiFluxCore;
import fluxnetworks.common.handler.PacketHandler;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketGUIPermission implements IMessageHandler<PacketGUIPermission.GUIPermissionMessage, IMessage> {

    @Override
    public IMessage onMessage(GUIPermissionMessage message, MessageContext ctx) {
        EntityPlayer player = PacketHandler.getPlayer(ctx);
        if(player != null) {
            Gui gui = Minecraft.getMinecraft().currentScreen;
            if (gui instanceof GuiFluxCore) {
                GuiFluxCore guiFluxCore = (GuiFluxCore) gui;
                guiFluxCore.accessPermission = message.accessPermission;
            }
        }
        return null;
    }

    public static class GUIPermissionMessage implements IMessage {

        public AccessPermission accessPermission;

        public GUIPermissionMessage() {
        }

        public GUIPermissionMessage(AccessPermission permission) {
            this.accessPermission = permission;
        }

        @Override
        public void fromBytes(ByteBuf buf) {
            accessPermission = AccessPermission.values()[buf.readInt()];
        }

        @Override
        public void toBytes(ByteBuf buf) {
            buf.writeInt(accessPermission.ordinal());
        }
    }
}
