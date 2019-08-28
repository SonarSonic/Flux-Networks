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

    long addToNetwork(long maxAmount, boolean simulate);

    long removeFromNetwork(long maxAmount, boolean simulate, boolean pre);

    NBTTagCompound writeNetworkedNBT(NBTTagCompound tag);

    void readNetworkedNBT(NBTTagCompound tag);
}
