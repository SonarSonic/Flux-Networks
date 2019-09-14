package fluxnetworks.common.core;

public class Localization {

    public String key;
    public String translated = ""; // MUST!!!

    public Localization(String key) {
        this.key = key;
    }

    public String t() {
        return translated;
    }

}
