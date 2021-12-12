package sonar.fluxnetworks.common.connection;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraftforge.server.ServerLifecycleHooks;
import sonar.fluxnetworks.FluxConfig;
import sonar.fluxnetworks.FluxNetworks;
import sonar.fluxnetworks.api.FluxConstants;
import sonar.fluxnetworks.api.network.SecurityLevel;
import sonar.fluxnetworks.register.Messages;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;
import java.util.Collection;
import java.util.UUID;

/**
 * Manage all logical flux networks and save their data to the world.
 * <p>
 * Only on logical server side. Only on server thread.
 */
@NotThreadSafe
public final class FluxNetworkManager extends SavedData {

    private static final String NETWORK_DATA = FluxNetworks.MODID + "data";

    private static volatile FluxNetworkManager data;

    private static final String NETWORKS = "networks";
    //private static final String TICKETS = "tickets";
    private static final String UNIQUE_ID = "uniqueID";

    /*public static String NETWORK_PASSWORD = "networkPassword";
    public static String SECURITY_TYPE = "networkSecurity";
    public static String ENERGY_TYPE = "networkEnergy";
    public static String WIRELESS_MODE = "wirelessMode";*/

    /*public static String NETWORK_FOLDERS = "folders";
    public static String UNLOADED_CONNECTIONS = "unloaded";*/

    /*public static String OLD_NETWORK_ID = "id";
    public static String OLD_NETWORK_NAME = "name";
    public static String OLD_NETWORK_COLOR = "colour";
    public static String OLD_NETWORK_ACCESS = "access";*/

    private final Int2ObjectMap<FluxNetwork> mNetworks = new Int2ObjectOpenHashMap<>();
    //private final Map<ResourceLocation, LongSet> tickets = new HashMap<>();

    // next network id, -1 for invalid
    private int mUniqueID;

    private FluxNetworkManager() {
    }

    private FluxNetworkManager(@Nonnull CompoundTag tag) {
        read(tag);
    }

    @Nonnull
    public static FluxNetworkManager getInstance() {
        if (data == null) {
            ServerLevel level = ServerLifecycleHooks.getCurrentServer().overworld();
            data = level.getDataStorage()
                    .computeIfAbsent(FluxNetworkManager::new, FluxNetworkManager::new, NETWORK_DATA);
            FluxNetworks.LOGGER.debug("FluxNetworkData has been successfully loaded");
        }
        return data;
    }

    // called when the server instance changed, e.g. switching single player saves
    public static void release() {
        if (data != null) {
            data = null;
            FluxNetworks.LOGGER.debug("FluxNetworkData has been unloaded");
        }
    }

    @Nonnull
    public static FluxNetwork getNetwork(int id) {
        return getInstance().mNetworks.getOrDefault(id, FluxNetworkInvalid.INSTANCE);
    }

    @Nonnull
    public static Collection<FluxNetwork> getAllNetworks() {
        return getInstance().mNetworks.values();
    }

    /*
     * Get a set of block pos with given dimension key, a pos represents a flux tile entity
     * that wants to load the chunk it's in
     *
     * @param dim dimension
     * @return all block pos that want to load chunks they are in
     */
    /*@Nonnull
    public static LongSet getTickets(@Nonnull RegistryKey<World> dim) {
        return get().tickets.computeIfAbsent(dim.getLocation(), d -> new LongOpenHashSet());
    }*/

    @Nullable
    public FluxNetwork createNetwork(@Nonnull Player creator, @Nonnull String name, int color,
                                     @Nonnull SecurityLevel level, @Nonnull String password) {
        if (FluxConfig.maximumPerPlayer != -1) {
            UUID uuid = creator.getUUID();
            int count = 0;
            for (var n : mNetworks.values()) {
                if (n.getOwnerUUID().equals(uuid) && ++count >= FluxConfig.maximumPerPlayer) {
                    return null;
                }
            }
        }

        //noinspection StatementWithEmptyBody
        while (mNetworks.containsKey(++mUniqueID)) ;
        FluxNetworkServer network = new FluxNetworkServer(mUniqueID, name, color, creator);
        network.getSecurity().set(level, password);

        mNetworks.put(network.getNetworkID(), network);
        Messages.getNetworkUpdate(network, FluxConstants.TYPE_NET_BASIC).sendToAll();
        return network;
    }

    public void deleteNetwork(@Nonnull FluxNetwork network) {
        if (mNetworks.remove(network.getNetworkID()) == network) {
            network.onDelete();
            Messages.sendNetworkDelete(network.getNetworkID());
        }
    }

    @Override
    public boolean isDirty() {
        // always dirty as a convenience
        return true;
    }

    private void read(@Nonnull CompoundTag compound) {
        mUniqueID = compound.getInt(UNIQUE_ID);

        ListTag list = compound.getList(NETWORKS, Tag.TAG_COMPOUND);
        for (int i = 0; i < list.size(); i++) {
            CompoundTag tag = list.getCompound(i);
            FluxNetworkServer network = new FluxNetworkServer();
            network.readCustomTag(tag, FluxConstants.TYPE_SAVE_ALL);
            FluxNetwork old = mNetworks.put(network.getNetworkID(), network);
            if (old != null) {
                FluxNetworks.LOGGER.warn("""
                                Network IDs are not unique when reading data. Overriding it.
                                Source: {}
                                Dest: {}""",
                        network, old);
            }
        }

        /*CompoundNBT tag = nbt.getCompound(TICKETS);
        for (String key : tag.keySet()) {
            ListNBT l2 = tag.getList(key, Constants.NBT.TAG_LONG);
            LongSet set = tickets.computeIfAbsent(new ResourceLocation(key), d -> new LongOpenHashSet());
            for (INBT n : l2) {
                try {
                    set.add(((LongNBT) n).getLong());
                } catch (RuntimeException ignored) {

                }
            }
        }
        data = this;*/
    }

