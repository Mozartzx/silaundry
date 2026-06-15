package silaundry.model.enums;

// Pilihan kategori warna yang dipakai sebagai dasar smart grouping pakaian.
public enum KategoriWarna {
    PUTIH("Putih", "Grup Putih"),
    TERANG("Terang", "Grup Warna Terang"),
    GELAP("Gelap", "Grup Gelap"),
    MUDAH_LUNTUR("Mudah Luntur", "Grup Mudah Luntur");

    private final String displayName;
    private final String smartGroupLabel;

    KategoriWarna(String displayName, String smartGroupLabel) {
        this.displayName = displayName;
        this.smartGroupLabel = smartGroupLabel;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getSmartGroupLabel() {
        return smartGroupLabel;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
