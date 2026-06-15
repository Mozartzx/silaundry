package silaundry.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import silaundry.model.enums.StatusPembayaran;

// Menyatukan data pesanan dan pembayaran supaya tabel riwayat mudah ditampilkan.
public class RiwayatPembayaran {
    private final String idPesanan;
    private final String namaPelanggan;
    private final LocalDate tanggalPesanan;
    private final double totalTagihan;
    private final String metode;
    private final double jumlahBayar;
    private final StatusPembayaran status;
    private final LocalDateTime tanggalBayar;

    public RiwayatPembayaran(String idPesanan, String namaPelanggan, LocalDate tanggalPesanan,
            double totalTagihan, String metode, double jumlahBayar, StatusPembayaran status,
            LocalDateTime tanggalBayar) {
        this.idPesanan = idPesanan;
        this.namaPelanggan = namaPelanggan;
        this.tanggalPesanan = tanggalPesanan;
        this.totalTagihan = totalTagihan;
        this.metode = metode;
        this.jumlahBayar = jumlahBayar;
        this.status = status;
        this.tanggalBayar = tanggalBayar;
    }

    public String getIdPesanan() {
        return idPesanan;
    }

    public String getNamaPelanggan() {
        return namaPelanggan;
    }

    public LocalDate getTanggalPesanan() {
        return tanggalPesanan;
    }

    public double getTotalTagihan() {
        return totalTagihan;
    }

    public String getMetode() {
        return metode;
    }

    public double getJumlahBayar() {
        return jumlahBayar;
    }

    public StatusPembayaran getStatus() {
        return status;
    }

    public LocalDateTime getTanggalBayar() {
        return tanggalBayar;
    }
}
