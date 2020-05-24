package sonar.fluxnetworks.common.data;

import com.google.common.collect.Lists;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import sonar.fluxnetworks.FluxNetworks;
import sonar.fluxnetworks.api.network.*;
import sonar.fluxnetworks.api.tiles.IFluxConnector;
import sonar.fluxnetworks.api.utils.NBTType;
import sonar.fluxnetworks.common.capability.SuperAdminInstance;
import sonar.fluxnetworks.common.connection.FluxLiteConnector;
import sonar.fluxnetworks.common.connection.FluxNetworkCache;
import sonar.fluxnetworks.common.connection.FluxNetworkServer;
import sonar.fluxnetworks.common.handler.PacketHandler;
import sonar.fluxnetworks.common.network.NetworkUpdatePacket;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nonnull;
import java.util.*;

/**
 * Manage all flux network server and save network data to local.
 * Only on logic server side
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

    /*public static String OLD_NETWORK_ID = "id";
    public static String OLD_NETWORK_NAME = "name";
    public static String OLD_NETWORK_COLOR = "colour";
    public static String OLD_NETWORK_ACCESS = "access";*/

    public Map<Integer, IFluxNetwork> networks = new HashMap<>();
    public Map<Integer, List<ChunkPos>> loadedChunks = new HashMap<>(); // Forced Chunks

    public int uniqueID = 1;

    public FluxNetworkData() {
        super(NETWORK_DATA);
    }

    public static FluxNetworkData get() {
        if (data == null) {
            load();
        }
        return data;
    }

    private static void load() {
        ServerWorld saveWorld = ServerLifecycleHooks.getCurrentServer().getWorld(DimensionType.OVERWORLD);
        data = saveWorld.getSavedData().getOrCreate(FluxNetworkData::new, NETWORK_DATA);
        FluxNetworks.LOGGER.info("FluxNetworkData has been successfully loaded");
    }

    public static void release() {
        if (data != null) {
            data = null;
            FluxNetworks.LOGGER.info("FluxNetworkData has been unloaded");
        }
    }

    public void addNetwork(IFluxNetwork network) {
        networks.putIfAbsent(network.getNetworkID(), network);
        PacketHandler.INSTANCE.send(PacketDistributor.ALL.noArg(), new NetworkUpdatePacket(Lists.newArrayList(network), NBTType.NETWORK_GENERAL));
    }

    public void deleteNetwork(IFluxNetwork network) {
        PacketHandler.INSTANCE.send(PacketDistributor.ALL.noArg(), new NetworkUpdatePacket(Lists.newArrayList(network), NBTType.NETWORK_CLEAR));
        network.onDeleted();
        networks.remove(network.getNetworkID());
    }

    @Override
    public boolean isDirty() {
        return true;
    }

    @Override
    public void read(@Nonnull CompoundNBT nbt) {
        uniqueID = nbt.getInt(UNIQUE_ID);
        if(nbt.contains(NETWORKS)) {
            ListNBT list = nbt.getList(NETWORKS, Constants.NBT.TAG_COMPOUND);
            for(int i = 0; i < list.size(); i++) {
                CompoundNBT tag = list.getCompound(i);
                FluxNetworkServer network = new FluxNetworkServer();
                /*if(tag.contains(OLD_NETWORK_ID)) {
                    readOldData(network, tag);
                } else {*/
                network.readNetworkNBT(tag, NBTType.ALL_SAVE);
                //}
                addNetwork(network);
            }
        }
        readChunks(nbt);
        data = this;
    }

    @Nonnull
    @Override
    public CompoundNBT write(@Nonnull CompoundNBT compound) {
        compound.putInt(UNIQUE_ID, uniqueID);

        ListNBT list = new ListNBT();
        for(IFluxNetwork network : FluxNetworkCache.INSTANCE.getAllNetworks()) {
            CompoundNBT tag = new CompoundNBT();
            network.writeNetworkNBT(tag, NBTType.ALL_SAVE);
            list.add(tag);
        }
        compound.put(NETWORKS, list);

        CompoundNBT tag = new CompoundNBT();
        loadedChunks.forEach((dim, pos) -> writeChunks(dim, pos, tag));
        compound.put(LOADED_CHUNKS, tag);
        return compound;
    }

    public static void readPlayers(IFluxNetwork network, @Nonnull CompoundNBT nbt) {
        if(!nbt.contains(PLAYER_LIST)) {
            return;
        }
        List<NetworkMember> a = new ArrayList<>();
        ListNBT list = nbt.getList(PLAYER_LIST, Constants.NBT.TAG_COMPOUND);
        for(int i = 0; i < list.size(); i++) {
            CompoundNBT c = list.getCompound(i);
            a.add(new NetworkMember(c));
        }
        network.setSetting(NetworkSettings.NETWORK_PLAYERS, a);
    }

    public static void writePlayers(IFluxNetwork network, @Nonnull CompoundNBT nbt) {
        List<NetworkMember> a = network.getSetting(NetworkSettings.NETWORK_PLAYERS);
        if(!a.isEmpty()) {
            ListNBT list = new ListNBT();
            a.forEach(s -> list.add(s.writeNetworkNBT(new CompoundNBT())));
            nbt.put(PLAYER_LIST, list);
        }
    }

    public static void writeAllPlayers(IFluxNetwork network, @Nonnull CompoundNBT nbt) {
        List<NetworkMember> a = network.getSetting(NetworkSettings.NETWORK_PLAYERS);
        ListNBT list = new ListNBT();
        if(!a.isEmpty()) {
            a.forEach(s -> list.add(s.writeNetworkNBT(new CompoundNBT())));
        }
        List<ServerPlayerEntity> players = ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers();
        if(!players.isEmpty()) {
            players.stream().filter(p -> a.stream().noneMatch(s -> s.getPlayerUUID().equals(p.getUniqueID())))
                    .forEach(s -> list.add(NetworkMember.createNetworkMember(s, getPermission(s)).writeNetworkNBT(new CompoundNBT())));
        }
        nbt.put(PLAYER_LIST, list);
    }

    private static EnumAccessType getPermission(@Nonnull PlayerEntity player) {
        return SuperAdminInstance.isPlayerSuperAdmin(player) ? EnumAccessType.SUPER_ADMIN : EnumAccessType.NONE;
    }

    public static void readConnections(IFluxNetwork network, @Nonnull CompoundNBT nbt) {
        if(!nbt.contains(UNLOADED_CONNECTIONS)) {
            return;
        }
        List<IFluxConnector> a = new ArrayList<>();
        ListNBT list = nbt.getList(UNLOADED_CONNECTIONS, Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < list.size(); i++) {
            a.add(new FluxLiteConnector(list.getCompound(i)));
        }
        network.getSetting(NetworkSettings.ALL_CONNECTORS).addAll(a);
    }

    public static void writeConnections(IFluxNetwork network, @Nonnull CompoundNBT nbt) {
        List<IFluxConnector> a = network.getSetting(NetworkSettings.ALL_CONNECTORS);
        if(!a.isEmpty()) {
            ListNBT list = new ListNBT();
            a.forEach(s -> {
                if(!s.isChunkLoaded()) {
                    list.add(s.writeCustomNBT(new CompoundNBT(), NBTType.DEFAULT));
                }
            });
            nbt.put(UNLOADED_CONNECTIONS, list);
        }
    }

    public static void readAllConnections(IFluxNetwork network, @Nonnull CompoundNBT nbt) {
        if(!nbt.contains(UNLOADED_CONNECTIONS)) {
            return;
        }
        List<IFluxConnector> a = new ArrayList<>();
        ListNBT list = nbt.getList(UNLOADED_CONNECTIONS, Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < list.size(); i++) {
            a.add(new FluxLiteConnector(list.getCompound(i)));
        }
        network.setSetting(NetworkSettings.ALL_CONNECTORS, a);
    }

    public static void writeAllConnections(IFluxNetwork network, @Nonnull CompoundNBT nbt) {
        List<IFluxConnector> a = network.getSetting(NetworkSettings.ALL_CONNECTORS);
        if(!a.isEmpty()) {
            ListNBT list = new ListNBT();
            a.forEach(s -> list.add(s.writeCustomNBT(new CompoundNBT(), NBTType.DEFAULT)));
            nbt.put(UNLOADED_CONNECTIONS, list);
        }
    }

    private void readChunks(CompoundNBT nbt) {
        if(!nbt.contains(LOADED_CHUNKS)) {
            return;
        }
        CompoundNBT tags = nbt.getCompound(LOADED_CHUNKS);
        for(String key : tags.keySet()) {
            ListNBT list = tags.getList(key, Constants.NBT.TAG_COMPOUND);
            List<ChunkPos> pos = loadedChunks.computeIfAbsent(Integer.valueOf(key), l -> new ArrayList<>());
            for (int i = 0; i < list.size(); i++) {
                CompoundNBT tag = list.getCompound(i);
                pos.add(new ChunkPos(tag.getInt("x"), tag.getInt("z")));
            }
        }
    }

    private void writeChunks(int dim, List<ChunkPos> pos, CompoundNBT nbt) {
        if(!pos.isEmpty()) {
            ListNBT list = new ListNBT();
            pos.forEach(p -> {
                CompoundNBT t = new CompoundNBT();
                t.putInt("x", p.x);
                t.putInt("z", p.z);
                list.add(t);
            });
            nbt.put(String.valueOf(dim), list);
        }
    }

    /*private static void readOldData(FluxNetworkBase network, CompoundNBT nbt) {
        network.network_id.setValue(nbt.getInt(FluxNetworkData.OLD_NETWORK_ID));
        network.network_name.setValue(nbt.getString(FluxNetworkData.OLD_NETWORK_NAME));
        CompoundNBT color = nbt.getCompound(FluxNetworkData.OLD_NETWORK_COLOR);
        network.network_color.setValue(color.getInt("red") << 16 | color.getInt("green") << 8 | color.getInt("blue"));
        network.network_owner.setValue(nbt.getUniqueId(FluxNetworkData.OWNER_UUID));
        int c = nbt.getInt(FluxNetworkData.OLD_NETWORK_ACCESS);
        network.network_security.setValue(c > 0 ? EnumSecurityType.ENCRYPTED : EnumSecurityType.PUBLIC);
        network.network_password.setValue(String.valueOf((int) (Math.random() * 1000000)));
        network.network_energy.setValue(EnergyType.FE);
        FluxNetworkData.readPlayers(network, nbt);
        FluxNetworkData.readConnections(network, nbt);
    }*/

}
