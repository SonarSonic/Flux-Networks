package sonar.flux.network;

import java.util.ArrayList;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import sonar.core.SonarCore;
import sonar.core.api.utils.BlockCoords;
import sonar.flux.FluxNetworks;
import sonar.flux.api.ClientFlux;
import sonar.flux.api.network.IFluxCommon;
import sonar.flux.api.tiles.IFlux.ConnectionType;

public class PacketFluxConnectionsList implements IMessage {

	public ArrayList<ClientFlux> connections;
	public int networkID;

	public PacketFluxConnectionsList() {
	}

	public PacketFluxConnectionsList(ArrayList<ClientFlux> networks, int networkID) {
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
			ClientFlux net = new ClientFlux(BlockCoords.readFromNBT(c), ConnectionType.values()[c.getInteger("type")], c.getInteger("priority"), c.getLong("limit"), c.getString("name"));
			connections.add(net);
		}
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(networkID);
		NBTTagCompound tag = new NBTTagCompound();
		NBTTagList list = new NBTTagList();
		for (ClientFlux flux : connections) {
			NBTTagCompound netTag = new NBTTagCompound();
			BlockCoords.writeToNBT(netTag, flux.getCoords());
			netTag.setInteger("type", flux.getConnectionType().ordinal());
			netTag.setInteger("priority", flux.getCurrentPriority());
			netTag.setLong("limit", flux.getTransferLimit());
			netTag.setString("name", flux.customName);
			list.appendTag(netTag);
		}
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
