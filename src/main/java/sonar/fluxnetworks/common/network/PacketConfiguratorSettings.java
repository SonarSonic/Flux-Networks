package sonar.fluxnetworks.common.network;

import sonar.fluxnetworks.common.handler.PacketHandler;
import sonar.fluxnetworks.common.item.ItemConfigurator;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import sonar.fluxnetworks.common.core.FluxUtils;


public class PacketConfiguratorSettings implements IMessageHandler<PacketConfiguratorSettings.ConfiguratorSettingsMessage, IMessage> {

    @Override
    public IMessage onMessage(PacketConfiguratorSettings.ConfiguratorSettingsMessage message, MessageContext ctx) {
        EntityPlayer player = PacketHandler.getPlayer(ctx);
        ItemStack stack = player.getHeldItemMainhand();
        if(stack.getItem() instanceof ItemConfigurator){
            if(!message.tag.isEmpty()) {
                stack.setTagInfo(FluxUtils.CONFIGS_TAG, message.tag);
            }
            if(message.customName != null){
                stack.setStackDisplayName(message.customName);
            }
        }
        return null;
    }

    public static class ConfiguratorSettingsMessage implements IMessage {

        public String customName;
        public NBTTagCompound tag;

        public ConfiguratorSettingsMessage() {}

        public ConfiguratorSettingsMessage(String customName, NBTTagCompound tag) {
            this.customName = customName;
            this.tag = tag;
        }

        @Override
        public void fromBytes(ByteBuf buf) {
            customName = ByteBufUtils.readUTF8String(buf);
            tag = ByteBufUtils.readTag(buf);
        }

        @Override
        public void toBytes(ByteBuf buf) {
            ByteBufUtils.writeUTF8String(buf, customName);
            ByteBufUtils.writeTag(buf, tag);
        }
    }
}
