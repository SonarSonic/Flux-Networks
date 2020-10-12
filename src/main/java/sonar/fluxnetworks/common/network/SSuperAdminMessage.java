package sonar.fluxnetworks.common.network;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import sonar.fluxnetworks.api.misc.IMessage;
import sonar.fluxnetworks.client.FluxClientCache;
import sonar.fluxnetworks.client.gui.basic.GuiFluxCore;
import sonar.fluxnetworks.common.handler.NetworkHandler;

import javax.annotation.Nonnull;

public class SSuperAdminMessage implements IMessage {

    private boolean superAdmin;

    public SSuperAdminMessage() {
    }

    public SSuperAdminMessage(boolean superAdmin) {
        this.superAdmin = superAdmin;
    }

    @Override
    public void encode(@Nonnull PacketBuffer buffer) {
        buffer.writeBoolean(superAdmin);
    }

    @Override
    public void handle(@Nonnull PacketBuffer buffer, @Nonnull NetworkEvent.Context context) {
        FluxClientCache.superAdmin = buffer.readBoolean();
        PlayerEntity player = NetworkHandler.getPlayer(context);
        if (player != null) {
            Screen gui = Minecraft.getInstance().currentScreen;
            if (gui instanceof GuiFluxCore) {
                ((GuiFluxCore) gui).onSuperAdminChanged();
            }
        }
        buffer.release();
    }
}
