package sonar.fluxnetworks.common.tileentity;

import sonar.fluxnetworks.api.network.FluxDeviceType;
import sonar.fluxnetworks.api.network.ITransferHandler;
import sonar.fluxnetworks.api.device.IFluxPoint;
import sonar.fluxnetworks.common.connection.handler.FluxPointHandler;
import sonar.fluxnetworks.common.registry.RegistryBlocks;

public class TileFluxPoint extends TileFluxConnector implements IFluxPoint {

    public final FluxPointHandler handler = new FluxPointHandler(this);

    public TileFluxPoint() {
        super(RegistryBlocks.FLUX_POINT_TILE);
        customName = "Flux Point";
    }

    @Override
    public FluxDeviceType getDeviceType() {
        return FluxDeviceType.POINT;
    }

    @Override
    public ITransferHandler getTransferHandler() {
        return handler;
    }

}