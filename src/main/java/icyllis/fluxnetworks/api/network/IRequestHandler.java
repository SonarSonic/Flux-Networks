package icyllis.fluxnetworks.api.network;

import icyllis.fluxnetworks.api.tile.IFluxTile;

public interface IRequestHandler {

    void tick();

    void onRemoved();

    void queueConnectionAddition(IFluxTile tile);

    void queueConnectionRemoval(IFluxTile tile, boolean isChunkUnload);
}
