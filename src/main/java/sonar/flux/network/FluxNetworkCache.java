package sonar.flux.network;

import com.google.common.collect.Lists;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import sonar.core.api.energy.EnergyType;
import sonar.core.helpers.FunctionHelper;
import sonar.core.helpers.ListHelper;
import sonar.core.helpers.NBTHelper;
import sonar.core.listener.ISonarListenable;
import sonar.core.listener.ListenableList;
import sonar.core.listener.ListenerList;
import sonar.core.listener.PlayerListener;
import sonar.core.utils.CustomColour;
import sonar.core.utils.SimpleObservableList;
import sonar.flux.FluxConfig;
import sonar.flux.FluxNetworks;
import sonar.flux.api.AccessType;
import sonar.flux.api.network.FluxPlayer;
import sonar.flux.api.network.IFluxNetwork;
import sonar.flux.api.network.IFluxNetworkCache;
import sonar.flux.api.network.PlayerAccess;
import sonar.flux.api.tiles.IFlux;
import sonar.flux.connection.*;

import javax.annotation.Nullable;
import java.util.*;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.function.Predicate;

/** all the flux networks are created/stored/deleted here, an instance is found via the FluxAPI */
public class FluxNetworkCache implements IFluxNetworkCache, ISonarListenable<PlayerListener>, SimpleObservableList.IListWatcher<IFluxNetwork> {

	public static FluxNetworkCache instance() {
		return FluxNetworks.getServerCache();
	}

	public void clearNetworks() {
		FluxNetworkData.clear();
		stack_listeners.clear();
		disconnected_tiles.clear();
		disconnected_tiles_changed = false;
	}

	private int createNewUniqueID() {
		return FluxNetworkData.get().uniqueID++;
	}

	public Map<UUID, List<IFluxNetwork>> getNetworkMap(){
		return FluxNetworkData.get().networks;
	}

	/** returns the first network for which the predicate is true */
	public IFluxNetwork getNetwork(Predicate<IFluxNetwork> found) {
		for (Entry<UUID, List<IFluxNetwork>> entry : FluxNetworkData.get().networks.entrySet()) {
			Optional<IFluxNetwork> network = entry.getValue().stream().filter(found).findFirst();
			if(network.isPresent()){
				return network.get();
			}
		}
		return FluxNetworkInvalid.INVALID;
	}

	/** adds all networks for which the predicate is true to a new array list */
	public List<IFluxNetwork> getNetworks(Predicate<IFluxNetwork> found) {
		List<IFluxNetwork> list = new ArrayList<>();
		FluxNetworkData.get().networks.values().forEach(NETWORKS -> NETWORKS.stream().filter(found).forEach(list::add));
		return list;
	}

	/** iterates every network connected */
	public void forEachNetwork(Consumer<IFluxNetwork> action){
		FluxNetworkData.get().networks.values().forEach(l -> l.forEach(action));
	}

	/** returns a list of networks the player is allowed to connect to */
	public List<IFluxNetwork> getAllowedNetworks(EntityPlayer player, boolean admin) {
		return getNetworks(network -> admin || network.getPlayerAccess(player).canConnect());
	}

	/** gets a network with a specified unique id */
	public IFluxNetwork getNetwork(int iD) {
		return getNetwork(n -> !n.isFakeNetwork() && iD == n.getSetting(NetworkSettings.NETWORK_ID));
	}

	/** gets a list of all networks currently loaded */
	public List<IFluxNetwork> getAllNetworks() {
		List<IFluxNetwork> available = new ArrayList<>();
		FluxNetworkData.get().networks.values().forEach(available::addAll);
		return available;
	}

	/** creates a new observable list, adding this Network Cache as a viewer allowing the monitoring of network changes */
	public List<IFluxNetwork> instanceNetworkList(){
		SimpleObservableList<IFluxNetwork> list = new SimpleObservableList<>();
		list.addWatcher(this);
		return list;
	}


	/** checks the player hasn't reached their maximum network limit */
	public boolean hasSpaceForNetwork(EntityPlayer player) {
		if(FluxConfig.maximum_per_player == -1){
			return true;
		}
		UUID ownerUUID = FluxPlayer.getOnlineUUID(player);
		List<IFluxNetwork> created = FluxNetworkData.get().networks.getOrDefault(ownerUUID, new ArrayList<>());
		return created.size() < FluxConfig.maximum_per_player;
	}

	public IFluxNetwork createNetwork(EntityPlayer player, String name, CustomColour colour, AccessType access, boolean disableConvert, EnergyType defaultEnergy) {
		UUID playerUUID = EntityPlayer.getUUID(player.getGameProfile());
		FluxNetworkData.get().networks.computeIfAbsent(playerUUID, (UUID) -> instanceNetworkList());

		int iD = createNewUniqueID();

		FluxPlayer owner = FluxPlayer.createFluxPlayer(player, PlayerAccess.OWNER);
		FluxNetworkServer network = new FluxNetworkServer(iD, owner.getOnlineUUID(), owner.getCachedName(), name, colour, access, disableConvert, defaultEnergy);
		network.getSetting(NetworkSettings.NETWORK_PLAYERS).add(owner);

		FluxNetworkData.get().addNetwork(network);
		FluxNetworks.proxy.logNewNetwork(network);
		return network;
	}

	public void onPlayerRemoveNetwork(IFluxNetwork remove) {
		FluxNetworkData.get().removeNetwork(remove);
		FluxNetworks.proxy.logRemoveNetwork(remove);
	}

