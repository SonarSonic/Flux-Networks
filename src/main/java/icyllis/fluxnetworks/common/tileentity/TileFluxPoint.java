package icyllis.fluxnetworks.common.tileentity;

import icyllis.fluxnetworks.api.tile.ConnectionType;
import icyllis.fluxnetworks.system.registry.RegistryTiles;
import net.minecraft.util.Direction;

public class TileFluxPoint extends TileFluxConnector {

    public TileFluxPoint() {
        super(RegistryTiles.FLUX_POINT);
    }

    @Override
    public long addPhantomEnergyToNetwork(Direction side, long amount, boolean simulate) {
        return 0;
    }

    @Override
    public ConnectionType getConnectionType() {
        return ConnectionType.POINT;
    }
}
