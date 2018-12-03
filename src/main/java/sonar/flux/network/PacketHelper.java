package sonar.flux.network;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import sonar.core.api.energy.EnergyType;
import sonar.core.api.utils.BlockCoords;
import sonar.core.helpers.SonarHelper;
import sonar.core.utils.CustomColour;
import sonar.flux.FluxNetworks;
import sonar.flux.api.AccessType;
import sonar.flux.api.AdditionType;
import sonar.flux.api.FluxError;
import sonar.flux.api.RemovalType;
import sonar.flux.api.network.FluxPlayer;
import sonar.flux.api.network.IFluxNetwork;
import sonar.flux.api.network.PlayerAccess;
import sonar.flux.client.gui.GuiTab;
import sonar.flux.common.tileentity.TileFlux;
import sonar.flux.connection.NetworkSettings;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.UUID;

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
				source.getNetwork().queueConnectionRemoval(source, RemovalType.REMOVE);
				network.queueConnectionAddition(source, AdditionType.ADD);
			} else {
				return new PacketFluxError(source.getPos(), FluxError.ACCESS_DENIED);
			}
		}
		return null;
	}

	//// EDIT NETWORK \\\\

	public static NBTTagCompound createNetworkEditPacket(int networkID, String networkName, CustomColour networkColour, AccessType accessType, boolean enableConvert, EnergyType defaultEnergy) {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setInteger(FluxNetworkData.NETWORK_ID, networkID);
		tag.setString(FluxNetworkData.NETWORK_NAME, networkName);
		tag.setInteger(FluxNetworkData.COLOUR, networkColour.getRGB());
		tag.setInteger(FluxNetworkData.ACCESS, accessType.ordinal());
		tag.setBoolean(FluxNetworkData.CONVERSION, enableConvert);
		EnergyType.writeToNBT(defaultEnergy, tag, FluxNetworkData.ENERGY_TYPE);
		return tag;
	}

	public static IMessage doNetworkEditPacket(TileFlux source, EntityPlayer player, NBTTagCompound packetTag) {
		int networkID = packetTag.getInteger(FluxNetworkData.NETWORK_ID);
		String newName = packetTag.getString(FluxNetworkData.NETWORK_NAME);
		CustomColour colour = new CustomColour(packetTag.getInteger(FluxNetworkData.COLOUR));
		AccessType access = AccessType.values()[packetTag.getInteger(FluxNetworkData.ACCESS)];
		boolean enableConvert = packetTag.getBoolean(FluxNetworkData.CONVERSION);
		EnergyType energyType = EnergyType.readFromNBT(packetTag, FluxNetworkData.ENERGY_TYPE);

		IFluxNetwork common = FluxNetworks.getServerCache().getNetwork(networkID);
		if (!common.isFakeNetwork()) {
			if (common.getPlayerAccess(player).canEdit()) {
				common.setSetting(NetworkSettings.NETWORK_NAME, newName);
				common.setSetting(NetworkSettings.NETWORK_ACCESS, access);
				common.setSetting(NetworkSettings.NETWORK_COLOUR, colour);
				common.setSetting(NetworkSettings.NETWORK_CONVERSION, enableConvert);
				common.setSetting(NetworkSettings.NETWORK_ENERGY_TYPE, energyType);
			} else {
				return new PacketFluxError(source.getPos(), FluxError.EDIT_NETWORK);
			}
		}
		return null;
	}

	//// CREATE NETWORK \\\\

	public static NBTTagCompound createNetworkCreationPacket(String networkName, CustomColour networkColour, AccessType accessType, boolean enableConvert, EnergyType defaultEnergy) {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setString(FluxNetworkData.NETWORK_NAME, networkName);
		tag.setInteger(FluxNetworkData.COLOUR, networkColour.getRGB());
		tag.setInteger(FluxNetworkData.ACCESS, accessType.ordinal());
		tag.setBoolean(FluxNetworkData.CONVERSION, enableConvert);
		EnergyType.writeToNBT(defaultEnergy, tag, FluxNetworkData.ENERGY_TYPE);
		return tag;
	}

	public static IMessage doNetworkCreationPacket(TileFlux source, EntityPlayer player, NBTTagCompound packetTag) {
		String newName = packetTag.getString(FluxNetworkData.NETWORK_NAME);
		CustomColour colour = new CustomColour(packetTag.getInteger(FluxNetworkData.COLOUR));
		AccessType access = AccessType.values()[packetTag.getInteger(FluxNetworkData.ACCESS)];
		boolean enableConvert = packetTag.getBoolean(FluxNetworkData.CONVERSION);
		EnergyType energyType = EnergyType.readFromNBT(packetTag, FluxNetworkData.ENERGY_TYPE);
		
		if (FluxNetworks.getServerCache().hasSpaceForNetwork(player)) {
			FluxNetworks.getServerCache().createNetwork(player, newName, colour, access, enableConvert, energyType);
		}
		return null;
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
		if (!toDelete.isFakeNetwork()) {
			if (toDelete.getPlayerAccess(player).canDelete()) {
				FluxNetworks.getServerCache().onPlayerRemoveNetwork(toDelete);
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
			source.priority.setValue(priority);
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
			source.limit.setValue(priority);
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
		IFluxNetwork network = FluxNetworks.getServerCache().getNetwork(networkID);
		if (!network.isFakeNetwork()) {
			if (network.getPlayerAccess(player).canEdit()) {
				network.addPlayerAccess(playerName, access);
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
		IFluxNetwork network = FluxNetworks.getServerCache().getNetwork(networkID);
		if (!network.isFakeNetwork()) {
			if (network.getPlayerAccess(player).canEdit()) {
				network.removePlayerAccess(playerRemoved, access);
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
			if (FluxPlayer.getOnlineUUID(player).equals(playerChanged)) {
				//don't allow editing of their own permissions.
				return null;
			}
			PlayerAccess access = PlayerAccess.values()[packetTag.getInteger("playerAccess")];
			access = SonarHelper.incrementEnum(access, PlayerAccess.values());
			while (access.canDelete()) {
				access = SonarHelper.incrementEnum(access, PlayerAccess.values());
			}

			IFluxNetwork network = FluxNetworks.getServerCache().getNetwork(networkID);
			if (!network.isFakeNetwork()) {
				if (network.getPlayerAccess(player).canEdit()) {
					Optional<FluxPlayer> settings = network.getValidFluxPlayer(playerChanged);
					if (settings.isPresent()) {
						settings.get().setAccess(access);
						network.getSyncSetting(NetworkSettings.NETWORK_PLAYERS).setDirty(true);
					} else {
						return new PacketFluxError(source.getPos(), FluxError.INVALID_USER);
					}

				} else {
					return new PacketFluxError(source.getPos(), FluxError.EDIT_NETWORK);
				}
			}
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

	public static NBTTagCompound createStateChangePacket(GuiTab oldTab, @Nullable GuiTab newTab) {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setInteger("oldTab", oldTab.ordinal());
		if(newTab != null) {
			tag.setInteger("newTab", newTab.ordinal());
		}
		return tag;
	}

	public static IMessage doStateChangePacket(TileFlux source, EntityPlayer player, NBTTagCompound packetTag) {
		GuiTab oldTab = GuiTab.values()[packetTag.getInteger("oldTab")];
		ListenerHelper.onPlayerCloseTab(source, player, oldTab);
		if(packetTag.hasKey("newTab")) {
			GuiTab newTab = GuiTab.values()[packetTag.getInteger("newTab")];
			ListenerHelper.onPlayerOpenTab(source, player, newTab);
		}
		return null;
	}

	//// RESET_CONNECTED_BLOCKS \\\\

	public static NBTTagCompound createResetConnectedBlocksPacket() {
		return new NBTTagCompound();
	}

	public static IMessage doResetConnectedBlocksPacket(TileFlux source, EntityPlayer player, NBTTagCompound packetTag) {
		IFluxNetwork network = source.getNetwork();
		network.debugConnectedBlocks();
		return null;
	}

	//// VALIDATE FLUX CONNECTIONS \\\\

	public static NBTTagCompound createValidateConnectionsPacket() {
		return new NBTTagCompound();
	}

	public static IMessage doValidateConnectionsPacket(TileFlux source, EntityPlayer player, NBTTagCompound packetTag) {
		IFluxNetwork network = source.getNetwork();
		network.debugValidateFluxConnections();
		return null;
	}
}
