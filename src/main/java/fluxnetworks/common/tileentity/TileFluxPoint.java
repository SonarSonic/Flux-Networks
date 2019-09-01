package fluxnetworks.common.tileentity;

import fluxnetworks.api.tileentity.IFluxPoint;
import fluxnetworks.common.registry.RegistryBlocks;
import net.minecraft.item.ItemStack;

public class TileFluxPoint extends TileFluxConnector implements IFluxPoint {

    public TileFluxPoint() {
        customName = "Flux Point";
    }

    @Override
    public ConnectionType getConnectionType() {
        return ConnectionType.POINT;
    }

}
