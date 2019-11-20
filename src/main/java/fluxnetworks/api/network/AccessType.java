package fluxnetworks.api.network;

import fluxnetworks.api.translate.FluxTranslate;
import fluxnetworks.api.translate.Translation;

public enum AccessType {
    OWNER(FluxTranslate.OWNER, 0xffaa00),
    ADMIN(FluxTranslate.ADMIN, 0x66cc00),
    USER(FluxTranslate.USER, 0x6699ff),
    NONE(FluxTranslate.BLOCKED, 0xa9a9a9),
    SUPER_ADMIN(FluxTranslate.SUPER_ADMIN, 0x4b0082);

    public Translation localization;
    public int color;

    AccessType(Translation localization, int color) {
        this.localization = localization;
        this.color = color;
    }

    public String getDisplayText() {
        return localization.t();
    }

    public int getLevelColor() {
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
