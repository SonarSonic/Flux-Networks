package fluxnetworks.api.network;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;

import java.util.List;

public interface ITransferHandler {

    void onServerStartTick();

    void onWorldEndTick();

    long getBuffer();

    long getRequest();

    long getAdded();

    long getRemoved();

    long getChange();

    void updateTransfers(EnumFacing...faces);

    List<IFluxTransfer> getTransfers();

    long addToNetwork(long maxAmount);

    long removeFromNetwork(long maxAmount, boolean simulate);

    NBTTagCompound writeNetworkedNBT(NBTTagCompound tag);

    void readNetworkedNBT(NBTTagCompound tag);
}
