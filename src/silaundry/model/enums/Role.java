package silaundry.model.enums;

// Daftar role yang menentukan menu serta hak akses setelah pengguna login.
public enum Role {
    ADMIN("Admin"),
    PELANGGAN("Pelanggan");

    private final String displayName;

    Role(String displayName) {
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
