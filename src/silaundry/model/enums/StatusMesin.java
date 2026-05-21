package silaundry.model.enums;

public enum StatusMesin {
    TERSEDIA("Tersedia"),
    DIGUNAKAN("Digunakan"),
    PERAWATAN("Perawatan");

    private final String displayName;

    StatusMesin(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
