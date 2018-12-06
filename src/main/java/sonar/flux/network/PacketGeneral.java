package sonar.flux.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import sonar.core.SonarCore;
import sonar.flux.FluxNetworks;

public class PacketGeneral implements IMessage {

    public PacketGeneralType type;
    public NBTTagCompound packetTag;
    public int dimension;

    public PacketGeneral() {}

    /** must be used if the TYPE isn't local */
    public PacketGeneral(PacketGeneralType type, NBTTagCompound packetTag) {
        super();
        this.type = type;
        this.packetTag = packetTag;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        type = PacketGeneralType.values()[buf.readInt()];
        dimension = buf.readInt();
        packetTag = ByteBufUtils.readTag(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(type.ordinal());
        buf.writeInt(dimension);
        ByteBufUtils.writeTag(buf, packetTag);
    }

    public static class Handler implements IMessageHandler<PacketGeneral, IMessage> {

        @Override
        public IMessage onMessage(PacketGeneral message, MessageContext ctx) {
            SonarCore.proxy.getThreadListener(ctx.side).addScheduledTask(() -> {
                // some actions like marking settings dirty will be wiped if not triggered at the start of the tick.
                FluxNetworks.proxy.scheduleRunnable(() -> {
                        EntityPlayer player = SonarCore.proxy.getPlayerEntity(ctx);
                        if (player != null) {
                            IMessage returnedMessage = message.type.doPacket(player, message.packetTag);
                            if (returnedMessage != null && player instanceof EntityPlayerMP) {
                                FluxNetworks.network.sendTo(returnedMessage, (EntityPlayerMP) player);
                            }
                        }
                    }
                );


            });
            return null;
        }
    }
}
