package sonar.flux.network;

import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

import com.google.common.collect.Lists;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import sonar.core.helpers.FunctionHelper;
import sonar.core.utils.CustomColour;
import sonar.flux.FluxEvents;
import sonar.flux.FluxNetworks;
import sonar.flux.api.FluxListener;
import sonar.flux.api.network.IFluxNetwork;
import sonar.flux.api.network.IFluxNetworkCache;
import sonar.flux.api.network.IFluxCommon.AccessType;
import sonar.flux.connection.BasicFluxNetwork;
import sonar.flux.connection.EmptyFluxNetwork;
import sonar.flux.connection.FluxHelper;

/** all the flux networks are created/stored/deleted here, an instance is found via the FluxAPI */
public class FluxNetworkCache implements IFluxNetworkCache {

	public ConcurrentHashMap<UUID, ArrayList<IFluxNetwork>> networks = new ConcurrentHashMap<UUID, ArrayList<IFluxNetwork>>();

	public int uniqueID = 1;

	public void clearNetworks() {
		networks.clear();
	}

	public int createNewUniqueID() {
		int id = uniqueID++;
		return id;
	}

	/** goes through every network, if the predicate is true it will return the network, if false it will continue */
	public IFluxNetwork forEachNetwork(Predicate<IFluxNetwork> found) {
		for (Entry<UUID, ArrayList<IFluxNetwork>> entry : networks.entrySet()) {
			for (IFluxNetwork common : entry.getValue()) {
				if (found.test(common)) {
					return common;
				}
			}
		}
		return EmptyFluxNetwork.INSTANCE;
	}

	public ArrayList<IFluxNetwork> getAllNetworks() {
		ArrayList<IFluxNetwork> available = Lists.newArrayList();
		networks.values().forEach(l -> l.forEach(n -> available.add(n)));
		return available;
	}

	public void addNetwork(IFluxNetwork network) {
		if (network.getOwnerUUID() != null) {
			networks.computeIfAbsent(network.getOwnerUUID(), FunctionHelper.ARRAY).add(network);
		}
	}

	public void removeNetwork(IFluxNetwork common) {
		if (common.getOwnerUUID() != null && networks.get(common.getOwnerUUID()) != null) {
			common.onRemoved();
			networks.get(common.getOwnerUUID()).remove(common);
		}
	}

	public IFluxNetwork getNetwork(int iD) {
		return forEachNetwork(n -> !n.isFakeNetwork() && iD == n.getNetworkID());
	}

	public ArrayList<IFluxNetwork> getAllowedNetworks(EntityPlayer player, boolean admin) {
		ArrayList<IFluxNetwork> available = Lists.newArrayList();
		for (IFluxNetwork network : getAllNetworks()) {
			if (network.getPlayerAccess(player).canConnect()) {
				available.add(network);
			}
		}
		return available;
	}

	public IFluxNetwork createNetwork(EntityPlayer player, String name, CustomColour colour, AccessType access) {
		UUID playerUUID = FluxHelper.getOwnerUUID(player);
		networks.putIfAbsent(playerUUID, Lists.newArrayList());
		for (IFluxNetwork network : (ArrayList<IFluxNetwork>) networks.get(playerUUID).clone()) {
			if (network.getNetworkName().equals(name)) {
				return network;
			}
		}
		int iD = createNewUniqueID();
		BasicFluxNetwork network = new BasicFluxNetwork(iD, playerUUID, name, colour, access);
		network.cachedOwnerName.setObject(player.getDisplayNameString());
		addNetwork(network);
		FluxEvents.logNewNetwork(network);
		return network;
	}

	public void onPlayerRemoveNetwork(UUID uuid, IFluxNetwork remove) {
		if (networks.get(uuid) != null) {
			removeNetwork(remove);
			remove.onRemoved();
			FluxEvents.logNewNetwork(remove);
		}
	}
	
	public static FluxNetworkCache instance() {
		return FluxNetworks.getServerCache();
	}
}
