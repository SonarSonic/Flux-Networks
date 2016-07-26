package sonar.flux.api;

import java.util.ArrayList;

public interface INetworkStatistics {

	public int getPlugCount();
	
	public int getPointCount();
	
	public EnergyStats getLatestStats();
	
	public ArrayList<EnergyStats> getRecordedStats();
	
}
