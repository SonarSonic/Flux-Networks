package sonar.fluxnetworks.api.misc;

import java.util.UUID;

//TODO add things
public class FluxConstants {

    public static final int INVALID_NETWORK_ID = -1;
    public static final int INVALID_NETWORK_COLOR = 0xb2b2b2;

    public static final UUID DEFAULT_UUID = new UUID(-1, -1);

    public static final int FLAG_SAVE_ALL = 1;
    public static final int FLAG_TILE_UPDATE = 2;
    public static final int FLAG_TILE_DROP = 11;

    public static final int FLAG_NET_BASIS = 21;
    public static final int FLAG_NET_MEMBERS = 22;
    public static final int FLAG_NET_CONNECTIONS = 23;
    public static final int FLAG_NET_STATISTICS = 24;
    public static final int FLAG_NET_DELETE = 32;

    public static final String NETWORK_ID = "networkID";
    public static final String NETWORK_NAME = "networkName";
    public static final String NETWORK_COLOR = "networkColor";
    public static final String OWNER_UUID = "ownerUUID";
    public static final String PLAYER_LIST = "playerList";
    public static final String CONNECTIONS = "connections";
}
