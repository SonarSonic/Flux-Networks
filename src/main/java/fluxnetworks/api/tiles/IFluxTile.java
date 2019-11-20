package fluxnetworks.api.tiles;

import fluxnetworks.api.network.ConnectionType;
import fluxnetworks.api.network.IFluxNetwork;
import fluxnetworks.api.network.INetworkConnector;
import fluxnetworks.api.utils.INetworkNBT;
import net.minecraft.entity.player.PlayerEntity;

import java.util.UUID;

public interface IFluxTile extends INetworkNBT, INetworkConnector {

    ConnectionType getConnectionType();

    UUID getConnectionOwner();

    String getCustomName();

    int getLogicalPriority();

    int getActualPriority();

    long getLogicalLimit();

    long getActualLimit();

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

}
