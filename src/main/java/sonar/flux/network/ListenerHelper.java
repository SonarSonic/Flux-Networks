package sonar.flux.network;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import sonar.core.helpers.NBTHelper;
import sonar.core.listener.ListenerList;
import sonar.core.listener.PlayerListener;
import sonar.flux.FluxNetworks;
import sonar.flux.api.IFluxItemGui;
import sonar.flux.api.network.IFluxNetwork;
import sonar.flux.client.gui.EnumGuiTab;
import sonar.flux.common.events.FluxItemListenerEvent;
import sonar.flux.common.events.FluxTileListenerEvent;
import sonar.flux.common.tileentity.TileFlux;
import sonar.flux.connection.FluxListener;
import sonar.flux.connection.FluxNetworkInvalid;

public class ListenerHelper {

	//// ITEMS \\\\

	public static void onPlayerOpenItemGui(ItemStack stack, EntityPlayer player) {
		Preconditions.checkState(!player.getEntityWorld().isRemote);
		ListenerList<PlayerListener> listeners = FluxNetworkCache.instance().getOrCreateStackListeners(stack);
		int uuid = FluxNetworkCache.instance().getOrCreateUniqueID(stack);
		IFluxNetwork network = getViewingNetwork(stack);
		MinecraftForge.EVENT_BUS.post(new FluxItemListenerEvent.AddConnectionListener(stack, uuid, network, listeners));
		if(!network.isFakeNetwork()) {
			FluxNetworks.network.sendTo(new PacketNetworkUpdate(Lists.newArrayList(network), NBTHelper.SyncType.SAVE, true), (EntityPlayerMP) player);
		}
	}

	public static void onPlayerCloseItemGui(ItemStack stack, EntityPlayer player) {
		Preconditions.checkState(!player.getEntityWorld().isRemote);
		ListenerList<PlayerListener> listeners = FluxNetworkCache.instance().getOrCreateStackListeners(stack);
		int uuid = FluxNetworkCache.instance().getOrCreateUniqueID(stack);
		IFluxNetwork network = getViewingNetwork(stack);
		MinecraftForge.EVENT_BUS.post(new FluxItemListenerEvent.RemoveConnectionListener(stack, uuid, network, listeners));
	}

	public static void onPlayerOpenItemTab(ItemStack stack, EntityPlayer player, EnumGuiTab tab) {
		Preconditions.checkState(!player.getEntityWorld().isRemote);
		ListenerList<PlayerListener> listeners = FluxNetworkCache.instance().getOrCreateStackListeners(stack);
		IFluxNetwork network = getViewingNetwork(stack);
		tab.getMonitoringTypes().forEach(m -> {
			listeners.addListener(player, m);
			m.doOpenPacket(network, (EntityPlayerMP) player);
		});
	}

	public static void onPlayerCloseItemTab(ItemStack stack, EntityPlayer player, EnumGuiTab tab){
		Preconditions.checkState(!player.getEntityWorld().isRemote);
		ListenerList<PlayerListener> listeners = FluxNetworkCache.instance().getOrCreateStackListeners(stack);
		IFluxNetwork network = getViewingNetwork(stack);
		tab.getMonitoringTypes().forEach(m -> {
			listeners.removeListener(player, true, m);
			m.doClosePacket(network, (EntityPlayerMP) player);
		});
	}

	public static void onViewingNetworkChanged(ItemStack stack, IFluxNetwork old, IFluxNetwork network) {
		ListenerList<PlayerListener> listeners = FluxNetworkCache.instance().getOrCreateStackListeners(stack);
		int uuid = FluxNetworkCache.instance().getOrCreateUniqueID(stack);

		if (listeners.hasListeners()) {
			MinecraftForge.EVENT_BUS.post(new FluxItemListenerEvent.RemoveConnectionListener(stack, uuid, old, listeners));
			MinecraftForge.EVENT_BUS.post(new FluxItemListenerEvent.AddConnectionListener(stack, uuid, network, listeners));
			for(FluxListener l : FluxListener.values()){
				listeners.getListeners(l).forEach(p -> l.doClosePacket(network, p.player));
				listeners.getListeners(l).forEach(p -> l.doOpenPacket(network, p.player));
			}
		}
	}

	public static IFluxNetwork getViewingNetwork(ItemStack stack){
		if(stack.getItem() instanceof IFluxItemGui){
			int networkID = ((IFluxItemGui)stack.getItem()).getViewingNetworkID(stack);
			return FluxNetworkCache.instance().getNetwork(networkID);
		}
		return FluxNetworkInvalid.INVALID;
	}


	//// TILES \\\\

	public static void onPlayerOpenTileGui(TileFlux flux, EntityPlayer player) {
		Preconditions.checkState(!flux.getWorld().isRemote);
		MinecraftForge.EVENT_BUS.post(new FluxTileListenerEvent.AddConnectionListener(flux, flux.getNetwork()));

		if(!flux.getNetwork().isFakeNetwork()) {
			FluxNetworks.network.sendTo(new PacketNetworkUpdate(Lists.newArrayList(flux.getNetwork()), NBTHelper.SyncType.SAVE, true), (EntityPlayerMP) player);
		}
	}

	public static void onPlayerCloseTileGui(TileFlux flux, EntityPlayer player) {
		Preconditions.checkState(!flux.getWorld().isRemote);
		MinecraftForge.EVENT_BUS.post(new FluxTileListenerEvent.RemoveConnectionListener(flux, flux.getNetwork()));
	}

	public static void onPlayerOpenTileTab(TileFlux flux, EntityPlayer player, EnumGuiTab tab) {
		Preconditions.checkState(!flux.getWorld().isRemote);
		tab.getMonitoringTypes().forEach(m -> {
			flux.listeners.addListener(player, m);
			m.doOpenPacket(flux.getNetwork(), (EntityPlayerMP) player);
		});
	}

	public static void onPlayerCloseTileTab(TileFlux flux, EntityPlayer player, EnumGuiTab tab){
		Preconditions.checkState(!flux.getWorld().isRemote);
		tab.getMonitoringTypes().forEach(m -> {
			flux.listeners.removeListener(player, true, m);
			m.doClosePacket(flux.getNetwork(), (EntityPlayerMP) player);
		});
	}

	public static void onNetworkChanged(TileFlux flux, IFluxNetwork old, IFluxNetwork network) {
		Preconditions.checkState(!flux.getWorld().isRemote);
		if (flux.getListenerList().hasListeners()) {
			MinecraftForge.EVENT_BUS.post(new FluxTileListenerEvent.RemoveConnectionListener(flux, old));
			MinecraftForge.EVENT_BUS.post(new FluxTileListenerEvent.AddConnectionListener(flux, network));
			for(FluxListener l : FluxListener.values()){
				flux.listeners.getListeners(l).forEach(p -> l.doClosePacket(network, p.player));
				flux.listeners.getListeners(l).forEach(p -> l.doOpenPacket(network, p.player));
			}
		}
	}

}