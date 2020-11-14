package sonar.fluxnetworks.common.network;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import sonar.fluxnetworks.api.misc.FeedbackInfo;
import sonar.fluxnetworks.api.misc.FluxCapabilities;
import sonar.fluxnetworks.api.misc.IMessage;
import sonar.fluxnetworks.api.network.ISuperAdmin;
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
        PlayerEntity player = NetworkHandler.getPlayer(context);
        if (player == null) {
            return;
        }
        ISuperAdmin superAdmin = FluxUtils.get(player.getCapability(FluxCapabilities.SUPER_ADMIN));
        if (superAdmin != null && (superAdmin.hasPermission() || SuperAdmin.canActivateSuperAdmin(player))) {
            superAdmin.changePermission();
            if (superAdmin.hasPermission()) {
                NetworkHandler.INSTANCE.reply(new SFeedbackMessage(FeedbackInfo.SA_ON), context);
            } else {
                NetworkHandler.INSTANCE.reply(new SFeedbackMessage(FeedbackInfo.SA_OFF), context);
            }
            NetworkHandler.INSTANCE.reply(new SSuperAdminMessage(superAdmin.hasPermission()), context);
        }
    }
}
