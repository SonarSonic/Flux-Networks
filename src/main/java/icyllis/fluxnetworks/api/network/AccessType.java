package icyllis.fluxnetworks.api.network;

import icyllis.fluxnetworks.system.misc.FluxTranslate;
import net.minecraft.util.text.ITextComponent;

public enum AccessType {
    OWNER(FluxTranslate.OWNER, 0xffaa00),
    ADMIN(FluxTranslate.ADMIN, 0x66cc00),
    USER(FluxTranslate.USER, 0x6699ff),
    NONE(FluxTranslate.BLOCKED, 0xa9a9a9),
    SUPER_ADMIN(FluxTranslate.SUPER_ADMIN, 0x4b0082);

    public ITextComponent textComponent;
    public int color;

    AccessType(ITextComponent textComponent, int color) {
        this.textComponent = textComponent;
        this.color = color;
    }

    public String getDisplayText() {
        return textComponent.getFormattedText();
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
