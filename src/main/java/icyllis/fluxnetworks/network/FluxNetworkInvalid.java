package icyllis.fluxnetworks.network;

import icyllis.fluxnetworks.api.network.IFluxNetwork;
import icyllis.fluxnetworks.api.network.INetworkSetting;
import icyllis.fluxnetworks.api.network.INetworkTransfer;
import icyllis.fluxnetworks.api.util.NBTType;
import net.minecraft.nbt.CompoundNBT;

public class FluxNetworkInvalid implements IFluxNetwork {

    public static final FluxNetworkInvalid INSTANCE = new FluxNetworkInvalid();

    @Override
    public INetworkSetting getSetting() {
        return null;
    }

    @Override
    public INetworkTransfer getTransfer() {
        return null;
    }

    @Override
    public void tick() {

    }

    @Override
    public boolean isValid() {
        return false;
    }

    @Override
    public void readNetworkNBT(CompoundNBT nbt, NBTType type) {

    }

    @Override
    public CompoundNBT writeNetworkNBT(CompoundNBT nbt, NBTType type) {
        return nbt;
    }
}
