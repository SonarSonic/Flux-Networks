package fluxnetworks.api;

import fluxnetworks.FluxTranslate;
import fluxnetworks.common.core.Localization;
import net.minecraft.util.text.TextFormatting;

public enum AccessPermission {
    OWNER(FluxTranslate.OWNER, 0xffaa00, TextFormatting.GOLD),
    ADMIN(FluxTranslate.ADMIN, 0x66cc00, TextFormatting.GREEN),
    USER(FluxTranslate.USER, 0x6699ff, TextFormatting.BLUE),
    NONE(FluxTranslate.BLOCKED, 0xa9a9a9, TextFormatting.GRAY),
    SUPER_ADMIN(FluxTranslate.SUPER_ADMIN, 0x4b0082, TextFormatting.DARK_PURPLE);

    public Localization localization;
    public int color;
    public TextFormatting formatting;

    AccessPermission(Localization localization, int color, TextFormatting formatting) {
        this.localization = localization;
        this.color = color;
        this.formatting = formatting;
    }

    public String getName() {
        return formatting + localization.t();
    }

    public int getColor() {
        return color;
    }

    public boolean canAccess() {
        return this != NONE;
    }

    public boolean canEdit() {
        return canAccess() && this != USER;
    }

    public boolean canDelete() {
        return this == OWNER || this == SUPER_ADMIN;
    }

}
