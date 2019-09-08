package fluxnetworks.api.tileentity;

import net.minecraft.nbt.NBTTagCompound;

public interface ILiteConnector extends IFluxConnector {

    NBTTagCompound writeNetworkData(NBTTagCompound tag);

    void readNetworkData(NBTTagCompound tag);
}
