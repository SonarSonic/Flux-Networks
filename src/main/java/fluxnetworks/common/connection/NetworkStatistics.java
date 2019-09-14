package fluxnetworks.common.connection;

import fluxnetworks.api.network.FluxType;
import fluxnetworks.api.network.IFluxNetwork;
import net.minecraft.nbt.NBTTagCompound;

public class NetworkStatistics {

    public final IFluxNetwork network;

    private int timer;

    public int fluxPlugCount;
    public int fluxPointCount;
    public int fluxControllerCount;
    public int fluxStorageCount;


    public NetworkStatistics(IFluxNetwork network) {
        this.network = network;
    }

    public void onEndServerTick() {
        if(timer == 0) {
            weakTick();
        }
        timer++;
        timer %= 20;
    }

    public void weakTick() {
        fluxControllerCount = network.getConnections(FluxType.controller).size();
        fluxStorageCount = network.getConnections(FluxType.storage).size();
        fluxPlugCount = network.getConnections(FluxType.plug).size() - fluxStorageCount;
        fluxPointCount = network.getConnections(FluxType.point).size() - fluxStorageCount - fluxControllerCount;
    }

    public NBTTagCompound writeNBT(NBTTagCompound tag) {
        tag.setInteger("i1", fluxPlugCount);
        tag.setInteger("i2", fluxPointCount);
        tag.setInteger("i3", fluxControllerCount);
        tag.setInteger("i4", fluxStorageCount);
        return tag;
    }

    public void readNBT(NBTTagCompound tag) {
        fluxPlugCount = tag.getInteger("i1");
        fluxPointCount = tag.getInteger("i2");
        fluxControllerCount = tag.getInteger("i3");
        fluxStorageCount = tag.getInteger("i4");
    }
}
