package sonar.fluxnetworks.common.tileentity;

import sonar.fluxnetworks.api.network.EnumConnectionType;
import sonar.fluxnetworks.api.tiles.IFluxPlug;
import sonar.fluxnetworks.common.registry.RegistryBlocks;

public class TileFluxPlug extends TileFluxConnector implements IFluxPlug {

    public TileFluxPlug() {
        super(RegistryBlocks.FLUX_PLUG_TILE);
        customName = "Flux Plug";
    }

    @Override
    public EnumConnectionType getConnectionType() {
        return EnumConnectionType.PLUG;
    }
}
