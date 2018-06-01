package sonar.flux.api.energy.internal;

import sonar.core.api.energy.EnergyType;
import sonar.core.api.utils.ActionType;
import sonar.flux.FluxNetworks;

public interface IEnergyTransfer extends IFluxTransfer {

	long addToNetwork(long add, ActionType actionType);

	long removeFromNetwork(long remove, ActionType actionType);

	default long addToNetworkWithConvert(long add, EnergyType energyType, ActionType actionType) {
		return FluxNetworks.TRANSFER_HANDLER.convertedAction(add, energyType, getEnergyType(), s -> addToNetwork(s, actionType));
	}

	default long removeFromNetworkWithConvert(long remove, EnergyType energyType, ActionType actionType) {
		return FluxNetworks.TRANSFER_HANDLER.convertedAction(remove, energyType, getEnergyType(), s -> removeFromNetwork(s, actionType));
	}
}
