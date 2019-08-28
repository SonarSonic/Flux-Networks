package fluxnetworks.common.connection;

import fluxnetworks.FluxNetworks;
import fluxnetworks.api.network.IFluxNetwork;
import fluxnetworks.common.core.SyncType;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nonnull;
import java.util.*;

/**
 * Save network data to local.
 */
public class FluxNetworkData extends WorldSavedData {

    private static final String NETWORK_DATA = "FluxNetworksXData";

    private static FluxNetworkData data;

    public static String NETWORKS = "networks";
    public static String UID = "uid";

    public static String NETWORK_ID = "networkID";
    public static String OWNER_UUID = "ownerUUID";
    public static String NETWORK_NAME = "networkName";
    public static String SECURITY_TYPE = "securityType";
    public static String NETWORK_PASSWORD = "networkPassword";
    public static String NETWORK_COLOR = "networkColor";
    public static String ENERGY_TYPE = "energyType";
    public static String PLAYER_LIST = "playerList";

    public Map<Integer, IFluxNetwork> networks = new HashMap<>();

    public int uid = 1;

    public FluxNetworkData(String name) {
        super(name);
    }

    public FluxNetworkData() {
        this(NETWORK_DATA);
    }

    public static void clear() {
        if(data != null) {
            data = null;
            FluxNetworks.logger.info("FluxNetworkData has been unloaded");
        }
    }

    @Override
    public boolean isDirty() {
        return true;
    }

    public static FluxNetworkData get() {
        if(data == null) {
            World world = DimensionManager.getWorld(0);
            WorldSavedData savedData = world.loadData(FluxNetworkData.class, FluxNetworkData.NETWORK_DATA);
            if (savedData == null) {
                FluxNetworks.logger.info("No FluxNetworkData found");
                FluxNetworkData newData = new FluxNetworkData(NETWORK_DATA);
                world.setData(NETWORK_DATA, newData);
                data = newData;
            } else {
                data = (FluxNetworkData) savedData;
                FluxNetworks.logger.info("FluxNetworkData has been successfully loaded");
            }
        }
        /*World world = DimensionManager.getWorld(0);
        MapStorage mapStorage = world.getMapStorage();
        FluxNetworkData data = (FluxNetworkData) mapStorage.getOrLoadData(FluxNetworkData.class, NETWORK_DATA);

        if(data == null) {
            data = new FluxNetworkData(NETWORK_DATA);
            mapStorage.setData(NETWORK_DATA, data);
            data.markDirty();
            FluxNetworks.logger.info("A new data has created.");
        }
        FluxNetworks.logger.info("World data loaded");*/
        return data;
    }

    public void addNetwork(IFluxNetwork network) {
        networks.putIfAbsent(network.getNetworkID(), network);
    }

    public void removeNetwork(IFluxNetwork network) {
        network.onRemoved();
        networks.remove(network.getNetworkID());
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        uid = nbt.getInteger(UID);
        if(nbt.hasKey(NETWORKS)) {
            NBTTagList list = nbt.getTagList(NETWORKS, Constants.NBT.TAG_COMPOUND);
            for(int i = 0; i < list.tagCount(); i++) {
                NBTTagCompound tag = list.getCompoundTagAt(i);
                FluxNetworkServer network = new FluxNetworkServer();
                network.readNetworkNBT(tag, SyncType.ALL);
                addNetwork(network);
            }
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound.setInteger(UID, uid);
        NBTTagList list = new NBTTagList();
        for(IFluxNetwork network : FluxNetworkCache.instance.getAllNetworks()) {
            NBTTagCompound tag = new NBTTagCompound();
            network.writeNetworkNBT(tag, SyncType.ALL);
            list.appendTag(tag);
        }
        compound.setTag(NETWORKS, list);
        return compound;
    }

    public static void readPlayers(IFluxNetwork network, @Nonnull NBTTagCompound nbt) {
        if(!nbt.hasKey(PLAYER_LIST)) {
            return;
        }
        List<NetworkMember> a = new ArrayList<>();
        NBTTagList list = nbt.getTagList(PLAYER_LIST, Constants.NBT.TAG_COMPOUND);
        for(int i = 0; i < list.tagCount(); i++) {
            NBTTagCompound c = list.getCompoundTagAt(i);
            a.add(new NetworkMember(c));
        }
        network.setSetting(NetworkSettings.NETWORK_PLAYERS, a);
    }

    public static NBTTagCompound writePlayers(IFluxNetwork network, @Nonnull NBTTagCompound nbt) {
        List<NetworkMember> a = network.getSetting(NetworkSettings.NETWORK_PLAYERS);
        if(!a.isEmpty()) {
            NBTTagList list = new NBTTagList();
            a.forEach(s -> list.appendTag(s.writeNetworkNBT(new NBTTagCompound())));
            nbt.setTag(PLAYER_LIST, list);
        }
        return nbt;
    }

}
