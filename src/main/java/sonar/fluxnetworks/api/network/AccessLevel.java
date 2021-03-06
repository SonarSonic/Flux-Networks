package sonar.fluxnetworks.api.network;

import sonar.fluxnetworks.api.text.FluxTranslate;
import net.minecraft.util.text.TextFormatting;

import javax.annotation.Nonnull;

public enum AccessLevel {
    OWNER(FluxTranslate.OWNER, 0xffaa00, TextFormatting.GOLD),
    ADMIN(FluxTranslate.ADMIN, 0x66cc00, TextFormatting.GREEN),
    USER(FluxTranslate.USER, 0x6699ff, TextFormatting.BLUE),
    BLOCKED(FluxTranslate.BLOCKED, 0xa9a9a9, TextFormatting.GRAY),
    SUPER_ADMIN(FluxTranslate.SUPER_ADMIN, 0x4b0082, TextFormatting.DARK_PURPLE);

    private final FluxTranslate localization;
    private final int color;
    private final TextFormatting formatting;

    AccessLevel(FluxTranslate localization, int color, TextFormatting formatting) {
        this.localization = localization;
        this.color = color;
        this.formatting = formatting;
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
