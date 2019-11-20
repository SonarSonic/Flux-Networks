package icyllis.fluxnetworks.common.tileentity;

import icyllis.fluxnetworks.api.network.ConnectionType;
import icyllis.fluxnetworks.system.registry.RegistryTiles;

public class TileFluxPlug extends TileFluxCore {

    public TileFluxPlug() {
        super(RegistryTiles.FLUX_PLUG);
    }

    @Override
    public ConnectionType getConnectionType() {
        return ConnectionType.PLUG;
    }
}
