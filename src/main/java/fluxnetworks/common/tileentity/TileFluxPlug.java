package fluxnetworks.common.tileentity;

import fluxnetworks.api.ConnectionType;
import fluxnetworks.api.tileentity.IFluxPlug;

public class TileFluxPlug extends TileFluxConnector implements IFluxPlug {

    public TileFluxPlug() {
        customName = "Flux Plug";
    }

    @Override
    public ConnectionType getConnectionType() {
        return ConnectionType.PLUG;
    }

}
