package fluxnetworks.common.tileentity;

import fluxnetworks.api.tileentity.IFluxPlug;
import fluxnetworks.common.registry.RegistryBlocks;
import net.minecraft.item.ItemStack;

public class TileFluxPlug extends TileFluxConnector implements IFluxPlug {

    public TileFluxPlug() {
        customName = "Flux Plug";
    }

    @Override
    public ConnectionType getConnectionType() {
        return ConnectionType.PLUG;
    }

    @Override
    public ItemStack getDisplayStack() {
        return new ItemStack(RegistryBlocks.FLUX_PLUG);
    }

}
