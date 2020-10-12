package sonar.fluxnetworks.common.network;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import sonar.fluxnetworks.api.gui.EnumFeedbackInfo;
import sonar.fluxnetworks.api.misc.FluxConfigurationType;
import sonar.fluxnetworks.api.network.IFluxNetwork;
import sonar.fluxnetworks.common.handler.PacketHandler;
import sonar.fluxnetworks.common.item.ItemFluxConfigurator;
import net.minecraft.item.ItemStack;
import sonar.fluxnetworks.common.misc.FluxUtils;
import sonar.fluxnetworks.common.storage.FluxNetworkData;

public class ConfiguratorNetworkConnectPacket extends AbstractPacket {

    public int id;
    public String password;

    public ConfiguratorNetworkConnectPacket(int id, String password){
        this.id = id;
        this.password = password;
    }

    public ConfiguratorNetworkConnectPacket(PacketBuffer buf){
        id = buf.readInt();
        password = buf.readString(256);
    }

    @Override
    public void encode(PacketBuffer buf) {
        buf.writeInt(id);
        buf.writeString(password, 256);
    }

    @Override
    public Object handle(NetworkEvent.Context ctx) {
        PlayerEntity player = PacketHandler.getPlayer(ctx);

        IFluxNetwork network = FluxNetworkData.getNetwork(id);
        if(network.isValid()) {
            if (!network.getPlayerAccess(player).canUse()) {
                if (password.isEmpty()) {
                    return new SFeedbackMessage(EnumFeedbackInfo.PASSWORD_REQUIRE);
                }
                if (!password.equals(network.getNetworkPassword())) {
                    return new SFeedbackMessage(EnumFeedbackInfo.REJECT);
                }
            }
            ItemStack stack = player.getHeldItemMainhand();
            if(stack.getItem() instanceof ItemFluxConfigurator){
                CompoundNBT configs = stack.getOrCreateChildTag(FluxUtils.CONFIGS_TAG);
                configs.putInt(FluxConfigurationType.NETWORK.getNBTName(), id);
            }
            return new SFeedbackMessage(EnumFeedbackInfo.SUCCESS);
        }
        return null;
    }
}
