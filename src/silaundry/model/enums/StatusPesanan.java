package silaundry.model.enums;

public enum StatusPesanan {
    BARU("Baru"),
    DITERIMA("Diterima"),
    DIPROSES("Diproses"),
    DICUCI("Dicuci"),
    DIKERINGKAN("Dikeringkan"),
    DISETRIKA("Disetrika"),
    SIAP_DIAMBIL("Siap Diambil"),
    SELESAI("Selesai"),
    DIBATALKAN("Dibatalkan");

    private final String displayName;

    StatusPesanan(String displayName) {
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
