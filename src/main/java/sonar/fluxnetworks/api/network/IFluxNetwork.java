package sonar.fluxnetworks.api.network;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import sonar.fluxnetworks.api.device.IFluxDevice;
import sonar.fluxnetworks.api.misc.NBTType;

import javax.annotation.Nonnull;
import java.util.*;

public interface IFluxNetwork {

    default int getNetworkID() {
        return getSetting(NetworkSettings.NETWORK_ID);
    }

    default String getNetworkName() {
        return getSetting(NetworkSettings.NETWORK_NAME);
    }

    <T> T getSetting(NetworkSettings<T> setting);

    <T> void setSetting(NetworkSettings<T> settings, T value);

    /*@Deprecated
    default void onStartServerTick() {

    }*/

    default void onEndServerTick() {

    }

    default void onDeleted() {

    }

    @Nonnull
    EnumAccessType getAccessPermission(PlayerEntity player);

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

    Optional<NetworkMember> getNetworkMember(UUID player);

    /* Server only */
    void enqueueConnectionAddition(@Nonnull IFluxDevice device);

    /* Server only */
    void enqueueConnectionRemoval(@Nonnull IFluxDevice device, boolean chunkUnload);

    /**
     * Returns whether this network is a valid network.
     *
     * @return {@code true} if it is valid, {@code false} otherwise
     */
    boolean isValid();

    void readNetworkNBT(CompoundNBT nbt, NBTType type);

    void writeNetworkNBT(CompoundNBT nbt, NBTType type);
}
