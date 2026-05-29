package silaundry.model;

import java.time.LocalDateTime;

public class Notifikasi {
    private String idNotifikasi;
    private String idPesanan;
    private String pesan;
    private LocalDateTime tanggalKirim;
    private boolean sudahDibaca;

    public Notifikasi(String idNotifikasi, String idPesanan, String pesan, LocalDateTime tanggalKirim,
            boolean sudahDibaca) {
        this.idNotifikasi = idNotifikasi;
        this.idPesanan = idPesanan;
        this.pesan = pesan;
        this.tanggalKirim = tanggalKirim;
        this.sudahDibaca = sudahDibaca;
    }

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
