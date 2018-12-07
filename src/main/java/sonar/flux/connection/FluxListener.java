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
import sonar.flux.api.ClientFlux;
import sonar.flux.api.network.FluxPlayer;
import sonar.flux.api.network.IFluxNetwork;
import sonar.flux.api.tiles.IFlux;
import sonar.flux.network.FluxNetworkCache;
import sonar.flux.network.PacketDisconnectedTiles;
import sonar.flux.network.PacketNetworkStatistics;
import sonar.flux.network.PacketNetworkUpdate;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public enum FluxListener {

	ADMIN(FluxListener::doAdminOpen, FluxListener::doAdminClose, (network) -> {}),
	// sync main flux properties
	SYNC_INDEX(FluxListener::doIndexOpen, (network, player) -> {}, FluxListener::doIndexSync),
	// sync current networks statistics
	SYNC_NETWORK_LIST(FluxListener::doNetworkListOpen, FluxListener::doNetworkListClose, FluxListener::doNetworkListSync),
	// sync current networks connections
	SYNC_NETWORK_CONNECTIONS(FluxListener::doConnectionsListOpen, (network, player) -> {}, FluxListener::doConnectionsListSync),
	// sync network stats
	SYNC_NETWORK_STATS(FluxListener::doNetworkStatsOpen, (network, player) -> {}, FluxListener::doNetworkStatsSync),
	// sync all connected players
	SYNC_PLAYERS(FluxListener::doPlayerListOpen, (network, player) -> {}, FluxListener::doPlayerListSync),
	// sync all connected players
	SYNC_DISCONNECTED_CONNECTIONS(FluxListener::doDisconnectedTilesOpen, (network, player) -> {}, FluxListener::doDisconnectedTilesSync);
	
	public IListenerAction openPacket;
	public IListenerAction closePacket; // NECESSARY ? CURRENTLY UNUSED & UNCALLED
	public IListenerSync syncPacket;
	
	FluxListener(IListenerAction openPacket, IListenerAction closePacket, IListenerSync syncPacket){
		this.openPacket = openPacket;
		this.closePacket = closePacket;
		this.syncPacket = syncPacket;
	}

	public void doOpenPacket(IFluxNetwork network, EntityPlayerMP player){
		openPacket.sendPacket(network, player);
	}

	public void doClosePacket(IFluxNetwork network, EntityPlayerMP player){
		closePacket.sendPacket(network, player);
	}

	public void sync(FluxNetworkServer network){
		syncPacket.sync(network);
	}



	public static void doAdminOpen(IFluxNetwork network, EntityPlayerMP player){
		FluxNetworkCache.instance().updateAdminListeners();
		//FluxNetworkCache.instance().getListenerList().addListener(player, FluxListener.ADMIN);
	}



	public static void doAdminClose(IFluxNetwork network, EntityPlayerMP player){
		//FluxNetworkCache.instance().getListenerList().addListener(player, FluxListener.ADMIN);
	}

	public static void doIndexOpen(IFluxNetwork network, EntityPlayerMP player){

	}

	public static void doIndexSync(FluxNetworkServer network){
		network.flux_tile_listeners.forEach(flux -> flux.getListenerList().getListeners(FluxListener.SYNC_INDEX).forEach(p ->{
			if (flux instanceof TileEntitySyncable) {
				TileEntitySyncable tile = (TileEntitySyncable) flux;
				NBTTagCompound tag = tile.writeData(new NBTTagCompound(), SyncType.SPECIAL);
				if(!tag.hasNoTags()){
					tile.getSyncListeners().forEach(l -> SonarCore.network.sendTo(new PacketTileSync(tile.getPos(), tag, NBTHelper.SyncType.SPECIAL), l.player));
				}
			}
		}));
	}

	public static void doNetworkListOpen(IFluxNetwork network, EntityPlayerMP player){
		List<IFluxNetwork> toSend = FluxNetworkCache.instance().getAllowedNetworks(player, false);
		if(!network.isFakeNetwork()) {
			ListHelper.addWithCheck(toSend, network);
		}
		FluxNetworks.network.sendTo(new PacketNetworkUpdate(toSend, SyncType.SAVE, true), player);
	}

	public static void doNetworkListClose(IFluxNetwork network, EntityPlayerMP player){

	}

	public static void doNetworkListSync(IFluxNetwork network){
		/*
		network.flux_tile_listeners.forEach(flux -> flux.getListenerList().getListeners(FluxListener.SYNC_NETWORK_LIST).forEach(p ->{
			FluxNetworks.network.sendTo(new PacketFluxNetworkUpdate(FluxNetworkCache.instance().getAllowedNetworks(p.player, false), SyncType.DEFAULT_SYNC, false), p.player);
		}));
		*/
	}


	public static void doConnectionsListOpen(IFluxNetwork network, EntityPlayerMP player){
		if(!network.isFakeNetwork()) {
			network.buildFluxConnections(); //update it just in case.
			FluxNetworks.network.sendTo(new PacketNetworkUpdate(Lists.newArrayList(network), SyncType.SAVE, false), player);
			FluxNetworks.network.sendTo(new PacketNetworkUpdate(Lists.newArrayList(network), SyncType.PACKET, false), player);
		}
	}

	public static void doConnectionsListSync(FluxNetworkServer network){
		if(FluxNetworkCache.instance().disconnected_tiles_changed){
			network.client_connections.setDirty(true);
			network.buildFluxConnections();
		}
		if(network.client_connections.isDirty()) {
			network.forEachListener(FluxListener.SYNC_NETWORK_CONNECTIONS, p -> {
				FluxNetworks.network.sendTo(new PacketNetworkUpdate(Lists.newArrayList(network), SyncType.SPECIAL, false), p.player);
			});
		}
	}

	public static void doNetworkStatsOpen(IFluxNetwork network, EntityPlayerMP player){
		if(!network.isFakeNetwork()) {
			FluxNetworks.network.sendTo(new PacketNetworkStatistics(network), player);
		}
	}

	public static void doNetworkStatsSync(FluxNetworkServer network){
		network.forEachListener(FluxListener.SYNC_INDEX, p -> doNetworkStatsOpen(network, p.player));
	}

	public static void doPlayerListOpen(IFluxNetwork network, EntityPlayerMP player){
		if(!network.isFakeNetwork()) {
			FluxNetworks.network.sendTo(new PacketNetworkUpdate(Lists.newArrayList(network), SyncType.SAVE, false), player);
			FluxNetworks.network.sendTo(new PacketNetworkUpdate(Lists.newArrayList(network), SyncType.PACKET, false), player);
		}
	}

	public static void doPlayerListSync(FluxNetworkServer network){
		if(network.network_players.isDirty()) {
			network.forEachListener(FluxListener.SYNC_PLAYERS, p -> {
				FluxNetworks.network.sendTo(new PacketNetworkUpdate(Lists.newArrayList(network), SyncType.SPECIAL, false), p.player);
			});
		}
	}

	public static void doDisconnectedTilesOpen(IFluxNetwork network, EntityPlayerMP player){
		UUID uuid = FluxPlayer.getOnlineUUID(player);
		List<IFlux> tiles = FluxNetworkCache.instance().disconnected_tiles.get(uuid);
		List<ClientFlux> client_flux = new ArrayList<>();
		if(tiles != null && !tiles.isEmpty()) {
			tiles.forEach(f -> client_flux.add(new ClientFlux(f)));
		}
		FluxNetworks.network.sendTo(new PacketDisconnectedTiles(client_flux), player);
	}

	public static void doDisconnectedTilesSync(FluxNetworkServer network){
		if(FluxNetworkCache.instance().disconnected_tiles_changed) {
			network.forEachListener(FluxListener.SYNC_DISCONNECTED_CONNECTIONS, p -> {
				doDisconnectedTilesOpen(network, p.player);
			});
		}
	}

	/**create open packet*/
	public interface IListenerAction {
		void sendPacket(IFluxNetwork network, EntityPlayerMP player);
	}

	/**create open packet*/
	public interface IListenerSync {
		void sync(FluxNetworkServer network);
	}
}