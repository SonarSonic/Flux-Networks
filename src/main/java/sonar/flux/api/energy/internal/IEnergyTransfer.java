package sonar.flux.api.energy.internal;

import sonar.core.api.energy.EnergyType;
import sonar.core.api.utils.ActionType;

public interface IEnergyTransfer extends IFluxTransfer {

	long addToNetwork(long add, ActionType actionType);

	long removeFromNetwork(long remove, ActionType actionType);

	default long addToNetworkWithConvert(long add, EnergyType energyType, ActionType actionType) {
		long convert = EnergyType.convert(add, energyType, getEnergyType());
		long added = addToNetwork(convert, actionType);
		long deconvert = EnergyType.convert(added, getEnergyType(), energyType);
		return deconvert;
	}

	default long removeFromNetworkWithConvert(long remove, EnergyType energyType, ActionType actionType) {
		long convert = EnergyType.convert(remove, energyType, getEnergyType());
		long removed = removeFromNetwork(convert, actionType);
		long deconvert = EnergyType.convert(removed, getEnergyType(), energyType);
		return deconvert;
	}
}
