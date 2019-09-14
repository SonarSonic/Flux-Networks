package fluxnetworks.common.tileentity;

import fluxnetworks.api.ConnectionType;
import fluxnetworks.api.tileentity.IFluxPoint;

public class TileFluxPoint extends TileFluxConnector implements IFluxPoint {

    public TileFluxPoint() {
        customName = "Flux Point";
    }

    @Override
    public ConnectionType getConnectionType() {
        return ConnectionType.POINT;
    }

}
