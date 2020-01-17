package icyllis.fluxnetworks.api.network;

import icyllis.fluxnetworks.system.misc.FluxTranslate;
import net.minecraft.util.text.ITextComponent;

public enum SecurityType {
    PUBLIC(FluxTranslate.PUBLIC),
    ENCRYPTED(FluxTranslate.ENCRYPTED);

    private ITextComponent textComponent;

    SecurityType(ITextComponent textComponent) {
        this.textComponent = textComponent;
    }

    public String getDisplayText() {
        return textComponent.getFormattedText();
    }

    public boolean isEncrypted() {
        return this == ENCRYPTED;
    }
}
