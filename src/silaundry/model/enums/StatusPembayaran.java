package silaundry.model.enums;

// Kondisi pembayaran yang membedakan tagihan belum dibayar dan sudah lunas.
public enum StatusPembayaran {
    BELUM_BAYAR("Belum Bayar"),
    LUNAS("Lunas");

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
