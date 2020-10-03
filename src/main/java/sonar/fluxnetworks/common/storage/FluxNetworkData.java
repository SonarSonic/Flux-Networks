package sonar.fluxnetworks.common.storage;

import com.google.common.collect.Lists;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import sonar.fluxnetworks.FluxConfig;
import sonar.fluxnetworks.FluxNetworks;
import sonar.fluxnetworks.api.device.IFluxDevice;
import sonar.fluxnetworks.api.misc.FluxConstants;
import sonar.fluxnetworks.api.misc.NBTType;
import sonar.fluxnetworks.api.network.*;
import sonar.fluxnetworks.common.capability.SuperAdmin;
import sonar.fluxnetworks.common.connection.FluxNetworkInvalid;
import sonar.fluxnetworks.common.connection.FluxNetworkServer;
import sonar.fluxnetworks.common.connection.SimpleFluxDevice;
import sonar.fluxnetworks.common.handler.NetworkHandler;
import sonar.fluxnetworks.common.handler.PacketHandler;
import sonar.fluxnetworks.common.network.SNetworkUpdateMessage;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
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

    private final Map<Integer, IFluxNetwork> networks = new HashMap<>();
    public final Map<Integer, List<ChunkPos>> loadedChunks = new HashMap<>(); // Forced Chunks

    private int uniqueID = 1; // -1 for invalid, 0 for default return value

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
        ServerWorld saveWorld = ServerLifecycleHooks.getCurrentServer().func_241755_D_();
        data = saveWorld.getSavedData().getOrCreate(FluxNetworkData::new, NETWORK_DATA);
        FluxNetworks.LOGGER.info("FluxNetworkData has been successfully loaded");
    }

    public static void release() {
        if (data != null) {
            data = null;
            FluxNetworks.LOGGER.info("FluxNetworkData has been unloaded");
        }
    }

    public static IFluxNetwork getNetwork(int id) {
        return get().networks.getOrDefault(id, FluxNetworkInvalid.INSTANCE);
    }

    @Nonnull
    public static Collection<IFluxNetwork> getAllNetworks() {
        return get().networks.values();
    }

    private boolean hasSpaceLeft(PlayerEntity player) {
        if (FluxConfig.maximumPerPlayer == -1) {
            return true;
        }
        UUID uuid = PlayerEntity.getUUID(player.getGameProfile());
        long created = networks.values().stream().filter(n -> n.getNetworkOwner().equals(uuid)).count();
        return created < FluxConfig.maximumPerPlayer;
    }

    @Nullable
    public static IFluxNetwork createNetwork(@Nonnull PlayerEntity player, String name, int color, SecurityType securityType, @Nullable String password) {
        FluxNetworkData data = get();
        if (!data.hasSpaceLeft(player)) {
            return null;
        }
        UUID uuid = PlayerEntity.getUUID(player.getGameProfile());

        NetworkMember owner = NetworkMember.createNetworkMember(player, AccessType.OWNER);
        FluxNetworkServer network = new FluxNetworkServer(data.uniqueID++, name, securityType, color, uuid, password);
        network.getNetworkMembers().add(owner);

        data.addNetwork(network);
        return network;
    }

    public void addNetwork(IFluxNetwork network) {
        networks.putIfAbsent(network.getNetworkID(), network);
        NetworkHandler.INSTANCE.sendToAll(new SNetworkUpdateMessage(Lists.newArrayList(network), FluxConstants.FLAG_NET_BASIS));
    }

    public void deleteNetwork(@Nonnull IFluxNetwork network) {
        network.onDeleted();
        networks.remove(network.getNetworkID());
        NetworkHandler.INSTANCE.sendToAll(new SNetworkUpdateMessage(Lists.newArrayList(network), FluxConstants.FLAG_NET_REMOVE));
    }

    @Override
    public boolean isDirty() {
        return true;
    }

    @Override
    public void read(@Nonnull CompoundNBT nbt) {
        uniqueID = nbt.getInt(UNIQUE_ID);
        if (nbt.contains(NETWORKS)) {
            ListNBT list = nbt.getList(NETWORKS, Constants.NBT.TAG_COMPOUND);
            for (int i = 0; i < list.size(); i++) {
                CompoundNBT tag = list.getCompound(i);
                FluxNetworkServer network = new FluxNetworkServer();
                /*if(tag.contains(OLD_NETWORK_ID)) {
                    readOldData(network, tag);
                } else {*/
                network.readCustomNBT(tag, FluxConstants.FLAG_SAVE_ALL);
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
        for (IFluxNetwork network : networks.values()) {
            CompoundNBT tag = new CompoundNBT();
            network.writeCustomNBT(tag, FluxConstants.FLAG_SAVE_ALL);
            list.add(tag);
        }
        compound.put(NETWORKS, list);

        CompoundNBT tag = new CompoundNBT();
        loadedChunks.forEach((dim, pos) -> writeChunks(dim, pos, tag));
        compound.put(LOADED_CHUNKS, tag);
        return compound;
    }

    public static void readPlayers(IFluxNetwork network, @Nonnull CompoundNBT nbt) {
        if (!nbt.contains(PLAYER_LIST)) {
            return;
        }
        List<NetworkMember> members = network.getNetworkMembers();
        ListNBT list = nbt.getList(PLAYER_LIST, Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < list.size(); i++) {
            CompoundNBT c = list.getCompound(i);
            members.add(new NetworkMember(c));
        }
    }

    public static void writePlayers(IFluxNetwork network, @Nonnull CompoundNBT nbt) {
        List<NetworkMember> members = network.getNetworkMembers();
        if (!members.isEmpty()) {
            ListNBT list = new ListNBT();
            members.forEach(s -> list.add(s.writeNetworkNBT(new CompoundNBT())));
            nbt.put(PLAYER_LIST, list);
        }
    }

    public static void writeAllPlayers(IFluxNetwork network, @Nonnull CompoundNBT nbt) {
        List<NetworkMember> members = network.getNetworkMembers();
        ListNBT list = new ListNBT();
        if (!members.isEmpty()) {
            members.forEach(s -> list.add(s.writeNetworkNBT(new CompoundNBT())));
        }
        List<ServerPlayerEntity> players = ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers();
        if (!players.isEmpty()) {
            players.stream().filter(p -> members.stream().noneMatch(s -> s.getPlayerUUID().equals(p.getUniqueID())))
                    .forEach(s -> list.add(NetworkMember.createNetworkMember(s, getPermission(s)).writeNetworkNBT(new CompoundNBT())));
        }
        nbt.put(PLAYER_LIST, list);
    }

    private static AccessType getPermission(@Nonnull PlayerEntity player) {
        return SuperAdmin.isPlayerSuperAdmin(player) ? AccessType.SUPER_ADMIN : AccessType.BLOCKED;
    }

    public static void readConnections(IFluxNetwork network, @Nonnull CompoundNBT nbt) {
        if (!nbt.contains(UNLOADED_CONNECTIONS)) {
            return;
        }
        List<IFluxDevice> a = network.getAllDevices();
        ListNBT list = nbt.getList(UNLOADED_CONNECTIONS, Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < list.size(); i++) {
            a.add(new SimpleFluxDevice(list.getCompound(i)));
        }
    }

    public static void writeConnections(IFluxNetwork network, @Nonnull CompoundNBT nbt) {
        List<IFluxDevice> a = network.getAllDevices();
        if (!a.isEmpty()) {
            ListNBT list = new ListNBT();
            a.forEach(s -> {
                if (!s.isChunkLoaded()) {
                    list.add(s.writeCustomNBT(new CompoundNBT(), NBTType.DEFAULT));
                }
            });
            nbt.put(UNLOADED_CONNECTIONS, list);
        }
    }

    public static void readAllConnections(IFluxNetwork network, @Nonnull CompoundNBT nbt) {
        if (!nbt.contains(UNLOADED_CONNECTIONS)) {
            return;
        }
        List<IFluxDevice> a = network.getAllDevices();
        ListNBT list = nbt.getList(UNLOADED_CONNECTIONS, Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < list.size(); i++) {
            a.add(new SimpleFluxDevice(list.getCompound(i)));
        }
    }

    public static void writeAllConnections(IFluxNetwork network, @Nonnull CompoundNBT nbt) {
        List<IFluxDevice> a = network.getAllDevices();
        if (!a.isEmpty()) {
            ListNBT list = new ListNBT();
            a.forEach(s -> list.add(s.writeCustomNBT(new CompoundNBT(), NBTType.DEFAULT)));
            nbt.put(UNLOADED_CONNECTIONS, list);
        }
    }

    private void readChunks(CompoundNBT nbt) {
        if (!nbt.contains(LOADED_CHUNKS)) {
            return;
        }
        CompoundNBT tags = nbt.getCompound(LOADED_CHUNKS);
        for (String key : tags.keySet()) {
            ListNBT list = tags.getList(key, Constants.NBT.TAG_COMPOUND);
            List<ChunkPos> pos = loadedChunks.computeIfAbsent(Integer.valueOf(key), l -> new ArrayList<>());
            for (int i = 0; i < list.size(); i++) {
                CompoundNBT tag = list.getCompound(i);
                pos.add(new ChunkPos(tag.getInt("x"), tag.getInt("z")));
            }
        }
    }

    private void writeChunks(int dim, List<ChunkPos> pos, CompoundNBT nbt) {
        if (!pos.isEmpty()) {
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
