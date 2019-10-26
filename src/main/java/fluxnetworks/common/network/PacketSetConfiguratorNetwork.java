package fluxnetworks.common.network;

import fluxnetworks.FluxNetworks;
import fluxnetworks.api.gui.EnumFeedbackInfo;
import fluxnetworks.api.utils.FluxConfigurationType;
import fluxnetworks.api.network.IFluxNetwork;
import fluxnetworks.common.connection.FluxNetworkCache;
import fluxnetworks.api.network.NetworkSettings;
import fluxnetworks.common.item.ItemConfigurator;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import static fluxnetworks.common.core.FluxUtils.CONFIGS_TAG;

public class PacketSetConfiguratorNetwork implements IMessageHandler<PacketSetConfiguratorNetwork.SetConfiguratorNetworkMessage, IMessage> {

    @Override
    public IMessage onMessage(SetConfiguratorNetworkMessage message, MessageContext ctx) {
        EntityPlayer player = FluxNetworks.proxy.getPlayer(ctx);

        IFluxNetwork network = FluxNetworkCache.instance.getNetwork(message.id);
        if(!network.isInvalid()) {
            if (!network.getMemberPermission(player).canAccess()) {
                if (message.password.isEmpty()) {
                    return new PacketFeedback.FeedbackMessage(EnumFeedbackInfo.PASSWORD_REQUIRE);
                }
                if (!message.password.equals(network.getSetting(NetworkSettings.NETWORK_PASSWORD))) {
                    return new PacketFeedback.FeedbackMessage(EnumFeedbackInfo.REJECT);
                }
            }
            ItemStack stack = player.getHeldItemMainhand();
            if(stack.getItem() instanceof ItemConfigurator){
                NBTTagCompound configs = stack.getOrCreateSubCompound(CONFIGS_TAG);
                configs.setInteger(FluxConfigurationType.NETWORK.getNBTName(), message.id);
            }
            return new PacketFeedback.FeedbackMessage(EnumFeedbackInfo.SUCCESS);
        }
        return null;
    }

    public static class SetConfiguratorNetworkMessage implements IMessage{

        public int id;
        public String password;

        public SetConfiguratorNetworkMessage(){}

        public SetConfiguratorNetworkMessage(int id, String password){
            this.id = id;
            this.password = password;
        }

        @Override
        public void fromBytes(ByteBuf buf) {
            id = buf.readInt();
            password = ByteBufUtils.readUTF8String(buf);
        }

        @Override
        public void toBytes(ByteBuf buf) {
            buf.writeInt(id);
            ByteBufUtils.writeUTF8String(buf, password);
        }
    }
}
