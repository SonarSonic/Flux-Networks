package sonar.fluxnetworks.api.network;

import sonar.fluxnetworks.api.FluxTranslate;

import javax.annotation.Nonnull;

public enum SecurityLevel {
    PUBLIC(FluxTranslate.PUBLIC),
    ENCRYPTED(FluxTranslate.ENCRYPTED),
    PRIVATE(FluxTranslate.PRIVATE);

    /**
     * Prefers this without creating new array objects.
     */
    public static final SecurityLevel[] VALUES = values();

    private final FluxTranslate mTranslate;

    SecurityLevel(FluxTranslate translate) {
        mTranslate = translate;
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
        return mTranslate.get();
    }
}
