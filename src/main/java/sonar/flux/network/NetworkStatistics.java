package sonar.flux.network;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import net.minecraft.nbt.NBTTagCompound;
import sonar.core.api.nbt.INBTSyncable;
import sonar.core.helpers.NBTHelper;
import sonar.core.helpers.NBTHelper.SyncType;
import sonar.core.network.sync.ISyncPart;
import sonar.core.network.sync.SyncTagType;
import sonar.flux.api.EnergyStats;
import sonar.flux.api.IFlux;
import sonar.flux.api.INetworkStatistics;
import sonar.flux.api.IFlux.ConnectionType;

public class NetworkStatistics implements INBTSyncable, INetworkStatistics {

	public static final int keep = 15;// how many receive/send values to keep
	public static final int updateEvery = 20 * 5; // 5 second update time for graphing purposes

	public SyncTagType.INT pointCount = new SyncTagType.INT(0);
	public SyncTagType.INT plugCount = new SyncTagType.INT(1);
	public SyncTagType.INT storageCount = new SyncTagType.INT(2);
	public EnergyStats previousRecords = new EnergyStats(0, 0, 0);
	public EnergyStats latestRecords = new EnergyStats(0, 0, 0);

	/** public SyncTagType.LONG lastSend = new SyncTagType.LONG(2); public SyncTagType.LONG currentSend = new SyncTagType.LONG(3); public SyncTagType.LONG lastReceive = new SyncTagType.LONG(4); public SyncTagType.LONG currentReceive = new SyncTagType.LONG(5); public SyncTagType.LONG lastMaxSend = new SyncTagType.LONG(6); public SyncTagType.LONG currentMaxSend = new SyncTagType.LONG(7); public SyncTagType.LONG lastMaxReceive = new SyncTagType.LONG(8); public SyncTagType.LONG currentMaxReceive = new SyncTagType.LONG(9); */
	public final ArrayList<EnergyStats> records = new ArrayList<EnergyStats>();
	public int ticks = 0;

	public ArrayList<ISyncPart> parts = new ArrayList<ISyncPart>();
	{
		parts.addAll(Arrays.asList(pointCount, plugCount, storageCount));
	}
	
	public void inputStatistics(EnergyStats stats, HashMap<ConnectionType, ArrayList<IFlux>> connections) {
		plugCount.setObject(connections.getOrDefault(ConnectionType.PLUG, new ArrayList()).size());
		pointCount.setObject(connections.getOrDefault(ConnectionType.POINT, new ArrayList()).size());
		storageCount.setObject(connections.getOrDefault(ConnectionType.STORAGE, new ArrayList()).size());
		if (previousRecords != null && (ticks >= updateEvery || records.isEmpty())) {
			ticks = 0;
			records.add(new EnergyStats(previousRecords.transfer, previousRecords.maxSent, previousRecords.maxReceived));
			if (records.size() > keep) {
				records.remove(0);
			}
		} else {
			ticks++;
		}
		previousRecords = new EnergyStats(latestRecords.transfer, latestRecords.maxSent, latestRecords.maxReceived);
		latestRecords = stats;
	}

	@Override
	public void readData(NBTTagCompound nbt, SyncType type) {
		NBTHelper.readSyncParts(nbt, type, parts);
		previousRecords.readData(nbt, type);
	}

	@Override
	public NBTTagCompound writeData(NBTTagCompound nbt, SyncType type) {
		NBTHelper.writeSyncParts(nbt, type, parts, false);
		previousRecords.writeData(nbt, type);
		return nbt;
	}
	@Override
	public EnergyStats getLatestStats() {
		return previousRecords;
	}

	@Override
	public ArrayList<EnergyStats> getRecordedStats() {
		return records;
	}

	@Override
	public int getConnectionCount(ConnectionType type) {
		switch(type){
		case PLUG:
			return plugCount.getObject();
		case POINT:
			return pointCount.getObject();
		case STORAGE:
			return storageCount.getObject();
		default:
			break;		
		}
		return 0;
	}

}
