package fluxnetworks.common.handler.general;

import fluxnetworks.api.network.IFluxNetwork;
import fluxnetworks.common.connection.FluxNetworkServer;
import fluxnetworks.common.connection.manager.FluxSettingManager;

public class FluxDataHandler {

    public static final FluxDataHandler INSTANCE = new FluxDataHandler();

    public IFluxNetwork createdNetwork() {
        FluxSettingManager settingManager = new FluxSettingManager(1);
        IFluxNetwork network = new FluxNetworkServer(settingManager);
        return network;
    }
}
