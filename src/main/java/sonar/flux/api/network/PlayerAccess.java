package sonar.flux.api.network;

public enum PlayerAccess {
    OWNER, SHARED_OWNER, USER, BLOCKED;

    public boolean canDelete() {
        return this == OWNER;
    }

    public boolean canEdit() {
        return this == OWNER || this == SHARED_OWNER;
    }

    public boolean canConnect() {
        return this == OWNER || this == SHARED_OWNER || this == USER;
    }

    public String getName() {
        switch (this) {
            case BLOCKED:
                return "network.player.blocked";
            case OWNER:
                return "network.player.owner";
            case SHARED_OWNER:
                return "network.player.sharedOwner";
            case USER:
                return "network.player.user";
            default:
                return "";
        }
    }

    public PlayerAccess incrementAccess() {
        switch (this) {
            case USER:
                return SHARED_OWNER;
            case SHARED_OWNER:
                return BLOCKED;
            case BLOCKED:
                return USER;
            default:
                return USER;
        }
    }
}
