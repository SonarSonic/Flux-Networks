package sonar.flux.client;

import sonar.flux.api.FluxListener;

public enum GuiState {
	INDEX(176, 166), NETWORK_SELECT(176, 166), NETWORK_STATS(176, 166), NETWORK_CREATE(176, 166), NETWORK_EDIT(176, 166), CONNECTIONS(176, 166), EDIT_CONNECTION(176, 166), PLAYERS(176, 166);
	int x, y;

	GuiState(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public boolean hasScrollBar() {
		return (this == NETWORK_SELECT || this == CONNECTIONS || this == PLAYERS);
	}

	public FluxListener getViewingType() {
		if (this == CONNECTIONS || this == EDIT_CONNECTION) {
			return FluxListener.CONNECTIONS;
		}
		return FluxListener.SYNC_NETWORK;
	}

	public String getClientName() {
		switch (this) {
		case CONNECTIONS:
			return "network.nav.config";
		case EDIT_CONNECTION:
			return "network.nav.config";
		case INDEX:
			return "network.nav.home";
		case NETWORK_CREATE:
			return "network.create";
		case NETWORK_EDIT:
			return "network.edit";
		case NETWORK_SELECT:
			return "network.nav.networks";
		case NETWORK_STATS:
			return "network.nav.statistics";
		case PLAYERS:
			return "network.players";
		}
		return "";
	}
}