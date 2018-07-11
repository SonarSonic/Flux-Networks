package sonar.flux.api;

public enum ConnectionSettings {
    PRIORITY,
    TRANSFER_LIMIT,
    FOLDER_ID,
    CUSTOM_NAME;

    public boolean updateConnectionsList(){
        return true;
    }
}
