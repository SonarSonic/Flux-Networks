package sonar.fluxnetworks.common.connection;

import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongList;
import net.minecraft.nbt.CompoundTag;
import sonar.fluxnetworks.common.device.FluxDeviceEntity;

import java.util.List;

public class NetworkStatistics {

    public static final int CHANGE_COUNT = 6;

    private final FluxNetwork network;

    private int timer;

    public int fluxPlugCount;
    public int fluxPointCount;
    public int fluxControllerCount;
    public int fluxStorageCount;

    public long energyInput;
    public long energyOutput;

    public final LongList energyChange = new LongArrayList(CHANGE_COUNT);

    public long totalBuffer;
    public long totalEnergy;

    private long energyChange5;
    private long energyInput4;
    private long energyOutput4;

    public int averageTickMicro;
    private long runningTotalNano;

    private long startNanoTime;

    public NetworkStatistics(FluxNetwork network) {
        this.network = network;
        energyChange.size(CHANGE_COUNT);
    }

    public void startProfiling() {
        startNanoTime = System.nanoTime();
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
        runningTotalNano += System.nanoTime() - startNanoTime;

        timer = ++timer % 100;
    }

    /**
     * Called every 5 ticks
     */
    private void weakTick() {
        List<FluxDeviceEntity> plugs = network.getLogicalEntities(FluxNetwork.PLUG);
        plugs.forEach(p -> {
            if (!p.getDeviceType().isStorage()) {
                energyInput4 += p.getTransferChange();
            }
        });
        List<FluxDeviceEntity> points = network.getLogicalEntities(FluxNetwork.POINT);
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
        List<FluxDeviceEntity> devices = network.getLogicalEntities(FluxNetwork.ANY);
        devices.forEach(p -> {
            if (!p.getDeviceType().isStorage()) {
                totalBuffer += p.getTransferBuffer();
            }
        });
        List<FluxDeviceEntity> storages = network.getLogicalEntities(FluxNetwork.STORAGE);
        storages.forEach(p -> totalEnergy += p.getTransferBuffer());
        fluxControllerCount = network.getLogicalEntities(FluxNetwork.CONTROLLER).size();
        fluxStorageCount = storages.size();
        fluxPlugCount = network.getLogicalEntities(FluxNetwork.PLUG).size() - fluxStorageCount;
        fluxPointCount = network.getLogicalEntities(FluxNetwork.POINT).size() - fluxStorageCount - fluxControllerCount;
        energyInput = energyInput4 / 4;
        energyOutput = energyOutput4 / 4;
        energyInput4 = 0;
        energyOutput4 = 0;
        energyChange5 += Math.max(energyInput, energyOutput);

        averageTickMicro = (int) Math.min(runningTotalNano / 20000, Integer.MAX_VALUE);
        runningTotalNano = 0;
    }

    /**
     * Called every 100 ticks
     */
    private void weakestTick() {
        for (int i = 1; i < CHANGE_COUNT; i++) {
            energyChange.set(i - 1, energyChange.getLong(i));
        }
        energyChange.set(CHANGE_COUNT - 1, energyChange5 / 5);
        energyChange5 = 0;
    }

    public int getConnectionCount() {
        return this.fluxPlugCount + this.fluxPointCount + this.fluxStorageCount + this.fluxControllerCount;
    }

    public void writeNBT(CompoundTag tag) {
        tag.putInt("1", fluxPlugCount);
        tag.putInt("2", fluxPointCount);
        tag.putInt("3", fluxControllerCount);
        tag.putInt("4", fluxStorageCount);
        tag.putLong("5", energyInput);
        tag.putLong("6", energyOutput);
        tag.putLong("7", totalBuffer);
        tag.putLong("8", totalEnergy);
        tag.putInt("9", averageTickMicro);
        tag.putLongArray("a", energyChange);
    }

    public void readNBT(CompoundTag tag) {
        fluxPlugCount = tag.getInt("1");
        fluxPointCount = tag.getInt("2");
        fluxControllerCount = tag.getInt("3");
        fluxStorageCount = tag.getInt("4");
        energyInput = tag.getLong("5");
        energyOutput = tag.getLong("6");
        totalBuffer = tag.getLong("7");
        totalEnergy = tag.getLong("8");
        averageTickMicro = tag.getInt("9");
        long[] a = tag.getLongArray("a");
        for (int i = 0; i < a.length; i++) {
            energyChange.set(i, a[i]);
        }
    }
}
