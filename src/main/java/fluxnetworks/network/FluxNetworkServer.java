package fluxnetworks.network;

import fluxnetworks.api.network.IFluxNetwork;
import fluxnetworks.api.network.INetworkSetting;
import fluxnetworks.api.utils.NBTType;
import fluxnetworks.network.manager.NetworkSettingManager;
import net.minecraft.nbt.CompoundNBT;

public class FluxNetworkServer implements IFluxNetwork {

    private final INetworkSetting setting = new NetworkSettingManager();

    public FluxNetworkServer() {

    }

    @Override
    public INetworkSetting getSetting() {
        return setting;
    }

    @Override
    public void tick() {

    }

    @Override
    public void readNetworkNBT(CompoundNBT nbt, NBTType type) {

    }

    @Override
    public CompoundNBT writeNetworkNBT(CompoundNBT nbt, NBTType type) {
        return null;
    }
}
