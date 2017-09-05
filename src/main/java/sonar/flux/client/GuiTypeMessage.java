package sonar.flux.client;

import sonar.flux.api.FluxListener;

public enum GuiTypeMessage {
    INDEX, NETWORK_SELECT, CONNECTIONS, NETWORK_STATS, NETWORK_EDIT, PLAYERS, NETWORK_CREATE;

    public FluxListener getViewingType() {
        if (this == CONNECTIONS) {
            return FluxListener.CONNECTIONS;
        }
        if (this == NETWORK_STATS) {
            return FluxListener.STATISTICS;
        }
        return FluxListener.SYNC_NETWORK;
    }
}