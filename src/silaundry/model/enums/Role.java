package silaundry.model.enums;

// Daftar role yang menentukan menu serta hak akses setelah pengguna login.
public enum Role {
    PEMILIK("Pemilik"),
    KARYAWAN("Karyawan"),
    PELANGGAN("Pelanggan");

    private final String displayName;

    Role(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static Role fromDb(String value) {
        return Role.valueOf(value.toUpperCase());
    }

    @Override
    public String toString() {
        return displayName;
    }
}
