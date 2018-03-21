package sonar.flux.api;

public enum FluxListener {
	
    ADMIN,
    //sync main flux properties
    SYNC_INDEX, 
    //sync current networks statistics
    SYNC_NETWORK_LIST,
    //sync current networks connections
    SYNC_NETWORK_CONNECTIONS,  
    //sync network stats
    SYNC_NETWORK_STATS,
    //sync all connected players
    SYNC_PLAYERS;
}