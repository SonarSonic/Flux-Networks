package sonar.flux.client;

import java.util.List;

import sonar.flux.api.tiles.IFluxController;
import sonar.flux.api.tiles.IFluxStorage;
import sonar.flux.client.tabs.GuiTabConnectionIndex;
import sonar.flux.client.tabs.GuiTabControllerIndex;
import sonar.flux.client.tabs.GuiTabNetworkConnections;
import sonar.flux.client.tabs.GuiTabNetworkCreate;
import sonar.flux.client.tabs.GuiTabNetworkEdit;
import sonar.flux.client.tabs.GuiTabNetworkPlayers;
import sonar.flux.client.tabs.GuiTabNetworkSelection;
import sonar.flux.client.tabs.GuiTabNetworkStatistics;
import sonar.flux.client.tabs.GuiTabStorageIndex;
import sonar.flux.common.tileentity.TileController;
import sonar.flux.common.tileentity.TileFlux;
import sonar.flux.common.tileentity.TileStorage;

public enum GuiTab {
	INDEX(0), NETWORK_SELECT(128), CONNECTIONS(64), NETWORK_STATS(192), NETWORK_EDIT(256), PLAYERS(386), NETWORK_CREATE(320);

	public int texX;
	
	GuiTab(int texX){
		this.texX = texX;
	}
	
	public String getClientName() {
		return name();
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
			return new GuiTabConnectionIndex(flux, tabs);
		case NETWORK_CREATE:
			return new GuiTabNetworkCreate(flux, tabs);
		case NETWORK_EDIT:
			return new GuiTabNetworkEdit(flux, tabs);
		case NETWORK_SELECT:
			return new GuiTabNetworkSelection(flux, tabs);
		case NETWORK_STATS:
			return new GuiTabNetworkStatistics(flux, tabs);
		case PLAYERS:
			return new GuiTabNetworkPlayers(flux, tabs);
		default:
			break;	
		}
		return null;	
	}
	
}
