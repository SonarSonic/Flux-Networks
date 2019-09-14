package fluxnetworks.api;

import fluxnetworks.FluxTranslate;
import fluxnetworks.common.core.Localization;

public enum SecurityType {
    PUBLIC(FluxTranslate.PUBLIC),
    ENCRYPTED(FluxTranslate.ENCRYPTED);

    private Localization localization;

    SecurityType(Localization localization) {
        this.localization = localization;
    }

    public String getName() {
        return localization.t();
    }

    public boolean isEncrypted() {
        return this == ENCRYPTED;
    }
}
