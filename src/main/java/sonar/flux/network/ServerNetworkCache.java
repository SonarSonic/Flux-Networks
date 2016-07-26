package sonar.flux.network;

import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import sonar.core.utils.CustomColour;
import sonar.flux.FluxNetworks;
import sonar.flux.api.IFluxCommon;
import sonar.flux.api.IFluxCommon.AccessType;
import sonar.flux.api.IFluxNetwork;
import sonar.flux.connection.BasicFluxNetwork;

public class ServerNetworkCache extends CommonNetworkCache<ServerNetworkCache, IFluxNetwork> {

	public ArrayList<NetworkViewer> adminViewers = new ArrayList();
	public ConcurrentHashMap<Integer, ArrayList<NetworkViewer>> singleViewers = new ConcurrentHashMap<Integer, ArrayList<NetworkViewer>>();
	//the networks which need updating and the types of viewer to update.
	public ConcurrentHashMap<Integer, ArrayList<ViewingType>> updatedViewers = new ConcurrentHashMap<Integer, ArrayList<ViewingType>>();

	public static class NetworkViewer {
		public boolean sentFirstPacket = false;
		public final EntityPlayer player;
		public final ViewingType type;

		public NetworkViewer(EntityPlayer player, ViewingType type) {
			this.player = player;
			this.type = type;
		}

		public void sentFirstPacket() {
			this.sentFirstPacket = true;
		}
	}

	public ArrayList<IFluxNetwork> getAllowedNetworks(String playerName, boolean admin) {
		ArrayList<IFluxNetwork> available = new ArrayList();
		for (Entry<Integer, IFluxNetwork> entry : allNetworks.entrySet()) {
			IFluxCommon common = (IFluxNetwork) entry.getValue();
			//if (common instanceof IFluxNetwork) {
			IFluxNetwork network = (IFluxNetwork) common;
			if (network.isPlayerAllowed(playerName)) {
				available.add(network);
			}
			/*
			 * } else { if (common.getOwnerName().equals(playerName)) { available.add(common); } }
			 */
		}
		return available;
	}

	public IFluxNetwork createNetwork(String playerName, String name, CustomColour colour, AccessType access) {
		networks.putIfAbsent(playerName, new ArrayList());
		for (IFluxNetwork network : (ArrayList<IFluxNetwork>) networks.get(playerName).clone()) {
			if (network.getNetworkName().equals(name)) {
				return network;
			}
		}
		int iD = createNewUniqueID();
		IFluxNetwork network = new BasicFluxNetwork(iD, playerName, name, colour, access);
		addNetwork(network);
		FluxNetworks.logger.info("[NEW NETWORK] '" + network.getNetworkName() + "' with ID '" + network.getNetworkID() + "' was created by " + playerName);
		return network;
	}
	/*
	public void reloadNetwork(int iD, String playerName, String name, CustomColour colour, AccessType type) {
		addNetwork(new BasicFluxNetwork(iD, playerName, name, colour, type));
	}
	*/
	public void deleteNetwork(String playerName, IFluxNetwork toDelete) {
		if (networks.get(playerName) != null) {
			removeNetwork(toDelete);
			FluxNetworks.logger.info("[DELETE NETWORK] '" + toDelete.getNetworkName() + "' with ID '" + toDelete.getNetworkID() + "' was deleted by " + playerName);
		}
	}

	public void addViewer(EntityPlayer player, ViewingType type, int networkID) {
		NetworkViewer viewer = new NetworkViewer(player, type);
		switch (type) {
		case ADMIN:
			adminViewers.add(viewer);
			break;
		case CLIENT:
		case ONE_NET:
		case CONNECTIONS:
			singleViewers.putIfAbsent(networkID, new ArrayList());
			singleViewers.get(networkID).add(viewer);
			break;
		}
		sendViewerPackets(viewer, networkID);
		//networks.get(player.getName()).clear();
	}

	public void removeViewer(EntityPlayer player) {
		for (NetworkViewer viewer : (ArrayList<NetworkViewer>) adminViewers.clone()) {
			if (viewer.player.equals(player)) {
				adminViewers.remove(viewer);
			}
		}
		for (Entry<Integer, ArrayList<NetworkViewer>> entry : singleViewers.entrySet()) {
			for (NetworkViewer viewer : (ArrayList<NetworkViewer>) entry.getValue().clone()) {
				if (viewer.player.equals(player)) {
					entry.getValue().remove(viewer);
				}
			}
		}
	}

	public void markNetworkDirty(int id) {
		ArrayList<NetworkViewer> viewers = singleViewers.get(id);
		if (viewers != null && !viewers.isEmpty()) {
			for (NetworkViewer viewer : viewers) {
				viewer.sentFirstPacket = false;
			}
		}
	}

	public ConcurrentHashMap<Integer, ArrayList<NetworkViewer>> getViewers() {
		return singleViewers;
	}

	public void sendAllViewerPackets() {
		for (NetworkViewer viewer : (ArrayList<NetworkViewer>) adminViewers.clone()) {
			sendViewerPackets(viewer, -1);
		}
		for (Entry<Integer, ArrayList<NetworkViewer>> entry : singleViewers.entrySet()) {
			for (NetworkViewer viewer : (ArrayList<NetworkViewer>) entry.getValue().clone()) {
				sendViewerPackets(viewer, entry.getKey());
			}
		}
	}

	public void sendViewerPackets(NetworkViewer viewer, int id) {
		if (viewer.player != null && !viewer.player.getEntityWorld().isRemote) {
			if (viewer.type.forceSync() || !viewer.sentFirstPacket || updatedViewers.contains(id)) {
				viewer.sentFirstPacket();
				switch (viewer.type) {
				case CLIENT:
				case ONE_NET:
					ArrayList<IFluxNetwork> toSend = getAllowedNetworks(viewer.player.getName(), true);
					FluxNetworks.network.sendTo(new PacketFluxNetworkList(toSend), (EntityPlayerMP) viewer.player);
					break;
				case CONNECTIONS:
					IFluxNetwork common = getNetwork(id);
					common.buildFluxConnections();
					FluxNetworks.network.sendTo(new PacketFluxConnectionsList(common.getClientFluxConnection(), id), (EntityPlayerMP) viewer.player);
					break;
				default:
					break;
				}
				updatedViewers.remove(viewer);
			}
		}
	}

	public void removeAllViewers() {
		singleViewers.clear();
		adminViewers.clear();
	}

}
