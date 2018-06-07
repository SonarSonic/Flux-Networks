package sonar.flux.api.energy.internal;

import net.minecraft.util.EnumFacing;
import sonar.core.api.energy.EnergyType;
import sonar.core.api.utils.ActionType;

import java.util.List;

public interface ITransferHandler {

	void onStartServerTick();
	
	void onEndWorldTick();

	long getBuffer();
	
	long getAdded();
	
	long getRemoved();

    boolean hasTransfers();

    void updateTransfers(EnumFacing ...faces);

    List<IFluxTransfer> getTransfers();

	long addToNetwork(long maxTransferRF, EnergyType type, ActionType actionType);
	
	long removeFromNetwork(long maxTransferRF, EnergyType type, ActionType actionType);
}
