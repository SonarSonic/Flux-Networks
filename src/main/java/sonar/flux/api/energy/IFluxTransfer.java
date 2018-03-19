package sonar.flux.api.energy;

import sonar.core.api.energy.EnergyType;
import sonar.core.api.nbt.INBTSyncable;

public interface IFluxTransfer extends INBTSyncable {

	void onStartServerTick();
	
	void onEndWorldTick();
	
	void addedToNetwork(long add);
	
	void removedFromNetwork(long remove);
	
	EnergyType getEnergyType();
	
	default boolean isInvalid(){
		return false;
	}
	
}
