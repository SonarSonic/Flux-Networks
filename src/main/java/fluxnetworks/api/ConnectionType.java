package fluxnetworks.api;

public enum ConnectionType {
    CONTROLLER(0x647878),
    POINT(0x882828),
    PLUG(0x609732),
    STORAGE(0x295e8a);

    public int color;

    ConnectionType(int color) {
        this.color = color;
    }

    public boolean canAddEnergy() {
        return this == PLUG;
    }

    public boolean canRemoveEnergy() {
        return this == POINT;
    }

    public boolean isController() {
        return this == CONTROLLER;
    }

    public boolean isStorage() {
        return this == STORAGE;
    }
}
