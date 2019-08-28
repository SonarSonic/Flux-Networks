package fluxnetworks.api;

public enum FeedbackInfo {
    NONE(""),
    REJECT("The remote server rejected your request"),
    NO_OWNER("You aren't the owner of the network"),
    NO_SPACE(""),
    HAS_CONTROLLER("The network already has a controller"),
    PASSWORD_REQUIRE(""),
    SUCCESS("");

    public String info;

    FeedbackInfo(String info) {
        this.info = info;
    }

    public boolean hasFeedback() {
        return this != NONE;
    }

}
