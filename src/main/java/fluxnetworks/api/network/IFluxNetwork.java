package fluxnetworks.api.network;

import fluxnetworks.api.utils.INetworkNBT;
import fluxnetworks.api.utils.NBTType;
import net.minecraft.nbt.CompoundNBT;

public interface IFluxNetwork extends INetworkNBT {

    default int getNetworkID() {
        return getSetting().getNetworkID();
    }

    INetworkSetting getSetting();

    void tick();
}
