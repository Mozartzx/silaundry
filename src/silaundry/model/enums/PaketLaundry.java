package silaundry.model.enums;

// Pilihan paket layanan yang menentukan harga dan estimasi pengerjaan laundry.
public enum PaketLaundry {
    STANDARD_2_HARI("Standard 2 Hari", 2),
    EXPRESS_1_HARI("Express 1 Hari", 1);

    private final String displayName;
    private final int estimasiHariDefault;

    PaketLaundry(String displayName, int estimasiHariDefault) {
        this.displayName = displayName;
        this.estimasiHariDefault = estimasiHariDefault;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getEstimasiHariDefault() {
        return estimasiHariDefault;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
