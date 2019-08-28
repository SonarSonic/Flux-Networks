package fluxnetworks.api;

public enum MemberPermission {
    OWNER("Owner", 0xffcc00),
    ACCESS("Member", 0x6699ff),
    ADMIN("Admin", 0x66cc00),
    NONE("No Permission", 0xa9a9a9),
    SUPER_ADMIN("Super Admin", 0x4b0082);

    public String name;
    public int color;

    MemberPermission(String name, int color) {
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
        return canAccess() && this != ACCESS;
    }

    public boolean canDelete() {
        return this == OWNER || this == SUPER_ADMIN;
    }

}
