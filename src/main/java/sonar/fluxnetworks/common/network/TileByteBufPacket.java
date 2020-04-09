package sonar.fluxnetworks.common.network;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import sonar.fluxnetworks.api.tiles.ITileByteBuf;
import sonar.fluxnetworks.common.handler.PacketHandler;
import io.netty.buffer.ByteBuf;
import net.minecraft.util.math.BlockPos;

public class TileByteBufPacket extends AbstractPacket {

    public ITileByteBuf tile;
    public BlockPos pos;
    public int id;
    public ByteBuf buf;

    public TileByteBufPacket(PacketBuffer buf) {
        pos = new BlockPos(buf.readInt(), buf.readInt(), buf.readInt());
        id = buf.readInt();
        this.buf = buf.retain();
    }

    public TileByteBufPacket(ITileByteBuf tile, BlockPos pos, int id) {
        this.tile = tile;
        this.pos = pos;
        this.id = id;
    }

    @Override
    public void encode(PacketBuffer buf) {
        buf.writeInt(pos.getX());
        buf.writeInt(pos.getY());
        buf.writeInt(pos.getZ());
        buf.writeInt(id);
        tile.writePacket(buf, id);
    }

    @Override
    public Object handle(NetworkEvent.Context ctx) {
        PlayerEntity player = PacketHandler.getPlayer(ctx);
        if(player != null) {
            ITileByteBuf tile = (ITileByteBuf) player.getEntityWorld().getTileEntity(pos);
            if (tile != null) {
                tile.readPacket(new PacketBuffer(buf), id);
                buf.release();
            }
        }
        return null;
    }
}
