package sonar.fluxnetworks.common.network;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import sonar.fluxnetworks.client.gui.basic.GuiFluxCore;
import sonar.fluxnetworks.common.connection.FluxNetworkCache;
import sonar.fluxnetworks.common.handler.PacketHandler;
import net.minecraft.client.Minecraft;

public class SuperAdminPacket extends AbstractPacket {

    public boolean superAdmin;

    public SuperAdminPacket(PacketBuffer buf) {
        superAdmin = buf.readBoolean();
    }

    public SuperAdminPacket(boolean superAdmin) {
        this.superAdmin = superAdmin;
    }

    @Override
    public void encode(PacketBuffer buf) {
        buf.writeBoolean(superAdmin);
    }

    @Override
    public Object handle(NetworkEvent.Context ctx) {
        FluxNetworkCache.instance.superAdminClient = superAdmin;
        PlayerEntity player = PacketHandler.getPlayer(ctx);
        if(player != null) {
            Screen gui = Minecraft.getInstance().currentScreen;
            if (gui instanceof GuiFluxCore) {
                GuiFluxCore guiFluxCore = (GuiFluxCore) gui;
                guiFluxCore.onSuperAdminChanged();
            }
        }
        return null;
    }
}
