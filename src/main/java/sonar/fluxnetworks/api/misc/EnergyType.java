package sonar.fluxnetworks.api.misc;

public enum EnergyType {
    FE("Forge Energy", "FE", "FE/t"),
    EU("Energy Units", "EU", "EU/t");

    private final String name;
    private final String storage;
    private final String usage;

    EnergyType(String name, String storage, String usage) {
        this.name = name;
        this.storage = storage;
        this.usage = usage;
    }

    public String getName() {
        return name;
    }

    public String getStorageSuffix() {
        return storage;
    }

    public String getUsageSuffix() {
        return usage;
    }
}
