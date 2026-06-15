package silaundry.model.enums;

// Tahapan proses pesanan sekaligus tempat aturan perpindahan status disimpan.
public enum StatusPesanan {
    BARU("Baru"),
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

    public boolean dapatBerubahKe(StatusPesanan statusBaru) {
        // Perpindahan status dibatasi agar proses laundry tetap berurutan.
        if (statusBaru == null || statusBaru == this || isFinal()) {
            return false;
        }
        if (statusBaru == DIBATALKAN) {
            return this == BARU || this == DIPROSES;
        }
        return switch (this) {
            case BARU -> statusBaru == DIPROSES;
            case DIPROSES -> statusBaru == DICUCI;
            case DICUCI -> statusBaru == DIKERINGKAN;
            case DIKERINGKAN -> statusBaru == DISETRIKA;
            case DISETRIKA -> statusBaru == SIAP_DIAMBIL;
            case SIAP_DIAMBIL -> statusBaru == SELESAI;
            case SELESAI, DIBATALKAN -> false;
        };
    }

    public boolean membutuhkanItemPakaian() {
        // Item wajib tersedia ketika pesanan sudah memasuki tahap pencucian dan seterusnya.
        return this == DICUCI || this == DIKERINGKAN || this == DISETRIKA
                || this == SIAP_DIAMBIL || this == SELESAI;
    }

    public boolean dapatMengubahItem() {
        // Detail pakaian dikunci setelah proses pencucian dimulai.
        return this == BARU || this == DIPROSES;
    }

    public boolean dapatMenerimaPembayaran() {
        // Pesanan batal tidak boleh menerima pembayaran baru.
        return this != DIBATALKAN;
    }

    public boolean isFinal() {
        // Status final tidak dapat dipindahkan lagi ke tahap lain.
        return this == SELESAI || this == DIBATALKAN;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
