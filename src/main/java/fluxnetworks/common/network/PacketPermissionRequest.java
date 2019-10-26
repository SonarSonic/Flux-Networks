package fluxnetworks.common.network;

import fluxnetworks.api.network.EnumAccessType;
import fluxnetworks.api.network.IFluxNetwork;
import fluxnetworks.common.connection.FluxNetworkCache;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.UUID;

public class PacketPermissionRequest implements IMessageHandler<PacketPermissionRequest.PermissionRequestMessage, IMessage> {

    @SuppressWarnings("ConstantConditions")
    @Override
    public IMessage onMessage(PermissionRequestMessage message, MessageContext ctx) {
        IFluxNetwork network = FluxNetworkCache.instance.getNetwork(message.networkID);
        EntityPlayer player = FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayerByUUID(message.uuid);
        return new PacketGUIPermission.GUIPermissionMessage((network.isInvalid() || player == null) ? EnumAccessType.NONE : network.getMemberPermission(player));
    }

    public static class PermissionRequestMessage implements IMessage {

        public int networkID;
        public UUID uuid;

        public PermissionRequestMessage() {
        }

        public PermissionRequestMessage(int networkID, UUID uuid) {
            this.networkID = networkID;
            this.uuid = uuid;
        }

        @Override
        public void fromBytes(ByteBuf buf) {
            networkID = buf.readInt();
            uuid = new UUID(buf.readLong(), buf.readLong());
        }

        @Override
        public void toBytes(ByteBuf buf) {
            buf.writeInt(networkID);
            buf.writeLong(uuid.getMostSignificantBits());
            buf.writeLong(uuid.getLeastSignificantBits());
        }
    }
}
