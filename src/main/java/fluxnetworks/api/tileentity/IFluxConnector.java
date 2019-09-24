package fluxnetworks.api.tileentity;

import fluxnetworks.api.ConnectionType;
import fluxnetworks.api.Coord4D;
import fluxnetworks.api.network.IFluxNetwork;
import fluxnetworks.api.network.ITransferHandler;
import fluxnetworks.common.core.FluxGuiStack;
import fluxnetworks.common.core.NBTType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import java.util.UUID;

/**
 * extended by IFluxPoint and IFluxPlug
 */
public interface IFluxConnector {

    NBTTagCompound writeCustomNBT(NBTTagCompound tag, NBTType type);

    void readCustomNBT(NBTTagCompound tag, NBTType type);

    int getNetworkID();

    int getPriority();

    int getActualPriority(); // ignore surge

    IFluxNetwork getNetwork();

    UUID getConnectionOwner();

    ConnectionType getConnectionType();

    boolean canAccess(EntityPlayer player);

    long getCurrentLimit();

    long getActualLimit(); // ignore disable limit

    default long getMaxTransferLimit() {
        return Long.MAX_VALUE;
    }

    boolean isActive();

    boolean isChunkLoaded();

    boolean isForcedLoading();

    void connect(IFluxNetwork network);

    void disconnect(IFluxNetwork network);

    ITransferHandler getTransferHandler();

    World getDimension();

    Coord4D getCoords();

    int getFolderID();

    String getCustomName();

    boolean getDisableLimit();

    boolean getSurgeMode();

    default long getBuffer() {
        return getTransferHandler().getEnergyStored();
    }

    default long getChange() {
        return getTransferHandler().getChange();
    }

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
