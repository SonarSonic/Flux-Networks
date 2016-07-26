package sonar.flux.network;

import io.netty.buffer.ByteBuf;

import java.util.ArrayList;

import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import sonar.flux.FluxNetworks;
import sonar.flux.api.IFluxCommon;

public class PacketFluxNetworkList implements IMessage {

	public ArrayList<? extends IFluxCommon> networks;

	public PacketFluxNetworkList() {}

	public PacketFluxNetworkList(ArrayList<? extends IFluxCommon> toSend) {
		this.networks = toSend;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		networks = FluxNetworks.cache.fromBytes(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		CommonNetworkCache.toBytes(buf, networks);
	}

	public static class Handler implements IMessageHandler<PacketFluxNetworkList, IMessage> {
		@Override
		public IMessage onMessage(PacketFluxNetworkList message, MessageContext ctx) {
			return CommonNetworkCache.onPacket(message, ctx);
		}
	}
}
