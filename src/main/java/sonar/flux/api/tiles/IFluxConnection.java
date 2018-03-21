package sonar.flux.api.tiles;

import net.minecraft.util.EnumFacing;
import sonar.core.api.energy.EnergyType;
import sonar.core.api.utils.ActionType;

public interface IFluxConnection extends IFlux {

	/**phantom power refers to energy which has been forced into the system*/
	public long addPhantomEnergyToNetwork(EnumFacing from, long max_add, EnergyType energy_type, ActionType type);

	public long removePhantomEnergyFromNetwork(EnumFacing from, long max_remove, EnergyType energy_type, ActionType type);
	
}
