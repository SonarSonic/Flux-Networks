package sonar.flux.client;

import sonar.flux.network.FluxNetworkCache.ViewingType;

public enum GuiState {
	INDEX(176, 166), NETWORK_SELECT(176, 166), NETWORK_STATS(176, 166), NETWORK_CREATE(176, 166), NETWORK_EDIT(176, 166), CONNECTIONS(176, 166), PLAYERS(176, 166);
	int x, y;

	GuiState(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public boolean hasScrollBar() {
		return (this == NETWORK_SELECT || this == CONNECTIONS || this == PLAYERS);
	}
	
	public ViewingType getViewingType(){
		if(this==CONNECTIONS){
			return ViewingType.CONNECTIONS;
		}		
		return ViewingType.NETWORK;		
	}
}