package sonar.fluxnetworks.common.tileentity;

import sonar.fluxnetworks.api.network.EnumConnectionType;
import sonar.fluxnetworks.api.network.ITransferHandler;
import sonar.fluxnetworks.api.tiles.IFluxController;
import sonar.fluxnetworks.api.tiles.IFluxEnergy;
import sonar.fluxnetworks.common.connection.transfer.ControllerTransfer;
import sonar.fluxnetworks.common.connection.handler.SingleTransferHandler;
import sonar.fluxnetworks.common.registry.RegistryBlocks;

public class TileFluxController extends TileFluxCore implements IFluxController, IFluxEnergy {

    public final SingleTransferHandler handler = new SingleTransferHandler(this, new ControllerTransfer(this));

    public TileFluxController() {
        super(RegistryBlocks.FLUX_CONTROLLER_TILE);
        customName = "Flux Controller";
    }

    @Override
    public EnumConnectionType getConnectionType() {
        return EnumConnectionType.CONTROLLER;
    }

    @Override
    public ITransferHandler getTransferHandler() {
        return handler;
    }

    @Override
    public long addEnergy(long amount, boolean simulate) {
        return 0;
    }

    @Override
    public long removeEnergy(long amount, boolean simulate) {
        return 0;
    }

    @Override
    public long getEnergy() {
        return 0;
    }
}
