package sonar.fluxnetworks.api.network;

import sonar.fluxnetworks.api.FluxTranslate;

import javax.annotation.Nonnull;

public enum SecurityLevel {
    PUBLIC("gui.fluxnetworks.security.public"),
    ENCRYPTED("gui.fluxnetworks.security.encrypted"),
    PRIVATE("gui.fluxnetworks.security.private");

    /**
     * Prefers this without creating new array objects.
     */
    public static final SecurityLevel[] VALUES = values();

    private final String mTranslateKey;

    SecurityLevel(String translateKey) {
        mTranslateKey = translateKey;
    }

    @Nonnull
    public static SecurityLevel byId(int id) {
        return VALUES[id];
    }

    public byte getId() {
        return (byte) ordinal();
    }

    @Nonnull
    public String getText() {
        return FluxTranslate.translate(mTranslateKey);
    }
}
