package fluxnetworks.common.tileentity;

import fluxnetworks.api.network.EnumConnectionType;
import fluxnetworks.api.tiles.IFluxPoint;

public class TileFluxPoint extends TileFluxConnector implements IFluxPoint {

    public TileFluxPoint() {
        customName = "Flux Point";
    }

    @Override
    public EnumConnectionType getConnectionType() {
        return EnumConnectionType.POINT;
    }

    @Override
    public String getPeripheralName() {
        return "flux_point";
    }
}
