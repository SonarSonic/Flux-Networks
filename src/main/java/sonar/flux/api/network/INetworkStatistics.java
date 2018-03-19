package sonar.flux.api.network;

import java.util.List;

import sonar.core.api.nbt.INBTSyncable;
import sonar.flux.api.tiles.IFlux.ConnectionType;

public interface INetworkStatistics extends INBTSyncable {

    int getConnectionCount(ConnectionType type);

    EnergyStats getLatestStats();

    List<EnergyStats> getRecordedStats();

    EnergyStats getCurrentStats();
}
