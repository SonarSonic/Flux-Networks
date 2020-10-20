package sonar.fluxnetworks.common.connection;

import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongList;
import net.minecraft.nbt.CompoundNBT;
import sonar.fluxnetworks.api.device.IFluxPlug;
import sonar.fluxnetworks.api.device.IFluxPoint;
import sonar.fluxnetworks.api.device.IFluxStorage;
import sonar.fluxnetworks.api.network.FluxLogicType;
import sonar.fluxnetworks.api.network.IFluxNetwork;

import java.util.List;

public class NetworkStatistics {

    private final IFluxNetwork network;

    private int timer;

    public int fluxPlugCount;
    public int fluxPointCount;
    public int fluxControllerCount;
    public int fluxStorageCount;

    public long energyInput;
    public long energyOutput;

    public LongList energyChange = new LongArrayList(6);

    public long totalBuffer;
    public long totalEnergy;

    private long energyChange5;
    private long energyInput4;
    private long energyOutput4;

    public long averageTickMicro;
    private long runningTotalMicro;

    private long networkNanoTime;

    public NetworkStatistics(IFluxNetwork network) {
        this.network = network;
        for (int i = 0; i < 6; i++) {
            energyChange.add(0L);
        }
    }

    public void startProfiling() {
        networkNanoTime = System.nanoTime();
    }

    public void stopProfiling() {
        if (timer == 0) {
            weakestTick();
        }
        if (timer % 5 == 0) {
            weakTick();
        }
        if (timer % 20 == 0) {
            weakerTick();
        }
        runningTotalMicro += (System.nanoTime() - networkNanoTime) / 1000;

        timer++;
        timer %= 100;
    }

    /**
     * Called every 5 ticks
     */
    private void weakTick() {
        List<IFluxPlug> plugs = network.getConnections(FluxLogicType.PLUG);
        plugs.forEach(p -> {
            if (!p.getDeviceType().isStorage()) {
                energyInput4 += p.getTransferChange();
            }
        });
        List<IFluxPoint> points = network.getConnections(FluxLogicType.POINT);
        points.forEach(p -> {
            if (!p.getDeviceType().isStorage()) {
                energyOutput4 -= p.getTransferChange();
            }
        });
    }

    /**
     * Called every 20 ticks
     */
    private void weakerTick() {
        totalBuffer = 0;
        totalEnergy = 0;
        List<IFluxPlug> plugs = network.getConnections(FluxLogicType.PLUG);
        plugs.forEach(p -> {
            if (p.getDeviceType().isPlug()) {
                totalBuffer += p.getTransferBuffer();
            }
        });
        List<IFluxStorage> storages = network.getConnections(FluxLogicType.STORAGE);
        storages.forEach(p -> totalEnergy += p.getTransferBuffer());
        fluxControllerCount = network.getConnections(FluxLogicType.CONTROLLER).size();
        fluxStorageCount = storages.size();
        fluxPlugCount = plugs.size() - fluxStorageCount;
        fluxPointCount = network.getConnections(FluxLogicType.POINT).size() - fluxStorageCount - fluxControllerCount;
        energyInput = energyInput4 / 4;
        energyOutput = energyOutput4 / 4;
        energyInput4 = 0;
        energyOutput4 = 0;
        energyChange5 += Math.max(energyInput, energyOutput);

        averageTickMicro = runningTotalMicro / 20;
        runningTotalMicro = 0;
    }

    /**
     * Called every 100 ticks
     */
    private void weakestTick() {
        long change = energyChange5 / 5;
        energyChange5 = 0;
        for (int i = 0; i < energyChange.size(); i++) {
            if (i > 0) {
                energyChange.set(i - 1, energyChange.getLong(i));
            }
        }
        energyChange.set(5, change);
    }

    public int getConnectionCount() {
        return this.fluxPlugCount + this.fluxPointCount + this.fluxStorageCount + this.fluxControllerCount;
    }

    public void writeNBT(CompoundNBT tag) {
        tag.putInt("1", fluxPlugCount);
        tag.putInt("2", fluxPointCount);
        tag.putInt("3", fluxControllerCount);
        tag.putInt("4", fluxStorageCount);
        tag.putLong("5", energyInput);
        tag.putLong("6", energyOutput);
        tag.putLong("7", totalBuffer);
        tag.putLong("8", totalEnergy);
        tag.putLong("9", averageTickMicro);
        for (int i = 0; i < 6; i++) {
            tag.putLong("a" + i, energyChange.getLong(i));
        }
    }

    public void readNBT(CompoundNBT tag) {
        fluxPlugCount = tag.getInt("1");
        fluxPointCount = tag.getInt("2");
        fluxControllerCount = tag.getInt("3");
        fluxStorageCount = tag.getInt("4");
        energyInput = tag.getLong("5");
        energyOutput = tag.getLong("6");
        totalBuffer = tag.getLong("7");
        totalEnergy = tag.getLong("8");
        averageTickMicro = tag.getLong("9");
        for (int i = 0; i < 6; i++) {
            energyChange.set(i, tag.getLong("a" + i));
        }
    }
}
