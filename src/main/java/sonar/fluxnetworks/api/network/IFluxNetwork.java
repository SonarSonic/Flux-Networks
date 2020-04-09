package sonar.fluxnetworks.api.network;

import com.google.common.collect.Lists;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import sonar.fluxnetworks.api.tiles.IFluxConnector;
import sonar.fluxnetworks.api.utils.NBTType;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IFluxNetwork {

    default int getNetworkID(){
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

    default void onRemoved() {}

    default EnumAccessType getMemberPermission(PlayerEntity player) {
        return EnumAccessType.NONE;
    }

    default void addNewMember(String name) {}

    default void removeMember(UUID uuid) {}

    default <T extends IFluxConnector> List<T> getConnections(FluxCacheTypes<T> type) {return Lists.newArrayList();}

    default Optional<NetworkMember> getValidMember(UUID player) {return Optional.empty();}

    default void queueConnectionAddition(IFluxConnector flux) {}

    default void queueConnectionRemoval(IFluxConnector flux, boolean chunkUnload) {}

    default boolean isInvalid() {return false;}

    void readNetworkNBT(CompoundNBT nbt, NBTType type);

    CompoundNBT writeNetworkNBT(CompoundNBT nbt, NBTType type);

}
