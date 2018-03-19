package sonar.flux.api.energy;

import java.util.List;

import sonar.core.api.utils.ActionType;

public interface ITransferHandler {

	void onStartServerTick();
	
	void onEndWorldTick();
	
	//void onNetworkChanged();

    boolean hasTransfers();

    void updateTransfers();

    List<IFluxTransfer> getTransfers();

	long addToNetwork(long maxTransferRF, ActionType actionType);
	
	long removeFromNetwork(long maxTransferRF, ActionType actionType);
}
