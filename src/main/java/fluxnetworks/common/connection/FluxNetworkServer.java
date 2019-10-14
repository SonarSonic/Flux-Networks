package fluxnetworks.common.connection;

import fluxnetworks.api.network.IFluxNetwork;
import fluxnetworks.common.connection.manager.FluxSettingManager;

public class FluxNetworkServer implements IFluxNetwork {

    private final FluxSettingManager settingManager;

    public FluxNetworkServer(FluxSettingManager settingManager) {
        this.settingManager = settingManager;
    }

    public FluxSettingManager getSettingManager() {
        return settingManager;
    }
}
