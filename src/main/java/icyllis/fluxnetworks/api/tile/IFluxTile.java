package icyllis.fluxnetworks.api.tile;

import icyllis.fluxnetworks.api.network.IFluxNetwork;
import icyllis.fluxnetworks.api.network.INetworkConnector;
import icyllis.fluxnetworks.api.util.INetworkNBT;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.UUID;

public interface IFluxTile extends INetworkNBT, INetworkConnector {

    ConnectionType getConnectionType();

    ITransferHandler getTransferHandler();

    UUID getConnectionOwner();

    String getCustomName();

    int getLogicalPriority();

    int getActualPriority();

    long getLogicalLimit();

    long getActualLimit();

    default long getBuffer() {
        return getTransferHandler().getEnergyStored();
    }

    default long getChange() {
        return getTransferHandler().getEnergyChange();
    }

    default long getMaxTransferLimit() {
        return Long.MAX_VALUE;
    }

    boolean isDisableLimit();

    boolean isSurgeMode();

    boolean isChunkLoaded();

    boolean isForcedLoading();

    boolean canAccess(PlayerEntity player);

    void connect(IFluxNetwork network);

    void disconnect(IFluxNetwork network);

    World getWorld();

    BlockPos getPos();

}
