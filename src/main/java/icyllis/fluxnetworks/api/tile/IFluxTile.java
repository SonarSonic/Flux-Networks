package icyllis.fluxnetworks.api.tile;

import icyllis.fluxnetworks.api.network.ConnectionType;
import icyllis.fluxnetworks.api.network.IFluxNetwork;
import icyllis.fluxnetworks.api.network.INetworkConnector;
import icyllis.fluxnetworks.api.util.INetworkNBT;
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
