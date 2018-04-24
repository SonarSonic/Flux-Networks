package sonar.flux.network;

import java.util.ArrayList;
import java.util.List;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import sonar.core.SonarCore;
import sonar.core.helpers.NBTHelper;
import sonar.core.helpers.NBTHelper.SyncType;
import sonar.flux.FluxNetworks;
import sonar.flux.api.network.IFluxCommon;
import sonar.flux.api.network.IFluxNetwork;
import sonar.flux.connection.BasicFluxNetwork;

//TODO make a single packet version for updates
public class PacketFluxNetworkList implements IMessage {
	public List<? extends IFluxNetwork> networks;
	public boolean update;

	public PacketFluxNetworkList() {}

	public PacketFluxNetworkList(List<IFluxNetwork> toSend, boolean update) {
		this.networks = toSend;
		this.update = update;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		update = buf.readBoolean();

		NBTTagCompound compound = ByteBufUtils.readTag(buf);
		NBTTagList list = compound.getTagList("nets", 10);
		List<IFluxNetwork> networks = new ArrayList<>();
		for (int i = 0; i < list.tagCount(); i++) {
			networks.add(NBTHelper.instanceNBTSyncable(BasicFluxNetwork.class, list.getCompoundTagAt(i)));
		}
		this.networks = networks;
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeBoolean(update);
		NBTTagCompound tag = new NBTTagCompound();
		NBTTagList list = new NBTTagList();
		for (IFluxCommon network : networks) {
			if (network != null && !network.isFakeNetwork() && network.getNetworkID() != -1) {
				list.appendTag(network.writeData(new NBTTagCompound(), SyncType.SAVE));
			}
		}
		tag.setTag("nets", list);
		ByteBufUtils.writeTag(buf, tag);
	}

	public static class Handler implements IMessageHandler<PacketFluxNetworkList, IMessage> {

		@Override
		public IMessage onMessage(PacketFluxNetworkList message, MessageContext ctx) {
			if (ctx.side == Side.CLIENT) {
				SonarCore.proxy.getThreadListener(ctx.side).addScheduledTask(() -> FluxNetworks.getClientCache().updateNetworksFromPacket(message.networks, message.update));
			}
			return null;
		}
	}
}
