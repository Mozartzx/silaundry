package silaundry.controller;

import java.sql.SQLException;
import java.util.List;
import silaundry.dao.UserDAO;
import silaundry.model.Karyawan;
import silaundry.model.Pelanggan;
import silaundry.util.IdGenerator;
import silaundry.util.PasswordUtil;

// Mengatur data pelanggan dan karyawan serta validasi ketika akun dibuat atau dihapus.
public class PenggunaController {
    private final UserDAO userDAO = new UserDAO();

    // Mengambil pelanggan aktif untuk tabel pemilik dan pilihan pesanan karyawan.
    public List<Pelanggan> getAllPelanggan() throws SQLException {
        return userDAO.findAllPelanggan();
    }

    // Mengambil daftar karyawan yang masih tersedia di sistem.
    public List<Karyawan> getAllKaryawan() throws SQLException {
        return userDAO.findAllKaryawan();
    }

    // Mengambil satu ID karyawan bila sistem membutuhkan petugas awal.
    public String getDefaultKaryawanId() throws SQLException {
        return userDAO.findFirstKaryawanId();
    }

    // Membentuk akun pelanggan baru setelah semua data dasar lolos validasi.
    public void tambahPelanggan(String username, String nama, String telepon, String password, String alamat)
            throws SQLException {
        validasiPengguna(username, nama, telepon, password);
        if (alamat == null || alamat.trim().length() < 5) {
            throw new IllegalArgumentException("Alamat pelanggan minimal 5 karakter.");
        }
        Pelanggan pelanggan = new Pelanggan(
                IdGenerator.generate("USR"),
                username.trim(),
                nama.trim(),
                telepon.replaceAll("[^0-9]", ""),
                PasswordUtil.hash(password),
                IdGenerator.generate("PLG"),
                alamat.trim());
        userDAO.createPelanggan(pelanggan);
    }

    // Membentuk akun karyawan baru dengan shift Pagi atau Malam.
    public void tambahKaryawan(String username, String nama, String telepon, String password, String shift)
            throws SQLException {
        validasiPengguna(username, nama, telepon, password);
        String shiftBersih = shift == null ? "" : shift.trim();
        if (!shiftBersih.equalsIgnoreCase("Pagi") && !shiftBersih.equalsIgnoreCase("Malam")) {
            throw new IllegalArgumentException("Shift kerja hanya dapat dipilih Pagi atau Malam.");
        }
        shiftBersih = shiftBersih.equalsIgnoreCase("Pagi") ? "Pagi" : "Malam";
        Karyawan karyawan = new Karyawan(
                IdGenerator.generate("USR"),
                username.trim(),
                nama.trim(),
                telepon.replaceAll("[^0-9]", ""),
                PasswordUtil.hash(password),
                IdGenerator.generate("KRY"),
                shiftBersih);
        userDAO.createKaryawan(karyawan);
    }

    // Menghapus akun karyawan, sedangkan pesanan lamanya tetap menjadi riwayat.
    public void hapusKaryawan(String idKaryawan) throws SQLException {
        userDAO.deleteKaryawan(idKaryawan);
    }

    // Validasi ini dipakai bersama agar aturan akun pelanggan dan karyawan konsisten.
    private void validasiPengguna(String username, String nama, String telepon, String password) {
        String usernameBersih = username == null ? "" : username.trim();
        String namaBersih = nama == null ? "" : nama.trim();
        String teleponBersih = telepon == null ? "" : telepon.replaceAll("[^0-9]", "");
        if (!usernameBersih.matches("[A-Za-z0-9._]{4,30}")) {
            throw new IllegalArgumentException(
                    "Username harus 4-30 karakter dan hanya boleh berisi huruf, angka, titik, atau underscore.");
        }
        if (namaBersih.length() < 3) {
            throw new IllegalArgumentException("Nama lengkap minimal 3 karakter.");
        }
        if (teleponBersih.length() < 8 || teleponBersih.length() > 15) {
            throw new IllegalArgumentException("Nomor telepon harus terdiri dari 8-15 angka.");
        }
        if (password == null || password.length() < 6) {
            throw new IllegalArgumentException("Password minimal 6 karakter.");
        }
    }
}
