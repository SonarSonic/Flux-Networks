package sonar.fluxnetworks.common.tileentity;

import sonar.fluxnetworks.FluxConfig;
import sonar.fluxnetworks.api.device.IFluxPlug;
import sonar.fluxnetworks.api.network.FluxDeviceType;
import sonar.fluxnetworks.api.network.ITransferHandler;
import sonar.fluxnetworks.common.connection.transfer.FluxPlugHandler;
import sonar.fluxnetworks.common.registry.RegistryBlocks;

import javax.annotation.Nonnull;

public class TileFluxPlug extends TileFluxConnector implements IFluxPlug {

    public final FluxPlugHandler handler = new FluxPlugHandler(this);

    public TileFluxPlug() {
        super(RegistryBlocks.FLUX_PLUG_TILE, "Flux Plug", FluxConfig.defaultLimit);
    }

    @Override
    public FluxDeviceType getDeviceType() {
        return FluxDeviceType.PLUG;
    }

    @Nonnull
    @Override
    public ITransferHandler getTransferHandler() {
        return handler;
    }
}
