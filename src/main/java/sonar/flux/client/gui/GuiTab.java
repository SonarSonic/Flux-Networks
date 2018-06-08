package sonar.flux.client.gui;

import com.google.common.collect.Lists;
import sonar.core.translate.Localisation;
import sonar.flux.FluxTranslate;
import sonar.flux.api.FluxListener;
import sonar.flux.client.gui.tabs.*;
import sonar.flux.common.tileentity.TileController;
import sonar.flux.common.tileentity.TileFlux;

import java.util.ArrayList;
import java.util.List;

public enum GuiTab {
	INDEX(FluxTranslate.GUI_TAB_INDEX, Lists.newArrayList(FluxListener.SYNC_INDEX, FluxListener.SYNC_NETWORK_STATS), 0, 0),//
	NETWORK_SELECTION(FluxTranslate.GUI_TAB_NETWORK_SELECTION, Lists.newArrayList(FluxListener.SYNC_NETWORK_LIST), 128, 0),//
	CONNECTIONS(FluxTranslate.GUI_TAB_CONNECTIONS, Lists.newArrayList(FluxListener.SYNC_NETWORK_CONNECTIONS), 64, 0),//
	NETWORK_STATISTICS(FluxTranslate.GUI_TAB_STATISTICS, Lists.newArrayList(FluxListener.SYNC_NETWORK_STATS), 192, 0),//
	NETWORK_EDIT(FluxTranslate.GUI_TAB_NETWORK_EDIT, Lists.newArrayList(FluxListener.SYNC_INDEX), 256, 0),//
	PLAYERS(FluxTranslate.GUI_TAB_PLAYERS, Lists.newArrayList(FluxListener.SYNC_PLAYERS, FluxListener.SYNC_NETWORK_CONNECTIONS), 386, 0),//
	NETWORK_CREATE(FluxTranslate.GUI_TAB_NETWORK_CREATE, Lists.newArrayList(FluxListener.SYNC_INDEX), 320, 0),//
	DEBUG(FluxTranslate.GUI_TAB_DEBUG, new ArrayList<>(), 450, 0),
	WIRELESS_CHARGING(FluxTranslate.GUI_TAB_WIRELESS_CHARGING, new ArrayList<>(), 0, 128);

	public Localisation name;
	public List<FluxListener> types;
	public int texX, texY;
	
	GuiTab(Localisation name, List<FluxListener> types, int texX, int texY){
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
	
	public Object getGuiScreen(TileFlux flux, List<GuiTab> tabs){
		switch(this){
		case CONNECTIONS:
			return new GuiTabNetworkConnections(flux, tabs);
		case INDEX:
			return flux.getIndexScreen(tabs);
		case NETWORK_CREATE:
			return new GuiTabNetworkCreate(flux, tabs);
		case NETWORK_EDIT:
			return new GuiTabNetworkEdit(flux, tabs);
		case NETWORK_SELECTION:
			return new GuiTabNetworkSelection(flux, tabs);
		case NETWORK_STATISTICS:
			return new GuiTabNetworkStatistics(flux, tabs);
		case PLAYERS:
			return new GuiTabNetworkPlayers(flux, tabs);
		case DEBUG:
			return new GuiTabNetworkDebug(flux, tabs);
		case WIRELESS_CHARGING:
			return new GuiTabWirelessCharging((TileController) flux, tabs);
		}
		return null;	
	}
	
}
