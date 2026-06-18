package silaundry.model;

import silaundry.model.enums.Role;

// Parent class yang menyimpan data login umum milik semua role pengguna.
public abstract class Pengguna {
    private String idPengguna;
    private String username;
    private String namaLengkap;
    private String nomorTelepon;
    private String kataSandi;
    private Role role;

    // Constructor mengisi data umum yang dimiliki semua jenis pengguna.
    protected Pengguna(String idPengguna, String username, String namaLengkap, String nomorTelepon,
            String kataSandi, Role role) {
        this.idPengguna = idPengguna;
        this.username = username;
        this.namaLengkap = namaLengkap;
        this.nomorTelepon = nomorTelepon;
        this.kataSandi = kataSandi;
        this.role = role;
    }

    public void login() {
        System.out.println(namaLengkap + " login sebagai " + role.getDisplayName());
    }

    public void logout() {
        System.out.println(namaLengkap + " logout");
    }

    // Setiap jenis pengguna wajib menjelaskan hak aksesnya dengan implementasi masing-masing.
    public abstract String getInformasiPeran();

    // Getter dan setter digunakan untuk membaca atau mengubah data pengguna.
    public String getIdPengguna() {
        return idPengguna;
    }

    public void setIdPengguna(String idPengguna) {
        this.idPengguna = idPengguna;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNamaLengkap() {
        return namaLengkap;
    }

    public void setNamaLengkap(String namaLengkap) {
        this.namaLengkap = namaLengkap;
    }

    public String getNomorTelepon() {
        return nomorTelepon;
    }

    public void setNomorTelepon(String nomorTelepon) {
        this.nomorTelepon = nomorTelepon;
    }

    public String getKataSandi() {
        return kataSandi;
    }

    public void setKataSandi(String kataSandi) {
        this.kataSandi = kataSandi;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
