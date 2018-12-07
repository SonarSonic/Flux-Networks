package sonar.flux.client.gui;

import com.google.common.collect.Lists;
import net.minecraft.item.ItemStack;
import sonar.core.translate.Localisation;
import sonar.flux.FluxNetworks;
import sonar.flux.FluxTranslate;
import sonar.flux.api.IFluxItemGui;
import sonar.flux.client.gui.tabs.*;
import sonar.flux.common.tileentity.TileFlux;
import sonar.flux.connection.FluxListener;

import java.util.ArrayList;
import java.util.List;

public enum EnumGuiTab {
	INDEX(FluxTranslate.GUI_TAB_INDEX, Lists.newArrayList(FluxListener.SYNC_INDEX, FluxListener.SYNC_NETWORK_STATS), 0, 0),//
	NETWORK_SELECTION(FluxTranslate.GUI_TAB_NETWORK_SELECTION, Lists.newArrayList(FluxListener.SYNC_NETWORK_LIST), 128, 0),//
	CONNECTIONS(FluxTranslate.GUI_TAB_CONNECTIONS, Lists.newArrayList(FluxListener.SYNC_NETWORK_CONNECTIONS, FluxListener.SYNC_DISCONNECTED_CONNECTIONS), 64, 0),//
	NETWORK_STATISTICS(FluxTranslate.GUI_TAB_STATISTICS, Lists.newArrayList(FluxListener.SYNC_NETWORK_STATS), 192, 0),//
	NETWORK_EDIT(FluxTranslate.GUI_TAB_NETWORK_EDIT, Lists.newArrayList(FluxListener.SYNC_INDEX), 256, 0),//
	PLAYERS(FluxTranslate.GUI_TAB_PLAYERS, Lists.newArrayList(FluxListener.SYNC_PLAYERS, FluxListener.SYNC_NETWORK_CONNECTIONS), 386, 0),//
	NETWORK_CREATE(FluxTranslate.GUI_TAB_NETWORK_CREATE, Lists.newArrayList(FluxListener.SYNC_INDEX), 320, 0),//
	DEBUG(FluxTranslate.GUI_TAB_DEBUG, new ArrayList<>(), 450, 0),
	WIRELESS_CHARGING(FluxTranslate.GUI_TAB_WIRELESS_CHARGING, new ArrayList<>(), 0, 128),

	//// ADMIN \\\\
	ADMIN_NETWORK_SELECTION(FluxTranslate.GUI_TAB_NETWORK_SELECTION, Lists.newArrayList(FluxListener.ADMIN), 128, 0);//

	public Localisation name;
	public List<FluxListener> types;
	public int texX, texY;
	
	EnumGuiTab(Localisation name, List<FluxListener> types, int texX, int texY){
		this.name = name;
		this.types = types;
		this.texX = texX;
		this.texY = texY;
	}
	
	public String getClientName() {
		return name.t();
	}
	
	public List<FluxListener> getMonitoringTypes(){
		return types;
	}
	
	public Object getGuiScreen(List<EnumGuiTab> tabs){
		switch(this){
		case INDEX:
			ItemStack stack = FluxNetworks.proxy.getFluxStack();
			TileFlux tile = FluxNetworks.proxy.getFluxTile();
			if(tile != null){
				return tile.getIndexScreen(tabs);
			}else if(stack != null && stack.getItem() instanceof IFluxItemGui){
				return ((IFluxItemGui)stack.getItem()).getIndexScreen(stack, tabs);
			}
			return null;
		case CONNECTIONS:
			return new GuiTabNetworkConnections(tabs);
		case NETWORK_CREATE:
			return new GuiTabNetworkCreate(tabs);
		case NETWORK_EDIT:
			return new GuiTabNetworkEdit(tabs);
		case NETWORK_SELECTION:
			return new GuiTabNetworkSelection(tabs);
		case NETWORK_STATISTICS:
			return new GuiTabNetworkStatistics(tabs);
		case PLAYERS:
			return new GuiTabNetworkPlayers(tabs);
		case DEBUG:
			return new GuiTabNetworkDebug(tabs);
		case WIRELESS_CHARGING:
			return new GuiTabWirelessCharging(tabs);
		case ADMIN_NETWORK_SELECTION:
			return new GuiTabNetworkAdminSelection(tabs);
		}
		return null;	
	}
	
}
