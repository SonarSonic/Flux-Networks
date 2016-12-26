package sonar.flux.api;

import java.util.ArrayList;

import sonar.flux.api.IFlux.ConnectionType;

public interface INetworkStatistics {

	public int getConnectionCount(ConnectionType type);
		
	public EnergyStats getLatestStats();
	
	public ArrayList<EnergyStats> getRecordedStats();
	
}
