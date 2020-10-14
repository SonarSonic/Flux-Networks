package sonar.fluxnetworks.api.network;

import sonar.fluxnetworks.api.text.FluxTranslate;

public enum SecurityType {
    PUBLIC(FluxTranslate.PUBLIC),
    ENCRYPTED(FluxTranslate.ENCRYPTED);

    private final FluxTranslate localization;

    SecurityType(FluxTranslate localization) {
        this.localization = localization;
    }

    public String getName() {
        return localization.t();
    }

    public boolean isEncrypted() {
        return this == ENCRYPTED;
    }
}
