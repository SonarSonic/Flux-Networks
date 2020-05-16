package sonar.fluxnetworks.common.network;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import sonar.fluxnetworks.api.utils.Capabilities;
import sonar.fluxnetworks.common.capability.SuperAdminInstance;
import sonar.fluxnetworks.common.handler.PacketHandler;

public class SuperAdminRequestPacket extends AbstractPacket {

    public SuperAdminRequestPacket() {}

    public SuperAdminRequestPacket(PacketBuffer b) {}

    @Override
    public void encode(PacketBuffer b){}

    @Override
    public Object handle(NetworkEvent.Context ctx) {
        PlayerEntity player = PacketHandler.getPlayer(ctx);

        player.getCapability(Capabilities.SUPER_ADMIN).ifPresent(iSuperAdmin -> {
            if (iSuperAdmin.isSuperAdmin() || SuperAdminInstance.canActivateSuperAdmin(player)) {
                iSuperAdmin.iterateSuperAdmin();
                reply(ctx, new SuperAdminPacket(iSuperAdmin.isSuperAdmin()));
            }
        });
        return null;
    }

}
