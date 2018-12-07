package sonar.flux.network;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppedEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import sonar.core.SonarCore;
import sonar.core.helpers.ListHelper;
import sonar.flux.FluxConfig;
import sonar.flux.FluxNetworks;
import sonar.flux.api.AdditionType;
import sonar.flux.api.FluxError;
import sonar.flux.api.RemovalType;
import sonar.flux.api.network.FluxCache;
import sonar.flux.api.network.IFluxNetwork;
import sonar.flux.api.tiles.IFluxController;
import sonar.flux.api.tiles.IFluxListenable;
import sonar.flux.common.entity.EntityFireItem;
import sonar.flux.common.events.FluxConnectionEvent;
import sonar.flux.common.events.FluxItemListenerEvent;
import sonar.flux.common.events.FluxNetworkEvent;
import sonar.flux.common.events.FluxTileListenerEvent;
import sonar.flux.common.tileentity.TileFlux;
import sonar.flux.connection.FluxNetworkServer;
import sonar.flux.connection.NetworkSettings;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FluxCommon {

	public FluxNetworkCache serverCache;
	public ClientNetworkCache clientCache;

	//// FML EVENTS \\\\

	public List<Runnable> runnables = new ArrayList<>();

	public void scheduleRunnable(Runnable run){
		runnables.add(run);
	}

	public void preInit(FMLPreInitializationEvent event) {
		serverCache = new FluxNetworkCache();
	}

	public void init(FMLInitializationEvent event) {}

	public void postInit(FMLPostInitializationEvent evt) {}

	public void shutdown(FMLServerStoppedEvent event) {
		serverCache.clearNetworks();
		runnables.clear();
	}

	//// PACKETS \\\\

	public static void registerPackets() {
		int id = 0;
		FluxNetworks.network.registerMessage(PacketTile.Handler.class, PacketTile.class, id++, Side.SERVER);
		FluxNetworks.network.registerMessage(PacketGeneral.Handler.class, PacketGeneral.class, id++, Side.SERVER);
		FluxNetworks.network.registerMessage(PacketNetworkUpdate.Handler.class, PacketNetworkUpdate.class, id++, Side.CLIENT);
		FluxNetworks.network.registerMessage(PacketConnectionsClientList.Handler.class, PacketConnectionsClientList.class, id++, Side.CLIENT);
		FluxNetworks.network.registerMessage(PacketDisconnectedTiles.Handler.class, PacketDisconnectedTiles.class, id++, Side.CLIENT);
		FluxNetworks.network.registerMessage(PacketEditedTiles.Handler.class, PacketEditedTiles.class, id++, Side.SERVER);
		FluxNetworks.network.registerMessage(PacketError.Handler.class, PacketError.class, id++, Side.CLIENT);
		FluxNetworks.network.registerMessage(PacketNetworkStatistics.Handler.class, PacketNetworkStatistics.class, id++, Side.CLIENT);
		FluxNetworks.network.registerMessage(PacketColourRequest.Handler.class, PacketColourRequest.class, id++, Side.SERVER);
		FluxNetworks.network.registerMessage(PacketColourCache.Handler.class, PacketColourCache.class, id++, Side.CLIENT);
		FluxNetworks.network.registerMessage(PacketNetworkDeleted.Handler.class, PacketNetworkDeleted.class, id++, Side.CLIENT);
		FluxNetworks.network.registerMessage(PacketConnectionsRefresh.Handler.class, PacketConnectionsRefresh.class, id++, Side.SERVER);
		FluxNetworks.network.registerMessage(PacketFluxItemNetwork.Handler.class, PacketFluxItemNetwork.class, id++, Side.SERVER);
		FluxNetworks.network.registerMessage(PacketUpdateGuiItem.Handler.class, PacketUpdateGuiItem.class, id++, Side.CLIENT);
		FluxNetworks.network.registerMessage(PacketUpdateGuiItem.Handler.class, PacketUpdateGuiItem.class, id++, Side.SERVER);
	}


	//// FORGE EVENTS \\\\


	@SubscribeEvent
	public void onServerTick(TickEvent.ServerTickEvent event) {
		if (event.side == Side.CLIENT) {
			return;
		}
		FluxNetworkCache cache = FluxNetworks.getServerCache();
		List<IFluxNetwork> networks = cache.getAllNetworks();
		if (event.phase == TickEvent.Phase.START) {
			FluxNetworks.proxy.runnables.forEach(Runnable::run);
			FluxNetworks.proxy.runnables.clear();
			for (IFluxNetwork network : networks) {
				network.onStartServerTick();
			}
			cache.onStartServerTick();
		}
		if (event.phase == TickEvent.Phase.END) {
			for (IFluxNetwork network : networks) {
				network.onEndServerTick();
			}
			cache.onEndServerTick();
		}
	}

	@SubscribeEvent
	public void dropFluxEvent(BlockEvent.HarvestDropsEvent drops) {
		if(drops.getWorld().isRemote){
			return;
		}
		if (!FluxConfig.enableFluxRedstoneDrop || !(drops.getState().getBlock() == Blocks.REDSTONE_ORE || (drops.getState().getBlock() == Blocks.LIT_REDSTONE_ORE)) || drops.getHarvester() instanceof FakePlayer || drops.isSilkTouching()) {
			return;
		}
		if (SonarCore.randInt(0, FluxConfig.redstone_ore_chance) == 1) {
			drops.getDrops().add(new ItemStack(FluxNetworks.flux, Math.max(1, SonarCore.randInt(FluxConfig.redstone_ore_min_drop, FluxConfig.redstone_ore_max_drop))));
		}
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onEntityAdded(EntityJoinWorldEvent event) {
		if (!FluxConfig.enableFluxRecipe || event.getWorld().isRemote) {
			return;
		}
		final Entity entity = event.getEntity();
		if (entity instanceof EntityItem && !(entity instanceof EntityFireItem)) {
			EntityItem entityItem = (EntityItem) entity;
			ItemStack stack = entityItem.getItem();
			if (!stack.isEmpty() && stack.getItem() == Items.REDSTONE) {
				EntityFireItem newEntity = new EntityFireItem(entityItem);
				entityItem.setDead();

				int i = MathHelper.floor(newEntity.posX / 16.0D);
				int j = MathHelper.floor(newEntity.posZ / 16.0D);
				event.getWorld().getChunkFromChunkCoords(i, j).addEntity(newEntity);
				event.getWorld().loadedEntityList.add(newEntity);
				event.getWorld().onEntityAdded(newEntity);

				event.setCanceled(true);
			}
		}
	}

	@SubscribeEvent
	public void onFluxConnected(FluxConnectionEvent.Connected event){
		if(!event.flux.getDimension().isRemote){
			event.flux.connect(event.network);
			if(event.network instanceof FluxNetworkServer) {
				FluxNetworkServer network = (FluxNetworkServer) event.network;
				network.sortConnections = true;

				if(event.flux instanceof IFluxController){
					List<IFluxController> controllers = network.getConnections(FluxCache.controller);
					if (controllers.size() > 1) {
						controllers.forEach(c -> network.queueConnectionRemoval(c, RemovalType.REMOVE));
						network.queueConnectionAddition((IFluxListenable) event.flux, AdditionType.ADD);
					}
				}
			}
		}
	}

	@SubscribeEvent
	public void onFluxDisconnected(FluxConnectionEvent.Disconnected event){
		if(!event.flux.getDimension().isRemote){
			event.flux.disconnect(event.network);
			if(event.network instanceof FluxNetworkServer) {
				FluxNetworkServer network = (FluxNetworkServer) event.network;
				network.sortConnections = true;
			}
		}
	}

	@SubscribeEvent
	public void onAddConnectionListener(FluxTileListenerEvent.AddConnectionListener event){
		if(!event.flux.getDimension().isRemote && event.network instanceof FluxNetworkServer){
			FluxNetworkServer network = (FluxNetworkServer) event.network;
			ListHelper.addWithCheck(network.flux_tile_listeners, event.flux);
		}
	}

	@SubscribeEvent
	public void onRemoveConnectionListener(FluxTileListenerEvent.RemoveConnectionListener event){
		if(!event.flux.getDimension().isRemote && !event.network.isFakeNetwork()){
			FluxNetworkServer network = (FluxNetworkServer) event.network;
			network.flux_tile_listeners.removeIf(f -> f == event.flux);
		}
	}

	@SubscribeEvent
	public void onAddItemConnectionListener(FluxItemListenerEvent.AddConnectionListener event){
		if(event.network instanceof FluxNetworkServer){
			FluxNetworkServer network = (FluxNetworkServer) event.network;
			network.flux_stack_listeners.put(event.unique_id, event.listeners);
		}
	}

	@SubscribeEvent
	public void onRemoveItemConnectionListener(FluxItemListenerEvent.RemoveConnectionListener event){
		if(event.network instanceof FluxNetworkServer){
			FluxNetworkServer network = (FluxNetworkServer) event.network;
			network.flux_stack_listeners.remove(event.unique_id);
		}
	}

	@SubscribeEvent
	public void onFluxConnectedSettingChanged(FluxConnectionEvent.SettingChanged event){
		if(!event.flux.getDimension().isRemote && event.flux.getNetwork() instanceof FluxNetworkServer){
			FluxNetworkServer network = (FluxNetworkServer) event.flux.getNetwork();
			network.markSettingDirty(event.setting, event.flux);
            network.sortConnections = true;
		}
	}

	@SubscribeEvent
	public void onNetworkSettingsChanged(FluxNetworkEvent.SettingsChanged event){
		for(NetworkSettings setting : NetworkSettings.SAVED){
			if(event.hasSettingChanged(setting)){
				FluxNetworkCache.instance().onSettingsChanged(event.network);
				break;
			}
		}

		/// UPDATE COLOUR

		if(event.hasSettingChanged(NetworkSettings.NETWORK_COLOUR) && event.network instanceof FluxNetworkServer){
			FluxNetworkServer network = (FluxNetworkServer) event.network;
			List<IFluxListenable> flux = network.getConnections(FluxCache.flux);
			flux.forEach(f -> {if(f instanceof TileFlux)
				((TileFlux) f).colour.setValue(event.network.getSetting(NetworkSettings.NETWORK_COLOUR).getRGB());
			});
		}
	}


	//// LOGGING \\\\

	public void logNewNetwork(IFluxNetwork network) {
		FluxNetworks.logger.info("[NEW NETWORK] '" + network.getSetting(NetworkSettings.NETWORK_NAME) + "' with ID '" + network.getNetworkID() + "' was created by " + network.getSetting(NetworkSettings.NETWORK_CACHED_NAME) + "( " + network.getSetting(NetworkSettings.NETWORK_OWNER) + " )");
	}

	public void logRemoveNetwork(IFluxNetwork network) {
		FluxNetworks.logger.info("[DELETE NETWORK] '" + network.getSetting(NetworkSettings.NETWORK_NAME) + "' with ID '" + network.getNetworkID() + "' was removed by " + network.getSetting(NetworkSettings.NETWORK_CACHED_NAME));
	}

	public void logLoadedNetwork(IFluxNetwork network) {
		FluxNetworks.logger.info("[LOADED NETWORK] '" + network.getSetting(NetworkSettings.NETWORK_NAME) + "' with ID '" + network.getNetworkID() + "' with owner " + network.getSetting(NetworkSettings.NETWORK_CACHED_NAME));
	}


	//// CLIENT HANDLING \\\\

	public void registerRenderThings() {}

	public void receiveColourCache(Map<Integer, Tuple<Integer, String>> cache){}

	public void clearNetwork(int networkID){}

	public void setFluxError(FluxError error){}

	@Nullable
	public FluxError getFluxError(){
		return null;
	}

	public void setFluxTile(TileFlux flux){}

	@Nullable
	public TileFlux getFluxTile(){
		return null;
	}

	public void setFluxStack(ItemStack flux_stack){}

	@Nullable
	public ItemStack getFluxStack(){
		return null;
	}
}
