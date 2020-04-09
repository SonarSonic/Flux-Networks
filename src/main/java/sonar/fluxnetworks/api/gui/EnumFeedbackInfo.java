package sonar.fluxnetworks.api.gui;

import sonar.fluxnetworks.api.translate.FluxTranslate;

public enum EnumFeedbackInfo {
    NONE(FluxTranslate.EMPTY),
    REJECT(FluxTranslate.REJECT),
    NO_OWNER(FluxTranslate.NO_OWNER),
    NO_ADMIN(FluxTranslate.NO_ADMIN),
    NO_SPACE(FluxTranslate.NO_SPACE),
    HAS_CONTROLLER(FluxTranslate.HAS_CONTROLLER),
    INVALID_USER(FluxTranslate.INVALID_USER),
    ILLEGAL_PASSWORD(FluxTranslate.ILLEGAL_PASSWORD),
    HAS_LOADER(FluxTranslate.HAS_LOADER),
    BANNED_LOADING(FluxTranslate.BANNED_LOADING),
    REJECT_SOME(FluxTranslate.REJECT_SOME),
    PASSWORD_REQUIRE(FluxTranslate.EMPTY),
    SUCCESS(FluxTranslate.EMPTY),
    SUCCESS_2(FluxTranslate.EMPTY); // Sometimes we need another success to compare to the first one

    private FluxTranslate localization;

    EnumFeedbackInfo(FluxTranslate localization) {
        this.localization = localization;
    }

    public boolean hasFeedback() {
        return this != NONE;
    }

    public String getInfo() {
        return localization.t();
    }

}
