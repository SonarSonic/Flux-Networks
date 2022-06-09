package sonar.fluxnetworks.api.misc;

import sonar.fluxnetworks.api.FluxTranslate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

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
    SA_ON(FluxTranslate.SA_ON),
    SA_OFF(FluxTranslate.SA_OFF),
    PASSWORD_REQUIRE(null),
    SUCCESS(null),
    SUCCESS_2(null); // Sometimes we need another success to compare to the first one

    @Nullable
    private final FluxTranslate localization;

    FeedbackInfo(@Nullable FluxTranslate localization) {
        this.localization = localization;
    }

    // a non-text feedback that wants to do some actions in GUI
    public boolean action() {
        return localization == null;
    }

    @Nonnull
    public String getText() {
        return localization == null ? "" : localization.get();
    }
}
