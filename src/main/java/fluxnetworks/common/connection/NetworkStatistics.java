package fluxnetworks.common.connection;

import fluxnetworks.api.network.FluxType;
import fluxnetworks.api.network.IFluxNetwork;
import fluxnetworks.api.tileentity.IFluxPlug;
import fluxnetworks.api.tileentity.IFluxPoint;
import fluxnetworks.api.tileentity.IFluxStorage;
import fluxnetworks.common.tileentity.TileFluxPlug;
import fluxnetworks.common.tileentity.TileFluxStorage;
import net.minecraft.nbt.NBTTagCompound;

import java.util.ArrayList;
import java.util.List;

public class NetworkStatistics {

    public final IFluxNetwork network;

    private int timer;

    public int fluxPlugCount;
    public int fluxPointCount;
    public int fluxControllerCount;
    public int fluxStorageCount;

    public long energyInput;
    public long energyOutput;
    public List<Long> energyChange = new ArrayList<>();

    public long totalBuffer;
    public long totalEnergy;

    private long energyChange5;
    private long energyInput4;
    private long energyOutput4;

    public NetworkStatistics(IFluxNetwork network) {
        this.network = network;
        for (int i = 0; i < 6; i++) {
            energyChange.add(0L);
        }
    }

    public void onEndServerTick() {
        if(timer == 0) {
            weakestTick();
        }
        if(timer % 5 == 0) {
            weakTick();
        }
        if(timer % 20 == 0) {
            weakerTick();
        }
        timer++;
        timer %= 100;
    }

    /**
     * Called every 5 ticks
     */
    @SuppressWarnings("unchecked")
    private void weakTick() {
        List<IFluxPlug> plugs = network.getConnections(FluxType.plug);
        plugs.forEach(p -> {
            if(!(p instanceof TileFluxStorage)) {
                energyInput4 += p.getTransferHandler().getChange();
            }
        });
        List<IFluxPoint> points = network.getConnections(FluxType.point);
        points.forEach(p -> {
            if(!(p instanceof TileFluxStorage)) {
                energyOutput4 -= p.getTransferHandler().getChange();
            }
        });
    }

    /**
     * Called every 20 ticks
     */
    @SuppressWarnings("unchecked")
    private void weakerTick() {
        totalBuffer = 0;
        totalEnergy = 0;
        List<IFluxPlug> plugs = network.getConnections(FluxType.plug);
        plugs.forEach(p -> {
            if(p instanceof TileFluxPlug) {
                totalBuffer += p.getTransferHandler().getBuffer();
            }
        });
        List<IFluxStorage> storages = network.getConnections(FluxType.storage);
        storages.forEach(p -> totalEnergy += p.getTransferHandler().getEnergyStored());
        fluxControllerCount = network.getConnections(FluxType.controller).size();
        fluxStorageCount = storages.size();
        fluxPlugCount = plugs.size() - fluxStorageCount;
        fluxPointCount = network.getConnections(FluxType.point).size() - fluxStorageCount - fluxControllerCount;
        energyInput = energyInput4 / 4;
        energyOutput = energyOutput4 / 4;
        energyInput4 = 0;
        energyOutput4 = 0;
        energyChange5 += Math.max(energyInput, energyOutput);
    }

    /**
     * Called every 100 ticks
     */
    private void weakestTick() {
        long change = energyChange5 / 5;
        energyChange5 = 0;
        for (int i = 0; i < energyChange.size(); i++) {
            if(i > 0) {
                energyChange.set(i - 1, energyChange.get(i));
            }
        }
        energyChange.set(5, change);
    }

    public NBTTagCompound writeNBT(NBTTagCompound tag) {
        tag.setInteger("i1", fluxPlugCount);
        tag.setInteger("i2", fluxPointCount);
        tag.setInteger("i3", fluxControllerCount);
        tag.setInteger("i4", fluxStorageCount);
        tag.setLong("l1", energyInput);
        tag.setLong("l2", energyOutput);
        tag.setLong("l3", totalBuffer);
        tag.setLong("l4", totalEnergy);
        for (int i = 0; i < energyChange.size(); i++) {
            tag.setLong("a" + i, energyChange.get(i));
        }
        return tag;
    }

    public void readNBT(NBTTagCompound tag) {
        fluxPlugCount = tag.getInteger("i1");
        fluxPointCount = tag.getInteger("i2");
        fluxControllerCount = tag.getInteger("i3");
        fluxStorageCount = tag.getInteger("i4");
        energyInput = tag.getLong("l1");
        energyOutput = tag.getLong("l2");
        totalBuffer = tag.getLong("l3");
        totalEnergy = tag.getLong("l4");
        for (int i = 0; i < 6; i++) {
            energyChange.set(i, tag.getLong("a" + i));
        }
    }
}
