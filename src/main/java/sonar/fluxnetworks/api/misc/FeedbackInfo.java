package sonar.fluxnetworks.api.misc;

import sonar.fluxnetworks.api.text.FluxTranslate;

public enum FeedbackInfo {
    NONE(null),
    REJECT(FluxTranslate.REJECT),
    NO_OWNER(FluxTranslate.NO_OWNER),
    NO_ADMIN(FluxTranslate.NO_ADMIN),
    NO_SPACE(FluxTranslate.NO_SPACE),
    HAS_CONTROLLER(FluxTranslate.HAS_CONTROLLER),
    INVALID_USER(FluxTranslate.INVALID_USER),
    ILLEGAL_PASSWORD(FluxTranslate.ILLEGAL_PASSWORD),
    BANNED_LOADING(FluxTranslate.BANNED_LOADING),
    PASSWORD_REQUIRE(null),
    SUCCESS(null),
    SUCCESS_2(null); // Sometimes we need another success to compare to the first one

    private final FluxTranslate localization;

    FeedbackInfo(FluxTranslate localization) {
        this.localization = localization;
    }

    public boolean hasFeedback() {
        return this != NONE;
    }

    public String getInfo() {
        return localization == null ? "": localization.t();
    }

}
