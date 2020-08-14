package sonar.fluxnetworks.api.network;

import com.google.common.collect.Lists;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import sonar.fluxnetworks.api.tiles.IFluxDevice;
import sonar.fluxnetworks.api.utils.NBTType;

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
    default void onStartServerTick() {}

    default void onEndServerTick() {}

    default void onDeleted() {}

    default EnumAccessType getMemberPermission(PlayerEntity player) {
        return EnumAccessType.NONE;
    }

    @Deprecated
    default void addNewMember(String name) {}

    @Deprecated
    default void removeMember(UUID uuid) {}

    default <T extends IFluxDevice> List<T> getConnections(FluxCacheType<T> type) {
        return Lists.newArrayList();
    }

    default Optional<NetworkMember> getValidMember(UUID player) {
        return Optional.empty();
    }

    /* Server only */
    default void queueConnectionAddition(IFluxDevice flux) {
    }

    /* Server only */
    default void queueConnectionRemoval(IFluxDevice flux, boolean chunkUnload) {
    }

    default boolean isInvalid() {
        return false;
    }

    void readNetworkNBT(CompoundNBT nbt, NBTType type);

    void writeNetworkNBT(CompoundNBT nbt, NBTType type);

}
