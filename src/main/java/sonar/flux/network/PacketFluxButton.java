package sonar.flux.network;

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
import sonar.core.SonarCore;
import sonar.core.network.PacketCoords;
import sonar.flux.FluxNetworks;
import sonar.flux.common.tileentity.TileFlux;

/** FIXME, shouldn't need to have coords attached */
public class PacketFluxButton extends PacketCoords {

	public PacketType type;
	public NBTTagCompound packetTag;
	public int dimension;

	public PacketFluxButton() {}

	public PacketFluxButton(PacketType type, BlockPos pos, NBTTagCompound packetTag) {
		super(pos);
		this.type = type;
		this.packetTag = packetTag;
	}

	/** must be used if the TYPE isn't local */
	public PacketFluxButton(PacketType type, BlockPos pos, NBTTagCompound packetTag, int dimension) {
		this(type, pos, packetTag);
		this.dimension = dimension;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		super.fromBytes(buf);
		type = PacketType.values()[buf.readInt()];
		dimension = buf.readInt();
		packetTag = ByteBufUtils.readTag(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		super.toBytes(buf);
		buf.writeInt(type.ordinal());
		buf.writeInt(dimension);
		ByteBufUtils.writeTag(buf, packetTag);
	}

	public static class Handler implements IMessageHandler<PacketFluxButton, IMessage> {

		@Override
		public IMessage onMessage(PacketFluxButton message, MessageContext ctx) {
			SonarCore.proxy.getThreadListener(ctx.side).addScheduledTask(() -> {
				EntityPlayer player = SonarCore.proxy.getPlayerEntity(ctx);
				if (player != null) {
					World world = player.getEntityWorld();
					if (world.provider.getDimension() != message.dimension) {
						MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
						world = server.getWorld(message.dimension);
					}
					TileEntity te = world.getTileEntity(message.pos);
					if (te instanceof TileFlux) {
						TileFlux source = (TileFlux) te;
						IMessage returnedMessage = message.type.doPacket(source, player, message.packetTag);
						if(returnedMessage != null && player instanceof EntityPlayerMP){
							FluxNetworks.network.sendTo(returnedMessage, (EntityPlayerMP)player);
						}
					}
				}
			});
			return null;
		}
	}
}
