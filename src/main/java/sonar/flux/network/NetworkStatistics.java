package sonar.flux.network;

import java.util.ArrayList;
import java.util.HashMap;

import com.google.common.collect.Lists;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import sonar.core.api.nbt.INBTSyncable;
import sonar.core.helpers.NBTHelper;
import sonar.core.helpers.NBTHelper.SyncType;
import sonar.core.network.sync.DirtyPart;
import sonar.core.network.sync.IDirtyPart;
import sonar.core.network.sync.ISyncPart;
import sonar.core.network.sync.ISyncableListener;
import sonar.core.network.sync.SyncTagType;
import sonar.core.network.sync.SyncableList;
import sonar.flux.api.network.EnergyStats;
import sonar.flux.api.network.FluxCache;
import sonar.flux.api.network.INetworkStatistics;
import sonar.flux.api.tiles.IFlux;
import sonar.flux.api.tiles.IFlux.ConnectionType;
import sonar.flux.api.tiles.IFluxListenable;

public class NetworkStatistics extends DirtyPart implements INetworkStatistics, ISyncableListener, ISyncPart {

	public static final int keep = 15;// how many receive/send values to keep
	public static final int updateEvery = 20 * 5; // 5 second update time for graphing purposes

	public SyncTagType.INT pointCount = new SyncTagType.INT(0);
	public SyncTagType.INT plugCount = new SyncTagType.INT(1);
	public SyncTagType.INT storageCount = new SyncTagType.INT(2);
	public EnergyStats previousRecords = new EnergyStats(0, 0, 0);
	public EnergyStats latestRecords = new EnergyStats(0, 0, 0);

	/** public SyncTagType.LONG lastSend = new SyncTagType.LONG(2); public SyncTagType.LONG currentSend = new SyncTagType.LONG(3); public SyncTagType.LONG lastReceive = new SyncTagType.LONG(4); public SyncTagType.LONG currentReceive = new SyncTagType.LONG(5); public SyncTagType.LONG lastMaxSend = new SyncTagType.LONG(6); public SyncTagType.LONG currentMaxSend = new SyncTagType.LONG(7); public SyncTagType.LONG lastMaxReceive = new SyncTagType.LONG(8); public SyncTagType.LONG currentMaxReceive = new SyncTagType.LONG(9); */
	//public final ArrayList<EnergyStats> records = new ArrayList<EnergyStats>();
	public int ticks = 0;

	public SyncableList parts = new SyncableList(this);
	{
		parts.addParts(pointCount, plugCount, storageCount);
	}
	
	public void inputStatistics(EnergyStats stats, HashMap<FluxCache, ArrayList<IFluxListenable>> connections) {
		plugCount.setObject(connections.getOrDefault(FluxCache.plug, Lists.newArrayList()).size());
		pointCount.setObject(connections.getOrDefault(FluxCache.point, Lists.newArrayList()).size());
		storageCount.setObject(connections.getOrDefault(FluxCache.storage, Lists.newArrayList()).size());
		/*
		if (previousRecords != null && (ticks >= updateEvery || records.isEmpty())) {
			ticks = 0;
			//records.add(new EnergyStats(previousRecords.transfer, previousRecords.maxSent, previousRecords.maxReceived));
			//if (records.size() > keep) {
			//	records.remove(0);
			//}
		} else {
			ticks++;
		}
		*/
		previousRecords = new EnergyStats(latestRecords.transfer, latestRecords.maxSent, latestRecords.maxReceived);
		latestRecords = stats;
		
	}

	@Override
	public void readData(NBTTagCompound nbt, SyncType type) {
		NBTTagCompound tag = nbt.getCompoundTag(getTagName());
		NBTHelper.readSyncParts(tag, type, parts);		
		previousRecords.readData(nbt, type);
	}

	@Override
	public NBTTagCompound writeData(NBTTagCompound nbt, SyncType type) {
		NBTTagCompound tag = new NBTTagCompound();
		NBTHelper.writeSyncParts(tag, type, parts, false);
		nbt.setTag(getTagName(), tag);
		previousRecords.writeData(nbt, type);
		return nbt;
	}

	@Override
	public void writeToBuf(ByteBuf buf) {
		ByteBufUtils.writeTag(buf, this.writeData(new NBTTagCompound(), SyncType.SAVE));
	}

	@Override
	public void readFromBuf(ByteBuf buf) {
		readData(ByteBufUtils.readTag(buf), SyncType.SAVE);
	}
	
	@Override
	public EnergyStats getLatestStats() {
		return previousRecords;
	}

	@Override
	public ArrayList<EnergyStats> getRecordedStats() {
		return null;//records;
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

	@Override
	public void markChanged(IDirtyPart part) {
		parts.markSyncPartChanged(part);
		markChanged();
	}

	@Override
	public boolean canSync(SyncType sync) {
		return sync.isType(SyncType.SAVE, SyncType.DEFAULT_SYNC);
	}

	@Override
	public String getTagName() {
		return "nstats";
	}

	@Override
	public EnergyStats getCurrentStats() {
		return latestRecords;
	}

}
