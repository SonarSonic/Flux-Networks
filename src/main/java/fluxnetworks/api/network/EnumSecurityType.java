package fluxnetworks.api.network;

import fluxnetworks.api.translate.FluxTranslate;
import fluxnetworks.api.translate.Translation;

public enum EnumSecurityType {
    PUBLIC(FluxTranslate.PUBLIC),
    ENCRYPTED(FluxTranslate.ENCRYPTED);

    private Translation localization;

    EnumSecurityType(Translation localization) {
        this.localization = localization;
    }

    public String getName() {
        return localization.t();
    }

    public boolean isEncrypted() {
        return this == ENCRYPTED;
    }
}
