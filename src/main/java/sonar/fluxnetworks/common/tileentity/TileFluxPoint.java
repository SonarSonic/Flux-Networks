package sonar.fluxnetworks.common.tileentity;

import sonar.fluxnetworks.FluxConfig;
import sonar.fluxnetworks.api.device.IFluxPoint;
import sonar.fluxnetworks.api.network.FluxDeviceType;
import sonar.fluxnetworks.api.network.ITransferHandler;
import sonar.fluxnetworks.common.connection.handler.FluxPointHandler;
import sonar.fluxnetworks.common.registry.RegistryBlocks;

public class TileFluxPoint extends TileFluxConnector implements IFluxPoint {

    public final FluxPointHandler handler = new FluxPointHandler(this);

    public TileFluxPoint() {
        super(RegistryBlocks.FLUX_POINT_TILE, "Flux Point", FluxConfig.defaultLimit);
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
