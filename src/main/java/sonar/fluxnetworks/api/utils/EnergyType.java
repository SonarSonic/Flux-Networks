package sonar.fluxnetworks.api.utils;

public enum EnergyType {
    FE("Forge Energy", "FE", "FE/t"),
    EU("Energy Units", "EU", "EU/t");

    private String name;
    private String storage;
    private String usage;

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
