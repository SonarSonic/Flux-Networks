package sonar.fluxnetworks.common.misc;

public enum NumberFormatType {
    FULL,                   // Full format
    COMPACT,                // Compact format (like 3.5M)
    COMMAS,                 // Language dependent comma separated format
    NONE                    // No output (empty string)
}
