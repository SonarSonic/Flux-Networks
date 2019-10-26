package fluxnetworks.api.translate;

public class Translation {

    public String key;
    public String translated = ""; // MUST!!!

    public Translation(String key) {
        this.key = key;
    }

    public String t() {
        return translated;
    }

}
