package sonar.flux.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import sonar.core.SonarCore;
import sonar.core.helpers.NBTHelper.SyncType;
import sonar.flux.FluxNetworks;
import sonar.flux.api.network.IFluxNetwork;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

//TODO make a single packet version for updates
public class PacketFluxNetworkUpdate implements IMessage{

	public SyncType type;
	public boolean wipe;
	public Map<Integer, NBTTagCompound> network_updates = new HashMap<>();

	public PacketFluxNetworkUpdate() {}

	public PacketFluxNetworkUpdate(List<IFluxNetwork> toSend, SyncType type, boolean wipe) {
		this.type = type;
		this.wipe = wipe;
		toSend.forEach(network -> {
			NBTTagCompound tag = new NBTTagCompound();
			network.writeData(tag, type);
			if(!tag.hasNoTags() && !network.isFakeNetwork()){
				network_updates.put(network.getNetworkID(), tag);
			}
		});
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		wipe = buf.readBoolean();
		type = SyncType.values()[buf.readInt()];
		int size = buf.readInt();
		for(int i = 0; i < size; i++){
			network_updates.put(buf.readInt(), ByteBufUtils.readTag(buf));
		}
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeBoolean(wipe);
		buf.writeInt(type.ordinal());
		buf.writeInt(network_updates.size());
		network_updates.forEach((I, NBT) -> {
			buf.writeInt(I);
			ByteBufUtils.writeTag(buf, NBT);
		});
	}

	public static class Handler implements IMessageHandler<PacketFluxNetworkUpdate, IMessage> {

		@Override
		public IMessage onMessage(PacketFluxNetworkUpdate message, MessageContext ctx) {
			if (ctx.side == Side.CLIENT) {
				SonarCore.proxy.getThreadListener(ctx.side).addScheduledTask(() -> {
					if(message.wipe)
						FluxNetworks.getClientCache().clearNetworks();
					FluxNetworks.getClientCache().updateNetworksFromPacket(message.network_updates, message.type);

				});
			}
			return null;
		}
	}
}
