package fluxnetworks.common.network;

import fluxnetworks.FluxNetworks;
import fluxnetworks.api.FeedbackInfo;
import fluxnetworks.api.network.IFluxNetwork;
import fluxnetworks.common.connection.FluxNetworkCache;
import fluxnetworks.common.core.NBTType;
import fluxnetworks.common.handler.PacketHandler;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PacketNetworkUpdate implements IMessageHandler<PacketNetworkUpdate.NetworkUpdateMessage, IMessage> {

    @Override
    public IMessage onMessage(NetworkUpdateMessage message, MessageContext ctx) {
        if(ctx.side == Side.CLIENT) {
            PacketHandler.handlePacket(() -> FluxNetworkCache.instance.updateClientFromPacket(message.updatedNetworks, message.type), ctx.netHandler);
        }

        return null;
    }

    public static class NetworkUpdateMessage implements IMessage {

        public Map<Integer, NBTTagCompound> updatedNetworks = new HashMap<>();
        public NBTType type;

        public NetworkUpdateMessage() {}

        public NetworkUpdateMessage(List<IFluxNetwork> toSend, NBTType type) {
            this.type = type;
            toSend.forEach(n -> {
                NBTTagCompound tag = new NBTTagCompound();
                n.writeNetworkNBT(tag, type);
                if(!tag.hasNoTags()) {
                    updatedNetworks.put(n.getNetworkID(), tag);
                }
            });
        }

        @Override
        public void fromBytes(ByteBuf buf) {
            type = NBTType.values()[buf.readInt()];
            int size = buf.readInt();
            for(int i = 0; i < size; i++) {
                updatedNetworks.put(buf.readInt(), ByteBufUtils.readTag(buf));
            }
        }

        @Override
        public void toBytes(ByteBuf buf) {
            buf.writeInt(type.ordinal());
            buf.writeInt(updatedNetworks.size());
            updatedNetworks.forEach((i, n) -> {
                buf.writeInt(i);
                ByteBufUtils.writeTag(buf, n);
            });
        }
    }
}
