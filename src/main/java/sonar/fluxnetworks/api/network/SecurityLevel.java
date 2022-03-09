package sonar.fluxnetworks.api.network;

import net.minecraft.locale.Language;

import javax.annotation.Nonnull;

public enum SecurityLevel {
    PUBLIC("gui.fluxnetworks.security.public"),
    ENCRYPTED("gui.fluxnetworks.security.encrypted"),
    PRIVATE("gui.fluxnetworks.security.private");

    /**
     * Prefers this without creating new array objects.
     */
    private static final SecurityLevel[] VALUES = values();

    @Nonnull
    public static SecurityLevel get(int id) {
        return VALUES[id];
    }

    public static int size() {
        return VALUES.length;
    }

    private final String mTranslateKey;

    SecurityLevel(String translateKey) {
        mTranslateKey = translateKey;
    }

    public byte index() {
        return (byte) ordinal();
    }

    public boolean isEncrypted() {
        return this == ENCRYPTED;
    }

    @Nonnull
    public String getText() {
        return Language.getInstance().getOrDefault(mTranslateKey);
    }
}
