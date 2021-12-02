package sonar.fluxnetworks.api.network;

import sonar.fluxnetworks.api.FluxTranslate;

import javax.annotation.Nonnull;

public enum SecurityLevel {
    PUBLIC(FluxTranslate.PUBLIC),
    ENCRYPTED(FluxTranslate.ENCRYPTED),
    PRIVATE(FluxTranslate.PRIVATE);

    public static final SecurityLevel[] VALUES = values();

    private final String mTranslateKey;

    SecurityLevel(String translateKey) {
        mTranslateKey = translateKey;
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
        return FluxTranslate.translate(mTranslateKey);
    }
}
