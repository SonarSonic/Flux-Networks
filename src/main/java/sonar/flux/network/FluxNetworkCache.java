package sonar.flux.network;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

import net.minecraft.entity.player.EntityPlayer;
import sonar.core.helpers.FunctionHelper;
import sonar.core.listener.ISonarListenable;
import sonar.core.listener.ListenableList;
import sonar.core.listener.ListenerTally;
import sonar.core.listener.PlayerListener;
import sonar.core.utils.CustomColour;
import sonar.flux.FluxConfig;
import sonar.flux.FluxEvents;
import sonar.flux.FluxNetworks;
import sonar.flux.api.AccessType;
import sonar.flux.api.FluxListener;
import sonar.flux.api.network.IFluxNetwork;
import sonar.flux.api.network.IFluxNetworkCache;
import sonar.flux.connection.BasicFluxNetwork;
import sonar.flux.connection.EmptyFluxNetwork;
import sonar.flux.connection.FluxHelper;

/** all the flux networks are created/stored/deleted here, an instance is found via the FluxAPI */
public class FluxNetworkCache implements IFluxNetworkCache, ISonarListenable<PlayerListener> {
	public ListenableList<PlayerListener> listeners = new ListenableList(this, FluxListener.values().length);
	public ConcurrentHashMap<UUID, ArrayList<IFluxNetwork>> networks = new ConcurrentHashMap<>();

	public int uniqueID = 1;

	public static FluxNetworkCache instance() {
		return FluxNetworks.getServerCache();
	}

	public void clearNetworks() {
		networks.clear();
		uniqueID = 1;
	}

	public int createNewUniqueID() {
		return uniqueID++;
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

	public IFluxNetwork getNetwork(int iD) {
		return forEachNetwork(n -> !n.isFakeNetwork() && iD == n.getNetworkID());
	}

	public ArrayList<IFluxNetwork> getAllNetworks() {
		ArrayList<IFluxNetwork> available = new ArrayList<>();
		networks.values().forEach(available::addAll);
		return available;
	}

	public ArrayList<IFluxNetwork> getAllowedNetworks(EntityPlayer player, boolean admin) {
		ArrayList<IFluxNetwork> available = new ArrayList<>();
		forEachNetwork(network -> {
			if (admin || network.getPlayerAccess(player).canConnect())
				available.add(network);
			return false;
		});

		return available;
	}

	public void addNetwork(IFluxNetwork network) {
		if (network.getOwnerUUID() != null) {
			networks.computeIfAbsent(network.getOwnerUUID(), FunctionHelper.ARRAY).add(network);
			updateNetworkListeners();
		}
	}

	public void removeNetwork(IFluxNetwork common) {
		if (common.getOwnerUUID() != null && networks.get(common.getOwnerUUID()) != null) {
			common.onRemoved();
			networks.get(common.getOwnerUUID()).remove(common);
			updateNetworkListeners();
		}
	}

	public boolean hasSpaceForNetwork(EntityPlayer player) {
		if(FluxConfig.maximum_per_player==-1){
			return true;
		}
		UUID ownerUUID = FluxHelper.getOwnerUUID(player);
		List<IFluxNetwork> created = networks.getOrDefault(ownerUUID, new ArrayList());
		return created.size() < FluxConfig.maximum_per_player;
	}

	public IFluxNetwork createNetwork(EntityPlayer player, String name, CustomColour colour, AccessType access) {
		UUID playerUUID = FluxHelper.getOwnerUUID(player);
		networks.putIfAbsent(playerUUID, new ArrayList<>());
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
			FluxEvents.logRemoveNetwork(remove);
		}
	}

	public void updateNetworkListeners() {
		List<PlayerListener> players = listeners.getListeners(FluxListener.SYNC_NETWORK);
		players.forEach(listener -> {
			ArrayList<IFluxNetwork> toSend = FluxNetworkCache.instance().getAllowedNetworks(listener.player, FluxHelper.isPlayerAdmin(listener.player));
			FluxNetworks.network.sendTo(new PacketFluxNetworkList(toSend, false), listener.player);
		});
	}

	public void updateAdminListeners() {
		List<PlayerListener> players = listeners.getListeners(FluxListener.ADMIN);
		players.forEach(listener -> {
			ArrayList<IFluxNetwork> toSend = FluxNetworkCache.instance().getAllowedNetworks(listener.player, true);
			FluxNetworks.network.sendTo(new PacketFluxNetworkList(toSend, false), listener.player);
		});
	}

	@Override
	public boolean isValid() {
		return true;
	}

	@Override
	public ListenableList<PlayerListener> getListenerList() {
		return listeners;
	}

	@Override
	public void onListenerAdded(ListenerTally<PlayerListener> tally) {}

	@Override
	public void onListenerRemoved(ListenerTally<PlayerListener> tally) {}

	@Override
	public void onSubListenableAdded(ISonarListenable<PlayerListener> listen) {
		// System.out.println("ADDED:" + listen);
	}

	@Override
	public void onSubListenableRemoved(ISonarListenable<PlayerListener> listen) {
		// System.out.println("REMOVED:" + listen);

	}
}
