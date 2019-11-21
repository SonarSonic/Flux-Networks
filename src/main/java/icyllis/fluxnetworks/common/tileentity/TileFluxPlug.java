package icyllis.fluxnetworks.common.tileentity;

import icyllis.fluxnetworks.api.tile.ConnectionType;
import icyllis.fluxnetworks.api.tile.ITransferHandler;
import icyllis.fluxnetworks.system.registry.RegistryTiles;
import net.minecraft.util.Direction;

public class TileFluxPlug extends TileFluxConnection {

    public TileFluxPlug() {
        super(RegistryTiles.FLUX_PLUG);
    }

    @Override
    public ConnectionType getConnectionType() {
        return ConnectionType.PLUG;
    }

    @Override
    public long addPhantomEnergyToNetwork(Direction side, long amount, boolean simulate) {
        return handler.addPhantomEnergyToNetwork(amount, side, simulate);
    }
}
