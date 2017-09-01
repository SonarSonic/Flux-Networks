package sonar.flux.api.network;

import java.util.ArrayList;

import sonar.core.api.nbt.INBTSyncable;
import sonar.flux.api.tiles.IFlux.ConnectionType;

public interface INetworkStatistics extends INBTSyncable {

	public int getConnectionCount(ConnectionType type);
	
	public EnergyStats getCurrentStats();
		
	public EnergyStats getLatestStats();
	
	public ArrayList<EnergyStats> getRecordedStats();
	
}
