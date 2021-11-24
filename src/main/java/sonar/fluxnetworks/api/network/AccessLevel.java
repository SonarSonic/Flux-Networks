package sonar.fluxnetworks.api.network;

import net.minecraft.ChatFormatting;
import sonar.fluxnetworks.api.FluxTranslate;

import javax.annotation.Nonnull;

public enum AccessLevel {
    OWNER(FluxTranslate.OWNER, 0xffaa00, ChatFormatting.GOLD),
    ADMIN(FluxTranslate.ADMIN, 0x66cc00, ChatFormatting.GREEN),
    USER(FluxTranslate.USER, 0x6699ff, ChatFormatting.BLUE),
    BLOCKED(FluxTranslate.BLOCKED, 0xa9a9a9, ChatFormatting.GRAY),
    SUPER_ADMIN(FluxTranslate.SUPER_ADMIN, 0x4b0082, ChatFormatting.DARK_PURPLE);

    private static final AccessLevel[] VALUES = values();

    private final FluxTranslate localization;
    private final int color;
    private final ChatFormatting formatting;

    AccessLevel(FluxTranslate localization, int color, ChatFormatting formatting) {
        this.localization = localization;
        this.color = color;
        this.formatting = formatting;
    }

    @Nonnull
    public static AccessLevel fromId(byte id) {
        return VALUES[id];
    }

    public byte getId() {
        return (byte) ordinal();
    }

    @Nonnull
    public String getName() {
        return formatting + localization.t();
    }

    public int getColor() {
        return color;
    }

    public boolean canUse() {
        return this != BLOCKED;
    }

    public boolean canEdit() {
        return canUse() && this != USER;
    }

    public boolean canDelete() {
        return this == OWNER || this == SUPER_ADMIN;
    }
}
