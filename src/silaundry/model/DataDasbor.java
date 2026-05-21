package silaundry.model;

public class DataDasbor {
    private String idDasbor;
    private int totalPesananAktif;
    private double estimasiPendapatan;
    private int totalItem;
    private int totalPelanggan;

    public DataDasbor(String idDasbor, int totalPesananAktif, double estimasiPendapatan, int totalItem,
            int totalPelanggan) {
        this.idDasbor = idDasbor;
        this.totalPesananAktif = totalPesananAktif;
        this.estimasiPendapatan = estimasiPendapatan;
        this.totalItem = totalItem;
        this.totalPelanggan = totalPelanggan;
    }

    public void perbaruiMetrikHarian(int totalPesananAktif, double estimasiPendapatan, int totalItem,
            int totalPelanggan) {
        this.totalPesananAktif = totalPesananAktif;
        this.estimasiPendapatan = estimasiPendapatan;
        this.totalItem = totalItem;
        this.totalPelanggan = totalPelanggan;
    }

    public void perbaruiMetrikHarian() {
        System.out.println("Metrik harian diperbarui.");
    }

    public String getIdDasbor() {
        return idDasbor;
    }

    public int getTotalPesananAktif() {
        return totalPesananAktif;
    }

    public double getEstimasiPendapatan() {
        return estimasiPendapatan;
    }

    public int getTotalItem() {
        return totalItem;
    }

    public int getTotalPelanggan() {
        return totalPelanggan;
    }
}
