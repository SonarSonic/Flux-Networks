package icyllis.fluxnetworks.network.manager;

import icyllis.fluxnetworks.api.network.INetworkSetting;

public class NetworkSettingManager implements INetworkSetting {

    private int networkID;

    public NetworkSettingManager() {

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
