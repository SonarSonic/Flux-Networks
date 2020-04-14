package sonar.fluxnetworks.common.tileentity;

import net.minecraft.util.Direction;
import sonar.fluxnetworks.api.network.EnumConnectionType;
import sonar.fluxnetworks.api.network.ITransferHandler;
import sonar.fluxnetworks.api.tiles.IFluxPlug;
import sonar.fluxnetworks.common.connection.handler.FluxControllerHandler;
import sonar.fluxnetworks.common.connection.handler.FluxPlugHandler;
import sonar.fluxnetworks.common.registry.RegistryBlocks;

public class TileFluxPlug extends TileFluxConnector implements IFluxPlug {

    public final FluxPlugHandler handler = new FluxPlugHandler(this);

    public TileFluxPlug() {
        super(RegistryBlocks.FLUX_PLUG_TILE);
        customName = "Flux Plug";
    }

    public long addPhantomEnergyToNetwork(Direction dir, long amount, boolean simulate) {
        return isActive() && getConnectionType().canAddEnergy() ? handler.addEnergy(amount, dir, simulate) : 0;
    }

    @Override
    public EnumConnectionType getConnectionType() {
        return EnumConnectionType.PLUG;
    }

    @Override
    public ITransferHandler getTransferHandler() {
        return handler;
    }
}
