package sonar.flux.client;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Lists;

import sonar.flux.api.FluxListener;
import sonar.flux.client.tabs.GuiTabConnectionIndex;
import sonar.flux.client.tabs.GuiTabControllerIndex;
import sonar.flux.client.tabs.GuiTabFluxConnectorIndex;
import sonar.flux.client.tabs.GuiTabNetworkConnections;
import sonar.flux.client.tabs.GuiTabNetworkCreate;
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
	INDEX(0), NETWORK_SELECTION(128), CONNECTIONS(64), NETWORK_STATISTICS(192), NETWORK_EDIT(256), PLAYERS(386), NETWORK_CREATE(320);

	public int texX;
	
	GuiTab(int texX){
		this.texX = texX;
	}
	
	public String getClientName() {
		switch(this){
		case INDEX:
			return GUI.GUI_TAB_INDEX.toString();
		case NETWORK_SELECTION:
			return GUI.GUI_TAB_NETWORK_SELECTION.toString();
		case CONNECTIONS:
			return GUI.GUI_TAB_CONNECTIONS.toString();
		case NETWORK_STATISTICS:
			return GUI.GUI_TAB_STATISTICS.toString();
		case NETWORK_EDIT:
			return GUI.GUI_TAB_NETWORK_EDIT.toString();
		case PLAYERS:
			return GUI.GUI_TAB_PLAYERS.toString();
		case NETWORK_CREATE:
			return GUI.GUI_TAB_NETWORK_CREATE.toString();
		default:
			break;		
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
			//admin screen
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
		}
		return null;	
	}
	
}
