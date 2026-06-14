package silaundry.model;

import java.time.LocalDateTime;

public class DetailPembayaran {
    private String idDetail;
    private String idPembayaran;
    private LocalDateTime waktuBayar;
    private String metode;
    private double jumlah;
    private String keterangan;

    public DetailPembayaran(String idDetail, String idPembayaran, LocalDateTime waktuBayar, String metode,
            double jumlah, String keterangan) {
        this.idDetail = idDetail;
        this.idPembayaran = idPembayaran;
        this.waktuBayar = waktuBayar;
        this.metode = metode;
        this.jumlah = jumlah;
        this.keterangan = keterangan;
    }

    public void generateStruk() {
        System.out.println(formatStruk());
    }

    public String formatStruk() {
        return "Struk " + idDetail + " - " + idPembayaran + " - " + metode + " - Rp " + jumlah
                + " - " + waktuBayar;
    }

    public String getIdDetail() {
        return idDetail;
    }

    public String getIdPembayaran() {
        return idPembayaran;
    }

    public LocalDateTime getWaktuBayar() {
        return waktuBayar;
    }

    public String getMetode() {
        return metode;
    }

    public double getJumlah() {
        return jumlah;
    }

    public String getKeterangan() {
        return keterangan;
    }
}
