package sonar.flux.api.energy;

import sonar.core.api.utils.ActionType;

public interface IEnergyTransfer extends IFluxTransfer {

	long addToNetwork(long maxTransferRF, ActionType actionType);
	
	long removeFromNetwork(long maxTransferRF, ActionType actionType);
	
}
