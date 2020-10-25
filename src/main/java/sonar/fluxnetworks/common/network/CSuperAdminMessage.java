package sonar.fluxnetworks.common.network;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import sonar.fluxnetworks.api.misc.FluxCapabilities;
import sonar.fluxnetworks.api.misc.IMessage;
import sonar.fluxnetworks.api.network.ISuperAdmin;
import sonar.fluxnetworks.api.text.FluxTranslate;
import sonar.fluxnetworks.common.capability.SuperAdmin;
import sonar.fluxnetworks.common.misc.FluxUtils;

import javax.annotation.Nonnull;

public class CSuperAdminMessage implements IMessage {

    public CSuperAdminMessage() {

    }

    @Override
    public void encode(@Nonnull PacketBuffer buffer) {

    }

    @Override
    public void handle(@Nonnull PacketBuffer buffer, @Nonnull NetworkEvent.Context context) {
        PlayerEntity player = FluxUtils.getPlayer(context);
        if (player == null) {
            return;
        }
        ISuperAdmin superAdmin = FluxUtils.getCap(player, FluxCapabilities.SUPER_ADMIN);
        if (superAdmin != null && (superAdmin.hasPermission() || SuperAdmin.canActivateSuperAdmin(player))) {
            superAdmin.changePermission();
            if (superAdmin.hasPermission()) {
                player.sendStatusMessage(FluxTranslate.SA_ON.getTextComponent(), true);
            } else {
                player.sendStatusMessage(FluxTranslate.SA_OFF.getTextComponent(), true);
            }
            NetworkHandler.INSTANCE.reply(new SSuperAdminMessage(superAdmin.hasPermission()), context);
        }
    }
}
