package sonar.fluxnetworks.common.network;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import sonar.fluxnetworks.api.network.EnumAccessType;
import sonar.fluxnetworks.api.network.IFluxNetwork;
import sonar.fluxnetworks.common.connection.FluxNetworkCache;

import java.util.UUID;

public class GUIPermissionRequestPacket extends AbstractPacket {

    public int networkID;
    public UUID uuid;

    public GUIPermissionRequestPacket(PacketBuffer buf) {
        networkID = buf.readInt();
        uuid = new UUID(buf.readLong(), buf.readLong());
    }

    public GUIPermissionRequestPacket(int networkID, UUID uuid) {
        this.networkID = networkID;
        this.uuid = uuid;
    }

    @Override
    public void encode(PacketBuffer buf) {
        buf.writeInt(networkID);
        buf.writeLong(uuid.getMostSignificantBits());
        buf.writeLong(uuid.getLeastSignificantBits());
    }

    @Override
    public Object handle(NetworkEvent.Context ctx) {
        IFluxNetwork network = FluxNetworkCache.INSTANCE.getNetwork(networkID);
        PlayerEntity player = ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayerByUUID(uuid);
        EnumAccessType accessType = (!network.isValid() || player == null) ? EnumAccessType.NONE : network.getMemberPermission(player);
        return new GUIPermissionPacket(accessType);
    }
}
