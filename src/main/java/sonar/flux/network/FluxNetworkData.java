package sonar.flux.network;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.util.Constants.NBT;
import sonar.core.helpers.ListHelper;
import sonar.core.helpers.NBTHelper.SyncType;
import sonar.core.sync.ISonarValue;
import sonar.flux.FluxEvents;
import sonar.flux.FluxNetworks;
import sonar.flux.api.ClientFlux;
import sonar.flux.api.NetworkFluxFolder;
import sonar.flux.api.network.FluxPlayer;
import sonar.flux.api.network.IFluxNetwork;
import sonar.flux.connection.FluxNetworkServer;
import sonar.flux.connection.NetworkSettings;

import javax.annotation.Nonnull;
import java.util.*;

public class FluxNetworkData extends WorldSavedData {

	private static FluxNetworkData data;

	public Map<UUID, List<IFluxNetwork>> networks = new HashMap<>();
	public int uniqueID = 1;
	public int stack_unique_id = 0;

	public static final String IDENTIFIER = "sonar.flux.networks.configurations";
	public static String TAG_LIST = "networks";
	public static String UNIQUE_ID = "uniqueID";
	public static String UNIQUE_STACK_ID = "stackID";
	public static String NETWORK_ID = "id";
	public static String COLOUR = "colour";
	public static String OWNER_UUID = "ownerUUID";
	public static String CACHE_PLAYER = "cachePName";
	public static String NETWORK_NAME = "name";
	public static String ACCESS = "access";
	public static String CONVERSION = "convert";
	public static String ENERGY_TYPE = "energy_type";
	public static String PLAYER_LIST = "playerList";
	public static String UNLOADED_CONNECTIONS = "unloaded";
	public static String FOLDERS = "network_folders";

	public FluxNetworkData(String name) {
		super(name);
	}

	public FluxNetworkData() {
		this(IDENTIFIER);
	}


	//// INSTANCE METHODS \\\\

	public static FluxNetworkData get() {
		if(data == null){
			World world = DimensionManager.getWorld(0);
			WorldSavedData savedData = world.loadData(FluxNetworkData.class, FluxNetworkData.IDENTIFIER);
			if(savedData == null){
				FluxNetworks.logger.info("No FluxNetworkData found");
				FluxNetworkData newData = new FluxNetworkData(IDENTIFIER);
				world.setData(IDENTIFIER, newData);
				data = newData;
			}else{
				data = (FluxNetworkData) savedData;
				FluxNetworks.logger.info("FluxNetworkData has been successfully loaded");
			}
		}
		return data;
	}

	public static void clear() {
		if (data != null) {
			data = null;
			FluxNetworks.logger.info("FluxNetworkData has been unloaded");
		}
	}

	public boolean isDirty() {
		return true;
	}


	//// LOAD / SAVE METHODS \\\\\

	public void addNetwork(IFluxNetwork network) {
		UUID owner = network.getSetting(NetworkSettings.NETWORK_OWNER);
		if (owner != null) {
			networks.computeIfAbsent(owner, (UUID) -> FluxNetworkCache.instance().instanceNetworkList()).add(network);
		}
	}

	public void removeNetwork(IFluxNetwork common) {
		UUID owner = common.getSetting(NetworkSettings.NETWORK_OWNER);
		common.onRemoved();
		if (owner != null && networks.get(owner) != null) {
			networks.get(owner).remove(common);
		}
	}

	@Override
	public void readFromNBT(@Nonnull NBTTagCompound nbt) {
		uniqueID = nbt.getInteger(UNIQUE_ID);
		stack_unique_id = nbt.getInteger(UNIQUE_STACK_ID);
		if (nbt.hasKey(TAG_LIST)) {
			NBTTagList list = nbt.getTagList(TAG_LIST, NBT.TAG_COMPOUND);
			for (int i = 0; i < list.tagCount(); i++) {

				NBTTagCompound tag = list.getCompoundTagAt(i);
				FluxNetworkServer network = new FluxNetworkServer();
				network.readData(tag, SyncType.SAVE);

				readPlayers(network, tag);
				readFolders(network, tag);
				readConnections(network.getSyncSetting(NetworkSettings.UNLOADED_CONNECTIONS), FluxNetworkData.UNLOADED_CONNECTIONS, network, tag);

				addNetwork(network);
				FluxEvents.logLoadedNetwork(network);
			}
		}
	}

