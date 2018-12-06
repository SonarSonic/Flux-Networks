package sonar.flux.network;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import sonar.core.SonarCore;
import sonar.flux.FluxNetworks;
import sonar.flux.api.FluxError;

public class PacketError implements IMessage {

	public FluxError error;

	public PacketError() {}

	public PacketError(FluxError error) {
		super();
		this.error = error;
	}

	public void fromBytes(ByteBuf buf) {
		error = FluxError.values()[buf.readInt()];
	}

	public void toBytes(ByteBuf buf) {
		buf.writeInt(error.ordinal());
	}

	public static class Handler implements IMessageHandler<PacketError, IMessage> {

		@Override
		public IMessage onMessage(PacketError message, MessageContext ctx) {
			SonarCore.proxy.getThreadListener(ctx.side).addScheduledTask(() -> {
				FluxNetworks.proxy.setFluxError(message.error);
			});
			return null;
		}
	}
}
