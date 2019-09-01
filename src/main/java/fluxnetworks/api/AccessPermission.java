package fluxnetworks.api;

public enum AccessPermission {
    OWNER("Owner", 0xffcc00),
    ADMIN("Admin", 0x66cc00),
    USER("User", 0x6699ff),
    NONE("Blocked", 0xa9a9a9),
    SUPER_ADMIN("Super Admin", 0x4b0082);

    public String name;
    public int color;

    AccessPermission(String name, int color) {
        this.name = name;
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public int getColor() {
        return color;
    }

    public boolean canAccess() {
        return this != NONE;
    }

    public boolean canEdit() {
        return canAccess() && this != USER;
    }

    public boolean canDelete() {
        return this == OWNER || this == SUPER_ADMIN;
    }

}
