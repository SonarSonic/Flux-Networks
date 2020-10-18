package sonar.fluxnetworks.common.network;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import sonar.fluxnetworks.api.device.IFluxDevice;
import sonar.fluxnetworks.api.gui.EnumFeedbackInfo;
import sonar.fluxnetworks.api.misc.FluxConstants;
import sonar.fluxnetworks.api.network.FluxLogicType;
import sonar.fluxnetworks.api.network.IFluxNetwork;
import sonar.fluxnetworks.api.network.SecurityType;
import sonar.fluxnetworks.common.storage.FluxNetworkData;
import sonar.fluxnetworks.common.tileentity.TileFluxDevice;

import javax.annotation.Nonnull;
import java.util.List;

public class CEditNetworkMessage extends CCreateNetworkMessage {

    private int networkID;

    public CEditNetworkMessage() {
    }

    public CEditNetworkMessage(int networkID, String name, int color, SecurityType security, String password) {
        super(name, color, security, password);
        this.networkID = networkID;
    }

    @Override
    public void encode(@Nonnull PacketBuffer buffer) {
        super.encode(buffer);
        buffer.writeVarInt(networkID);
    }

    @Override
    protected void handle(@Nonnull PacketBuffer buffer, @Nonnull NetworkEvent.Context context, PlayerEntity player) {
        int networkID = buffer.readVarInt();
        IFluxNetwork network = FluxNetworkData.getNetwork(networkID);
        if (!network.isValid()) {
            return;
        }
        if (network.getPlayerAccess(player).canEdit()) {
            if (!network.getNetworkName().equals(name)) {
                network.setNetworkName(name);
            }
            if (network.getNetworkColor() != color) {
                network.setNetworkColor(color);
                List<IFluxDevice> list = network.getConnections(FluxLogicType.ANY);
                list.forEach(device -> {
                    if (device instanceof TileFluxDevice) {
                        ((TileFluxDevice) device).sendFullUpdatePacket();
                    }
                }); // update color data
            }
            network.getSecurity().set(security, password);
            NetworkHandler.INSTANCE.reply(new SNetworkUpdateMessage(network, FluxConstants.TYPE_NET_BASIC), context);
            NetworkHandler.INSTANCE.reply(new SFeedbackMessage(EnumFeedbackInfo.SUCCESS_2), context);
        } else {
            NetworkHandler.INSTANCE.reply(new SFeedbackMessage(EnumFeedbackInfo.NO_ADMIN), context);
        }
    }
}
