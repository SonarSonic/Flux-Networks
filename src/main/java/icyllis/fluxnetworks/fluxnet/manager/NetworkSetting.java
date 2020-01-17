package icyllis.fluxnetworks.fluxnet.manager;

import icyllis.fluxnetworks.api.network.INetworkSetting;

public class NetworkSetting implements INetworkSetting {

    private int networkID;

    public NetworkSetting() {

    }

    @Override
    public int getNetworkID() {
        return networkID;
    }

    @Override
    public void build(int networkID) {
        this.networkID = networkID;
    }
}
