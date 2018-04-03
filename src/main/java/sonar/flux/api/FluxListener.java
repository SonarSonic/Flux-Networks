package sonar.flux.api;

import java.util.List;

import net.minecraft.nbt.NBTTagCompound;
import sonar.core.SonarCore;
import sonar.core.helpers.NBTHelper.SyncType;
import sonar.core.network.PacketTileSync;
import sonar.flux.FluxNetworks;
import sonar.flux.api.network.IFluxNetwork;
import sonar.flux.api.tiles.IFluxListenable;
import sonar.flux.common.tileentity.TileFlux;
import sonar.flux.connection.BasicFluxNetwork;
import sonar.flux.network.FluxNetworkCache;
import sonar.flux.network.PacketFluxConnectionsList;
import sonar.flux.network.PacketFluxNetworkList;
import sonar.flux.network.PacketNetworkStatistics;

public enum FluxListener {

	ADMIN((network, listeners) -> {}),
	// sync main flux properties
	SYNC_INDEX(FluxListener::sendNetworkIndex),
	// sync current networks statistics
	SYNC_NETWORK_LIST(FluxListener::sendNetworkList),
	// sync current networks connections
	SYNC_NETWORK_CONNECTIONS(FluxListener::sendConnectionPackets),
	// sync network stats
	SYNC_NETWORK_STATS(FluxListener::sendConnectionStatistics),
	// sync all connected players
	SYNC_PLAYERS(FluxListener::sendPlayerList);
	
	public IPacketAction action;
	
	FluxListener(IPacketAction action){
		this.action = action;
	}

	public void sendPackets(BasicFluxNetwork network, List<IFluxListenable> flux_listeners){
		action.sendPackets(network, flux_listeners);
	}
	
	public static void sendNetworkIndex(BasicFluxNetwork network, List<IFluxListenable> flux_listeners) {
		flux_listeners.forEach(flux -> flux.getListenerList().getListeners(FluxListener.SYNC_INDEX).forEach(l -> {
			if (flux instanceof TileFlux) {
				TileFlux tile = (TileFlux) flux;
				NBTTagCompound syncTag = tile.writeData(new NBTTagCompound(), SyncType.SPECIAL);
				SonarCore.network.sendTo(new PacketTileSync(tile.getCoords().getBlockPos(), syncTag, SyncType.SPECIAL), l.player);
			}
			// FIXME send block infomation at the end of the energy tick
			// FluxNetworks.network.sendTo(new PacketNetworkStatistics(getNetworkID(), getStatistics()), l.player);
		}));
	}

	public static void sendNetworkList(BasicFluxNetwork network, List<IFluxListenable> flux_listeners) {
		flux_listeners.forEach(flux -> flux.getListenerList().getListeners(FluxListener.SYNC_NETWORK_LIST).forEach(l -> {
			List<IFluxNetwork> toSend = FluxNetworkCache.instance().getAllowedNetworks(l.player, false);
			FluxNetworks.network.sendTo(new PacketFluxNetworkList(toSend, false), l.player);
		}));
	}

	public static void sendConnectionPackets(BasicFluxNetwork network, List<IFluxListenable> flux_listeners) {
		network.buildFluxConnections();
		flux_listeners.forEach(flux -> flux.getListenerList().getListeners(FluxListener.SYNC_NETWORK_CONNECTIONS).forEach(l -> {
			FluxNetworks.network.sendTo(new PacketFluxConnectionsList(network.getClientFluxConnection(), network.getNetworkID()), l.player);
		}));
	}

	public static void sendConnectionStatistics(BasicFluxNetwork network, List<IFluxListenable> flux_listeners) {
		flux_listeners.forEach(flux -> flux.getListenerList().getListeners(FluxListener.SYNC_NETWORK_STATS).forEach(l -> {
			FluxNetworks.network.sendTo(new PacketNetworkStatistics(network.getNetworkID(), network.getStatistics()), l.player);
		}));
	}

	public static void sendPlayerList(BasicFluxNetwork network, List<IFluxListenable> flux_listeners) {
		flux_listeners.forEach(flux -> flux.getListenerList().getListeners(FluxListener.SYNC_PLAYERS).forEach(l -> {
			List<IFluxNetwork> toSend = FluxNetworkCache.instance().getAllowedNetworks(l.player, false);
			FluxNetworks.network.sendTo(new PacketFluxNetworkList(toSend, false), l.player);
		}));
	}

	public interface IPacketAction {

		public void sendPackets(BasicFluxNetwork network, List<IFluxListenable> flux_listeners);

	}
}