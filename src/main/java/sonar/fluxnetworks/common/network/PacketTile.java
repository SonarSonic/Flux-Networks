package sonar.fluxnetworks.common.network;

import sonar.fluxnetworks.common.handler.PacketHandler;
import sonar.fluxnetworks.common.tileentity.TileFluxCore;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketTile implements IMessageHandler<PacketTile.TileMessage, IMessage> {

    @Override
    public IMessage onMessage(TileMessage message, MessageContext ctx) {
        EntityPlayer player = PacketHandler.getPlayer(ctx);
        if(player != null) {
            World world = player.getEntityWorld();
            if(world.provider.getDimension() != message.dimension) {
                MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
                world = server.getWorld(message.dimension);
            }
            TileEntity tile = world.getTileEntity(message.pos);
            if(tile instanceof TileFluxCore) {
                TileFluxCore flux = (TileFluxCore) tile;
                PacketHandler.handlePacket(() -> {
                    IMessage returned = message.handler.handler.handlePacket(flux, player, message.tag);
                    if(returned != null && player instanceof EntityPlayerMP) {
                        PacketHandler.network.sendTo(returned, (EntityPlayerMP) player);
                    }
                }, ctx.netHandler);
            }
        }
        return null;
    }

    public static class TileMessage implements IMessage {

        public PacketTileType handler;
        public NBTTagCompound tag;
        public BlockPos pos;
        public int dimension;

        public TileMessage() {}

        public TileMessage(PacketTileType handler, NBTTagCompound tag, BlockPos pos, int dimension) {
            this.handler = handler;
            this.tag = tag;
            this.pos = pos;
            this.dimension = dimension;
        }

        @Override
        public void fromBytes(ByteBuf buf) {
            pos = new BlockPos(buf.readInt(), buf.readInt(), buf.readInt());
            dimension = buf.readInt();
            handler = PacketTileType.values()[buf.readInt()];
            tag = ByteBufUtils.readTag(buf);
        }

        @Override
        public void toBytes(ByteBuf buf) {
            buf.writeInt(pos.getX());
            buf.writeInt(pos.getY());
            buf.writeInt(pos.getZ());
            buf.writeInt(dimension);
            buf.writeInt(handler.ordinal());
            ByteBufUtils.writeTag(buf, tag);
        }
    }
}
