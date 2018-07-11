package sonar.flux.network;


import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import sonar.core.SonarCore;
import sonar.core.helpers.NBTHelper.SyncType;
import sonar.flux.FluxNetworks;
import sonar.flux.api.ClientFlux;
import sonar.flux.api.NetworkFluxFolder;
import sonar.flux.api.network.IFluxNetwork;
import sonar.flux.connection.NetworkSettings;

import java.util.ArrayList;
import java.util.List;

public class PacketConnectionsClientList implements IMessage {

	public List<ClientFlux> connections;
	public List<NetworkFluxFolder> folders;
	public int networkID;

	public PacketConnectionsClientList() {}

	public PacketConnectionsClientList(IFluxNetwork network) {
		this.connections = network.getSetting(NetworkSettings.CLIENT_CONNECTIONS);
		this.folders = network.getSetting(NetworkSettings.NETWORK_FOLDERS);
		this.networkID = network.getSetting(NetworkSettings.NETWORK_ID);
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		this.networkID = buf.readInt();

		NBTTagCompound compound = ByteBufUtils.readTag(buf);

		NBTTagList flux_list = compound.getTagList("flux", Constants.NBT.TAG_COMPOUND);
		this.connections = new ArrayList<>();
		for (int i = 0; i < flux_list.tagCount(); i++) {
			NBTTagCompound c = flux_list.getCompoundTagAt(i);
			connections.add(new ClientFlux(c));
		}

		NBTTagList folders_list = compound.getTagList("network_folders", Constants.NBT.TAG_COMPOUND);
		this.folders = new ArrayList<>();
		for (int i = 0; i < folders_list.tagCount(); i++) {
			NBTTagCompound c = folders_list.getCompoundTagAt(i);
			folders.add(new NetworkFluxFolder(c));
		}
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(networkID);

		NBTTagCompound tag = new NBTTagCompound();

		NBTTagList flux_list = new NBTTagList();
		connections.forEach(flux -> flux_list.appendTag(flux.writeData(new NBTTagCompound(), SyncType.SAVE)));
		tag.setTag("flux", flux_list);

		NBTTagList folders_list = new NBTTagList();
		folders.forEach(flux -> folders_list.appendTag(flux.writeData(new NBTTagCompound(), SyncType.SAVE)));
		tag.setTag("network_folders", folders_list);

		ByteBufUtils.writeTag(buf, tag);
	}

	public static class Handler implements IMessageHandler<PacketConnectionsClientList, IMessage> {
		@Override
		public IMessage onMessage(PacketConnectionsClientList message, MessageContext ctx) {
			if (ctx.side == Side.CLIENT) {
                SonarCore.proxy.getThreadListener(ctx.side).addScheduledTask(() -> {
					IFluxNetwork common = FluxNetworks.getClientCache().getNetwork(message.networkID);
					if (!common.isFakeNetwork()) {
						common.setSettingInternal(NetworkSettings.CLIENT_CONNECTIONS, message.connections);
						common.setSettingInternal(NetworkSettings.NETWORK_FOLDERS, message.folders);
					}
                });
			}
			return null;
		}
	}
}
