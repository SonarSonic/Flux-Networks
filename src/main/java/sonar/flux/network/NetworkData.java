package sonar.flux.network;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.util.Constants.NBT;
import sonar.core.helpers.NBTHelper;
import sonar.core.helpers.NBTHelper.SyncType;
import sonar.core.utils.CustomColour;
import sonar.flux.FluxEvents;
import sonar.flux.FluxNetworks;
import sonar.flux.api.network.IFluxCommon.AccessType;
import sonar.flux.api.network.IFluxNetwork;
import sonar.flux.connection.BasicFluxNetwork;

import java.util.UUID;

public class NetworkData extends WorldSavedData {

	public static final String tag = "sonar.flux.networks.configurations";
    public static String TAG_LIST = "networks";
    public static String UNIQUE_ID = "uniqueID";
    public static String NETWORK_ID = "id";
    public static String COLOUR = "colour";
    public static String OWNER_UUID = "ownerUUID";
    public static String CACHE_PLAYER = "cachePName";
    public static String NETWORK_NAME = "name";
    public static String ACCESS = "access";
    public static String PLAYER_LIST = "playerList";

	public NetworkData(String name) {
		super(name);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
        FluxNetworks.getServerCache().uniqueID = nbt.getInteger(UNIQUE_ID);
        if (nbt.hasKey(TAG_LIST)) {
            NBTTagList list = nbt.getTagList(TAG_LIST, NBT.TAG_COMPOUND);
			for (int i = 0; i < list.tagCount(); i++) {
                try {
				NBTTagCompound tag = list.getCompoundTagAt(i);
                    int networkID = tag.getInteger(NETWORK_ID);
                    String networkName = tag.getString(NETWORK_NAME);
                    UUID ownerUUID = tag.getUniqueId(OWNER_UUID);
                    String cachedPlayer = tag.getString(CACHE_PLAYER);
                    CustomColour colour = NBTHelper.instanceNBTSyncable(CustomColour.class, tag.getCompoundTag(COLOUR));
                    AccessType type = AccessType.valueOf(tag.getString(ACCESS));
                    BasicFluxNetwork network = new BasicFluxNetwork(networkID, ownerUUID, networkName, colour, type);
                    network.getPlayers().readData(tag.getCompoundTag(PLAYER_LIST), SyncType.SAVE);
                    FluxNetworks.getServerCache().addNetwork(network);
                    FluxEvents.logLoadedNetwork(network);
                } catch (Throwable t) {
                    FluxNetworks.logger.info("FAILED TO LOAD A NETWORK");
                    t.printStackTrace();
				}
			}
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        nbt.setInteger(UNIQUE_ID, FluxNetworks.getServerCache().uniqueID);
		NBTTagList list = new NBTTagList();
		for (IFluxNetwork network : FluxNetworks.getServerCache().getAllNetworks()) {
				NBTTagCompound tag = new NBTTagCompound();
            tag.setInteger(NETWORK_ID, network.getNetworkID());
            tag.setUniqueId(OWNER_UUID, network.getOwnerUUID());
            tag.setString(CACHE_PLAYER, network.getCachedPlayerName());
            tag.setString(NETWORK_NAME, network.getNetworkName());
            tag.setTag(COLOUR, network.getNetworkColour().writeData(new NBTTagCompound(), SyncType.SAVE));
            tag.setString(ACCESS, network.getAccessType().name());
            tag.setTag(PLAYER_LIST, network.getPlayers().writeData(new NBTTagCompound(), SyncType.SAVE));
				list.appendTag(tag);
		}
		if (!list.hasNoTags()) {
            nbt.setTag(TAG_LIST, list);
            FluxNetworks.logger.debug("ALL " + list.tagCount() + " Networks were saved successfully");
		}

		return nbt;
	}

	public boolean isDirty() {
        return true;
	}
}
