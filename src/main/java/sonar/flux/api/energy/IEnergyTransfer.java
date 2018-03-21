package sonar.flux.api.energy;

import sonar.core.api.energy.EnergyType;
import sonar.core.api.utils.ActionType;

public interface IEnergyTransfer extends IFluxTransfer {

	long addToNetwork(long maxTransferRF, EnergyType energyType, ActionType actionType);
	
	long removeFromNetwork(long maxTransferRF, EnergyType energyType, ActionType actionType);
	
}
