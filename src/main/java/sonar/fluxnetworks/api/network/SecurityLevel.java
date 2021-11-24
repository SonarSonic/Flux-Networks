package sonar.fluxnetworks.api.network;

import sonar.fluxnetworks.api.FluxTranslate;

import javax.annotation.Nonnull;

public enum SecurityLevel {
    PUBLIC(FluxTranslate.PUBLIC),
    ENCRYPTED(FluxTranslate.ENCRYPTED),
    PRIVATE(FluxTranslate.PRIVATE);

    private static final SecurityLevel[] VALUES = values();

    private final FluxTranslate localization;

    SecurityLevel(FluxTranslate localization) {
        this.localization = localization;
    }

    @Nonnull
    public static SecurityLevel fromId(byte id) {
        return VALUES[id];
    }

    public byte getId() {
        return (byte) ordinal();
    }

    @Nonnull
    public String getName() {
        return localization.t();
    }
}
