package sonar.flux.network;

import net.minecraft.entity.player.EntityPlayer;
import sonar.flux.FluxNetworks;
import sonar.flux.api.FluxListener;
import sonar.flux.api.network.IFluxNetwork;
import sonar.flux.client.gui.GuiTab;
import sonar.flux.common.tileentity.TileFlux;
import sonar.flux.connection.BasicFluxNetwork;

public class ListenerHelper {

	public static void onPlayerOpenTileGui(TileFlux flux, EntityPlayer player) {
		if (flux.isServer()) {
			flux.getNetwork().addFluxListener(flux);
		}
	}

	public static void onPlayerCloseTileGui(TileFlux flux, EntityPlayer player) {
		if (flux.isServer()) {
			flux.listeners.clearListener(flux.listeners.findListener(player));
			if (!flux.getListenerList().hasListeners()) {
				flux.getNetwork().removeFluxListener(flux);
			}
		}
	}

	public static void onNetworkChanged(TileFlux flux, IFluxNetwork old, IFluxNetwork network) {
		if (flux.isServer()) {
			if (flux.getListenerList().hasListeners()) {
				old.removeFluxListener(flux);
				network.addFluxListener(flux);
			}
		}
	}

	public static void onPlayerOpenTab(TileFlux flux, EntityPlayer player, GuiTab tab) {
		if (flux.isServer()) {
			flux.listeners.clearListener(flux.listeners.findListener(player));
			tab.getMonitoringTypes().forEach(m -> {
				flux.listeners.addListener(player, m);
				if(!flux.getNetwork().isFakeNetwork()){
					m.sendPackets((BasicFluxNetwork) flux.getNetwork(), flux.getNetwork().getFluxListeners());
				}
			});

		}
	}

	public static void onNetworkListChanged() {
		FluxNetworks.getServerCache().getAllNetworks().forEach(network -> FluxListener.SYNC_NETWORK_LIST.sendPackets((BasicFluxNetwork) network, network.getFluxListeners()));
	}

}
