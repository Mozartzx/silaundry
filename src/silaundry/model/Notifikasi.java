package silaundry.model;

import java.time.LocalDateTime;

// Menyimpan pesan status laundry yang dapat dibaca oleh pelanggan di aplikasi.
public class Notifikasi {
    private String idNotifikasi;
    private String idPesanan;
    private String pesan;
    private LocalDateTime tanggalKirim;
    private boolean sudahDibaca;

    // Constructor mengisi pesan status yang akan ditampilkan kepada pelanggan.
    public Notifikasi(String idNotifikasi, String idPesanan, String pesan, LocalDateTime tanggalKirim,
            boolean sudahDibaca) {
        this.idNotifikasi = idNotifikasi;
        this.idPesanan = idPesanan;
        this.pesan = pesan;
        this.tanggalKirim = tanggalKirim;
        this.sudahDibaca = sudahDibaca;
    }

    // Getter dan setter digunakan saat notifikasi dibaca dari halaman pelanggan.
    public String getIdNotifikasi() {
        return idNotifikasi;
    }

    public String getIdPesanan() {
        return idPesanan;
    }

    public String getPesan() {
        return pesan;
    }

    public LocalDateTime getTanggalKirim() {
        return tanggalKirim;
    }

    public boolean isSudahDibaca() {
        return sudahDibaca;
    }

    public void setSudahDibaca(boolean sudahDibaca) {
        this.sudahDibaca = sudahDibaca;
    }
}
