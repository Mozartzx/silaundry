package silaundry.model.enums;

public enum StatusPembayaran {
    BELUM_BAYAR("Belum Bayar"),
    SEBAGIAN("Sebagian"),
    LUNAS("Lunas"),
    DIBATALKAN("Dibatalkan");

    private final String displayName;

    StatusPembayaran(String displayName) {
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
