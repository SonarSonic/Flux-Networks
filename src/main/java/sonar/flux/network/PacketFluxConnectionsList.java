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
import sonar.core.helpers.NBTHelper.SyncType;
import sonar.flux.FluxNetworks;
import sonar.flux.api.ClientFlux;
import sonar.flux.api.network.IFluxCommon;

public class PacketFluxConnectionsList implements IMessage {

	public List<ClientFlux> connections;
	public int networkID;

	public PacketFluxConnectionsList() {
	}

	public PacketFluxConnectionsList(List<ClientFlux> networks, int networkID) {
		this.connections = networks;
		this.networkID = networkID;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		this.networkID = buf.readInt();
        this.connections = new ArrayList<>();
		NBTTagCompound compound = ByteBufUtils.readTag(buf);
		NBTTagList list = compound.getTagList("connects", 10);

		for (int i = 0; i < list.tagCount(); i++) {
			NBTTagCompound c = list.getCompoundTagAt(i);
			connections.add(new ClientFlux(c));
		}
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(networkID);
		NBTTagCompound tag = new NBTTagCompound();
		NBTTagList list = new NBTTagList();
		connections.forEach(flux -> list.appendTag(flux.writeData(new NBTTagCompound(), SyncType.SAVE)));
		tag.setTag("connects", list);
		ByteBufUtils.writeTag(buf, tag);
	}

	public static class Handler implements IMessageHandler<PacketFluxConnectionsList, IMessage> {
		@Override
		public IMessage onMessage(PacketFluxConnectionsList message, MessageContext ctx) {
			if (ctx.side == Side.CLIENT) {
                SonarCore.proxy.getThreadListener(ctx.side).addScheduledTask(() -> {
				String playerName = SonarCore.proxy.getPlayerEntity(ctx).getName();
				IFluxCommon common = FluxNetworks.getClientCache().getNetwork(message.networkID);
				if (!common.isFakeNetwork()) {
					common.setClientConnections(message.connections);
				}
                });
			}
			return null;
		}
	}
}
