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
    public static final SecurityLevel[] VALUES = values();

    @Nonnull
    public static SecurityLevel fromKey(byte id) {
        return VALUES[id];
    }

    private final String mTranslateKey;

    SecurityLevel(String translateKey) {
        mTranslateKey = translateKey;
    }

    public byte getKey() {
        return (byte) ordinal();
    }

    public boolean isEncrypted() {
        return this == ENCRYPTED;
    }

    @Nonnull
    public String getName() {
        return Language.getInstance().getOrDefault(mTranslateKey);
    }
}
