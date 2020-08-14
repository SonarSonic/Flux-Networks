package sonar.fluxnetworks.api.tiles;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import sonar.fluxnetworks.api.network.*;
import sonar.fluxnetworks.api.utils.Coord4D;
import sonar.fluxnetworks.common.core.FluxGuiStack;
import sonar.fluxnetworks.api.utils.NBTType;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.UUID;

/**
 * Defines a tile entity that can be connected to a flux network
 */
///TODO remove common references
public interface IFluxDevice extends INetworkConnector {

    CompoundNBT writeCustomNBT(CompoundNBT tag, NBTType type);

    void readCustomNBT(CompoundNBT tag, NBTType type);

    int getPriority();

    int getActualPriority(); // ignore surge

    UUID getConnectionOwner();

    EnumConnectionType getConnectionType();

    boolean canAccess(PlayerEntity player);

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

    @Nonnull
    World getFluxWorld();

    Coord4D getCoords();

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
