package sonar.fluxnetworks.common.network;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import sonar.fluxnetworks.api.network.AccessLevel;
import sonar.fluxnetworks.api.network.IFluxNetwork;

import javax.annotation.Nonnull;

public class CGuiPermissionMessage extends CDeleteNetworkMessage {

    public CGuiPermissionMessage() {
    }

    public CGuiPermissionMessage(int networkID) {
        super(networkID);
    }

    @Override
    protected void handle(@Nonnull PacketBuffer buffer, @Nonnull NetworkEvent.Context context, @Nonnull PlayerEntity player, @Nonnull IFluxNetwork network) {
        AccessLevel access = network.getPlayerAccess(player);
        NetworkHandler.INSTANCE.reply(new SGuiPermissionMessage(access), context);
    }
}
