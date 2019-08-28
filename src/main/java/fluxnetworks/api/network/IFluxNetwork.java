package fluxnetworks.api.network;

import fluxnetworks.api.MemberPermission;
import fluxnetworks.api.tileentity.IFluxConnector;
import fluxnetworks.common.connection.NetworkSettings;
import fluxnetworks.common.core.SyncType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

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

    default MemberPermission getMemberPermission(EntityPlayer player) {
        return MemberPermission.NONE;
    }

    default void queueConnectionAddition(IFluxConnector flux) {}

    default void queueConnectionRemoval(IFluxConnector flux, boolean chunkUnload) {}

    default boolean isInvalid() {return false;}

    void readNetworkNBT(NBTTagCompound nbt, SyncType type);

    NBTTagCompound writeNetworkNBT(NBTTagCompound nbt, SyncType type);

}
