package sonar.flux.api;

public enum FluxListener {
	
    FULL_NETWORK, 
    ADMIN,
    //sync all networks
    SYNC_NETWORK, 
    //sync current networks statistics
    STATISTICS, 
    //sync current networks connections
    CONNECTIONS;

    public boolean forceSync() {
        return this == FULL_NETWORK;
    }
}