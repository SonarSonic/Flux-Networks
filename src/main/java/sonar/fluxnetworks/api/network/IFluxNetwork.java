package sonar.fluxnetworks.api.network;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import sonar.fluxnetworks.api.device.IFluxDevice;
import sonar.fluxnetworks.common.connection.NetworkStatistics;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
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

    String getNetworkName();

    void setNetworkName(String name);

    /*@Deprecated
    <T> T getSetting(NetworkSettings<T> setting);

    @Deprecated
    <T> void setSetting(NetworkSettings<T> settings, T value);*/

    SecurityType getNetworkSecurity();

    @Nullable
    String getNetworkPassword();

    /**
     * Returns the network color in 0xRRGGBB format
     *
     * @return network color
     */
    int getNetworkColor();

    void setNetworkColor(int color);

    UUID getNetworkOwner();

    NetworkStatistics getNetworkStatistics();

    List<NetworkMember> getNetworkMembers();

    List<IFluxDevice> getAllDevices();

    /*@Deprecated
    default void onStartServerTick() {

    }*/

    default void onEndServerTick() {

    }

    default void onDeleted() {

    }

    @Nonnull
    AccessType getPlayerAccess(PlayerEntity player);

    /*@Deprecated
    default void addNewMember(String name) {

    }

    @Deprecated
    default void removeMember(UUID uuid) {

    }*/

    /**
     * Get all online devices with given logic type
     *
     * @param type logic type
     * @param <T> device type
     * @return a list of online devices
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
