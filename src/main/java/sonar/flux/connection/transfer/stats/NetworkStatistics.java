package sonar.flux.connection.transfer.stats;

import net.minecraft.nbt.NBTTagCompound;
import sonar.core.api.nbt.INBTSyncable;
import sonar.core.helpers.NBTHelper.SyncType;
import sonar.flux.api.network.FluxCache;
import sonar.flux.api.tiles.IFluxListenable;
import sonar.flux.api.tiles.IFluxStorage;
import sonar.flux.connection.FluxNetworkBase;
import sonar.flux.connection.FluxNetworkServer;
import sonar.flux.connection.transfer.handlers.ConnectionTransferHandler;

import java.util.List;

public class NetworkStatistics implements INBTSyncable {

	public final FluxNetworkBase network;
	public long network_energy;
	public long network_energy_change;
	public long network_energy_capacity;
	public long total_energy_added;
	public long total_energy_removed;

	public int block_connection_count;
	public int flux_connection_count;
	public int flux_plug_count;
	public int flux_point_count;
	public int flux_storage_count;
	public int flux_controller_count;

	public NetworkStatistics(FluxNetworkBase network) {
		this.network = network;
	}

	public void onStartServerTick() {
		FluxNetworkServer network = ((FluxNetworkServer) this.network);
		List<IFluxListenable> connections = network.getConnections(FluxCache.flux);
		connections.forEach(flux -> flux.getTransferHandler().onStartServerTick());
	}

	public void onEndWorldTick() {
		FluxNetworkServer network = ((FluxNetworkServer) this.network);
		List<IFluxListenable> connections = network.getConnections(FluxCache.flux);
		if (network.hasGuiListeners()) {
			block_connection_count = 0;
			total_energy_added = 0;
			total_energy_removed = 0;
			connections.forEach(flux -> {
				flux.getTransferHandler().onEndWorldTick(); // very important to make sure transfer counters are reset
				if (flux.getTransferHandler() instanceof ConnectionTransferHandler) {
					block_connection_count += flux.getTransferHandler().getTransfers().size();
					total_energy_added += flux.getTransferHandler().getAdded();
					total_energy_removed += flux.getTransferHandler().getRemoved();
				}
			});

			//// STORAGE STATISTICS \\\\
			List<IFluxStorage> storage = network.getConnections(FluxCache.storage);
			long lastEnergy = network_energy;
			network_energy = 0;
			network_energy_capacity = 0;
			for (IFluxStorage s : storage) {
				network_energy += s.getEnergyStored();
				network_energy_capacity += s.getMaxEnergyStored();
			}
			network_energy_change = network_energy - lastEnergy;
			//// CONNECTION COUNT STATISTICS \\\\
			flux_connection_count = network.getConnections(FluxCache.flux).size();
			flux_plug_count = network.getConnections(FluxCache.plug).size();
			flux_point_count = network.getConnections(FluxCache.point).size();
			flux_controller_count = network.getConnections(FluxCache.controller).size();
			flux_storage_count = storage.size();
			
			//storages implements IFluxPlug and Point, so need to be removed from this count
			flux_plug_count -= flux_storage_count;
			flux_point_count -= (flux_storage_count + flux_controller_count);
		}else{
			connections.forEach(flux -> flux.getTransferHandler().onEndWorldTick());
		}
	}

	@Override
	public void readData(NBTTagCompound nbt, SyncType type) {
		network_energy = nbt.getLong("l1");
		network_energy_capacity = nbt.getLong("l2");
		network_energy_change = nbt.getLong("l3");
		total_energy_added = nbt.getLong("l4");
		total_energy_removed = nbt.getLong("l5");

		block_connection_count = nbt.getInteger("i1");
		flux_connection_count = nbt.getInteger("i2");
		flux_plug_count = nbt.getInteger("i3");
		flux_point_count = nbt.getInteger("i4");
		flux_storage_count = nbt.getInteger("i5");
		flux_controller_count = nbt.getInteger("i6");
	}

	@Override
	public NBTTagCompound writeData(NBTTagCompound nbt, SyncType type) {
		long[] write_long = new long[] { network_energy, network_energy_capacity, network_energy_change, total_energy_added, total_energy_removed };
		for (int i = 0; i < write_long.length; i++) {
			nbt.setLong("l" + (i + 1), write_long[i]);
		}

		int[] write_int = new int[] { block_connection_count, flux_connection_count, flux_plug_count, flux_point_count, flux_storage_count, flux_controller_count };
		for (int i = 0; i < write_int.length; i++) {
			nbt.setInteger("i" + (i + 1), write_int[i]);
		}
		return nbt;
	}
}
