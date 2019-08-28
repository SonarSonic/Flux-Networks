package fluxnetworks.common.tileentity;

import fluxnetworks.api.network.ITransferHandler;
import fluxnetworks.common.registry.RegistryBlocks;
import net.minecraft.item.ItemStack;

public class TileController extends TileFluxCore {

    public TileController() {
        customName = "Flux Controller";
    }

    @Override
    public ConnectionType getConnectionType() {
        return ConnectionType.CONTROLLER;
    }

    @Override
    public ITransferHandler getTransferHandler() {
        return null;
    }

    @Override
    public ItemStack getDisplayStack() {
        return new ItemStack(RegistryBlocks.FLUX_CONTROLLER);
    }

}
