package icyllis.fluxnetworks.system.util;

import icyllis.fluxnetworks.network.FluxNetworkServer;

import java.util.UUID;

public class FluxLibs {

    public static final String TAG_DROP = "FluxData";

    public static UUID UUID_EMPTY = new UUID(-1, -1);

    public static final FluxNetworkServer INVALID_NETWORK = new FluxNetworkServer();
    static {
        INVALID_NETWORK.getSetting().build(-1);
    }
}
