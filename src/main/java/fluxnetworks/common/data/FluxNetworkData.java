package fluxnetworks.common.data;

import fluxnetworks.FluxNetworks;
import fluxnetworks.api.EnergyType;
import fluxnetworks.api.SecurityType;
import fluxnetworks.api.network.IFluxNetwork;
import fluxnetworks.api.tileentity.ILiteConnector;
import fluxnetworks.common.connection.*;
import fluxnetworks.common.core.NBTType;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nonnull;
import java.io.File;
import java.util.*;

/**
 * Save network data to local.
 */
public class FluxNetworkData extends WorldSavedData {

    private static final String NETWORK_DATA = FluxNetworks.MODID + "data";

    private static FluxNetworkData data;

    public static String NETWORKS = "networks";
    public static String LOADED_CHUNKS = "loadedChunks";
    public static String UNIQUE_ID = "uniqueID";

    public static String NETWORK_ID = "networkID";
    public static String NETWORK_NAME = "networkName";
    public static String NETWORK_COLOR = "networkColor";
    public static String NETWORK_PASSWORD = "networkPassword";
    public static String SECURITY_TYPE = "networkSecurity";
    public static String ENERGY_TYPE = "networkEnergy";
    public static String OWNER_UUID = "ownerUUID";
    public static String WIRELESS_MODE = "wirelessMode";

    public static String PLAYER_LIST = "playerList";
    public static String NETWORK_FOLDERS = "folders";
    public static String UNLOADED_CONNECTIONS = "unloaded";

    public static String OLD_NETWORK_ID = "id";
    public static String OLD_NETWORK_NAME = "name";
    public static String OLD_NETWORK_COLOR = "colour";
    public static String OLD_NETWORK_ACCESS = "access";

    public Map<Integer, IFluxNetwork> networks = new HashMap<>();
    public Map<Integer, List<ChunkPos>> loadedChunks = new HashMap<>();

    public int uniqueID = 1;

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
            // Emm... tastes good
            File oldFile = new File(world.getSaveHandler().getWorldDirectory(), "data/sonar.flux.networks.configurations.dat");
            if(oldFile.exists()) {
                oldFile.renameTo(new File(oldFile.getParent(), FluxNetworkData.NETWORK_DATA + ".dat"));
                FluxNetworks.logger.info("Old FluxNetworkData found");
            }
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
        uniqueID = nbt.getInteger(UNIQUE_ID);
        if(nbt.hasKey(NETWORKS)) {
            NBTTagList list = nbt.getTagList(NETWORKS, Constants.NBT.TAG_COMPOUND);
            for(int i = 0; i < list.tagCount(); i++) {
                NBTTagCompound tag = list.getCompoundTagAt(i);
                FluxNetworkServer network = new FluxNetworkServer();
                if(tag.hasKey(OLD_NETWORK_ID)) {
                    readOldData(network, tag);
                } else {
                    network.readNetworkNBT(tag, NBTType.ALL);
                }
                addNetwork(network);
            }
        }
        readChunks(nbt);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound.setInteger(UNIQUE_ID, uniqueID);

        NBTTagList list = new NBTTagList();
        for(IFluxNetwork network : FluxNetworkCache.instance.getAllNetworks()) {
            NBTTagCompound tag = new NBTTagCompound();
            network.writeNetworkNBT(tag, NBTType.ALL);
            list.appendTag(tag);
        }
        compound.setTag(NETWORKS, list);

        NBTTagCompound tag = new NBTTagCompound();
        loadedChunks.forEach((dim, pos) -> writeChunks(dim, pos, tag));
        compound.setTag(LOADED_CHUNKS, tag);
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

    public static void readConnections(IFluxNetwork network, @Nonnull NBTTagCompound nbt) {
        if(!nbt.hasKey(UNLOADED_CONNECTIONS)) {
            return;
        }
        List<ILiteConnector> a = new ArrayList<>();
        NBTTagList list = nbt.getTagList(UNLOADED_CONNECTIONS, Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < list.tagCount(); i++) {
            a.add(new FluxLiteConnector(list.getCompoundTagAt(i)));
        }
        network.setSetting(NetworkSettings.UNLOADED_CONNECTORS, a);
    }

    public static NBTTagCompound writeConnections(IFluxNetwork network, @Nonnull NBTTagCompound nbt) {
        List<ILiteConnector> a = network.getSetting(NetworkSettings.UNLOADED_CONNECTORS);
        if(!a.isEmpty()) {
            NBTTagList list = new NBTTagList();
            a.forEach(s -> list.appendTag(s.writeNetworkData(new NBTTagCompound())));
            nbt.setTag(UNLOADED_CONNECTIONS, list);
        }
        return nbt;
    }

    private void readChunks(NBTTagCompound nbt) {
        if(!nbt.hasKey(LOADED_CHUNKS)) {
            return;
        }
        NBTTagCompound tags = nbt.getCompoundTag(LOADED_CHUNKS);
        for(String key : tags.getKeySet()) {
            NBTTagList list = tags.getTagList(key, Constants.NBT.TAG_COMPOUND);
            List<ChunkPos> pos = loadedChunks.computeIfAbsent(Integer.valueOf(key), l -> new ArrayList<>());
            for (int i = 0; i < list.tagCount(); i++) {
                NBTTagCompound tag = list.getCompoundTagAt(i);
                pos.add(new ChunkPos(tag.getInteger("x"), tag.getInteger("z")));
            }
        }
    }

    private NBTTagCompound writeChunks(int dim, List<ChunkPos> pos, NBTTagCompound nbt) {
        if(!pos.isEmpty()) {
            NBTTagList list = new NBTTagList();
            pos.forEach(p -> {
                NBTTagCompound t = new NBTTagCompound();
                t.setInteger("x", p.x);
                t.setInteger("z", p.z);
                list.appendTag(t);
            });
            nbt.setTag(String.valueOf(dim), list);
        }
        return nbt;
    }

    private static void readOldData(FluxNetworkBase network, NBTTagCompound nbt) {
        network.network_id.setValue(nbt.getInteger(FluxNetworkData.OLD_NETWORK_ID));
        network.network_name.setValue(nbt.getString(FluxNetworkData.OLD_NETWORK_NAME));
        NBTTagCompound color = nbt.getCompoundTag(FluxNetworkData.OLD_NETWORK_COLOR);
        network.network_color.setValue(color.getInteger("red") << 16 | color.getInteger("green") << 8 | color.getInteger("blue"));
        network.network_owner.setValue(nbt.getUniqueId(FluxNetworkData.OWNER_UUID));
        int c = nbt.getInteger(FluxNetworkData.OLD_NETWORK_ACCESS);
        network.network_security.setValue(c > 0 ? SecurityType.ENCRYPTED : SecurityType.PUBLIC);
        network.network_password.setValue(String.valueOf((int) (Math.random() * 1000000)));
        network.network_energy.setValue(EnergyType.RF);
        FluxNetworkData.readPlayers(network, nbt);
        FluxNetworkData.readConnections(network, nbt);
    }

}
