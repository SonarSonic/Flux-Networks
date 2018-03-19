package sonar.flux.connection.transfer.stats;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import net.minecraft.nbt.NBTTagCompound;
import sonar.core.api.nbt.INBTSyncable;
import sonar.core.helpers.NBTHelper.SyncType;
import sonar.core.network.sync.DirtyPart;
import sonar.core.network.sync.IDirtyPart;
import sonar.core.network.sync.ISyncableListener;
import sonar.flux.api.network.FluxCache;
import sonar.flux.api.network.IFluxCommon;
import sonar.flux.api.network.IFluxNetwork;
import sonar.flux.api.tiles.IFlux;
import sonar.flux.api.tiles.IFluxListenable;

public class NetworkStatistics extends DirtyPart implements INBTSyncable, IDirtyPart {
	
	public final IFluxCommon network;
	
	public NetworkStatistics(IFluxCommon network){
		this.network = network;
	}
	
	public int plugCount, pointCount, storageCount;
	
	public void onStartServerTick(){
		List<IFluxListenable> connections = ((IFluxNetwork)network).getConnections(FluxCache.flux);
		connections.forEach(flux -> flux.getTransferHandler().onStartServerTick());		
	}
	
	public void onEndWorldTick() {
		List<IFluxListenable> connections = ((IFluxNetwork)network).getConnections(FluxCache.flux);
		connections.forEach(flux -> flux.getTransferHandler().onEndWorldTick());		
		//save and send
	}

	@Override
	public void readData(NBTTagCompound nbt, SyncType type) {
		
	}

	@Override
	public NBTTagCompound writeData(NBTTagCompound nbt, SyncType type) {
		return nbt;
	}
}
