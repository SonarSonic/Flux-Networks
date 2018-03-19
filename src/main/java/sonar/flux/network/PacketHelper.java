package sonar.flux.network;

import java.util.UUID;

import com.mojang.authlib.GameProfile;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import sonar.core.api.utils.BlockCoords;
import sonar.core.helpers.SonarHelper;
import sonar.core.utils.CustomColour;
import sonar.flux.FluxNetworks;
import sonar.flux.api.AccessType;
import sonar.flux.api.AdditionType;
import sonar.flux.api.FluxError;
import sonar.flux.api.RemovalType;
import sonar.flux.api.network.IFluxCommon;
import sonar.flux.api.network.IFluxNetwork;
import sonar.flux.api.network.PlayerAccess;
import sonar.flux.client.GuiTypeMessage;
import sonar.flux.common.containers.ContainerFlux;
import sonar.flux.common.tileentity.TileFlux;
import sonar.flux.connection.FluxHelper;

public class PacketHelper {

	public static void sendPacketToServer(PacketType type, TileFlux source, NBTTagCompound packetTag) {
		BlockCoords coords = source.getCoords();
		FluxNetworks.network.sendToServer(new PacketFluxButton(type, coords.getBlockPos(), packetTag, coords.getDimension()));
	}

	public static void sendPacketToServer(PacketType type, BlockCoords coords, NBTTagCompound packetTag) {
		FluxNetworks.network.sendToServer(new PacketFluxButton(type, coords.getBlockPos(), packetTag, coords.getDimension()));
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
				source.getNetwork().removeConnection(source, RemovalType.REMOVE);
				network.addConnection(source, AdditionType.ADD);
			} else {
				return new PacketFluxError(source.getPos(), FluxError.ACCESS_DENIED);
			}
		}
		return null;
	}

	//// EDIT NETWORK \\\\

	public static NBTTagCompound createNetworkEditPacket(int networkID, String networkName, CustomColour networkColour, AccessType accessType) {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setInteger("networkID", networkID);
		tag.setString("networkName", networkName);
		tag.setInteger("colourRGB", networkColour.getRGB());
		tag.setInteger("accessType", accessType.ordinal());
		return tag;
	}

	public static IMessage doNetworkEditPacket(TileFlux source, EntityPlayer player, NBTTagCompound packetTag) {
		int networkID = packetTag.getInteger("networkID");
		String newName = packetTag.getString("networkName");
		CustomColour colour = new CustomColour(packetTag.getInteger("colourRGB"));
		AccessType access = AccessType.values()[packetTag.getInteger("accessType")];

		IFluxNetwork common = FluxNetworks.getServerCache().getNetwork(networkID);
		if (!common.isFakeNetwork()) {
			if (common.getPlayerAccess(player).canEdit()) {
				common.setNetworkName(newName);
				common.setAccessType(access);
				common.setCustomColour(colour);
				common.markDirty();
			} else {
				return new PacketFluxError(source.getPos(), FluxError.EDIT_NETWORK);
			}
		}
		return null;
	}

	//// CREATE NETWORK \\\\

	public static NBTTagCompound createNetworkCreationPacket(String networkName, CustomColour networkColour, AccessType accessType) {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setString("networkName", networkName);
		tag.setInteger("colourRGB", networkColour.getRGB());
		tag.setInteger("accessType", accessType.ordinal());
		return tag;
	}

	public static IMessage doNetworkCreationPacket(TileFlux source, EntityPlayer player, NBTTagCompound packetTag) {
		String newName = packetTag.getString("networkName");
		CustomColour colour = new CustomColour(packetTag.getInteger("colourRGB"));
		AccessType access = AccessType.values()[packetTag.getInteger("accessType")];
		if (FluxNetworks.getServerCache().hasSpaceForNetwork(player)) {
			IFluxNetwork network = FluxNetworks.getServerCache().createNetwork(player, newName, colour, access);
		}
		return new PacketFluxError(source.getPos(), FluxError.NOT_OWNER);
	}

	//// DELETE NETWORK \\\\

	public static NBTTagCompound createNetworkDeletePacket(int networkID) {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setInteger("networkID", networkID);
		return tag;
	}

	public static IMessage doNetworkDeletePacket(TileFlux source, EntityPlayer player, NBTTagCompound packetTag) {
		int networkID = packetTag.getInteger("networkID");
		IFluxNetwork toDelete = FluxNetworks.getServerCache().getNetwork(networkID);
		if (!toDelete.isFakeNetwork() && toDelete instanceof IFluxNetwork) {
			if (toDelete.getPlayerAccess(player).canDelete()) {
				FluxNetworks.getServerCache().onPlayerRemoveNetwork(FluxHelper.getOwnerUUID(player), toDelete);
			} else {
				return new PacketFluxError(source.getPos(), FluxError.NOT_OWNER);
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
			source.priority.setObject(priority);
		} else {
			return new PacketFluxError(source.getPos(), FluxError.ACCESS_DENIED);
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
			source.limit.setObject(priority);
		} else {
			return new PacketFluxError(source.getPos(), FluxError.ACCESS_DENIED);
		}
		return null;
	}

	//// ADD PLAYER \\\\

	public static NBTTagCompound createAddPlayerPacket(int networkID, String playerName, PlayerAccess playerAccess) {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setInteger("networkID", networkID);
		tag.setString("playerName", playerName);
		tag.setInteger("playerAccess", playerAccess.ordinal());
		return tag;
	}

	public static IMessage doAddPlayerPacket(TileFlux source, EntityPlayer player, NBTTagCompound packetTag) {
		int networkID = packetTag.getInteger("networkID");
		String playerName = packetTag.getString("playerName");
		PlayerAccess access = PlayerAccess.values()[packetTag.getInteger("playerAccess")];

		GameProfile profile = SonarHelper.getGameProfileForUsername(playerName);
		if (profile == null || profile.getId() == null) {
			return new PacketFluxError(source.getPos(), FluxError.INVALID_USER);
		}
		UUID newPlayer = profile.getId();
		IFluxCommon common = FluxNetworks.getServerCache().getNetwork(networkID);
		if (!common.isFakeNetwork() && common instanceof IFluxNetwork) {
			if (((IFluxNetwork) common).getPlayerAccess(player).canEdit()) {
				IFluxNetwork network = (IFluxNetwork) common;
				network.addPlayerAccess(newPlayer, access);
				network.markDirty();
			} else {
				return new PacketFluxError(source.getPos(), FluxError.EDIT_NETWORK);
			}
		}
		return null;
	}

	//// REMOVE PLAYER \\\\

	public static NBTTagCompound createRemovePlayerPacket(int networkID, UUID playerRemoved, PlayerAccess playerAccess) {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setInteger("networkID", networkID);
		tag.setUniqueId("playerRemoved", playerRemoved);
		tag.setInteger("playerAccess", playerAccess.ordinal());
		return tag;
	}

	public static IMessage doRemovePlayerPacket(TileFlux source, EntityPlayer player, NBTTagCompound packetTag) {
		int networkID = packetTag.getInteger("networkID");
		UUID playerRemoved = packetTag.getUniqueId("playerRemoved");
		PlayerAccess access = PlayerAccess.values()[packetTag.getInteger("playerAccess")];
		IFluxCommon common = FluxNetworks.getServerCache().getNetwork(networkID);
		if (!common.isFakeNetwork() && common instanceof IFluxNetwork) {
			if (((IFluxNetwork) common).getPlayerAccess(player).canEdit()) {
				IFluxNetwork network = (IFluxNetwork) common;
				network.removePlayerAccess(playerRemoved, access);
				network.markDirty();
			} else {
				return new PacketFluxError(source.getPos(), FluxError.EDIT_NETWORK);
			}
		}
		return null;
	}

	//// CHANGE PLAYER \\\\

	public static NBTTagCompound createChangePlayerPacket(int networkID, UUID playerChanged, PlayerAccess playerAccess) {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setInteger("networkID", networkID);
		tag.setUniqueId("playerChanged", playerChanged);
		tag.setInteger("playerAccess", playerAccess.ordinal());
		return tag;
	}

	public static IMessage doChangePlayerPacket(TileFlux source, EntityPlayer player, NBTTagCompound packetTag) {
		int networkID = packetTag.getInteger("networkID");
		UUID playerChanged = packetTag.getUniqueId("playerChanged");
		if (playerChanged != null) {
			PlayerAccess access = PlayerAccess.values()[packetTag.getInteger("playerAccess")];
			access = SonarHelper.incrementEnum(access, PlayerAccess.values());
			while(access.canDelete()){access=SonarHelper.incrementEnum(access, PlayerAccess.values());}
			
			IFluxCommon common = FluxNetworks.getServerCache().getNetwork(networkID);
			if (!common.isFakeNetwork() && common instanceof IFluxNetwork) {
				if (((IFluxNetwork) common).getPlayerAccess(player).canEdit()) {
					if (!FluxHelper.getOwnerUUID(player).equals(playerChanged)) {
						IFluxNetwork network = (IFluxNetwork) common;
						network.addPlayerAccess(playerChanged, access);
						network.markDirty();
					} else {
						// FluxNetworks.network.sendTo(new PacketFluxError(flux.getPos(), FluxError.NOT_OWNER), (EntityPlayerMP) player);
					}
				} else {
					return new PacketFluxError(source.getPos(), FluxError.EDIT_NETWORK);
				}
			}
		}
		return new PacketFluxError(source.getPos(), FluxError.INVALID_USER);

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
		if (networkID == network.getNetworkID() && network.getPlayerAccess(player).canConnect() && source.playerUUID.getUUID().equals(FluxHelper.getOwnerUUID(player))) {
			network.removeConnection(source, RemovalType.REMOVE);
		}
		return null;

	}

	//// DISCONNECT \\\\

	public static NBTTagCompound createStateChangePacket(GuiTypeMessage guiType) {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setInteger("guiType", guiType.ordinal());
		return tag;
	}

	public static IMessage doStateChangePacket(TileFlux source, EntityPlayer player, NBTTagCompound packetTag) {
		GuiTypeMessage state = GuiTypeMessage.values()[packetTag.getInteger("guiType")];
		Container container = player.openContainer;
		if (container != null && container instanceof ContainerFlux) {
			((ContainerFlux) container).switchState(state);
		}
		return null;

	}
}
