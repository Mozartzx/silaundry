package silaundry.model;

import java.time.LocalDateTime;

public class Notifikasi implements INotifiable {
    private int id;
    private String idNotifikasi;
    private String idPesanan;
    private String pesan;
    private LocalDateTime tanggalKirim;
    private boolean sudahDibaca;

    public Notifikasi(String idNotifikasi, String idPesanan, String pesan, LocalDateTime tanggalKirim,
            boolean sudahDibaca) {
        this(extractNumericId(idNotifikasi), idNotifikasi, idPesanan, pesan, tanggalKirim, sudahDibaca);
    }

    public Notifikasi(int id, String idNotifikasi, String idPesanan, String pesan, LocalDateTime tanggalKirim,
            boolean sudahDibaca) {
        this.id = id;
        this.idNotifikasi = idNotifikasi;
        this.idPesanan = idPesanan;
        this.pesan = pesan;
        this.tanggalKirim = tanggalKirim;
        this.sudahDibaca = sudahDibaca;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public void kirimNotifikasi() {
        System.out.println(pesan);
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

    private static int extractNumericId(String value) {
        String digits = value == null ? "" : value.replaceAll("\\D+", "");
        if (digits.isEmpty()) {
            return 0;
        }
        return Integer.parseInt(digits);
    }
}
