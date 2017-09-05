package sonar.flux.api.network;

import sonar.core.api.nbt.INBTSyncable;
import sonar.flux.api.tiles.IFlux.ConnectionType;

import java.util.ArrayList;

public interface INetworkStatistics extends INBTSyncable {

    int getConnectionCount(ConnectionType type);

    EnergyStats getLatestStats();

    ArrayList<EnergyStats> getRecordedStats();

    EnergyStats getCurrentStats();
}
