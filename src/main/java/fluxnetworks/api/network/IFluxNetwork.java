package fluxnetworks.api.network;

import fluxnetworks.api.utils.NBTType;
import net.minecraft.nbt.CompoundNBT;

public interface IFluxNetwork {

    default int getNetworkID() {
        return getSetting().getNetworkID();
    }

    INetworkSetting getSetting();

    void tick();

    void readNetworkNBT(CompoundNBT nbt, NBTType type);

    CompoundNBT writeNetworkNBT(CompoundNBT nbt, NBTType type);
}
