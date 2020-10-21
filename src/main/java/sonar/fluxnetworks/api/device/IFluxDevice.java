package sonar.fluxnetworks.api.device;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.world.World;
import sonar.fluxnetworks.api.network.FluxDeviceType;
import sonar.fluxnetworks.api.network.IFluxNetwork;
import sonar.fluxnetworks.api.network.INetworkConnector;
import sonar.fluxnetworks.api.network.ITransferHandler;

import javax.annotation.Nonnull;
import java.util.UUID;

/**
 * Defines a device that can be connected to a flux network
 */
public interface IFluxDevice extends INetworkConnector {

    void writeCustomNBT(CompoundNBT tag, int type);

    void readCustomNBT(CompoundNBT tag, int type);

    UUID getConnectionOwner();

    FluxDeviceType getDeviceType();

    boolean canPlayerAccess(PlayerEntity player);

    int getLogicPriority(); // consider surge, for transfer on server

    int getRawPriority(); // ignore surge, for numeric display on client

    void setPriority(int priority);

    long getLogicLimit(); // consider disable limit

    long getRawLimit(); // ignore disable limit

    void setTransferLimit(long limit);

    /**
     * If this device is storage, this method returns the max energy storage of it,
     * or Long.MAX_VALUE otherwise
     *
     * @return max transfer limit
     */
    long getMaxTransferLimit();

    boolean isActive();

    boolean isChunkLoaded();

    boolean isForcedLoading();

    void setForcedLoading(boolean forcedLoading);

    /**
     * This method invoked by FluxNetworkServer.
     * To connect to a network, call {@link IFluxNetwork#enqueueConnectionAddition(IFluxDevice)}
     *
     * @param network the network invoked this method
     */
    void onConnect(IFluxNetwork network);

    /**
     * This method invoked by FluxNetworkServer.
     * To disconnect from current network, call getNetwork().enqueueConnectionRemoval()
     */
    void onDisconnect();

    @Nonnull
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

    void setCustomName(String customName);

    boolean getDisableLimit();

    void setDisableLimit(boolean disableLimit);

    boolean getSurgeMode();

    void setSurgeMode(boolean surgeMode);

    /**
     * Transfer handler is unavailable on client, this method is mainly used for gui display on client
     * If this device is storage, this method returns the energy stored of it
     *
     * @return internal buffer or energy stored
     */
    long getTransferBuffer();

    long getTransferChange();

    @Nonnull
    ItemStack getDisplayStack();
}
