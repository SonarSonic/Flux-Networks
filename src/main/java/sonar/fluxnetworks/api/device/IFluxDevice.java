package sonar.fluxnetworks.api.device;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.GlobalPos;
import sonar.fluxnetworks.api.network.*;
import sonar.fluxnetworks.api.misc.Coord4D;
import sonar.fluxnetworks.common.misc.FluxGuiStack;
import sonar.fluxnetworks.api.misc.NBTType;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.UUID;

/**
 * Defines a device that can be connected to a flux network
 */
///TODO remove common references
public interface IFluxDevice extends INetworkConnector {

    CompoundNBT writeCustomNBT(CompoundNBT tag, NBTType type);

    void readCustomNBT(CompoundNBT tag, NBTType type);

    int getLogicPriority(); // consider surge, for transfer on server

    int getRawPriority(); // ignore surge, for numeric display on client

    UUID getConnectionOwner();

    FluxDeviceType getDeviceType();

    boolean canAccess(PlayerEntity player);

    long getCurrentLimit();

    long getActualLimit(); // ignore disable limit

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
    void connect(IFluxNetwork network);

    /**
     * This method invoked by FluxNetworkServer.
     * To disconnect from any network, call getNetwork().enqueueConnectionRemoval()
     */
    void disconnect();

    ITransferHandler getTransferHandler();

    @Nonnull
    World getFluxWorld();

    @Deprecated
    default Coord4D getCoords() {
        throw new UnsupportedOperationException();
    }

    @Nonnull
    GlobalPos getGlobalPos();

    int getFolderID();

    String getCustomName();

    boolean getDisableLimit();

    boolean getSurgeMode();

    default long getBuffer() {
        return getTransferHandler().getBuffer();
    }

    default long getChange() {
        return getTransferHandler().getChange();
    }

    default void setChunkLoaded(boolean chunkLoaded) {}

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
