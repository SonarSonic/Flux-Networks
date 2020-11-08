package sonar.fluxnetworks.api.network;

import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.GlobalPos;
import sonar.fluxnetworks.api.device.IFluxDevice;
import sonar.fluxnetworks.common.connection.NetworkStatistics;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IFluxNetwork {

    /**
     * Returns the network ID
     *
     * @return a positive integer or {@link sonar.fluxnetworks.api.misc.FluxConstants#INVALID_NETWORK_ID}
     */
    int getNetworkID();

    /**
     * Returns the network name
     *
     * @return the name of this network
     * @see sonar.fluxnetworks.client.FluxClientCache#getDisplayName(CompoundNBT)
     */
    String getNetworkName();

    void setNetworkName(String name);

    /**
     * Returns the network color in 0xRRGGBB format
     *
     * @return network color
     */
    int getNetworkColor();

    void setNetworkColor(int color);

    int getWirelessMode();

    void setWirelessMode(int wireless);

    UUID getOwnerUUID();

    void setOwnerUUID(UUID uuid);

    NetworkSecurity getSecurity();

    NetworkStatistics getStatistics();

    /**
     * Returns a collection object that contains all network members
     *
     * @return all members
     */
    Collection<NetworkMember> getAllMembers();

    /**
     * Returns the original object of current network members
     *
     * @return members
     */
    Object2ObjectMap<UUID, NetworkMember> getMembersMap();

    Optional<NetworkMember> getMemberByUUID(UUID playerUUID);

    /**
     * Get all connections including loaded tile entities (TileFluxDevice) and unloaded (SimpleFluxDevice)
     * On client, all are SimpleFluxDevice
     *
     * @return the list of all connections
     * @see #getConnections(FluxLogicType)
     */
    Collection<IFluxDevice> getAllConnections();

    /**
     * Get connection by global pos from all connections collection
     *
     * @param pos global pos
     * @return possible device
     * @see #getAllConnections()
     */
    Optional<IFluxDevice> getConnectionByPos(GlobalPos pos);

    /*@Deprecated
    default void onStartServerTick() {

    }*/

    default void onEndServerTick() {

    }

    default void onDelete() {

    }

    @Nonnull
    AccessLevel getPlayerAccess(PlayerEntity player);

    /*@Deprecated
    default void addNewMember(String name) {

    }

    @Deprecated
    default void removeMember(UUID uuid) {

    }*/

    /**
     * Get all loaded connections with given logic type,
     * this method should be only invoked on server side
     *
     * @param type logic type
     * @param <T>  device type
     * @return the list of connections
     */
    @Nonnull
    <T extends IFluxDevice> List<T> getConnections(FluxLogicType type);

    /* Server only */
    long getBufferLimiter();

    /* Server only */
    void markSortConnections();

    /* Server only */
    void enqueueConnectionAddition(@Nonnull IFluxDevice device);

    /* Server only */
    void enqueueConnectionRemoval(@Nonnull IFluxDevice device, boolean chunkUnload);

    /**
     * Returns whether this network is a valid network.
     *
     * @return {@code true} if it is valid, {@code false} otherwise
     * @see sonar.fluxnetworks.api.misc.FluxConstants#INVALID_NETWORK_ID
     */
    boolean isValid();

    void writeCustomNBT(CompoundNBT nbt, int type);

    void readCustomNBT(CompoundNBT nbt, int type);
}
