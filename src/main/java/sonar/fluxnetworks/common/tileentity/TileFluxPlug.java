package sonar.fluxnetworks.common.tileentity;

import net.minecraft.util.Direction;
import sonar.fluxnetworks.FluxConfig;
import sonar.fluxnetworks.api.network.FluxDeviceType;
import sonar.fluxnetworks.api.network.ITransferHandler;
import sonar.fluxnetworks.api.device.IFluxPlug;
import sonar.fluxnetworks.common.connection.handler.FluxPlugHandler;
import sonar.fluxnetworks.common.registry.RegistryBlocks;

public class TileFluxPlug extends TileFluxConnector implements IFluxPlug {

    public final FluxPlugHandler handler = new FluxPlugHandler(this);

    public TileFluxPlug() {
        super(RegistryBlocks.FLUX_PLUG_TILE, "Flux Plug", FluxConfig.defaultLimit);
    }

    public long addPhantomEnergyToNetwork(Direction dir, long amount, boolean simulate) {
        return isActive() && getDeviceType().isPlug() ? handler.addEnergy(amount, dir, simulate) : 0;
    }

    @Override
    public FluxDeviceType getDeviceType() {
        return FluxDeviceType.PLUG;
    }

    @Override
    public ITransferHandler getTransferHandler() {
        return handler;
    }
}
