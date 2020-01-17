package icyllis.fluxnetworks.api.network;

import icyllis.fluxnetworks.api.tile.IFluxTile;
import icyllis.fluxnetworks.api.util.INetworkNBT;

import java.util.List;

public interface IFluxNetwork extends INetworkNBT {

    default int getNetworkID() {
        return getNetworkSetting().getNetworkID();
    }

    INetworkSetting getNetworkSetting();

    INetworkTransfer getNetworkTransfer();

    IRequestHandler getRequestHandler();

    <T extends IFluxTile> List<T> getConnections(FluxCacheTypes<T> type);

    void tick();

    boolean isValid();

    void onRemoved();
}
