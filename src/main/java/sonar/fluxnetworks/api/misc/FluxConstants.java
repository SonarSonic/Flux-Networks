package sonar.fluxnetworks.api.misc;

import java.util.UUID;

public class FluxConstants {

    public static final int INVALID_NETWORK_ID = -1;
    public static final int INVALID_NETWORK_COLOR = 0xb2b2b2;

    public static final UUID DEFAULT_UUID = new UUID(-1, -1);

    // NBT access, storage
    public static final int TYPE_SAVE_ALL = 1;

    // NBT access, tile update or read stack
    public static final int TYPE_TILE_UPDATE = 11;
    public static final int TYPE_TILE_DROP = 19;

    public static final int TYPE_CONNECTION_UPDATE = 20;

    // NBT access, network data-sync or operation
    public static final int TYPE_NET_BASIC = 21;
    public static final int TYPE_NET_MEMBERS = 22;
    public static final int TYPE_NET_CONNECTIONS = 23;
    public static final int TYPE_NET_STATISTICS = 24;
    public static final int TYPE_NET_DELETE = 29;

    // Network connections editing
    public static final int FLAG_EDIT_NAME = 1;
    public static final int FLAG_EDIT_PRIORITY = 1 << 1;
    public static final int FLAG_EDIT_LIMIT = 1 << 2;
    public static final int FLAG_EDIT_SURGE_MODE = 1 << 3;
    public static final int FLAG_EDIT_DISABLE_LIMIT = 1 << 4;
    public static final int FLAG_EDIT_CHUNK_LOADING = 1 << 5;
    public static final int FLAG_EDIT_DISCONNECT = 1 << 6;

    // Network members editing
    public static final int TYPE_NEW_MEMBER = 1;
    public static final int TYPE_SET_ADMIN = 2;
    public static final int TYPE_SET_USER = 3;
    public static final int TYPE_CANCEL_MEMBERSHIP = 4;
    public static final int TYPE_TRANSFER_OWNERSHIP = 5;

    // NBT tag key
    public static final String TAG_FLUX_DATA = "FluxData"; // item stack of device
    public static final String TAG_FLUX_CONFIG = "FluxConfig"; // configurator

    // NBT tag key, root
    public static final String FLUX_COLOR = "FluxColor";

    // NBT tag key
    public static final String NETWORK_ID = "1";
    public static final String CUSTOM_NAME = "2";
    public static final String PRIORITY = "3";
    public static final String LIMIT = "4";
    public static final String FLAGS = "5";
    public static final String PLAYER_UUID = "6";
    public static final String CLIENT_COLOR = "7";

    public static final String SURGE_MODE = "21";
    public static final String DISABLE_LIMIT = "22";
    public static final String DEVICE_TYPE = "23";

    public static final String FORCED_LOADING = "29";
    public static final String CHUNK_LOADED = "31";

    public static final String BUFFER = "71";
    public static final String ENERGY = "72"; // equals to buffer, but with different display text
    public static final String CHANGE = "73";
}