	@Nonnull
    @Override
	public NBTTagCompound writeToNBT(@Nonnull NBTTagCompound nbt) {
		nbt.setInteger(UNIQUE_ID, uniqueID);
		nbt.setInteger(UNIQUE_STACK_ID, stack_unique_id);
		if (networks.size() > 0) {
			NBTTagList list = new NBTTagList();
			for (IFluxNetwork network : FluxNetworks.getServerCache().getAllNetworks()) {

				NBTTagCompound tag = new NBTTagCompound();
				network.writeData(tag, SyncType.SAVE);

				writePlayers(network, tag);
				writeFolders(network, tag);
				writeConnections(network.getSyncSetting(NetworkSettings.UNLOADED_CONNECTIONS), FluxNetworkData.UNLOADED_CONNECTIONS, network, tag);

				list.appendTag(tag);
			}
			nbt.setTag(TAG_LIST, list);
			FluxNetworks.logger.debug("ALL " + list.tagCount() + " Networks were saved successfully");
		}
		return nbt;
	}

	//// LOAD / SAVE HELPERS \\\\

	public static void readPlayers(IFluxNetwork network, @Nonnull NBTTagCompound tag) {
		if(tag.hasKey(PLAYER_LIST)) {
			List<FluxPlayer> players = new ArrayList<>();
			NBTTagList player_list = tag.getTagList(PLAYER_LIST, NBT.TAG_COMPOUND);
			for (int j = 0; j < player_list.tagCount(); j++) {
				NBTTagCompound c = player_list.getCompoundTagAt(j);
				ListHelper.addWithCheck(players, new FluxPlayer(c));
			}
			network.setSettingInternal(NetworkSettings.NETWORK_PLAYERS, players);
		}
	}

	public static NBTTagCompound writePlayers(IFluxNetwork network, @Nonnull NBTTagCompound tag) {
		List<FluxPlayer> players = network.getSetting(NetworkSettings.NETWORK_PLAYERS);
		if(!players.isEmpty()) {
			NBTTagList player_list = new NBTTagList();
			players.forEach(player -> player_list.appendTag(player.writeData(new NBTTagCompound(), SyncType.SAVE)));
			tag.setTag(PLAYER_LIST, player_list);
		}
		return tag;
	}

	public static void readFolders(IFluxNetwork network, @Nonnull NBTTagCompound tag) {
		if(tag.hasKey(FOLDERS)) {
			NBTTagList folder_tag_list = tag.getTagList(FOLDERS, NBT.TAG_COMPOUND);
			List<NetworkFluxFolder> folders = new ArrayList<>();
			for (int j = 0; j < folder_tag_list.tagCount(); j++) {
				folders.add(new NetworkFluxFolder(folder_tag_list.getCompoundTagAt(j)));
			}
			network.getSyncSetting(NetworkSettings.NETWORK_FOLDERS).setValueInternal(folders);
		}
	}

	public static NBTTagCompound writeFolders(IFluxNetwork network, @Nonnull NBTTagCompound tag) {
		List<NetworkFluxFolder> folders = network.getSetting(NetworkSettings.NETWORK_FOLDERS);
		if(!folders.isEmpty()){
			NBTTagList folders_tag_list = new NBTTagList();
			folders.forEach(folder -> folders_tag_list.appendTag(folder.writeData(new NBTTagCompound(), SyncType.SAVE)));
			tag.setTag(FOLDERS, folders_tag_list);
		}
		return tag;
	}

	public static void readConnections(ISonarValue<List<ClientFlux>> value, String key, IFluxNetwork network, @Nonnull NBTTagCompound tag) {
		if(tag.hasKey(key)) {
			List<ClientFlux> loaded = new ArrayList<>();
			NBTTagList connections = tag.getTagList(key, NBT.TAG_COMPOUND);
			for (int j = 0; j < connections.tagCount(); j++) {
				NBTTagCompound c = connections.getCompoundTagAt(j);
				ListHelper.addWithCheck(loaded, new ClientFlux(c));
			}
			value.setValueInternal(loaded);
		}
	}
	public static NBTTagCompound writeConnections(ISonarValue<List<ClientFlux>> value, String key, IFluxNetwork network, @Nonnull NBTTagCompound tag) {
		List<ClientFlux> loaded = value.getValue();
		if(!loaded.isEmpty()) {
			NBTTagList connections = new NBTTagList();
			loaded.forEach(flux -> connections.appendTag(flux.writeData(new NBTTagCompound(), SyncType.SAVE)));
			tag.setTag(key, connections);
		}
		return tag;
	}
}
