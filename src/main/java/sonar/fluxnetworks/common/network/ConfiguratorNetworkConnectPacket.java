package sonar.fluxnetworks.common.network;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import sonar.fluxnetworks.api.gui.EnumFeedbackInfo;
import sonar.fluxnetworks.api.utils.FluxConfigurationType;
import sonar.fluxnetworks.api.network.IFluxNetwork;
import sonar.fluxnetworks.common.connection.FluxNetworkCache;
import sonar.fluxnetworks.api.network.NetworkSettings;
import sonar.fluxnetworks.common.handler.PacketHandler;
import sonar.fluxnetworks.common.item.FluxConfiguratorItem;
import net.minecraft.item.ItemStack;
import sonar.fluxnetworks.common.core.FluxUtils;

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
        buf.writeString(password);
    }

    @Override
    public Object handle(NetworkEvent.Context ctx) {
        PlayerEntity player = PacketHandler.getPlayer(ctx);

        IFluxNetwork network = FluxNetworkCache.instance.getNetwork(id);
        if(!network.isInvalid()) {
            if (!network.getMemberPermission(player).canAccess()) {
                if (password.isEmpty()) {
                    return new FeedbackPacket(EnumFeedbackInfo.PASSWORD_REQUIRE);
                }
                if (!password.equals(network.getSetting(NetworkSettings.NETWORK_PASSWORD))) {
                    return new FeedbackPacket(EnumFeedbackInfo.REJECT);
                }
            }
            ItemStack stack = player.getHeldItemMainhand();
            if(stack.getItem() instanceof FluxConfiguratorItem){
                CompoundNBT configs = stack.getOrCreateChildTag(FluxUtils.CONFIGS_TAG);
                configs.putInt(FluxConfigurationType.NETWORK.getNBTName(), id);
            }
            return new FeedbackPacket(EnumFeedbackInfo.SUCCESS);
        }
        return null;
    }
}
