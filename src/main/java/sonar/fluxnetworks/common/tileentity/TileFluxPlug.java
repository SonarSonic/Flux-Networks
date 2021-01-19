package sonar.fluxnetworks.common.tileentity;

import sonar.fluxnetworks.api.network.EnumConnectionType;
import sonar.fluxnetworks.api.tiles.IFluxPlug;

public class TileFluxPlug extends TileFluxConnector implements IFluxPlug {

    public TileFluxPlug() {
        customName = "Flux Plug";
    }

    @Override
    public EnumConnectionType getConnectionType() {
        return EnumConnectionType.PLUG;
    }

    @Override
    public String getPeripheralName() {
        return "flux_plug";
    }
}
