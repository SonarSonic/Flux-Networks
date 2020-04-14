package sonar.fluxnetworks.common.connection.handler;

import sonar.fluxnetworks.api.network.IFluxTransfer;
import sonar.fluxnetworks.common.connection.transfer.ControllerTransfer;
import sonar.fluxnetworks.common.tileentity.TileFluxController;

public class FluxControllerHandler extends AbstractPointHandler<TileFluxController> {

    public ControllerTransfer transfer;

    public FluxControllerHandler(TileFluxController fluxController) {
        super(fluxController);
        transfer = new ControllerTransfer(fluxController);
    }

    @Override
    public void onStartCycle() {
        super.onStartCycle();
        transfer.onStartCycle();
    }

    @Override
    public void onEndCycle() {
        super.onEndCycle();
        transfer.onEndCycle();
    }

    @Override
    public long removeEnergy(long energy, boolean simulate) {
        return transfer.addEnergy(energy, simulate);
    }

}