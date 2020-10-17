package sonar.fluxnetworks.common.network;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import sonar.fluxnetworks.api.gui.EnumFeedbackInfo;
import sonar.fluxnetworks.api.misc.FluxConfigurationType;
import sonar.fluxnetworks.api.misc.IMessage;
import sonar.fluxnetworks.api.network.IFluxNetwork;
import sonar.fluxnetworks.common.handler.NetworkHandler;
import sonar.fluxnetworks.common.item.ItemFluxConfigurator;
import sonar.fluxnetworks.common.misc.FluxUtils;
import sonar.fluxnetworks.common.storage.FluxNetworkData;

import javax.annotation.Nonnull;

public class CConfiguratorConnectMessage implements IMessage {

    private int id;
    private String password;

    public CConfiguratorConnectMessage() {
    }

    public CConfiguratorConnectMessage(int id, String password) {
        this.id = id;
        this.password = password;
    }

    @Override
    public void encode(@Nonnull PacketBuffer buffer) {
        buffer.writeVarInt(id);
        buffer.writeString(password, 256);
    }

    @Override
    public void handle(@Nonnull PacketBuffer buffer, @Nonnull NetworkEvent.Context context) {
        PlayerEntity player = NetworkHandler.getPlayer(context);
        if (player == null) {
            return;
        }
        int networkID = buffer.readVarInt();
        IFluxNetwork network = FluxNetworkData.getNetwork(networkID);
        if (network.isValid()) {
            if (!network.getPlayerAccess(player).canUse()) {
                String password = buffer.readString(256);
                if (password.isEmpty()) {
                    NetworkHandler.INSTANCE.reply(new SFeedbackMessage(EnumFeedbackInfo.PASSWORD_REQUIRE), context);
                    return;
                }
                if (!password.equals(network.getSecurity().getPassword())) {
                    NetworkHandler.INSTANCE.reply(new SFeedbackMessage(EnumFeedbackInfo.REJECT), context);
                    return;
                }
            }
            ItemStack stack = player.getHeldItemMainhand();
            if (stack.getItem() instanceof ItemFluxConfigurator) {
                CompoundNBT configs = stack.getOrCreateChildTag(FluxUtils.CONFIGS_TAG);
                configs.putInt(FluxConfigurationType.NETWORK.getNBTKey(), networkID);
            }
            NetworkHandler.INSTANCE.reply(new SFeedbackMessage(EnumFeedbackInfo.SUCCESS), context);
        }
    }
}
