package sonar.fluxnetworks.common.network;

import sonar.fluxnetworks.api.network.EnumAccessType;
import sonar.fluxnetworks.client.gui.basic.GuiFluxCore;
import sonar.fluxnetworks.common.handler.PacketHandler;
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
                guiFluxCore.onSuperAdminChanged();
            }
        }
        return null;
    }

    public static class GUIPermissionMessage implements IMessage {

        public EnumAccessType accessPermission;

        public GUIPermissionMessage() {
        }

        public GUIPermissionMessage(EnumAccessType permission) {
            this.accessPermission = permission;
        }

        @Override
        public void fromBytes(ByteBuf buf) {
            accessPermission = EnumAccessType.values()[buf.readInt()];
        }

        @Override
        public void toBytes(ByteBuf buf) {
            buf.writeInt(accessPermission.ordinal());
        }
    }
}
