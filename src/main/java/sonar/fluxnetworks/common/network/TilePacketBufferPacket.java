package sonar.fluxnetworks.common.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;
import sonar.fluxnetworks.api.misc.ITilePacketBuffer;
import sonar.fluxnetworks.common.handler.PacketHandler;

@Deprecated
public class TilePacketBufferPacket extends AbstractPacket {

    public ITilePacketBuffer tile;
    public BlockPos pos;
    public byte id;
    public ByteBuf buf;

    public TilePacketBufferPacket(PacketBuffer buf) {
        pos = new BlockPos(buf.readInt(), buf.readInt(), buf.readInt());
        id = buf.readByte();
        this.buf = buf.retain();
    }

    public TilePacketBufferPacket(ITilePacketBuffer tile, BlockPos pos, byte id) {
        this.tile = tile;
        this.pos = pos;
        this.id = id;
    }

    @Override
    public void encode(PacketBuffer buf) {
        buf.writeInt(pos.getX());
        buf.writeInt(pos.getY());
        buf.writeInt(pos.getZ());
        buf.writeByte(id);
        tile.writePacket(buf, id);
    }

    @Override
    public Object handle(NetworkEvent.Context ctx) {
        PlayerEntity player = PacketHandler.getPlayer(ctx);
        if(player != null) {
            TileEntity tile = player.getEntityWorld().getTileEntity(pos);
            if (tile instanceof ITilePacketBuffer) {
                ((ITilePacketBuffer) tile).readPacket(new PacketBuffer(buf), id);
                buf.release();
            }
        }
        return null;
    }
}
