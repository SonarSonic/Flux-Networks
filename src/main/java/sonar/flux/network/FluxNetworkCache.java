package sonar.flux.network;

import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import sonar.core.utils.CustomColour;
import sonar.flux.FluxNetworks;
import sonar.flux.api.IFluxCommon.AccessType;
import sonar.flux.api.IFluxNetwork;
import sonar.flux.api.IFluxNetworkCache;
import sonar.flux.connection.BasicFluxNetwork;
import sonar.flux.connection.EmptyFluxNetwork;

/** all the flux networks are created/stored/deleted here, an instance is found via the FluxAPI */
public class FluxNetworkCache implements IFluxNetworkCache {

	public ArrayList<NetworkViewer> adminViewers = new ArrayList();
	public ConcurrentHashMap<Integer, ArrayList<NetworkViewer>> singleViewers = new ConcurrentHashMap<Integer, ArrayList<NetworkViewer>>();
	public ConcurrentHashMap<Integer, ArrayList<ViewingType>> updatedViewers = new ConcurrentHashMap<Integer, ArrayList<ViewingType>>();
	public ConcurrentHashMap<UUID, ArrayList<IFluxNetwork>> networks = new ConcurrentHashMap<UUID, ArrayList<IFluxNetwork>>();

	public enum ViewingType {
		ADMIN, NETWORK, CONNECTIONS;

		public boolean forceSync() {
			return this == NETWORK;
		}
	}

	public int uniqueID = 1;

	public void clearNetworks() {
		networks.clear();
		singleViewers.clear();
		adminViewers.clear();
		updatedViewers.clear();
	}

	public int createNewUniqueID() {
		int id = uniqueID++;
		return id;
	}

	public ArrayList<IFluxNetwork> getAllNetworks() {
		ArrayList<IFluxNetwork> available = new ArrayList();
		for (Entry<UUID, ArrayList<IFluxNetwork>> entry : networks.entrySet()) {
			available.addAll(entry.getValue());
		}
		return available;
	}

	public void addNetwork(IFluxNetwork common) {
		if (common.getOwnerUUID() != null) {
			networks.putIfAbsent(common.getOwnerUUID(), new ArrayList());
			networks.get(common.getOwnerUUID()).add(common);
		}
	}

	public void removeNetwork(IFluxNetwork common) {
		if (common.getOwnerUUID() != null) {
			networks.putIfAbsent(common.getOwnerUUID(), new ArrayList());
			networks.get(common.getOwnerUUID()).remove(common);
		}
	}

	public IFluxNetwork getNetwork(int iD) {
		for (Entry<UUID, ArrayList<IFluxNetwork>> entry : networks.entrySet()) {
			for (IFluxNetwork common : entry.getValue()) {
				if (!common.isFakeNetwork() && iD == common.getNetworkID()) {
					return common;
				}
			}
		}
		return EmptyFluxNetwork.INSTANCE;
	}

	public ArrayList<IFluxNetwork> getAllowedNetworks(EntityPlayer player, boolean admin) {
		ArrayList<IFluxNetwork> available = new ArrayList();
		for (IFluxNetwork network : getAllNetworks()) {
			if (network.getPlayerAccess(player).canConnect()) {
				available.add(network);
			}
		}
		return available;
	}

	public IFluxNetwork createNetwork(UUID playerUUID, String name, CustomColour colour, AccessType access) {
		networks.putIfAbsent(playerUUID, new ArrayList());
		for (IFluxNetwork network : (ArrayList<IFluxNetwork>) networks.get(playerUUID).clone()) {
			if (network.getNetworkName().equals(name)) {
				return network;
			}
		}
		int iD = createNewUniqueID();
		IFluxNetwork network = new BasicFluxNetwork(iD, playerUUID, name, colour, access);
		addNetwork(network);
		FluxNetworks.logger.info("[NEW NETWORK] '" + network.getNetworkName() + "' with ID '" + network.getNetworkID() + "' was created by " + network.getCachedPlayerName());

		return network;
	}

	public void deleteNetwork(UUID playerName, IFluxNetwork toDelete) {
		if (networks.get(playerName) != null) {
			removeNetwork(toDelete);
			FluxNetworks.logger.info("[DELETE NETWORK] '" + toDelete.getNetworkName() + "' with ID '" + toDelete.getNetworkID() + "' was deleted by " + toDelete.getCachedPlayerName());
		}
	}

	public void addViewer(EntityPlayer player, ViewingType type, int networkID) {
		NetworkViewer viewer = new NetworkViewer(player, type);
		switch (type) {
		case ADMIN:
			adminViewers.add(viewer);
			break;
		case NETWORK:
		case CONNECTIONS:
			singleViewers.putIfAbsent(networkID, new ArrayList());
			singleViewers.get(networkID).add(viewer);
			break;
		}
		sendViewerPackets(viewer, networkID);
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
		singleViewers.clear();
	}

	public void markNetworkDirty(int id) {
		ArrayList<NetworkViewer> viewers = singleViewers.get(id);
		if (viewers != null && !viewers.isEmpty()) {
			viewers.forEach(viewer -> viewer.sentFirstPacket = false);
		}
	}

	public ConcurrentHashMap<Integer, ArrayList<NetworkViewer>> getViewers() {
		return singleViewers;
	}

	public void sendAllViewerPackets() {
		for (Entry<Integer, ArrayList<NetworkViewer>> entry : singleViewers.entrySet()) {
			((ArrayList<NetworkViewer>) entry.getValue().clone()).forEach(viewer -> sendViewerPackets(viewer, entry.getKey()));
		}
	}

	public void sendViewerPackets(NetworkViewer viewer, int id) {
		if (viewer.player != null) {
			if (viewer.type.forceSync() || !viewer.sentFirstPacket){// || updatedViewers.contains(id)) {
				viewer.sentFirstPacket();
				switch (viewer.type) {
				case NETWORK:
					ArrayList<IFluxNetwork> toSend = getAllowedNetworks(viewer.player, true);
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
				//updatedViewers.remove(id);
			}
		}
	}
}
