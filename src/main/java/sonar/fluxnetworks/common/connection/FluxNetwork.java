package sonar.fluxnetworks.common.connection;

import net.minecraft.Util;
import net.minecraft.core.GlobalPos;
import net.minecraft.nbt.*;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.server.ServerLifecycleHooks;
import sonar.fluxnetworks.api.FluxConstants;
import sonar.fluxnetworks.api.device.IFluxDevice;
import sonar.fluxnetworks.api.network.*;
import sonar.fluxnetworks.common.capability.FluxPlayer;
import sonar.fluxnetworks.common.device.TileFluxDevice;
import sonar.fluxnetworks.common.util.FluxUtils;

import javax.annotation.*;
import java.util.*;

/**
 * The base class of a flux network.
 * <p>
 * There are two common types of implementation: client and server.
 * Client instances are cache values that updated from server, used for pre-checks in UI.
 * Server instances are logical networks and are responsible for energy transfer.
 * <p>
 * When the client operates the server-side network, it needs double side checks to ensure security.
 * The server-side data will be persistent stored with the game save.
 */
@ParametersAreNonnullByDefault
public class FluxNetwork {

    /**
     * An invalid network avoids nullability checks, any operation on this network is invalid.
     * You can check {@link #isValid()} to skip your operations. Even if the operation is performed,
     * there will be no error.
     * <p>
     * A disconnected device is considered connected to this network.
     */
    public static final FluxNetwork INVALID = new FluxNetwork(
            FluxConstants.INVALID_NETWORK_ID, "", FluxConstants.INVALID_NETWORK_COLOR,
            SecurityLevel.PRIVATE, Util.NIL_UUID);

    /**
     * Constant IDs used to identify logical devices.
     *
     * @see #getLogicalEntities(int)
     */
    public static final int
            ANY = 0,
            PLUG = 1,
            POINT = 2,
            STORAGE = 3,
            CONTROLLER = 4;

    /**
     * Some contracts.
     */
    public static final int MAX_NETWORK_NAME_LENGTH = 24;
    public static final int MAX_PASSWORD_LENGTH = 16;

    private static final String NETWORK_NAME = "name";
    private static final String NETWORK_COLOR = "color";
    private static final String OWNER_UUID = "owner";
    private static final String SECURITY_LEVEL = "security";
    private static final String MEMBERS = "members";
    private static final String CONNECTIONS = "connections";

    //public ICustomValue<Integer> network_id = new CustomValue<>();
    //public ICustomValue<String> network_name = new CustomValue<>();
    //public ICustomValue<UUID> network_owner = new CustomValue<>();
    //public ICustomValue<SecurityType> network_security = new CustomValue<>();
    //public ICustomValue<String> network_password = new CustomValue<>();
    //public ICustomValue<Integer> network_color = new CustomValue<>();
    //public ICustomValue<EnergyType> network_energy = new CustomValue<>();
    //public ICustomValue<Integer> network_wireless = new CustomValue<>(0);
    //public ICustomValue<NetworkStatistics> network_stats = new CustomValue<>(new NetworkStatistics(this));

    private int mID;
    private String mName;
    private int mColor;
    private UUID mOwnerUUID;
    private SecurityLevel mSecurityLevel;

    final NetworkStatistics mStatistics = new NetworkStatistics(this);
    final HashMap<UUID, NetworkMember> mMemberMap = new HashMap<>();
    /**
     * Server: {@link TileFluxDevice} (loaded) and {@link PhantomFluxDevice} (unloaded)
     * <p>
     * Client: {@link PhantomFluxDevice} (data container)
     */
    final HashMap<GlobalPos, IFluxDevice> mConnectionMap = new HashMap<>();

    FluxNetwork() {
    }

    private FluxNetwork(int id, String name, int color, @Nonnull SecurityLevel security, @Nonnull UUID owner) {
        mID = id;
        mName = name;
        mColor = color;
        mSecurityLevel = security;
        mOwnerUUID = owner;
    }

    FluxNetwork(int id, String name, int color, @Nonnull SecurityLevel security, @Nonnull Player owner) {
        this(id, name, color, security, owner.getUUID());
        mMemberMap.put(mOwnerUUID, NetworkMember.create(owner, AccessLevel.OWNER));
    }

    /**
     * Returns the unique ID of this network.
     *
     * @return a positive integer or {@link FluxConstants#INVALID_NETWORK_ID}
     */
    public final int getNetworkID() {
        return mID;
    }

