package sonar.fluxnetworks.api.network;

import com.google.common.collect.Lists;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import sonar.fluxnetworks.api.device.IFluxDevice;
import sonar.fluxnetworks.api.misc.NBTType;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IFluxNetwork {

    default int getNetworkID() {
        return getSetting(NetworkSettings.NETWORK_ID);
    }

    default String getNetworkName() {
        return getSetting(NetworkSettings.NETWORK_NAME);
    }

    <T> T getSetting(NetworkSettings<T> setting);

    <T> void setSetting(NetworkSettings<T> settings, T value);

    @Deprecated
    default void onStartServerTick() {
    }

    default void onEndServerTick() {
    }

    default void onDeleted() {
    }

    default EnumAccessType getMemberPermission(PlayerEntity player) {
        return EnumAccessType.NONE;
    }

    @Deprecated
    default void addNewMember(String name) {

    }

    @Deprecated
    default void removeMember(UUID uuid) {

    }

    default <T extends IFluxDevice> List<T> getConnections(FluxLogicType type) {
        return Lists.newArrayList();
    }

    default Optional<NetworkMember> getNetworkMember(UUID player) {
        return Optional.empty();
    }

    /* Server only */
    default void enqueueConnectionAddition(IFluxDevice flux) {
    }

    /* Server only */
    default void enqueueConnectionRemoval(IFluxDevice flux, boolean chunkUnload) {
    }

    /**
     * Returns whether this network is a valid network.
     *
     * @return {@code true} if it is valid, {@code false} otherwise
     */
    default boolean isValid() {
        return true;
    }

    void readNetworkNBT(CompoundNBT nbt, NBTType type);

    void writeNetworkNBT(CompoundNBT nbt, NBTType type);

}
