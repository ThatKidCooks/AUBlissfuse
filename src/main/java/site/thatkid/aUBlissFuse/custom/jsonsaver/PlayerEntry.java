package site.thatkid.aUBlissFuse.custom.jsonsaver;

public class PlayerEntry {
    private String name;
    private String addedAt;  // ISO-8601 timestamp

    // Required for Gson
    public PlayerEntry() {}

    public PlayerEntry(String name, String addedAt) {
        this.name = name;
        this.addedAt = addedAt;
    }

    public String getName() {
        return name;
    }

    public String getAddedAt() {
        return addedAt;
    }
}
