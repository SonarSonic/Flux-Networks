package sonar.fluxnetworks.api.network;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import sonar.fluxnetworks.api.device.IFluxDevice;
import sonar.fluxnetworks.common.connection.NetworkStatistics;

import javax.annotation.Nonnull;
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
     * @see sonar.fluxnetworks.client.FluxClientCache#getDisplayNetworkName(int)
     */
    String getNetworkName();

    void setNetworkName(String name);

    /*@Deprecated
    <T> T getSetting(NetworkSettings<T> setting);

    @Deprecated
    <T> void setSetting(NetworkSettings<T> settings, T value);*/

    SecurityType getNetworkSecurity();

    String getNetworkPassword();

    /**
     * Returns the network color in 0xRRGGBB format
     *
     * @return network color
     */
    int getNetworkColor();

    void setNetworkColor(int color);

    UUID getOwnerUUID();

    NetworkStatistics getNetworkStatistics();

    List<NetworkMember> getMemberList();

    /**
     * Get all connections including loaded tile entities (TileFluxDevice) and unloaded (SimpleFLuxDevice)
     *
     * @return the list of all connections
     * @see #getConnections(FluxLogicType)
     */
    List<IFluxDevice> getAllConnections();

    /*@Deprecated
    default void onStartServerTick() {

    }*/

    default void onEndServerTick() {

    }

    default void onDeleted() {

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
     * Get all loaded connections with given logic type
     *
     * @param type logic type
     * @param <T>  device type
     * @return the list of connections
     */
    @Nonnull
    <T extends IFluxDevice> List<T> getConnections(FluxLogicType type);

    Optional<NetworkMember> getMemberByUUID(UUID playerUUID);

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

    void readCustomNBT(CompoundNBT nbt, int flags);

    void writeCustomNBT(CompoundNBT nbt, int flags);
}
