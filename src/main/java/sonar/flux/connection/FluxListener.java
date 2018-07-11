package sonar.flux.connection;

import com.google.common.collect.Lists;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import sonar.core.SonarCore;
import sonar.core.common.tile.TileEntitySyncable;
import sonar.core.helpers.ListHelper;
import sonar.core.helpers.NBTHelper;
import sonar.core.helpers.NBTHelper.SyncType;
import sonar.core.network.PacketTileSync;
import sonar.flux.FluxNetworks;
import sonar.flux.api.network.IFluxNetwork;
import sonar.flux.api.tiles.IFluxListenable;
import sonar.flux.network.FluxNetworkCache;
import sonar.flux.network.PacketFluxNetworkUpdate;
import sonar.flux.network.PacketNetworkStatistics;

import java.util.List;

public enum FluxListener {

	ADMIN((network, flux, player) -> {}, (network, flux, player) -> {}, (network) -> {}),
	// sync main flux properties
	SYNC_INDEX(FluxListener::doIndexOpen, (network, flux, player) -> {}, FluxListener::doIndexSync),
	// sync current networks statistics
	SYNC_NETWORK_LIST(FluxListener::doNetworkListOpen, FluxListener::doNetworkListClose, FluxListener::doNetworkListSync),
	// sync current networks connections
	SYNC_NETWORK_CONNECTIONS(FluxListener::doConnectionsListOpen, (network, flux, player) -> {}, FluxListener::doConnectionsListSync),
	// sync network stats
	SYNC_NETWORK_STATS(FluxListener::doNetworkStatsOpen, (network, flux, player) -> {}, FluxListener::doNetworkStatsSync),
	// sync all connected players
	SYNC_PLAYERS(FluxListener::doPlayerListOpen, (network, flux, player) -> {}, FluxListener::doPlayerListSync);
	
	public IListenerAction openPacket;
	public IListenerAction closePacket; // NECESSARY ? CURRENTLY UNUSED & UNCALLED
	public IListenerSync syncPacket;
	
	FluxListener(IListenerAction openPacket, IListenerAction closePacket, IListenerSync syncPacket){
		this.openPacket = openPacket;
		this.closePacket = closePacket;
		this.syncPacket = syncPacket;
	}

	public void doOpenPacket(IFluxNetwork network, IFluxListenable flux, EntityPlayerMP player){
		openPacket.sendPacket(network, flux, player);
	}

	public void doClosePacket(IFluxNetwork network, IFluxListenable flux, EntityPlayerMP player){
		closePacket.sendPacket(network, flux, player);
	}

	public void sync(FluxNetworkServer network){
		syncPacket.sync(network);
	}

	public static void doIndexOpen(IFluxNetwork network, IFluxListenable flux, EntityPlayerMP player){
		if (flux instanceof TileEntitySyncable) {
			TileEntitySyncable tile = (TileEntitySyncable) flux;

			//tile.sendSyncPacket(player, SyncType.SAVE);
		}
	}

	public static void doIndexSync(FluxNetworkServer network){
		network.flux_listeners.forEach(flux -> flux.getListenerList().getListeners(FluxListener.SYNC_INDEX).forEach(p ->{
			if (flux instanceof TileEntitySyncable) {
				TileEntitySyncable tile = (TileEntitySyncable) flux;
				NBTTagCompound tag = tile.writeData(new NBTTagCompound(), SyncType.SPECIAL);
				if(!tag.hasNoTags()){
					tile.getSyncListeners().forEach(l -> SonarCore.network.sendTo(new PacketTileSync(tile.getPos(), tag, NBTHelper.SyncType.SPECIAL), l.player));
				}
			}
		}));
	}

	public static void doNetworkListOpen(IFluxNetwork network, IFluxListenable flux, EntityPlayerMP player){
		List<IFluxNetwork> toSend = FluxNetworkCache.instance().getAllowedNetworks(player, false);
		if(!network.isFakeNetwork()) {
			ListHelper.addWithCheck(toSend, network);
		}
		FluxNetworks.network.sendTo(new PacketFluxNetworkUpdate(toSend, SyncType.SAVE, true), player);
	}

	public static void doNetworkListClose(IFluxNetwork network, IFluxListenable flux, EntityPlayerMP player){

	}

	public static void doNetworkListSync(IFluxNetwork network){
		/*
		network.flux_listeners.forEach(flux -> flux.getListenerList().getListeners(FluxListener.SYNC_NETWORK_LIST).forEach(p ->{
			FluxNetworks.network.sendTo(new PacketFluxNetworkUpdate(FluxNetworkCache.instance().getAllowedNetworks(p.player, false), SyncType.DEFAULT_SYNC, false), p.player);
		}));
		*/
	}


	public static void doConnectionsListOpen(IFluxNetwork network, IFluxListenable flux, EntityPlayerMP player){
		if(!network.isFakeNetwork()) {
			network.buildFluxConnections(); //update it just in case.
			FluxNetworks.network.sendTo(new PacketFluxNetworkUpdate(Lists.newArrayList(network), SyncType.SAVE, false), player);
			FluxNetworks.network.sendTo(new PacketFluxNetworkUpdate(Lists.newArrayList(network), SyncType.PACKET, false), player);
		}
	}

	public static void doConnectionsListSync(FluxNetworkServer network){
		if(network.client_connections.isDirty()) {
			network.flux_listeners.forEach(flux -> flux.getListenerList().getListeners(FluxListener.SYNC_INDEX).forEach(p -> {
				FluxNetworks.network.sendTo(new PacketFluxNetworkUpdate(Lists.newArrayList(network), SyncType.SPECIAL, false), p.player);
			}));
		}
	}

	public static void doNetworkStatsOpen(IFluxNetwork network, IFluxListenable flux, EntityPlayerMP player){
		if(!network.isFakeNetwork()) {
			FluxNetworks.network.sendTo(new PacketNetworkStatistics(network), player);
		}
	}

	public static void doNetworkStatsSync(FluxNetworkServer network){
		network.flux_listeners.forEach(flux -> flux.getListenerList().getListeners(FluxListener.SYNC_INDEX).forEach(p ->{
			doNetworkStatsOpen(network, flux, p.player);
		}));
	}

	public static void doPlayerListOpen(IFluxNetwork network, IFluxListenable flux, EntityPlayerMP player){
		if(!network.isFakeNetwork()) {
			FluxNetworks.network.sendTo(new PacketFluxNetworkUpdate(Lists.newArrayList(network), SyncType.SAVE, false), player);
			FluxNetworks.network.sendTo(new PacketFluxNetworkUpdate(Lists.newArrayList(network), SyncType.PACKET, false), player);
		}
	}

	public static void doPlayerListSync(FluxNetworkServer network){
		if(network.network_players.isDirty()) {
			network.flux_listeners.forEach(flux -> flux.getListenerList().getListeners(FluxListener.SYNC_INDEX).forEach(p -> {
				FluxNetworks.network.sendTo(new PacketFluxNetworkUpdate(Lists.newArrayList(network), SyncType.SPECIAL, false), p.player);
			}));
		}
	}

	/**create open packet*/
	public interface IListenerAction {
		void sendPacket(IFluxNetwork network, IFluxListenable flux, EntityPlayerMP player);
	}

	/**create open packet*/
	public interface IListenerSync {
		void sync(FluxNetworkServer network);
	}
}