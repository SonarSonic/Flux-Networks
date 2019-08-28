package fluxnetworks.common.network;

import fluxnetworks.api.tileentity.ITileByteBuf;
import fluxnetworks.common.handler.PacketHandler;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketByteBuf implements IMessageHandler<PacketByteBuf.ByteBufMessage, IMessage> {

    @Override
    public IMessage onMessage(ByteBufMessage message, MessageContext ctx) {
        EntityPlayer player = PacketHandler.getPlayer(ctx);

        if(player != null) {
            ITileByteBuf tile = (ITileByteBuf) player.getEntityWorld().getTileEntity(message.pos);
            if (tile != null) {
                PacketHandler.handlePacket(() -> {
                    tile.readPacket(message.buf, message.id);
                    message.buf.release();
                }, ctx.netHandler);
            }
        }
        return null;
    }

    public static class ByteBufMessage implements IMessage {

        public ITileByteBuf tile;
        public BlockPos pos;
        public int id;
        public ByteBuf buf;

        public ByteBufMessage() {}

        public ByteBufMessage(ITileByteBuf tile, BlockPos pos, int id) {
            this.tile = tile;
            this.pos = pos;
            this.id = id;
        }

        @Override
        public void fromBytes(ByteBuf buf) {
            pos = new BlockPos(buf.readInt(), buf.readInt(), buf.readInt());
            id = buf.readInt();
            this.buf = buf.retain();
        }

        @Override
        public void toBytes(ByteBuf buf) {
            buf.writeInt(pos.getX());
            buf.writeInt(pos.getY());
            buf.writeInt(pos.getZ());
            buf.writeInt(id);
            tile.writePacket(buf, id);
        }
    }
}
