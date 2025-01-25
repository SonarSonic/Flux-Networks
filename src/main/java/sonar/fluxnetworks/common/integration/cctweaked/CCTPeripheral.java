package sonar.fluxnetworks.common.integration.cctweaked;

import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.lua.ObjectLuaTable;
import dan200.computercraft.api.peripheral.IPeripheral;
import org.jetbrains.annotations.Nullable;
import sonar.fluxnetworks.api.device.IFluxDevice;
import sonar.fluxnetworks.common.device.TileFluxDevice;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class CCTPeripheral implements IPeripheral {

    record NetworkMemberRecord(String name, String accessLevel) {
    }

    record NetworkDeviceRecord(
            String type,
            long maxTransferLimit,
            boolean isChunkLoaded,

            boolean isForcedChunkLoaded,
            String customName,
            boolean limitDisabled,

            Map<String, Object> position,
            boolean surgeMode,
            long transferBuffer,
            long transferChange
    ) {}

    private final TileFluxDevice device;

    public CCTPeripheral(TileFluxDevice device) {
        this.device = device;
    }

    @Override
    public String getType() {
        return "flux-device";
    }

    @Override
    public boolean equals(@Nullable IPeripheral other) {
        return false;
    }

    @LuaFunction(mainThread = true)
    public String getNetworkName() {
        if (!isValid()) return null;

        String name = device.getNetwork().getNetworkName();
        if (name.isEmpty())
            return null;
        return name;
    }

    private Map<String, Object> objectToMap(Object object) {
        Map<String, Object> map = new HashMap<>();
        Field[] fields = object.getClass().getDeclaredFields();

        for (Field field: fields) {
            field.setAccessible(true);
            try {
                map.put(field.getName(), field.get(object));
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        return map;
    }

    @LuaFunction(mainThread = true)
    public int getNetworkID() {
        return device.getNetwork().getNetworkID();
    }

    @LuaFunction(mainThread = true)
    public long getEnergyInput() {
        return device.getNetwork().getStatistics().energyInput;
    }

    @LuaFunction(mainThread = true)
    public long getEnergyOutput() {
        return device.getNetwork().getStatistics().energyOutput;
    }

    @LuaFunction(mainThread = true)
    public long getEnergy() {
        return device.getNetwork().getStatistics().totalEnergy;
    }

    @LuaFunction(mainThread = true)
    public long getEnergyBuffer() {
        return device.getNetwork().getStatistics().totalBuffer;
    }

    @LuaFunction(mainThread = true)
    public long getPlugsCount() {
        return device.getNetwork().getStatistics().fluxPlugCount;
    }

    @LuaFunction(mainThread = true)
    public long getPointCount() {
        return device.getNetwork().getStatistics().fluxPointCount;
    }

    @LuaFunction(mainThread = true)
    public long getStorageCount() {
        return device.getNetwork().getStatistics().fluxStorageCount;
    }

    @LuaFunction(mainThread = true)
    public long getControllerCount() {
        return device.getNetwork().getStatistics().fluxControllerCount;
    }

    @LuaFunction(mainThread = true)
    public long getAVGTickUs() {
        return device.getNetwork().getStatistics().averageTickMicro;
    }

    @LuaFunction(mainThread = true)
    public String getSecurityLevel() {
        return device.getNetwork().getSecurityLevel().toString();
    }

    @LuaFunction(mainThread = true)
    public String getOwner() {
        if (!isValid()) return null;

        return device.getNetwork().getOwnerUUID().toString();
    }

    @LuaFunction(mainThread = true)
    public ObjectLuaTable getMembers() {
        if (!isValid()) return null;

        return new ObjectLuaTable(
                device.getNetwork().getAllMembers().stream().collect(Collectors.toMap(
                        item -> item.getPlayerUUID().toString(),
                        item -> objectToMap(new NetworkMemberRecord(item.getCachedName(), item.getAccessLevel().toString()))
                )
            )
        );
    }

    @LuaFunction(mainThread = true)
    public ObjectLuaTable getConnections() {
        if (!isValid()) return null;

        Map<Integer, Object> map = new HashMap<>();
        int i = 0;
        for (IFluxDevice device :  device.getNetwork().getAllConnections()) {
            map.put(
                    i++,
                    objectToMap(
                        new NetworkDeviceRecord(
                                device.getDeviceType().toString(),
                                device.getMaxTransferLimit(),
                                device.isChunkLoaded(),
                                device.isForcedLoading(),
                                device.getCustomName(),
                                device.getDisableLimit(),
                                objectToMap(device.getGlobalPos()),
                                device.getSurgeMode(),
                                device.getTransferBuffer(),
                                device.getTransferChange()
                        )
                    )
            );
        }

        return new ObjectLuaTable(map);
    }

    @LuaFunction(mainThread = true)
    public boolean isValid() {
        return device.getNetwork().isValid();
    }
}