    /**
     * @return the owner UUID
     */
    @Nonnull
    public final UUID getOwnerUUID() {
        return mOwnerUUID;
    }

    /**
     * Returns the network name. For an invalid network this is empty,
     * and client should display an alternative text instead.
     *
     * @return the name of this network
     */
    @Nonnull
    public final String getNetworkName() {
        return mName;
    }

    public void setNetworkName(@Nonnull String name) {
        mName = name;
    }

    /**
     * Returns the network color in 0xRRGGBB format.
     *
     * @return the network color
     */
    public final int getNetworkColor() {
        return mColor;
    }

    public void setNetworkColor(int color) {
        mColor = color;
    }

    /**
     * Returns the security level of this network.
     *
     * @return the security level of this network
     */
    @Nonnull
    public final SecurityLevel getSecurityLevel() {
        return mSecurityLevel;
    }

    public void setSecurityLevel(@Nonnull SecurityLevel level) {
        mSecurityLevel = level;
    }

    @Nonnull
    public NetworkStatistics getStatistics() {
        return mStatistics;
    }

    @Nullable
    public NetworkMember getMemberByUUID(@Nonnull UUID uuid) {
        return mMemberMap.get(uuid);
    }

    /**
     * Returns a collection object that contains all network members
     *
     * @return all members
     */
    @Nonnull
    public Collection<NetworkMember> getAllMembers() {
        return mMemberMap.values();
    }

    /**
     * Get connection by global pos from all connections collection
     *
     * @param pos global pos
     * @return possible device
     * @see #getAllConnections()
     */
    @Nullable
    public IFluxDevice getConnectionByPos(@Nonnull GlobalPos pos) {
        return mConnectionMap.get(pos);
    }

    /**
     * Get all connections including loaded entities and unloaded devices.
     *
     * @return the list of all connections
     * @see #getLogicalEntities(int)
     */
    @Nonnull
    public Collection<IFluxDevice> getAllConnections() {
        return mConnectionMap.values();
    }

    public void onEndServerTick() {
    }

    /**
     * Called when this network is deleted from its manager.
     */
    public void onDelete() {
        mMemberMap.clear();
        mConnectionMap.clear();
    }

    /**
     * Helper method to get player's access level for this network including super admin,
     * even if the player in not a member in the network.
     * <p>
     * Super admin only works on a server flux network.
     *
     * @param player the server player
     * @return access level
     */
    @Nonnull
    public AccessLevel getPlayerAccess(@Nonnull Player player) {
        NetworkMember member = getMemberByUUID(player.getUUID());
        if (member != null) {
            return member.getAccessLevel();
        }
        return mSecurityLevel == SecurityLevel.PUBLIC ? AccessLevel.USER : AccessLevel.BLOCKED;
    }

    public boolean canPlayerAccess(@Nonnull Player player, @Nonnull String password) {
        return getPlayerAccess(player).canUse();
    }

    /**
     * Get all network device entities with given logical type,
     * this method should be only invoked on the server side.
     *
     * @param logic the logical type
     * @return a list of devices
     */
    @Nonnull
    public List<TileFluxDevice> getLogicalEntities(int logic) {
        return Collections.emptyList();
    }

    public long getBufferLimiter() {
        return 0;
    }

    public boolean enqueueConnectionAddition(@Nonnull TileFluxDevice device) {
        return true;
    }

    public void enqueueConnectionRemoval(@Nonnull TileFluxDevice device, boolean chunkUnload) {
    }

    /*@Override
    public <T> T getSetting(NetworkSettings<T> setting) {
        return setting.getValue(this).getValue();
    }

    @Override
    public <T> void setSetting(NetworkSettings<T> settings, T value) {
        settings.getValue(this).setValue(value);
    }*/

    /**
     * Returns whether this network is a valid network.
     *
     * @return {@code true} if it is valid, {@code false} otherwise
     * @see FluxConstants#INVALID_NETWORK_ID
     */
    public boolean isValid() {
        return false;
    }

