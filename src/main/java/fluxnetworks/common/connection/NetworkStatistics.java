package fluxnetworks.common.connection;

import fluxnetworks.api.network.FluxType;
import fluxnetworks.api.network.IFluxNetwork;

public class NetworkStatistics {

    public final IFluxNetwork network;

    private int timer;

    public int fluxPlugCount;
    public int fluxPointCount;
    public int fluxControllerCount;
    public int fluxStorageCount;


    public NetworkStatistics(IFluxNetwork network) {
        this.network = network;
    }

    public void onEndServerTick() {
        if(timer == 0) {
            weakTick();
        }
        timer++;
        timer %= 20;
    }

    public void weakTick() {
        fluxControllerCount = network.getConnections(FluxType.controller).size();
        fluxStorageCount = network.getConnections(FluxType.storage).size();
        fluxPlugCount = network.getConnections(FluxType.plug).size() - fluxStorageCount;
        fluxPointCount = network.getConnections(FluxType.point).size() - fluxStorageCount - fluxControllerCount;
    }
}
