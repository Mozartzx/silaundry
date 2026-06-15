package silaundry.model;

import silaundry.model.enums.StatusPembayaran;

// Menyimpan hasil pembayaran sebuah pesanan beserta metode dan status pelunasannya.
public class Pembayaran {
    private String idPembayaran;
    private String idPesanan;
    private String metode;
    private double jumlah;
    private StatusPembayaran status;

    public Pembayaran(String idPembayaran, String idPesanan, String metode, double jumlah, StatusPembayaran status) {
        this.idPembayaran = idPembayaran;
        this.idPesanan = idPesanan;
        this.metode = metode;
        this.jumlah = jumlah;
        this.status = status;
    }

    public void prosesPembayaran() {
        // Nominal positif dianggap sebagai pembayaran yang sudah lunas.
        status = jumlah > 0 ? StatusPembayaran.LUNAS : StatusPembayaran.BELUM_BAYAR;
    }

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
}
