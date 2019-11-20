package icyllis.fluxnetworks.api.network;

import icyllis.fluxnetworks.api.util.INetworkNBT;

public interface IFluxNetwork extends INetworkNBT {

    default int getNetworkID() {
        return getSetting().getNetworkID();
    }

    INetworkSetting getSetting();

    void tick();
}
