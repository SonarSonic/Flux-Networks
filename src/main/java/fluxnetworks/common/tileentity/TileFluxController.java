package fluxnetworks.common.tileentity;

import fluxnetworks.api.network.ITransferHandler;
import fluxnetworks.api.tileentity.IFluxController;
import fluxnetworks.common.connection.ConnectionTransferHandler;
import fluxnetworks.common.registry.RegistryBlocks;
import net.minecraft.item.ItemStack;

public class TileFluxController extends TileFluxCore implements IFluxController {

    public final ConnectionTransferHandler handler = new ConnectionTransferHandler(this, this);

    public TileFluxController() {
        customName = "Flux Controller";
    }

    @Override
    public ConnectionType getConnectionType() {
        return ConnectionType.CONTROLLER;
    }

    @Override
    public ITransferHandler getTransferHandler() {
        return handler;
    }

}
