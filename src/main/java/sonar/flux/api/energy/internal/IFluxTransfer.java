package sonar.flux.api.energy.internal;

import net.minecraft.item.ItemStack;
import sonar.core.api.energy.EnergyType;
import sonar.core.api.nbt.INBTSyncable;

public interface IFluxTransfer extends INBTSyncable {

	void onStartServerTick();
	
	void onEndWorldTick();

	void addedToNetwork(long add, EnergyType energyType);
	
	void removedFromNetwork(long remove, EnergyType energyType);
		
	EnergyType getEnergyType();
	
	ItemStack getDisplayStack();
	
	default boolean isInvalid(){
		return false;
	}
	
}
