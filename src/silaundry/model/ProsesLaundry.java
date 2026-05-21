package silaundry.model;

import java.time.LocalDateTime;
import silaundry.model.enums.TahapLaundry;

public class ProsesLaundry {
    private String idProses;
    private String idPesanan;
    private String idMesin;
    private TahapLaundry tahap;
    private LocalDateTime waktuMulai;
    private LocalDateTime waktuSelesai;

    public ProsesLaundry(String idProses, String idPesanan, String idMesin, TahapLaundry tahap,
            LocalDateTime waktuMulai, LocalDateTime waktuSelesai) {
        this.idProses = idProses;
        this.idPesanan = idPesanan;
        this.idMesin = idMesin;
        this.tahap = tahap;
        this.waktuMulai = waktuMulai;
        this.waktuSelesai = waktuSelesai;
    }

    public void updateProses() {
        this.waktuSelesai = LocalDateTime.now();
    }

    public void updateProses(TahapLaundry tahap, LocalDateTime waktuSelesai) {
        this.tahap = tahap;
        this.waktuSelesai = waktuSelesai;
    }

    public String getIdProses() {
        return idProses;
    }

    public String getIdPesanan() {
        return idPesanan;
    }

    public String getIdMesin() {
        return idMesin;
    }

    public TahapLaundry getTahap() {
        return tahap;
    }

    public LocalDateTime getWaktuMulai() {
        return waktuMulai;
    }

    public LocalDateTime getWaktuSelesai() {
        return waktuSelesai;
    }
}
