package fluxnetworks.api.network;

import fluxnetworks.api.AccessPermission;
import fluxnetworks.api.tileentity.IFluxConnector;
import fluxnetworks.common.connection.NetworkMember;
import fluxnetworks.common.connection.NetworkSettings;
import fluxnetworks.common.core.NBTType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

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

    default void onStartServerTick() {}

    default void onEndServerTick() {}

    default void onRemoved() {}

    default AccessPermission getMemberPermission(EntityPlayer player) {
        return AccessPermission.NONE;
    }

    default void addNewMember(String name) {}

    default void removeMember(UUID uuid) {}

    default Optional<NetworkMember> getValidMember(UUID player) {return Optional.empty();}

    default void queueConnectionAddition(IFluxConnector flux) {}

    default void queueConnectionRemoval(IFluxConnector flux, boolean chunkUnload) {}

    default boolean isInvalid() {return false;}

    void readNetworkNBT(NBTTagCompound nbt, NBTType type);

    NBTTagCompound writeNetworkNBT(NBTTagCompound nbt, NBTType type);

}
