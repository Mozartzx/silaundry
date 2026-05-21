package silaundry.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import silaundry.model.enums.StatusPesanan;

public class Pesanan {
    private String idPesanan;
    private String idPelanggan;
    private String namaPelanggan;
    private String idKaryawan;
    private String namaKaryawan;
    private LocalDate tanggalMasuk;
    private LocalDate estimasiSelesai;
    private StatusPesanan statusPesanan;
    private double totalBiaya;
    private String catatan;
    private final List<ItemPakaian> daftarItem = new ArrayList<>();

    public Pesanan(String idPesanan, String idPelanggan, String idKaryawan, LocalDate tanggalMasuk,
            LocalDate estimasiSelesai, StatusPesanan statusPesanan, double totalBiaya, String catatan) {
        this.idPesanan = idPesanan;
        this.idPelanggan = idPelanggan;
        this.idKaryawan = idKaryawan;
        this.tanggalMasuk = tanggalMasuk;
        this.estimasiSelesai = estimasiSelesai;
        this.statusPesanan = statusPesanan;
        this.totalBiaya = totalBiaya;
        this.catatan = catatan;
    }

    public void tambahItemPakaian(ItemPakaian item) {
        daftarItem.add(item);
    }

    public double kalkulasiTotalBiaya() {
        return totalBiaya;
    }

    public void kirimNotifikasiSelesai() {
        System.out.println("Pesanan " + idPesanan + " selesai dan notifikasi dikirim.");
    }

    public String getIdPesanan() {
        return idPesanan;
    }

    public void setIdPesanan(String idPesanan) {
        this.idPesanan = idPesanan;
    }

    public String getIdPelanggan() {
        return idPelanggan;
    }

    public void setIdPelanggan(String idPelanggan) {
        this.idPelanggan = idPelanggan;
    }

    public String getNamaPelanggan() {
        return namaPelanggan;
    }

    public void setNamaPelanggan(String namaPelanggan) {
        this.namaPelanggan = namaPelanggan;
    }

    public String getIdKaryawan() {
        return idKaryawan;
    }

    public void setIdKaryawan(String idKaryawan) {
        this.idKaryawan = idKaryawan;
    }

    public String getNamaKaryawan() {
        return namaKaryawan;
    }

    public void setNamaKaryawan(String namaKaryawan) {
        this.namaKaryawan = namaKaryawan;
    }

    public LocalDate getTanggalMasuk() {
        return tanggalMasuk;
    }

    public void setTanggalMasuk(LocalDate tanggalMasuk) {
        this.tanggalMasuk = tanggalMasuk;
    }

    public LocalDate getEstimasiSelesai() {
        return estimasiSelesai;
    }

    public void setEstimasiSelesai(LocalDate estimasiSelesai) {
        this.estimasiSelesai = estimasiSelesai;
    }

    public StatusPesanan getStatusPesanan() {
        return statusPesanan;
    }

    public void setStatusPesanan(StatusPesanan statusPesanan) {
        this.statusPesanan = statusPesanan;
    }

    public double getTotalBiaya() {
        return totalBiaya;
    }

    public void setTotalBiaya(double totalBiaya) {
        this.totalBiaya = totalBiaya;
    }

    public String getCatatan() {
        return catatan;
    }

    public void setCatatan(String catatan) {
        this.catatan = catatan;
    }

    public List<ItemPakaian> getDaftarItem() {
        return daftarItem;
    }
}
