package fluxnetworks.common.tileentity;

import fluxnetworks.api.network.ConnectionType;
import fluxnetworks.system.registry.RegistryTiles;

public class TileFluxPlug extends TileFluxCore {

    public TileFluxPlug() {
        super(RegistryTiles.FLUX_PLUG);
    }

    @Override
    public ConnectionType getConnectionType() {
        return ConnectionType.PLUG;
    }
}
