package sonar.fluxnetworks.api.device;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.world.World;
import sonar.fluxnetworks.api.misc.NBTType;
import sonar.fluxnetworks.api.network.FluxDeviceType;
import sonar.fluxnetworks.api.network.IFluxNetwork;
import sonar.fluxnetworks.api.network.INetworkConnector;
import sonar.fluxnetworks.api.network.ITransferHandler;
import sonar.fluxnetworks.common.misc.FluxGuiStack;

import javax.annotation.Nonnull;
import java.util.UUID;

/**
 * Defines a device that can be connected to a flux network
 */
public interface IFluxDevice extends INetworkConnector {

    //TODO
    CompoundNBT writeCustomNBT(CompoundNBT tag, NBTType type);

    void readCustomNBT(CompoundNBT tag, NBTType type);

    int getLogicPriority(); // consider surge, for transfer on server

    int getRawPriority(); // ignore surge, for numeric display on client

    UUID getConnectionOwner();

    FluxDeviceType getDeviceType();

    boolean canPlayerAccess(PlayerEntity player);

    long getLogicLimit(); // consider disable limit

    long getRawLimit(); // ignore disable limit

    default long getMaxTransferLimit() {
        return Long.MAX_VALUE;
    }

    boolean isActive();

    boolean isChunkLoaded();

    boolean isForcedLoading();

    /**
     * This method invoked in network queue handling.
     * To connect to a network, call network.enqueueConnectionAddition()
     *
     * @param network the network invoked this method
     */
    void onConnect(IFluxNetwork network);

    /**
     * This method invoked by FluxNetworkServer.
     * To disconnect from any network, call getNetwork().enqueueConnectionRemoval()
     */
    void onDisconnect();

    ITransferHandler getTransferHandler();

    @Nonnull
    World getFluxWorld();

    /*@Deprecated
    default Coord4D getCoords() {
        throw new IllegalStateException();
    }*/

    @Nonnull
    GlobalPos getGlobalPos();

    /*int getFolderID();*/

    String getCustomName();

    boolean getDisableLimit();

    boolean getSurgeMode();

    default long getBuffer() {
        return getTransferHandler().getBuffer();
    }

    default long getChange() {
        return getTransferHandler().getChange();
    }

    default void setChunkLoaded(boolean chunkLoaded) {
    }

    default ItemStack getDisplayStack() {
        switch (getDeviceType()) {
            case POINT:
                return FluxGuiStack.FLUX_POINT;
            case PLUG:
                return FluxGuiStack.FLUX_PLUG;
            case CONTROLLER:
                return FluxGuiStack.FLUX_CONTROLLER;
            default:
                return ItemStack.EMPTY;
        }
    }
}
