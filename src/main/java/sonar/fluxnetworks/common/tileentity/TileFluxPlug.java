package sonar.fluxnetworks.common.tileentity;

import sonar.fluxnetworks.api.network.ConnectionType;
import sonar.fluxnetworks.api.network.ITransferHandler;
import sonar.fluxnetworks.api.tiles.IFluxPlug;
import sonar.fluxnetworks.common.connection.transfer.FluxPlugHandler;

public class TileFluxPlug extends TileFluxConnector implements IFluxPlug {

    private final FluxPlugHandler handler = new FluxPlugHandler(this);

    public TileFluxPlug() {
        customName = "Flux Plug";
    }

    @Override
    public ConnectionType getConnectionType() {
        return ConnectionType.PLUG;
    }

    @Override
    public ITransferHandler getTransferHandler() {
        return handler;
    }

    @Override
    public String getPeripheralName() {
        return "flux_plug";
    }
}
