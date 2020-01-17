package icyllis.fluxnetworks.fluxnet;

import icyllis.fluxnetworks.api.network.IFluxNetwork;
import icyllis.fluxnetworks.api.network.INetworkSetting;
import icyllis.fluxnetworks.api.network.INetworkTransfer;
import icyllis.fluxnetworks.api.util.NBTType;
import icyllis.fluxnetworks.fluxnet.manager.NetworkSetting;
import icyllis.fluxnetworks.fluxnet.manager.NetworkTransfer;
import net.minecraft.nbt.CompoundNBT;

public class FluxNetworkServer implements IFluxNetwork {

    private final INetworkSetting setting = new NetworkSetting();
    private final INetworkTransfer transfer = new NetworkTransfer();

    public FluxNetworkServer() {

    }

    @Override
    public INetworkSetting getSetting() {
        return setting;
    }

    @Override
    public INetworkTransfer getTransfer() {
        return transfer;
    }

    @Override
    public void tick() {

    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public void readNetworkNBT(CompoundNBT nbt, NBTType type) {

    }

    @Override
    public CompoundNBT writeNetworkNBT(CompoundNBT nbt, NBTType type) {
        return null;
    }
}
