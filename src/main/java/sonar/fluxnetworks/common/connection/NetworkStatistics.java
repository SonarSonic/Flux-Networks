package sonar.fluxnetworks.common.connection;

import net.minecraft.nbt.CompoundNBT;
import sonar.fluxnetworks.api.network.FluxCacheTypes;
import sonar.fluxnetworks.api.network.IFluxNetwork;
import sonar.fluxnetworks.api.tiles.IFluxPlug;
import sonar.fluxnetworks.api.tiles.IFluxPoint;
import sonar.fluxnetworks.api.tiles.IFluxStorage;
import sonar.fluxnetworks.common.tileentity.TileFluxPlug;
import sonar.fluxnetworks.common.tileentity.TileFluxStorage;

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

    public long average_tick_micro = 0;

    public long running_total_micro = 0;
    public long running_total_count = 0;

    public long network_nano_time;
    public long network_tick;

    public NetworkStatistics(IFluxNetwork network) {
        this.network = network;
        for (int i = 0; i < 6; i++) {
            energyChange.add(0L);
        }
    }

    public void startProfiling(){
        network_nano_time = System.nanoTime();
    }

    public void stopProfiling(){
        network_tick += (System.nanoTime()-network_nano_time)/1000;
    }

    public void onStartServerTick() {
        network_tick = 0;
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

        running_total_micro+=network_tick;
        running_total_count ++;

        if(running_total_count >= 20) {
            average_tick_micro = running_total_micro / running_total_count;

            running_total_micro = 0;
            running_total_count = 0;
        }

        timer++;
        timer %= 100;
    }

    /**
     * Called every 5 ticks
     */
    @SuppressWarnings("unchecked")
    private void weakTick() {
        List<IFluxPlug> plugs = network.getConnections(FluxCacheTypes.plug);
        plugs.forEach(p -> {
            if(!(p instanceof TileFluxStorage)) {
                energyInput4 += p.getTransferHandler().getChange();
            }
        });
        List<IFluxPoint> points = network.getConnections(FluxCacheTypes.point);
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
        List<IFluxPlug> plugs = network.getConnections(FluxCacheTypes.plug);
        plugs.forEach(p -> {
            if(p instanceof TileFluxPlug) {
                totalBuffer += p.getTransferHandler().getBuffer();
            }
        });
        List<IFluxStorage> storages = network.getConnections(FluxCacheTypes.storage);
        storages.forEach(p -> totalEnergy += p.getTransferHandler().getEnergyStored());
        fluxControllerCount = network.getConnections(FluxCacheTypes.controller).size();
        fluxStorageCount = storages.size();
        fluxPlugCount = plugs.size() - fluxStorageCount;
        fluxPointCount = network.getConnections(FluxCacheTypes.point).size() - fluxStorageCount - fluxControllerCount;
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

    public int getConnectionCount(){
        return this.fluxPlugCount + this.fluxPointCount + this.fluxStorageCount + this.fluxControllerCount;
    }

    public CompoundNBT writeNBT(CompoundNBT tag) {
        tag.putInt("i1", fluxPlugCount);
        tag.putInt("i2", fluxPointCount);
        tag.putInt("i3", fluxControllerCount);
        tag.putInt("i4", fluxStorageCount);
        tag.putLong("l1", energyInput);
        tag.putLong("l2", energyOutput);
        tag.putLong("l3", totalBuffer);
        tag.putLong("l4", totalEnergy);
        tag.putLong("l5", average_tick_micro);

        for (int i = 0; i < energyChange.size(); i++) {
            tag.putLong("a" + i, energyChange.get(i));
        }
        return tag;
    }

    public void readNBT(CompoundNBT tag) {
        fluxPlugCount = tag.getInt("i1");
        fluxPointCount = tag.getInt("i2");
        fluxControllerCount = tag.getInt("i3");
        fluxStorageCount = tag.getInt("i4");
        energyInput = tag.getLong("l1");
        energyOutput = tag.getLong("l2");
        totalBuffer = tag.getLong("l3");
        totalEnergy = tag.getLong("l4");
        average_tick_micro = tag.getLong("l5");
        for (int i = 0; i < 6; i++) {
            energyChange.set(i, tag.getLong("a" + i));
        }
    }
}
