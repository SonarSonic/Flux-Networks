package sonar.fluxnetworks.api.network;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.GlobalPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import sonar.fluxnetworks.api.device.IFluxDevice;
import sonar.fluxnetworks.common.blockentity.FluxDeviceEntity;
import sonar.fluxnetworks.common.connection.NetworkStatistics;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public interface IFluxNetwork {

    /**
     * Define logical types of network transfer handlers.
     */
    int
            ANY = 0,
            PLUG = 1,
            POINT = 2,
            STORAGE = 3,
            CONTROLLER = 4;

    /**
     * Returns the network ID
     *
     * @return a positive integer or {@link sonar.fluxnetworks.api.misc.FluxConstants#INVALID_NETWORK_ID}
     */
    int getNetworkID();

    UUID getOwnerUUID();

    /**
     * Returns the network name. For an invalid network this is empty,
     * and client should display an alternative text instead.
     *
     * @return the name of this network
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

    NetworkSecurity getSecurity();

    NetworkStatistics getStatistics();

    /**
     * Returns a collection object that contains all network members
     *
     * @return all members
     */
    Collection<NetworkMember> getAllMembers();

    Optional<NetworkMember> getMemberByUUID(UUID uuid);

    /**
     * Get all connections including loaded entities and unloaded devices.
     *
     * @return the list of all connections
     * @see #getLogicalEntities(int)
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

    void onEndServerTick();

    void onDelete();

    /**
     * Helper method to get player's access level for this network including super admin,
     * even if the player in not a member in the network.
     * Note this method is server only.
     *
     * @param player the server player
     * @return access level
     */
    AccessLevel getPlayerAccess(Player player);

    /*@Deprecated
    default void addNewMember(String name) {

    }

    @Deprecated
    default void removeMember(UUID uuid) {

    }*/

    /**
     * Get all network device entities with given logical type,
     * this method should be only invoked on the server side.
     *
     * @param logic the logical type
     * @return a list of devices
     */
    List<FluxDeviceEntity> getLogicalEntities(int logic);

    /* Server only */
    long getBufferLimiter();

    /* Server only */
    boolean enqueueConnectionAddition(FluxDeviceEntity device);

    /* Server only */
    void enqueueConnectionRemoval(FluxDeviceEntity device, boolean chunkUnload);

    /**
     * Returns whether this network is a valid network.
     *
     * @return {@code true} if it is valid, {@code false} otherwise
     * @see sonar.fluxnetworks.api.misc.FluxConstants#INVALID_NETWORK_ID
     */
    boolean isValid();

    void writeCustomTag(CompoundTag tag, int type);

    void readCustomTag(CompoundTag tag, int type);
}
