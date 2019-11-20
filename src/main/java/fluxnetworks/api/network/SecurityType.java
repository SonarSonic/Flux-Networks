package fluxnetworks.api.network;

import fluxnetworks.api.translate.FluxTranslate;
import fluxnetworks.api.translate.Translation;

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
