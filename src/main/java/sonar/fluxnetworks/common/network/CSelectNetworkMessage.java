package sonar.fluxnetworks.common.network;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;
import sonar.fluxnetworks.api.device.IFluxDevice;
import sonar.fluxnetworks.api.misc.FeedbackInfo;
import sonar.fluxnetworks.api.misc.IMessage;
import sonar.fluxnetworks.api.network.FluxLogicType;
import sonar.fluxnetworks.api.network.IFluxNetwork;
import sonar.fluxnetworks.common.storage.FluxNetworkData;

import javax.annotation.Nonnull;

public class CSelectNetworkMessage implements IMessage {

    private BlockPos pos;
    private int networkID;
    private String password;

    public CSelectNetworkMessage() {
    }

    public CSelectNetworkMessage(BlockPos pos, int networkID, String password) {
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
        PlayerEntity player = NetworkHandler.getPlayer(context);
        if (player == null)
            return;

        TileEntity tile = player.world.getTileEntity(buffer.readBlockPos());
        if (!(tile instanceof IFluxDevice))
            return;

        IFluxDevice flux = (IFluxDevice) tile;
        int networkID = buffer.readVarInt();
        if (flux.getNetworkID() == networkID)
            return;

        IFluxNetwork network = FluxNetworkData.getNetwork(networkID);
        if (!network.isValid())
            return;

        if (flux.getDeviceType().isController() && !network.getConnections(FluxLogicType.CONTROLLER).isEmpty()) {
            NetworkHandler.INSTANCE.reply(new SFeedbackMessage(FeedbackInfo.HAS_CONTROLLER), context);
        } else {
            if (checkAccess(buffer, context, player, network))
                return;
            flux.setConnectionOwner(PlayerEntity.getUUID(player.getGameProfile()));
            network.enqueueConnectionAddition(flux);
            NetworkHandler.INSTANCE.reply(new SFeedbackMessage(FeedbackInfo.SUCCESS), context);
        }
    }

    static boolean checkAccess(@Nonnull PacketBuffer buffer, @Nonnull NetworkEvent.Context context, PlayerEntity player, @Nonnull IFluxNetwork network) {
        if (!network.getPlayerAccess(player).canUse()) {
            String password = buffer.readString(256);
            if (password.isEmpty()) {
                NetworkHandler.INSTANCE.reply(new SFeedbackMessage(FeedbackInfo.PASSWORD_REQUIRE), context);
                return true;
            }
            if (!password.equals(network.getSecurity().getPassword())) {
                NetworkHandler.INSTANCE.reply(new SFeedbackMessage(FeedbackInfo.REJECT), context);
                return true;
            }
        }
        return false;
    }
}
