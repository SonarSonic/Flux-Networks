package sonar.flux.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import sonar.core.SonarCore;
import sonar.flux.common.item.FluxConfigurator;

public class PacketConfiguratorSettings implements IMessage {

    public NBTTagCompound disabledTag;

    public PacketConfiguratorSettings() {
    }

    public PacketConfiguratorSettings(NBTTagCompound disabledTag) {
        this.disabledTag = disabledTag;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        disabledTag = ByteBufUtils.readTag(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeTag(buf, disabledTag);
    }

    public static class Handler implements IMessageHandler<PacketConfiguratorSettings, IMessage> {

        @Override
        public IMessage onMessage(PacketConfiguratorSettings message, MessageContext ctx) {
            SonarCore.proxy.getThreadListener(ctx).addScheduledTask(() -> {
                EntityPlayer player = SonarCore.proxy.getPlayerEntity(ctx);
                ItemStack heldItem = player.getHeldItem(player.getActiveHand());
                if (!heldItem.isEmpty() && heldItem.getItem() instanceof FluxConfigurator) {
                    NBTTagCompound tag = heldItem.getTagCompound();
                    if (tag == null) {
                        heldItem.setTagCompound(tag = new NBTTagCompound());
                    }
                    tag.setTag(FluxConfigurator.DISABLED_TAG, message.disabledTag);
                }
            });
            return null;
        }
    }
}