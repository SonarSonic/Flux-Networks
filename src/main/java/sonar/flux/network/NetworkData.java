package sonar.flux.network;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.storage.WorldSavedData;
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
import java.util.ArrayList;
import java.util.List;

public class NetworkData extends WorldSavedData {

	public static final String IDENTIFIER = "sonar.flux.networks.configurations";
	public static String TAG_LIST = "networks";
	public static String UNIQUE_ID = "uniqueID";
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

	public NetworkData(String name) {
		super(name);
	}

	public NetworkData() {
		this(IDENTIFIER);
	}

	@Override
	public void readFromNBT(@Nonnull NBTTagCompound nbt) {
		FluxNetworkCache cache = FluxNetworks.getServerCache();
		cache.uniqueID = nbt.getInteger(UNIQUE_ID);
		if (nbt.hasKey(TAG_LIST)) {
			NBTTagList list = nbt.getTagList(TAG_LIST, NBT.TAG_COMPOUND);
			for (int i = 0; i < list.tagCount(); i++) {

				NBTTagCompound tag = list.getCompoundTagAt(i);
				FluxNetworkServer network = new FluxNetworkServer();
				network.readData(tag, SyncType.SAVE);

				readPlayers(network, tag);
				readFolders(network, tag);
				readConnections(network.getSyncSetting(NetworkSettings.UNLOADED_CONNECTIONS), NetworkData.UNLOADED_CONNECTIONS, network, tag);

				cache.addNetwork(network);
				FluxEvents.logLoadedNetwork(network);
			}
		}
	}

	@Nonnull
    @Override
	public NBTTagCompound writeToNBT(@Nonnull NBTTagCompound nbt) {
		FluxNetworkCache cache = FluxNetworks.getServerCache();
		nbt.setInteger(UNIQUE_ID, cache.uniqueID);
		if (cache.getAllNetworks().size() > 0) {
			NBTTagList list = new NBTTagList();
			for (IFluxNetwork network : FluxNetworks.getServerCache().getAllNetworks()) {

				NBTTagCompound tag = new NBTTagCompound();
				network.writeData(tag, SyncType.SAVE);

				writePlayers(network, tag);
				writeFolders(network, tag);
				writeConnections(network.getSyncSetting(NetworkSettings.UNLOADED_CONNECTIONS), NetworkData.UNLOADED_CONNECTIONS, network, tag);

				list.appendTag(tag);
			}
			nbt.setTag(TAG_LIST, list);
			FluxNetworks.logger.debug("ALL " + list.tagCount() + " Networks were saved successfully");
		}
		return nbt;
	}

	public static void readPlayers(IFluxNetwork network, @Nonnull NBTTagCompound tag) {
		if(tag.hasKey(PLAYER_LIST)) {
			List<FluxPlayer> players = new ArrayList<>();
			NBTTagList player_list = tag.getTagList(PLAYER_LIST, NBT.TAG_COMPOUND);
			for (int j = 0; j < player_list.tagCount(); j++) {
				NBTTagCompound c = player_list.getCompoundTagAt(j);
				players.add(new FluxPlayer(c));
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

	public boolean isDirty() {
		return true;
	}
}
