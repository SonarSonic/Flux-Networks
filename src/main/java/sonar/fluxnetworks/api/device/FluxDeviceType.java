package sonar.fluxnetworks.api.device;

import javax.annotation.Nonnull;

public enum FluxDeviceType {
    POINT(0x882828),
    PLUG(0x609732),
    STORAGE(0x295e8a),
    CONTROLLER(0x647878);

    public static final FluxDeviceType[] VALUES = values();

    public final int mColor;

    FluxDeviceType(int color) {
        mColor = color;
    }

    @Nonnull
    public static FluxDeviceType fromId(byte id) {
        return VALUES[id];
    }

    public byte getId() {
        return (byte) ordinal();
    }

    public boolean isPlug() {
        return this == PLUG;
    }

    public boolean isPoint() {
        return this == POINT;
    }

    public boolean isController() {
        return this == CONTROLLER;
    }

    public boolean isStorage() {
        return this == STORAGE;
    }

    public boolean is(@Nonnull IFluxDevice device) {
        return this == device.getDeviceType();
    }
}
