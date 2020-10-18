package sonar.fluxnetworks.common.connection.handler;

import sonar.fluxnetworks.common.connection.transfer.ControllerTransfer;
import sonar.fluxnetworks.common.tileentity.TileFluxController;

public class FluxControllerHandler extends BasicPointHandler<TileFluxController> {

    public ControllerTransfer transfer;

    public FluxControllerHandler(TileFluxController fluxController) {
        super(fluxController);
        transfer = new ControllerTransfer(fluxController);
    }

    @Override
    public void onCycleStart() {
        super.onCycleStart();
        transfer.onStartCycle();
    }

    @Override
    public void onCycleEnd() {
        super.onCycleEnd();
        transfer.onEndCycle();
    }

    @Override
    public long sendToConsumers(long energy, boolean simulate) {
        return transfer.addEnergy(energy, simulate);
    }
}