package sonar.flux.connection;

import sonar.core.handlers.energy.EnergyTransferHandler;

public class FNEnergyTransferHandler extends EnergyTransferHandler {

    public FNEnergyTransferHandler() {
        super(new FNEnergyTransferProxy());
    }

}
