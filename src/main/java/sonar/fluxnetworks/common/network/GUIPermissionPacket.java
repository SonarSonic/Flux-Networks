package sonar.fluxnetworks.common.network;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import sonar.fluxnetworks.api.network.FluxAccessLevel;
import sonar.fluxnetworks.client.gui.basic.GuiFluxCore;
import sonar.fluxnetworks.common.handler.PacketHandler;
import net.minecraft.client.Minecraft;

public class GUIPermissionPacket extends AbstractPacket {

    public FluxAccessLevel accessPermission;

    public GUIPermissionPacket(PacketBuffer buf) {
        accessPermission = FluxAccessLevel.values()[buf.readInt()];
    }

    public GUIPermissionPacket(FluxAccessLevel permission) {
        this.accessPermission = permission;
    }

    @Override
    public void encode(PacketBuffer buf) {
        buf.writeInt(accessPermission.ordinal());
    }

    @Override
    public Object handle(NetworkEvent.Context ctx) {
        PlayerEntity player = PacketHandler.getPlayer(ctx);
        if(player != null) {
            Screen gui = Minecraft.getInstance().currentScreen;
            if (gui instanceof GuiFluxCore) {
                GuiFluxCore guiFluxCore = (GuiFluxCore) gui;
                guiFluxCore.accessPermission = accessPermission;
                guiFluxCore.onSuperAdminChanged();
            }
        }
        return null;
    }
}
