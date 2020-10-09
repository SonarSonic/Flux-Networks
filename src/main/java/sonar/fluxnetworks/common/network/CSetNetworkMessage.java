package sonar.fluxnetworks.common.network;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;
import sonar.fluxnetworks.api.device.IFluxDevice;
import sonar.fluxnetworks.api.gui.EnumFeedbackInfo;
import sonar.fluxnetworks.api.misc.IMessage;
import sonar.fluxnetworks.api.network.FluxLogicType;
import sonar.fluxnetworks.api.network.IFluxNetwork;
import sonar.fluxnetworks.common.handler.NetworkHandler;
import sonar.fluxnetworks.common.handler.PacketHandler;
import sonar.fluxnetworks.common.storage.FluxNetworkData;

import javax.annotation.Nonnull;

public class CSetNetworkMessage implements IMessage {

    private BlockPos pos;
    private int networkID;
    private String password;

    public CSetNetworkMessage() {
    }

    public CSetNetworkMessage(BlockPos pos, int networkID, String password) {
        this.pos = pos;
        this.networkID = networkID;
        this.password = password;
    }

    @Override
    public void encode(@Nonnull PacketBuffer buffer) {
        buffer.writeBlockPos(pos);
        buffer.writeVarInt(networkID);
        buffer.writeString(password, 256);
    }

    @Override
    public void handle(@Nonnull PacketBuffer buffer, @Nonnull NetworkEvent.Context context) {
        pos = buffer.readBlockPos();
        networkID = buffer.readVarInt();
        password = buffer.readString(256);
        PlayerEntity player = NetworkHandler.getPlayer(context);
        if (player == null) {
            return;
        }
        TileEntity tile = player.world.getTileEntity(pos);
        if (!(tile instanceof IFluxDevice)) {
            return;
        }
        IFluxDevice flux = (IFluxDevice) tile;
        if (flux.getNetworkID() == networkID) {
            return;
        }
        IFluxNetwork network = FluxNetworkData.getNetwork(networkID);
        if (network.isValid()) {
            if (flux.getDeviceType().isController() && !network.getConnections(FluxLogicType.CONTROLLER).isEmpty()) {
                PacketHandler.CHANNEL.reply(new FeedbackPacket(EnumFeedbackInfo.HAS_CONTROLLER), context);
                return;
            }
            if (!network.getPlayerAccess(player).canUse()) {
                if (password.isEmpty()) {
                    PacketHandler.CHANNEL.reply(new FeedbackPacket(EnumFeedbackInfo.PASSWORD_REQUIRE), context);
                    return;
                }
                if (!password.equals(network.getNetworkPassword())) {
                    PacketHandler.CHANNEL.reply(new FeedbackPacket(EnumFeedbackInfo.REJECT), context);
                    return;
                }
            }
            flux.setConnectionOwner(PlayerEntity.getUUID(player.getGameProfile()));
            network.enqueueConnectionAddition(flux);
            PacketHandler.CHANNEL.reply(new FeedbackPacket(EnumFeedbackInfo.SUCCESS), context);
        }
    }
}
