package icyllis.fluxnetworks.api.network;

import icyllis.fluxnetworks.api.translate.FluxTranslate;
import icyllis.fluxnetworks.api.translate.Translation;

public enum SecurityType {
    PUBLIC(FluxTranslate.PUBLIC),
    ENCRYPTED(FluxTranslate.ENCRYPTED);

    private Translation localization;

    SecurityType(Translation localization) {
        this.localization = localization;
    }

    public String getDisplayText() {
        return localization.t();
    }

    public boolean isEncrypted() {
        return this == ENCRYPTED;
    }
}
