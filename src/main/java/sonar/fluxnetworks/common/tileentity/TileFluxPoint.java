package sonar.fluxnetworks.common.tileentity;

import sonar.fluxnetworks.api.network.EnumConnectionType;
import sonar.fluxnetworks.api.tiles.IFluxPoint;
import sonar.fluxnetworks.common.registry.RegistryBlocks;

public class TileFluxPoint extends TileFluxConnector implements IFluxPoint {

    public TileFluxPoint() {
        super(RegistryBlocks.FLUX_POINT_TILE);
        customName = "Flux Point";
    }

    @Override
    public EnumConnectionType getConnectionType() {
        return EnumConnectionType.POINT;
    }



}
