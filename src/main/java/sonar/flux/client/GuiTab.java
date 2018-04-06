package sonar.flux.client;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Lists;

import sonar.flux.FluxTranslate;
import sonar.flux.api.FluxListener;
import sonar.flux.client.tabs.GuiTabConnectionIndex;
import sonar.flux.client.tabs.GuiTabControllerIndex;
import sonar.flux.client.tabs.GuiTabFluxConnectorIndex;
import sonar.flux.client.tabs.GuiTabNetworkConnections;
import sonar.flux.client.tabs.GuiTabNetworkCreate;
import sonar.flux.client.tabs.GuiTabNetworkDebug;
import sonar.flux.client.tabs.GuiTabNetworkEdit;
import sonar.flux.client.tabs.GuiTabNetworkPlayers;
import sonar.flux.client.tabs.GuiTabNetworkSelection;
import sonar.flux.client.tabs.GuiTabNetworkStatistics;
import sonar.flux.client.tabs.GuiTabStorageIndex;
import sonar.flux.common.tileentity.TileController;
import sonar.flux.common.tileentity.TileFlux;
import sonar.flux.common.tileentity.TileFluxPlug;
import sonar.flux.common.tileentity.TileFluxPoint;
import sonar.flux.common.tileentity.TileStorage;

public enum GuiTab {
	INDEX(0), NETWORK_SELECTION(128), CONNECTIONS(64), NETWORK_STATISTICS(192), NETWORK_EDIT(256), PLAYERS(386), NETWORK_CREATE(320), DEBUG(450);

	public int texX;
	
	GuiTab(int texX){
		this.texX = texX;
	}
	
	public String getClientName() {
		switch(this){
		case INDEX:
			return FluxTranslate.GUI_TAB_INDEX.t();
		case NETWORK_SELECTION:
			return FluxTranslate.GUI_TAB_NETWORK_SELECTION.t();
		case CONNECTIONS:
			return FluxTranslate.GUI_TAB_CONNECTIONS.t();
		case NETWORK_STATISTICS:
			return FluxTranslate.GUI_TAB_STATISTICS.t();
		case NETWORK_EDIT:
			return FluxTranslate.GUI_TAB_NETWORK_EDIT.t();
		case PLAYERS:
			return FluxTranslate.GUI_TAB_PLAYERS.t();
		case NETWORK_CREATE:
			return FluxTranslate.GUI_TAB_NETWORK_CREATE.t();
		case DEBUG:
			return FluxTranslate.GUI_TAB_DEBUG.t();	
		}
		return name();
	}
	
	public List<FluxListener> getMonitoringTypes(){
		switch(this){
		case CONNECTIONS:
			return Lists.newArrayList(FluxListener.SYNC_NETWORK_CONNECTIONS);
		case INDEX:
			return Lists.newArrayList(FluxListener.SYNC_INDEX, FluxListener.SYNC_NETWORK_STATS);
		case NETWORK_CREATE:
			return Lists.newArrayList(FluxListener.SYNC_INDEX);
		case NETWORK_EDIT:
			return Lists.newArrayList(FluxListener.SYNC_INDEX);
		case NETWORK_SELECTION:
			return Lists.newArrayList(FluxListener.SYNC_NETWORK_LIST);
		case NETWORK_STATISTICS:
			return Lists.newArrayList(FluxListener.SYNC_NETWORK_STATS);
		case PLAYERS:
			return Lists.newArrayList(FluxListener.SYNC_PLAYERS, FluxListener.SYNC_NETWORK_CONNECTIONS);		
		case DEBUG:
			break;
		default:
			break;
		}
		return new ArrayList<>();
	}
	
	public Object getGuiScreen(TileFlux flux, List<GuiTab> tabs){
		switch(this){
		case CONNECTIONS:
			return new GuiTabNetworkConnections(flux, tabs);
		case INDEX:
			if(flux instanceof TileController){
				return new GuiTabControllerIndex((TileController) flux, tabs);
			}
			if(flux instanceof TileStorage){
				return new GuiTabStorageIndex((TileStorage) flux, tabs);
			}
			if(flux instanceof TileFluxPlug){
				return new GuiTabFluxConnectorIndex((TileFluxPlug) flux, tabs);
			}
			if(flux instanceof TileFluxPoint){
				return new GuiTabFluxConnectorIndex((TileFluxPoint) flux, tabs);
			}
			return new GuiTabConnectionIndex(flux, tabs);
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
		}
		return null;	
	}
	
}
