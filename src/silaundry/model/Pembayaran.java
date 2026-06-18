package silaundry.model;

import java.time.LocalDateTime;
import silaundry.model.enums.StatusPembayaran;

// Menyimpan hasil pembayaran sebuah pesanan beserta metode dan status pelunasannya.
public class Pembayaran {
    private String idPembayaran;
    private String idPesanan;
    private String metode;
    private double jumlah;
    private StatusPembayaran status;
    private LocalDateTime tanggalBayar;

    // Constructor pertama dipakai ketika tanggal pembayaran belum tersedia.
    public Pembayaran(String idPembayaran, String idPesanan, String metode, double jumlah, StatusPembayaran status) {
        this(idPembayaran, idPesanan, metode, jumlah, status, null);
    }

    // Constructor kedua menunjukkan overloading ketika tanggal pembayaran sudah diketahui.
    public Pembayaran(String idPembayaran, String idPesanan, String metode, double jumlah,
            StatusPembayaran status, LocalDateTime tanggalBayar) {
        this.idPembayaran = idPembayaran;
        this.idPesanan = idPesanan;
        this.metode = metode;
        this.jumlah = jumlah;
        this.status = status;
        this.tanggalBayar = tanggalBayar;
    }

    public void prosesPembayaran() {
        // Nominal positif dianggap sebagai pembayaran yang sudah lunas.
        status = jumlah > 0 ? StatusPembayaran.LUNAS : StatusPembayaran.BELUM_BAYAR;
        tanggalBayar = status == StatusPembayaran.LUNAS ? LocalDateTime.now() : null;
    }

    // Getter berikut mengirim data pembayaran ke controller dan tabel riwayat.
    public String getIdPembayaran() {
        return idPembayaran;
    }

    public String getIdPesanan() {
        return idPesanan;
    }

    public String getMetode() {
        return metode;
    }

    public double getJumlah() {
        return jumlah;
    }

    public StatusPembayaran getStatus() {
        return status;
    }

    public LocalDateTime getTanggalBayar() {
        return tanggalBayar;
    }
}
