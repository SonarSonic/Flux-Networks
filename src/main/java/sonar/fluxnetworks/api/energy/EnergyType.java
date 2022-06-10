package sonar.fluxnetworks.api.energy;

import sonar.fluxnetworks.common.util.FluxUtils;

import javax.annotation.Nonnull;
import java.text.NumberFormat;

public enum EnergyType {
    FE("Forge Energy", "FE", "FE/t"),
    EU("Energy Unit", "EU", "EU/t");

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

    @Nonnull
    public String getUsage(long in) {
        return NumberFormat.getInstance().format(in) + " " + usage;
    }

    @Nonnull
    public String getUsageCompact(long in) {
        return FluxUtils.compact(in, usage);
    }

    @Nonnull
    public String getStorage(long in) {
        return NumberFormat.getInstance().format(in) + " " + storage;
    }

    @Nonnull
    public String getStorageCompact(long in) {
        return FluxUtils.compact(in, storage);
    }
}
