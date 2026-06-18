package silaundry.model;

// Menampung angka ringkasan yang ditampilkan pada dashboard admin.
public class DataDasbor {
    private String idDasbor;
    private int totalPesananAktif;
    private double estimasiPendapatan;
    private double pendapatanDiterima;
    private int totalItem;
    private int totalPelanggan;

    // Constructor menerima hasil perhitungan terbaru dari AdminController.
    public DataDasbor(String idDasbor, int totalPesananAktif, double estimasiPendapatan,
            double pendapatanDiterima, int totalItem, int totalPelanggan) {
        this.idDasbor = idDasbor;
        this.totalPesananAktif = totalPesananAktif;
        this.estimasiPendapatan = estimasiPendapatan;
        this.pendapatanDiterima = pendapatanDiterima;
        this.totalItem = totalItem;
        this.totalPelanggan = totalPelanggan;
    }

    // Method ini dapat memperbarui seluruh angka dashboard dalam satu proses.
    public void perbaruiMetrikHarian(int totalPesananAktif, double estimasiPendapatan,
            double pendapatanDiterima, int totalItem, int totalPelanggan) {
        this.totalPesananAktif = totalPesananAktif;
        this.estimasiPendapatan = estimasiPendapatan;
        this.pendapatanDiterima = pendapatanDiterima;
        this.totalItem = totalItem;
        this.totalPelanggan = totalPelanggan;
    }

    // Getter digunakan oleh halaman dashboard untuk menampilkan setiap metrik.
    public String getIdDasbor() {
        return idDasbor;
    }

    public int getTotalPesananAktif() {
        return totalPesananAktif;
    }

    public double getEstimasiPendapatan() {
        return estimasiPendapatan;
    }

    public double getPendapatanDiterima() {
        return pendapatanDiterima;
    }

    public int getTotalItem() {
        return totalItem;
    }

    public int getTotalPelanggan() {
        return totalPelanggan;
    }
}
