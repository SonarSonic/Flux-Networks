package fluxnetworks.api;

public enum FeedbackInfo {
    NONE(""),
    REJECT("The remote server rejected your request"),
    NO_OWNER("You aren't the owner of the network"),
    NO_ADMIN("You aren't the admin of the network"),
    NO_SPACE("You can't create more networks"),
    HAS_CONTROLLER("The network already has a controller"),
    INVALID_USER("Invalid user selected"),
    ILLEGAL_PASSWORD("Wrong password format, must be digit or letter"),
    HAS_LOADER("This chunk already has a chunk loader"),
    BANNED_LOADING("Chunk loading was banned from the server"),
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
