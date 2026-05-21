package silaundry.model.enums;

public enum TahapLaundry {
    PENERIMAAN("Penerimaan"),
    PENCUCIAN("Pencucian"),
    PENGERINGAN("Pengeringan"),
    PENYETRIKAAN("Penyetrikaan"),
    PENGEMASAN("Pengemasan"),
    SELESAI("Selesai");

    private final String displayName;

    TahapLaundry(String displayName) {
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