    public void writeCustomTag(@Nonnull CompoundTag tag, int type) {
        if (type == FluxConstants.NBT_NET_BASIC || type == FluxConstants.NBT_SAVE_ALL) {
            tag.putInt(FluxConstants.NETWORK_ID, mID);
            tag.putString(NETWORK_NAME, mName);
            tag.putInt(NETWORK_COLOR, mColor);
            tag.putUUID(OWNER_UUID, mOwnerUUID);
            tag.putByte(SECURITY_LEVEL, mSecurityLevel.getKey());
        }
        if (type == FluxConstants.NBT_SAVE_ALL) {
            Collection<NetworkMember> members = getAllMembers();
            if (!members.isEmpty()) {
                ListTag list = new ListTag();
                for (NetworkMember m : members) {
                    CompoundTag subTag = new CompoundTag();
                    m.writeNBT(subTag, true);
                    list.add(subTag);
                }
                tag.put(MEMBERS, list);
            }

            Collection<IFluxDevice> connections = getAllConnections();
            // all unloaded
            if (!connections.isEmpty()) {
                ListTag list = new ListTag();
                for (IFluxDevice d : connections) {
                    if (!d.isChunkLoaded()) {
                        CompoundTag subTag = new CompoundTag();
                        d.writeCustomTag(subTag, FluxConstants.NBT_SAVE_ALL);
                        list.add(subTag);
                    }
                }
                tag.put(CONNECTIONS, list);
            }
        }
        if (type == FluxConstants.NBT_NET_MEMBERS) {
            Collection<NetworkMember> members = getAllMembers();
            ListTag list = new ListTag();
            if (!members.isEmpty()) {
                for (NetworkMember m : members) {
                    CompoundTag subTag = new CompoundTag();
                    m.writeNBT(subTag, false);
                    list.add(subTag);
                }
            }
            List<ServerPlayer> players = ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers();
            if (!players.isEmpty()) {
                for (ServerPlayer p : players) {
                    if (getMemberByUUID(p.getUUID()) == null) {
                        CompoundTag subTag = new CompoundTag();
                        NetworkMember m = NetworkMember.create(p,
                                FluxPlayer.isPlayerSuperAdmin(p) ? AccessLevel.SUPER_ADMIN :
                                        AccessLevel.BLOCKED);
                        m.writeNBT(subTag, false);
                        list.add(subTag);
                    }
                }
            }
            tag.put(MEMBERS, list);
        }
        if (type == FluxConstants.NBT_NET_CONNECTIONS) {
            Collection<IFluxDevice> connections = getAllConnections();
            if (!connections.isEmpty()) {
                ListTag list = new ListTag();
                for (IFluxDevice d : connections) {
                    CompoundTag subTag = new CompoundTag();
                    d.writeCustomTag(subTag, FluxConstants.NBT_PHANTOM_UPDATE);
                    list.add(subTag);
                }
                tag.put(CONNECTIONS, list);
            }
        }
        if (type == FluxConstants.NBT_NET_STATISTICS) {
            mStatistics.writeNBT(tag);
        }
        /*if (flags == NBTType.NETWORK_GENERAL || flags == NBTType.ALL_SAVE) {
            nbt.putInt(FluxNetworkData.NETWORK_ID, network_id.getValue());
            nbt.putString(FluxNetworkData.NETWORK_NAME, network_name.getValue());
            nbt.putUniqueId(FluxNetworkData.OWNER_UUID, network_owner.getValue());
            nbt.putInt(FluxNetworkData.SECURITY_TYPE, network_security.getValue().ordinal());
            nbt.putString(FluxNetworkData.NETWORK_PASSWORD, network_password.getValue());
            nbt.putInt(FluxNetworkData.NETWORK_COLOR, network_color.getValue());
            nbt.putInt(FluxNetworkData.ENERGY_TYPE, network_energy.getValue().ordinal());
            nbt.putInt(FluxNetworkData.WIRELESS_MODE, network_wireless.getValue());

            if (flags == NBTType.ALL_SAVE) {
                FluxNetworkData.writePlayers(this, nbt);
                FluxNetworkData.writeConnections(this, nbt);
            }
        }

        if (flags == NBTType.NETWORK_PLAYERS) {
            FluxNetworkData.writeAllPlayers(this, nbt);
        }

        if (flags == NBTType.NETWORK_CONNECTIONS) {
            allDevices.getValue().removeIf(IFluxDevice::isChunkLoaded);
            List<IFluxDevice> connectors = getConnections(FluxLogicType.ANY);
            connectors.forEach(f -> allDevices.getValue().add(new SimpleFluxDevice(f)));
            FluxNetworkData.writeAllConnections(this, nbt);
        }
        if (flags == NBTType.NETWORK_STATISTICS) {
            network_stats.getValue().writeNBT(nbt);
        }
        if (flags == NBTType.NETWORK_CLEAR) {
            nbt.putBoolean("clear", true); // Nothing
        }*/
    }

