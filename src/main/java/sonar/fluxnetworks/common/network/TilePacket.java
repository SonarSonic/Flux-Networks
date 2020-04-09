package sonar.fluxnetworks.common.network;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import sonar.fluxnetworks.common.handler.PacketHandler;
import sonar.fluxnetworks.common.tileentity.TileFluxCore;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class TilePacket extends AbstractPacket {

    public TilePacketEnum handler;
    public CompoundNBT tag;
    public BlockPos pos;
    public int dimension;

    public TilePacket(PacketBuffer buf) {
        pos = new BlockPos(buf.readInt(), buf.readInt(), buf.readInt());
        dimension = buf.readInt();
        handler = TilePacketEnum.values()[buf.readInt()];
        tag = buf.readCompoundTag();
    }

    public TilePacket(TilePacketEnum handler, CompoundNBT tag, BlockPos pos, int dimension) {
        this.handler = handler;
        this.tag = tag;
        this.pos = pos;
        this.dimension = dimension;
    }

    @Override
    public void encode(PacketBuffer buf) {
        buf.writeInt(pos.getX());
        buf.writeInt(pos.getY());
        buf.writeInt(pos.getZ());
        buf.writeInt(dimension);
        buf.writeInt(handler.ordinal());
        buf.writeCompoundTag(tag);
    }

    @Override
    public Object handle(NetworkEvent.Context ctx) {
        PlayerEntity player = PacketHandler.getPlayer(ctx);
        if(player != null) {
            World world = player.getEntityWorld();
            if(world.getDimension().getType().getId() != dimension) {
                MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
                world = server.getWorld(DimensionType.getById(dimension));
            }
            TileEntity tile = world.getTileEntity(pos);
            if(tile instanceof TileFluxCore) {
                TileFluxCore flux = (TileFluxCore) tile;
                return handler.handler.handlePacket(flux, player, tag);
            }
        }
        return null;
    }
}