    @Nonnull
    @Override
    public CompoundTag save(@Nonnull CompoundTag compound) {
        compound.putInt(UNIQUE_ID, mUniqueID);

        ListTag list = new ListTag();
        for (FluxNetwork network : mNetworks.values()) {
            CompoundTag tag = new CompoundTag();
            network.writeCustomTag(tag, FluxConstants.TYPE_SAVE_ALL);
            list.add(tag);
        }
        compound.put(NETWORKS, list);

        /*CompoundNBT tag = new CompoundNBT();
        for (Map.Entry<ResourceLocation, LongSet> entry : tickets.entrySet()) {
            LongSet set = entry.getValue();
            if (!set.isEmpty()) {
                ListNBT l2 = new ListNBT();
                for (long l : set) {
                    l2.add(LongNBT.valueOf(l));
                }
                tag.put(entry.getKey().toString(), l2);
            }
        }
        compound.put(TICKETS, tag);*/
        return compound;
    }

    /*public static void readPlayers(IFluxNetwork network, @Nonnull CompoundNBT nbt) {
        if (!nbt.contains(FluxConstants.PLAYER_LIST)) {
            return;
        }
        List<NetworkMember> members = network.getMemberList();
        ListNBT list = nbt.getList(FluxConstants.PLAYER_LIST, Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < list.size(); i++) {
            CompoundNBT c = list.getCompound(i);
            members.add(new NetworkMember(c));
        }
    }

    public static void writePlayers(IFluxNetwork network, @Nonnull CompoundNBT nbt) {
        List<NetworkMember> members = network.getMemberList();
        if (!members.isEmpty()) {
            ListNBT list = new ListNBT();
            members.forEach(s -> list.add(s.writeNBT(new CompoundNBT())));
            nbt.put(FluxConstants.PLAYER_LIST, list);
        }
    }

    public static void writeAllPlayers(IFluxNetwork network, @Nonnull CompoundNBT nbt) {
        List<NetworkMember> members = network.getMemberList();
        ListNBT list = new ListNBT();
        if (!members.isEmpty()) {
            members.forEach(s -> list.add(s.writeNBT(new CompoundNBT())));
        }
        List<ServerPlayerEntity> players = ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers();
        if (!players.isEmpty()) {
            players.stream().filter(p -> members.stream().noneMatch(s -> s.getPlayerUUID().equals(p.getUniqueID())))
                    .forEach(s -> list.add(NetworkMember.create(s, getPermission(s)).writeNBT(new CompoundNBT())));
        }
        nbt.put(FluxConstants.PLAYER_LIST, list);
    }

    private static AccessLevel getPermission(@Nonnull PlayerEntity player) {
        return SuperAdmin.isPlayerSuperAdmin(player) ? AccessLevel.SUPER_ADMIN : AccessLevel.BLOCKED;
    }*/

    /*public static void readConnections(IFluxNetwork network, @Nonnull CompoundNBT nbt) {
        if (!nbt.contains(UNLOADED_CONNECTIONS)) {
            return;
        }
        List<IFluxDevice> a = network.getAllConnections();
        ListNBT list = nbt.getList(UNLOADED_CONNECTIONS, Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < list.size(); i++) {
            a.add(new SimpleFluxDevice(list.getCompound(i)));
        }
    }

    public static void writeConnections(IFluxNetwork network, @Nonnull CompoundNBT nbt) {
        List<IFluxDevice> a = network.getAllConnections();
        if (!a.isEmpty()) {
            ListNBT list = new ListNBT();
            a.forEach(s -> {
                if (!s.isChunkLoaded()) {
                    list.add(s.writeCustomNBT(new CompoundNBT(), 0));
                }
            });
            nbt.put(UNLOADED_CONNECTIONS, list);
        }
    }

    public static void readAllConnections(IFluxNetwork network, @Nonnull CompoundNBT nbt) {
        if (!nbt.contains(UNLOADED_CONNECTIONS)) {
            return;
        }
        List<IFluxDevice> a = network.getAllConnections();
        ListNBT list = nbt.getList(UNLOADED_CONNECTIONS, Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < list.size(); i++) {
            a.add(new SimpleFluxDevice(list.getCompound(i)));
        }
    }

    public static void writeAllConnections(IFluxNetwork network, @Nonnull CompoundNBT nbt) {
        List<IFluxDevice> a = network.getAllConnections();
        if (!a.isEmpty()) {
            ListNBT list = new ListNBT();
            a.forEach(s -> list.add(s.writeCustomNBT(new CompoundNBT(), NBTType.DEFAULT)));
            nbt.put(UNLOADED_CONNECTIONS, list);
        }
    }*/

    /*private void readChunks(CompoundNBT nbt) {
        if (!nbt.contains(LOADED_CHUNKS)) {
            return;
        }
        CompoundNBT tags = nbt.getCompound(LOADED_CHUNKS);
        for (String key : tags.keySet()) {
            ListNBT list = tags.getList(key, Constants.NBT.TAG_COMPOUND);
            List<ChunkPos> pos = forcedChunks.computeIfAbsent(Integer.valueOf(key), l -> new ArrayList<>());
            for (int i = 0; i < list.size(); i++) {
                CompoundNBT tag = list.getCompound(i);
                pos.add(new ChunkPos(tag.getInt("x"), tag.getInt("z")));
            }
        }
    }*/

    /*private void writeChunks(int dim, List<ChunkPos> pos, CompoundNBT nbt) {
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
    }*/

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
