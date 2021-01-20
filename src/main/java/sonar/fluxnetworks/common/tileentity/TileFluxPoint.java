package sonar.fluxnetworks.common.tileentity;

import sonar.fluxnetworks.api.network.ConnectionType;
import sonar.fluxnetworks.api.network.ITransferHandler;
import sonar.fluxnetworks.api.tiles.IFluxPoint;
import sonar.fluxnetworks.common.connection.transfer.FluxPointHandler;

import javax.annotation.Nonnull;

public class TileFluxPoint extends TileFluxConnector implements IFluxPoint {

    private final FluxPointHandler handler = new FluxPointHandler(this);

    public TileFluxPoint() {
        customName = "Flux Point";
    }

    @Override
    public ConnectionType getConnectionType() {
        return ConnectionType.POINT;
    }

    @Nonnull
    @Override
    public ITransferHandler getTransferHandler() {
        return handler;
    }

    @Override
    public String getPeripheralName() {
        return "flux_point";
    }
}
