package sonar.flux.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import sonar.core.SonarCore;
import sonar.flux.FluxNetworks;

public class PacketUpdateGuiItem implements IMessage {

    public ItemStack stack;

    public PacketUpdateGuiItem(){}

    public PacketUpdateGuiItem(ItemStack stack){
        this.stack = stack;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        stack = ByteBufUtils.readItemStack(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeItemStack(buf, stack);
    }


    public static class Handler implements IMessageHandler<PacketUpdateGuiItem, IMessage> {

        @Override
        public IMessage onMessage(PacketUpdateGuiItem message, MessageContext ctx) {
            SonarCore.proxy.getThreadListener(ctx.side).addScheduledTask(() -> {
                EntityPlayer player = SonarCore.proxy.getPlayerEntity(ctx);
                ItemStack stack = message.stack;
                ItemStack held = player.getHeldItemMainhand();
                if(!stack.isEmpty() && stack.getItem() == held.getItem()){
                    held.setTagCompound(stack.getTagCompound());
                    FluxNetworks.proxy.setFluxStack(message.stack);
                }
            });
            return null;
        }
    }
}
