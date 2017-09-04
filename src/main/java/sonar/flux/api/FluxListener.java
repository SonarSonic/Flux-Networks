package sonar.flux.api;

public enum FluxListener {
    FULL_NETWORK, SYNC_NETWORK, STATISTICS, CONNECTIONS; // FIX THIS

    public boolean forceSync() {
        return this == FULL_NETWORK;
    }
}