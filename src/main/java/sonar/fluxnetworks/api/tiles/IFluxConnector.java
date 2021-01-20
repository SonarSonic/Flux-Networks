package sonar.fluxnetworks.api.tiles;

import sonar.fluxnetworks.api.network.ConnectionType;
import sonar.fluxnetworks.api.utils.Coord4D;
import sonar.fluxnetworks.api.network.INetworkConnector;
import sonar.fluxnetworks.api.network.IFluxNetwork;
import sonar.fluxnetworks.api.network.ITransferHandler;
import sonar.fluxnetworks.common.core.FluxGuiStack;
import sonar.fluxnetworks.api.utils.NBTType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import java.util.UUID;

/**
 * Extended by IFluxPoint and IFluxPlug
 */
public interface IFluxConnector extends INetworkConnector {

    NBTTagCompound writeCustomNBT(NBTTagCompound tag, NBTType type);

    void readCustomNBT(NBTTagCompound tag, NBTType type);

    int getLogicPriority();

    int getRawPriority(); // ignore surge

    UUID getConnectionOwner();

    ConnectionType getConnectionType();

    boolean canAccess(EntityPlayer player);

    long getLogicLimit();

    long getRawLimit(); // ignore disable limit

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

    void connect(IFluxNetwork network);

    void disconnect(IFluxNetwork network);

    ITransferHandler getTransferHandler();

    World getFluxWorld();

    Coord4D getCoords();

    int getFolderID();

    String getCustomName();

    boolean getDisableLimit();

    boolean getSurgeMode();

    /**
     * Transfer handler is unavailable on client, this method is mainly used for gui display on client
     * If this device is storage, this method returns the energy stored of it
     *
     * @return internal buffer or energy stored
     */
    long getTransferBuffer();

    long getTransferChange();

    default void setChunkLoaded(boolean chunkLoaded) {}

    default ItemStack getDisplayStack() {
        switch (getConnectionType()) {
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
