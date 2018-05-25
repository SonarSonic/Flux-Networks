package sonar.flux.network;

import net.minecraft.entity.player.EntityPlayer;
import sonar.core.api.energy.EnergyType;
import sonar.core.helpers.FunctionHelper;
import sonar.core.listener.ISonarListenable;
import sonar.core.listener.ListenableList;
import sonar.core.listener.PlayerListener;
import sonar.core.utils.CustomColour;
import sonar.flux.FluxConfig;
import sonar.flux.FluxEvents;
import sonar.flux.FluxNetworks;
import sonar.flux.api.AccessType;
import sonar.flux.api.FluxListener;
import sonar.flux.api.network.FluxPlayer;
import sonar.flux.api.network.IFluxNetwork;
import sonar.flux.api.network.IFluxNetworkCache;
import sonar.flux.api.network.PlayerAccess;
import sonar.flux.connection.BasicFluxNetwork;
import sonar.flux.connection.EmptyFluxNetwork;
import sonar.flux.connection.FluxHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

/** all the flux networks are created/stored/deleted here, an instance is found via the FluxAPI */
public class FluxNetworkCache implements IFluxNetworkCache, ISonarListenable<PlayerListener> {
	
	public ListenableList<PlayerListener> listeners = new ListenableList<>(this, FluxListener.values().length);
	public ConcurrentHashMap<UUID, List<IFluxNetwork>> networks = new ConcurrentHashMap<>();

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
		for (Entry<UUID, List<IFluxNetwork>> entry : networks.entrySet()) {
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

	public List<IFluxNetwork> getAllNetworks() {
		List<IFluxNetwork> available = new ArrayList<>();
		networks.values().forEach(available::addAll);
		return available;
	}

	public List<IFluxNetwork> getAllowedNetworks(EntityPlayer player, boolean admin) {
		List<IFluxNetwork> available = new ArrayList<>();
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
			onNetworksChanged();
		}
	}

	public void removeNetwork(IFluxNetwork common) {
		if (common.getOwnerUUID() != null && networks.get(common.getOwnerUUID()) != null) {
			common.onRemoved();
			networks.get(common.getOwnerUUID()).remove(common);
			onNetworksChanged();
		}
	}

	public boolean hasSpaceForNetwork(EntityPlayer player) {
		if(FluxConfig.maximum_per_player==-1){
			return true;
		}
		UUID ownerUUID = FluxHelper.getOwnerUUID(player);
		List<IFluxNetwork> created = networks.getOrDefault(ownerUUID, new ArrayList<>());
		return created.size() < FluxConfig.maximum_per_player;
	}

	public IFluxNetwork createNetwork(EntityPlayer player, String name, CustomColour colour, AccessType access, boolean disableConvert, EnergyType defaultEnergy) {
		UUID playerUUID = EntityPlayer.getUUID(player.getGameProfile());
		networks.putIfAbsent(playerUUID, new ArrayList<>());
		for (IFluxNetwork network : networks.get(playerUUID)) {
			if (network.getNetworkName().equals(name)) {
				return network;
			}
		}
		int iD = createNewUniqueID();

		FluxPlayer owner = FluxPlayer.createFluxPlayer(player, PlayerAccess.OWNER);
		BasicFluxNetwork network = new BasicFluxNetwork(iD, owner.getOnlineUUID(), owner.getCachedName(), name, colour, access, disableConvert, defaultEnergy);
		network.getPlayers().add(owner);

		addNetwork(network);
		FluxEvents.logNewNetwork(network);
		return network;
	}

	public void onPlayerRemoveNetwork(UUID uuid, IFluxNetwork remove) {
		removeNetwork(remove);
		FluxEvents.logRemoveNetwork(remove);
	}
	
	public void updateNetworkListeners() {
		List<PlayerListener> players = listeners.getListeners(FluxListener.SYNC_NETWORK_LIST);
		players.forEach(listener -> {
			List<IFluxNetwork> toSend = FluxNetworkCache.instance().getAllowedNetworks(listener.player, FluxHelper.isPlayerAdmin(listener.player));
			FluxNetworks.network.sendTo(new PacketFluxNetworkList(toSend, false), listener.player);
		});
	}

	public void updateAdminListeners() {
		List<PlayerListener> players = listeners.getListeners(FluxListener.ADMIN);
		players.forEach(listener -> {
			List<IFluxNetwork> toSend = FluxNetworkCache.instance().getAllowedNetworks(listener.player, true);
			FluxNetworks.network.sendTo(new PacketFluxNetworkList(toSend, false), listener.player);
		});
	}
	
	public void onNetworksChanged(){
		updateNetworkListeners();
		ListenerHelper.onNetworkListChanged();
	}

	@Override
	public boolean isValid() {
		return true;
	}

	@Override
	public ListenableList<PlayerListener> getListenerList() {
		return listeners;
	}
}
