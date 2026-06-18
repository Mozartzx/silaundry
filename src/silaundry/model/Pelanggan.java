package silaundry.model;

import silaundry.model.enums.Role;

// Representasi akun pelanggan yang dapat memantau status dan riwayat laundry.
public class Pelanggan extends Pengguna {
    private String idPelanggan;
    private String alamat;

    // Constructor Pelanggan menyimpan data akun dan data khusus pelanggan.
    public Pelanggan(String idPengguna, String username, String namaLengkap, String nomorTelepon,
            String kataSandi, String idPelanggan, String alamat) {
        super(idPengguna, username, namaLengkap, nomorTelepon, kataSandi, Role.PELANGGAN);
        this.idPelanggan = idPelanggan;
        this.alamat = alamat;
    }

    public void lacakStatusCucian() {
        System.out.println("Melacak status cucian pelanggan " + idPelanggan);
    }

    public void lihatRiwayatPesanan() {
        System.out.println("Melihat riwayat pesanan pelanggan " + idPelanggan);
    }

    @Override
    public String getInformasiPeran() {
        return "Memantau status, riwayat pesanan, dan notifikasi laundry.";
    }

    // Getter dan setter digunakan untuk mengakses ID serta alamat pelanggan.
    public String getIdPelanggan() {
        return idPelanggan;
    }

    public void setIdPelanggan(String idPelanggan) {
        this.idPelanggan = idPelanggan;
    }

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }

    @Override
    public String toString() {
        return getNamaLengkap() + " (" + idPelanggan + ")";
    }
}
