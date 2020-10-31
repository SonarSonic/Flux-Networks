package sonar.fluxnetworks.common.network;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import sonar.fluxnetworks.api.misc.IMessage;
import sonar.fluxnetworks.api.network.AccessLevel;
import sonar.fluxnetworks.client.gui.basic.GuiFluxCore;

import javax.annotation.Nonnull;

public class SGuiPermissionMessage implements IMessage {

    private AccessLevel accessPermission;

    public SGuiPermissionMessage() {
    }

    public SGuiPermissionMessage(AccessLevel permission) {
        accessPermission = permission;
    }

    @Override
    public void encode(@Nonnull PacketBuffer buffer) {
        buffer.writeVarInt(accessPermission.ordinal());
    }

    @Override
    public void handle(@Nonnull PacketBuffer buffer, @Nonnull NetworkEvent.Context context) {
        PlayerEntity player = NetworkHandler.getPlayer(context);
        if (player == null) {
            buffer.release();
            return;
        }
        AccessLevel access = AccessLevel.values()[buffer.readVarInt()];
        Screen gui = Minecraft.getInstance().currentScreen;
        if (gui instanceof GuiFluxCore) {
            GuiFluxCore guiFluxCore = (GuiFluxCore) gui;
            guiFluxCore.accessLevel = access;
            guiFluxCore.onSuperAdminChanged();
        }
        buffer.release();
    }
}
