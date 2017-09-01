package sonar.flux.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import sonar.core.SonarCore;
import sonar.core.network.PacketCoords;
import sonar.core.network.PacketTileEntityHandler;
import sonar.flux.api.FluxError;
import sonar.flux.common.tileentity.TileEntityFlux;

public class PacketFluxError extends PacketCoords {

	public FluxError error;

	public PacketFluxError() {}

	public PacketFluxError(BlockPos pos, FluxError error) {
		super(pos);
		this.error = error;
	}

	public void fromBytes(ByteBuf buf) {
		super.fromBytes(buf);
		error = FluxError.values()[buf.readInt()];
	}

	public void toBytes(ByteBuf buf) {
		super.toBytes(buf);
		buf.writeInt(error.ordinal());
	}

	public static class Handler extends PacketTileEntityHandler<PacketFluxError> {

		@Override
		public IMessage processMessage(EntityPlayer player, MessageContext ctx, PacketFluxError message, TileEntity target) {
			if (target instanceof TileEntityFlux) {
				SonarCore.proxy.getThreadListener(ctx).addScheduledTask(new Runnable() {
					@Override
					public void run() {
						TileEntityFlux flux = (TileEntityFlux) target;
						flux.error = message.error;
					}
				});
			}
			return null;
		}
	}
}
