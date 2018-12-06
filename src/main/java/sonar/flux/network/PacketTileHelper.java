package sonar.flux.network;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import sonar.core.api.utils.BlockCoords;
import sonar.flux.FluxNetworks;
import sonar.flux.api.AdditionType;
import sonar.flux.api.FluxError;
import sonar.flux.api.RemovalType;
import sonar.flux.api.network.FluxPlayer;
import sonar.flux.api.network.IFluxNetwork;
import sonar.flux.client.gui.EnumGuiTab;
import sonar.flux.common.tileentity.TileFlux;

import javax.annotation.Nullable;

public class PacketTileHelper {

	public static void sendPacketToServer(PacketTileType type, TileFlux source, NBTTagCompound packetTag) {
		BlockCoords coords = source.getCoords();
		FluxNetworks.network.sendToServer(new PacketTile(type, coords.getBlockPos(), packetTag, coords.getDimension()));
	}

	public static void sendPacketToServer(PacketTileType type, BlockCoords coords, NBTTagCompound packetTag) {
		FluxNetworks.network.sendToServer(new PacketTile(type, coords.getBlockPos(), packetTag, coords.getDimension()));
	}

	//// SET NETWORK \\\\

	public static NBTTagCompound createNetworkSetPacket(int networkID) {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setInteger("networkID", networkID);
		return tag;
	}

	public static IMessage doNetworkSetPacket(TileFlux source, EntityPlayer player, NBTTagCompound packetTag) {
		int networkID = packetTag.getInteger("networkID");
		if (source.getNetwork().getNetworkID() == networkID) {
			return null;
		}
		IFluxNetwork network = FluxNetworks.getServerCache().getNetwork(networkID);
		if (!network.isFakeNetwork()) {
			if (network.getPlayerAccess(player).canConnect()) {
				source.getNetwork().queueConnectionRemoval(source, RemovalType.REMOVE);
				network.queueConnectionAddition(source, AdditionType.ADD);
			} else {
				return new PacketError(FluxError.ACCESS_DENIED);
			}
		}
		return null;
	}

	//// SET PRIORITY \\\\

	public static NBTTagCompound createSetPriorityPacket(int priority) {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setInteger("priority", priority);
		return tag;
	}

	public static IMessage doSetPriorityPacket(TileFlux source, EntityPlayer player, NBTTagCompound packetTag) {
		int priority = packetTag.getInteger("priority");
		if (source.canAccess(player).canEdit()) {
			source.priority.setValue(priority);
		} else {
			return new PacketError(FluxError.ACCESS_DENIED);
		}
		return null;
	}

	//// SET TRANSFER LIMIT \\\\

	public static NBTTagCompound createSetTransferLimitPacket(int transferLimit) {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setLong("transferLimit", transferLimit);
		return tag;
	}

	public static IMessage doSetTransferLimitPacket(TileFlux source, EntityPlayer player, NBTTagCompound packetTag) {
		long priority = packetTag.getLong("transferLimit");
		if (source.canAccess(player).canEdit()) {
			source.limit.setValue(priority);
		} else {
			return new PacketError(FluxError.ACCESS_DENIED);
		}
		return null;
	}

	//// DISCONNECT \\\\

	public static NBTTagCompound createDisconnectPacket(int networkID) {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setInteger("networkID", networkID);
		return tag;
	}

	public static IMessage doDisconnectPacket(TileFlux source, EntityPlayer player, NBTTagCompound packetTag) {
		int networkID = packetTag.getInteger("networkID");
		IFluxNetwork network = source.getNetwork();
		if (networkID == network.getNetworkID() && network.getPlayerAccess(player).canConnect() && source.playerUUID.getValue().equals(FluxPlayer.getOnlineUUID(player))) {
			network.queueConnectionRemoval(source, RemovalType.REMOVE);
		}
		return null;
	}

	//// TAB CHANGE \\\\

	public static NBTTagCompound createStateChangePacket(EnumGuiTab oldTab, @Nullable EnumGuiTab newTab) {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setInteger("oldTab", oldTab.ordinal());
		if(newTab != null) {
			tag.setInteger("newTab", newTab.ordinal());
		}
		return tag;
	}

	public static IMessage doStateChangePacket(TileFlux source, EntityPlayer player, NBTTagCompound packetTag) {
		EnumGuiTab oldTab = EnumGuiTab.values()[packetTag.getInteger("oldTab")];
		ListenerHelper.onPlayerCloseTileTab(source, player, oldTab);
		if(packetTag.hasKey("newTab")) {
			EnumGuiTab newTab = EnumGuiTab.values()[packetTag.getInteger("newTab")];
			ListenerHelper.onPlayerOpenTileTab(source, player, newTab);
		}
		return null;
	}

}