    public void readCustomTag(@Nonnull CompoundTag tag, int type) {
        if (type == FluxConstants.NBT_NET_BASIC || type == FluxConstants.NBT_SAVE_ALL) {
            mID = tag.getInt(FluxConstants.NETWORK_ID);
            mName = tag.getString(NETWORK_NAME);
            mColor = tag.getInt(NETWORK_COLOR);
            mOwnerUUID = tag.getUUID(OWNER_UUID);
            mSecurityLevel = SecurityLevel.fromKey(tag.getByte(SECURITY_LEVEL));
        }
        if (type == FluxConstants.NBT_SAVE_ALL) {
            ListTag list = tag.getList(MEMBERS, Tag.TAG_COMPOUND);
            for (int i = 0; i < list.size(); i++) {
                CompoundTag c = list.getCompound(i);
                NetworkMember m = new NetworkMember(c);
                mMemberMap.put(m.getPlayerUUID(), m);
            }
            list = tag.getList(CONNECTIONS, Tag.TAG_COMPOUND);
            for (int i = 0; i < list.size(); i++) {
                CompoundTag c = list.getCompound(i);
                PhantomFluxDevice f = PhantomFluxDevice.load(c);
                mConnectionMap.put(f.getGlobalPos(), f);
            }
        }
        if (type == FluxConstants.NBT_NET_MEMBERS) {
            mMemberMap.clear();
            ListTag list = tag.getList(MEMBERS, Tag.TAG_COMPOUND);
            for (int i = 0; i < list.size(); i++) {
                CompoundTag c = list.getCompound(i);
                NetworkMember m = new NetworkMember(c);
                mMemberMap.put(m.getPlayerUUID(), m);
            }
        }
        if (type == FluxConstants.NBT_NET_CONNECTIONS) {
            //TODO waiting for new GUI system, see GuiTabConnections, we request a full connections update
            // when we (re)open the gui, but if a tile removed by someone or on world unloads, this won't send
            // to player, so calling clear() here as a temporary solution, (f != null) is always false
            mConnectionMap.clear();

            ListTag list = tag.getList(CONNECTIONS, Tag.TAG_COMPOUND);
            for (int i = 0; i < list.size(); i++) {
                CompoundTag c = list.getCompound(i);
                GlobalPos pos = FluxUtils.readGlobalPos(c);
                IFluxDevice f = mConnectionMap.get(pos);
                if (f != null) {
                    f.readCustomTag(c, FluxConstants.NBT_PHANTOM_UPDATE);
                } else {
                    mConnectionMap.put(pos, PhantomFluxDevice.update(pos, c));
                }
            }
        }
        if (type == FluxConstants.NBT_NET_STATISTICS) {
            mStatistics.readNBT(tag);
        }
        /*if (flags == NBTType.NETWORK_GENERAL || flags == NBTType.ALL_SAVE) {
            network_id.setValue(nbt.getInt(FluxNetworkData.NETWORK_ID));
            network_name.setValue(nbt.getString(FluxNetworkData.NETWORK_NAME));
            network_owner.setValue(nbt.getUniqueId(FluxNetworkData.OWNER_UUID));
            network_security.setValue(SecurityType.values()[nbt.getInt(FluxNetworkData.SECURITY_TYPE)]);
            network_password.setValue(nbt.getString(FluxNetworkData.NETWORK_PASSWORD));
            network_color.setValue(nbt.getInt(FluxNetworkData.NETWORK_COLOR));
            network_energy.setValue(EnergyType.values()[nbt.getInt(FluxNetworkData.ENERGY_TYPE)]);
            network_wireless.setValue(nbt.getInt(FluxNetworkData.WIRELESS_MODE));

            if (flags == NBTType.ALL_SAVE) {
                FluxNetworkData.readPlayers(this, nbt);
                FluxNetworkData.readConnections(this, nbt);
            }
        }

        if (flags == NBTType.NETWORK_PLAYERS) {
            FluxNetworkData.readPlayers(this, nbt);
        }

        if (flags == NBTType.NETWORK_CONNECTIONS) {
            FluxNetworkData.readAllConnections(this, nbt);
        }
        if (flags == NBTType.NETWORK_STATISTICS) {
            network_stats.getValue().readNBT(nbt);
        }*/
    }

    @Override
    public String toString() {
        return "FluxNetwork{" +
                "networkID=" + mID +
                ", networkName='" + mName + '\'' +
                ", ownerUUID=" + mOwnerUUID +
                '}';
    }
}
