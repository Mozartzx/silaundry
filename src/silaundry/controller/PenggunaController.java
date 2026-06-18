package silaundry.controller;

import java.util.List;
import silaundry.data.DataStore;
import silaundry.model.Pelanggan;
import silaundry.util.IdGenerator;

// Mengatur registrasi dan daftar pelanggan tanpa memakai database.
public class PenggunaController {
    private final DataStore dataStore = DataStore.getInstance();

    // Method ini mengirim daftar pelanggan ke halaman admin.
    public List<Pelanggan> getAllPelanggan() {
        return dataStore.getDaftarPelanggan();
    }

    public void tambahPelanggan(String username, String nama, String telepon, String password, String alamat) {
        validasiPengguna(username, nama, telepon, password);
        if (alamat == null || alamat.trim().length() < 5) {
            throw new IllegalArgumentException("Alamat pelanggan minimal 5 karakter.");
        }
        if (dataStore.usernameSudahDipakai(username.trim())) {
            throw new IllegalArgumentException("Username sudah digunakan.");
        }
        Pelanggan pelanggan = new Pelanggan(
                IdGenerator.generate("USR"),
                username.trim(),
                nama.trim(),
                telepon.replaceAll("[^0-9]", ""),
                password,
                IdGenerator.generate("PLG"),
                alamat.trim());
        dataStore.tambahPelanggan(pelanggan);
    }

    // Validasi dilakukan sebelum objek pelanggan dimasukkan ke ArrayList.
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
            throw new IllegalArgumentException("Password pelanggan minimal 6 karakter.");
        }
    }
}
