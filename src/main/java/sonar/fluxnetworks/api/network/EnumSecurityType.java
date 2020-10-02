package sonar.fluxnetworks.api.network;

import sonar.fluxnetworks.api.text.FluxTranslate;

public enum EnumSecurityType {
    PUBLIC(FluxTranslate.PUBLIC),
    ENCRYPTED(FluxTranslate.ENCRYPTED);

    private FluxTranslate localization;

    EnumSecurityType(FluxTranslate localization) {
        this.localization = localization;
    }

    public String getName() {
        return localization.t();
    }

    public boolean isEncrypted() {
        return this == ENCRYPTED;
    }
}
