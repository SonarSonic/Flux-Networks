package sonar.flux.network;

import com.google.common.collect.Lists;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import sonar.core.SonarCore;
import sonar.core.helpers.NBTHelper;
import sonar.flux.FluxNetworks;
import sonar.flux.api.IFluxItemGui;
import sonar.flux.api.network.IFluxNetwork;

public class PacketFluxItemNetwork implements IMessage {

    public int networkID;

    public PacketFluxItemNetwork() {}

    public PacketFluxItemNetwork(int networkID) {
        this.networkID = networkID;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        networkID = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(networkID);
    }

    public static class Handler implements IMessageHandler<PacketFluxItemNetwork, IMessage> {

        @Override
        public IMessage onMessage(PacketFluxItemNetwork message, MessageContext ctx) {
            SonarCore.proxy.getThreadListener(ctx.side).addScheduledTask(() -> {
                EntityPlayer player = SonarCore.proxy.getPlayerEntity(ctx);
                ItemStack heldItem = player.getHeldItem(player.getActiveHand());
                if (!heldItem.isEmpty() && heldItem.getItem() instanceof IFluxItemGui) {
                    IFluxNetwork network = FluxNetworkCache.instance().getNetwork(message.networkID);
                    if(!network.isFakeNetwork()) {
                        ((IFluxItemGui) heldItem.getItem()).setViewingNetworkID(heldItem, message.networkID);
                        FluxNetworks.network.sendTo(new PacketNetworkUpdate(Lists.newArrayList(network), NBTHelper.SyncType.SAVE, false), (EntityPlayerMP)player);
                        FluxNetworks.network.sendTo(new PacketUpdateGuiItem(heldItem), (EntityPlayerMP)player);
                    }
                }
            });
            return null;
        }
    }
}