package fluxnetworks.api;

public enum SecurityType {
    PUBLIC("Public"),
    ENCRYPTED("Encrypted");

    private String name;

    SecurityType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public boolean isEncrypted() {
        return this == ENCRYPTED;
    }
}