	public void onSettingsChanged(IFluxNetwork network) { //only called when saved settings are changed.
		List<PlayerListener> players = listeners.getListeners(FluxListener.SYNC_NETWORK_LIST);
		PacketNetworkUpdate packet = new PacketNetworkUpdate(Lists.newArrayList(network), NBTHelper.SyncType.SAVE, false);
		players.forEach(listener -> {if (network.getPlayerAccess(listener.player).canConnect())FluxNetworks.network.sendTo(packet, listener.player);});
	}

	@Override
	public void onElementAdded(@Nullable IFluxNetwork added) {
		List<PlayerListener> players = listeners.getListeners(FluxListener.SYNC_NETWORK_LIST);
		PacketNetworkUpdate packet = new PacketNetworkUpdate(Lists.newArrayList(added), NBTHelper.SyncType.SAVE, false);
		players.forEach(listener -> {if (added.getPlayerAccess(listener.player).canConnect())FluxNetworks.network.sendTo(packet, listener.player);});
	}

	@Override
	public void onElementRemoved(@Nullable IFluxNetwork remove) {
		List<PlayerListener> players = listeners.getListeners(FluxListener.SYNC_NETWORK_LIST);
		PacketNetworkDeleted packet = new PacketNetworkDeleted(remove);
		players.forEach(listener -> FluxNetworks.network.sendTo(packet, listener.player));
		updateNetworkListeners();
		updateAdminListeners();
	}

	@Override
	public void onListChanged() {
		updateNetworkListeners();
		updateAdminListeners();
	}

	//// LISTENERS \\\\

	private ListenableList<PlayerListener> listeners = new ListenableList<>(this, FluxListener.values().length);
	public Map<Integer, ListenerList<PlayerListener>> stack_listeners = new HashMap<>();

	public Map<UUID, List<IFlux>> disconnected_tiles = new HashMap<>();
	public boolean disconnected_tiles_changed = false;

	public void onStartServerTick(){}

	public void onEndServerTick(){
		if(disconnected_tiles_changed){
			disconnected_tiles_changed = false;
		}
	}

	@Override
	public ListenableList<PlayerListener> getListenerList() {
		return listeners;
	}

	public void updateNetworkListeners() {
		List<PlayerListener> players = listeners.getListeners(FluxListener.SYNC_NETWORK_LIST);
		stack_listeners.values().forEach(list -> ListHelper.addWithCheck(players, list.getListeners(FluxListener.SYNC_NETWORK_LIST)));
		players.forEach(listener -> {
			List<IFluxNetwork> toSend = FluxNetworkCache.instance().getAllowedNetworks(listener.player, FluxHelper.isPlayerAdmin(listener.player));
			FluxNetworks.network.sendTo(new PacketNetworkUpdate(toSend, NBTHelper.SyncType.SAVE, true), listener.player);
		});
	}

	public void updateAdminListeners() {
		List<PlayerListener> players = new ArrayList<>();
		stack_listeners.values().forEach(list -> ListHelper.addWithCheck(players, list.getListeners(FluxListener.ADMIN)));
		players.forEach(listener -> {
			List<IFluxNetwork> toSend = FluxNetworkCache.instance().getAllowedNetworks(listener.player, true);
			FluxNetworks.network.sendTo(new PacketNetworkUpdate(toSend, NBTHelper.SyncType.SAVE, true), listener.player);
		});
	}

	public void onTileConnected(IFlux flux){
		if(disconnected_tiles.get(flux.getConnectionOwner()) != null) {
			FluxNetworkCache.instance().disconnected_tiles.get(flux.getConnectionOwner()).remove(flux);
			FluxNetworkCache.instance().onDisconnectedTilesChanged();
		}
	}

	public void onTileDisconnected(IFlux flux){
		FluxNetworkCache.instance().disconnected_tiles.computeIfAbsent(flux.getConnectionOwner(), FunctionHelper.ARRAY);
		if(!FluxNetworkCache.instance().disconnected_tiles.get(flux.getConnectionOwner()).contains(flux)) {
			FluxNetworkCache.instance().disconnected_tiles.get(flux.getConnectionOwner()).add(flux);
			FluxNetworkCache.instance().onDisconnectedTilesChanged();
		}
	}

	public void onTileRemoved(IFlux flux){
		if(disconnected_tiles.get(flux.getConnectionOwner()) != null) {
			FluxNetworkCache.instance().disconnected_tiles.get(flux.getConnectionOwner()).remove(flux);
			FluxNetworkCache.instance().onDisconnectedTilesChanged();
		}
	}

	public void onDisconnectedTilesChanged(){
		disconnected_tiles_changed = true;
	}

	public ListenerList<PlayerListener> getOrCreateStackListeners(ItemStack stack){
		int id = getOrCreateUniqueID(stack);
		return stack_listeners.computeIfAbsent(id, I -> new ListenerList<>(FluxListener.values().length));
	}

	public int getOrCreateUniqueID(ItemStack stack){
		NBTTagCompound tag = stack.getOrCreateSubCompound("uuid");
		if(tag.hasKey("id")){
			return tag.getInteger("id");
		}else {
			int newID = FluxNetworkData.get().stack_unique_id++;
			tag.setInteger("id", newID);
			return newID;
		}
	}

	@Override
	public boolean isValid() {
		return true;
	}
}
