package sonar.flux.network;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.MinecraftForge;
import sonar.core.helpers.NBTHelper;
import sonar.flux.FluxNetworks;
import sonar.flux.api.network.IFluxNetwork;
import sonar.flux.client.gui.GuiTab;
import sonar.flux.common.events.FluxListenerEvent;
import sonar.flux.common.tileentity.TileFlux;
import sonar.flux.connection.FluxListener;

public class ListenerHelper {

	public static void onPlayerOpenTileGui(TileFlux flux, EntityPlayer player) {
		Preconditions.checkState(!flux.getWorld().isRemote);
		MinecraftForge.EVENT_BUS.post(new FluxListenerEvent.AddConnectionListener(flux, flux.getNetwork()));

		if(!flux.getNetwork().isFakeNetwork()) {
			FluxNetworks.network.sendTo(new PacketFluxNetworkUpdate(Lists.newArrayList(flux.getNetwork()), NBTHelper.SyncType.SAVE, true), (EntityPlayerMP) player);
		}
	}

	public static void onPlayerCloseTileGui(TileFlux flux, EntityPlayer player) {
		Preconditions.checkState(!flux.getWorld().isRemote);
		MinecraftForge.EVENT_BUS.post(new FluxListenerEvent.RemoveConnectionListener(flux, flux.getNetwork()));
	}

	public static void onNetworkChanged(TileFlux flux, IFluxNetwork old, IFluxNetwork network) {
		Preconditions.checkState(!flux.getWorld().isRemote);
		if (flux.getListenerList().hasListeners()) {
			MinecraftForge.EVENT_BUS.post(new FluxListenerEvent.RemoveConnectionListener(flux, old));
			MinecraftForge.EVENT_BUS.post(new FluxListenerEvent.AddConnectionListener(flux, network));
			for(FluxListener l : FluxListener.values()){
				flux.listeners.getListeners(l).forEach(p -> l.doClosePacket(network, flux, p.player));
				flux.listeners.getListeners(l).forEach(p -> l.doOpenPacket(network, flux, p.player));
			}
		}
	}

	public static void onPlayerOpenTab(TileFlux flux, EntityPlayer player, GuiTab tab) {
		Preconditions.checkState(!flux.getWorld().isRemote);
		tab.getMonitoringTypes().forEach(m -> {
			flux.listeners.addListener(player, m);
			m.doOpenPacket(flux.getNetwork(), flux, (EntityPlayerMP) player);
		});
	}

	public static void onPlayerCloseTab(TileFlux flux, EntityPlayer player, GuiTab tab){
		Preconditions.checkState(!flux.getWorld().isRemote);
		tab.getMonitoringTypes().forEach(m -> {
			flux.listeners.removeListener(player, true, m);
			m.doClosePacket(flux.getNetwork(), flux, (EntityPlayerMP) player);
		});
	}

}