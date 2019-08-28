package fluxnetworks.common.network;

import fluxnetworks.common.handler.PacketHandler;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * Gui operation packets.
 */
public class PacketGeneral implements IMessageHandler<PacketGeneral.GeneralMessage, IMessage> {

    @Override
    public IMessage onMessage(GeneralMessage message, MessageContext ctx) {

        EntityPlayer player = PacketHandler.getPlayer(ctx);
        if(player != null) {
            PacketHandler.handlePacket(() -> {
                IMessage returned = message.handler.handler.handlePacket(player, message.nbtTag);
                if(returned != null && player instanceof EntityPlayerMP) {
                    PacketHandler.network.sendTo(returned, (EntityPlayerMP) player);
                }
            }, ctx.netHandler);
        }
        return null;
    }

    public static class GeneralMessage implements IMessage {

        public PacketGeneralType handler;
        public NBTTagCompound nbtTag;

        public GeneralMessage() {};

        public GeneralMessage(PacketGeneralType handler, NBTTagCompound nbtTag) {
            super();
            this.handler = handler;
            this.nbtTag = nbtTag;
        }

        @Override
        public void fromBytes(ByteBuf buf) {
            handler = PacketGeneralType.values()[buf.readInt()];
            nbtTag = ByteBufUtils.readTag(buf);
        }

        @Override
        public void toBytes(ByteBuf buf) {
            buf.writeInt(handler.ordinal());
            ByteBufUtils.writeTag(buf, nbtTag);
        }
    }

}
