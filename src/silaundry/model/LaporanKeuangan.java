package silaundry.model;

// Menampung hasil rekap pendapatan dan jumlah pesanan pada periode tertentu.
public class LaporanKeuangan {
    private String idLaporan;
    private String periodeBulan;
    private double totalPendapatan;
    private int jumlahPesananSelesai;

    // Constructor menyimpan hasil rekap untuk periode yang sedang ditampilkan.
    public LaporanKeuangan(String idLaporan, String periodeBulan, double totalPendapatan, int jumlahPesananSelesai) {
        this.idLaporan = idLaporan;
        this.periodeBulan = periodeBulan;
        this.totalPendapatan = totalPendapatan;
        this.jumlahPesananSelesai = jumlahPesananSelesai;
    }

    public void cetakDataLaporan() {
        System.out.println(formatDataLaporan());
    }

    public String formatDataLaporan() {
        return periodeBulan + " | Pendapatan: Rp " + totalPendapatan + " | Pesanan selesai: " + jumlahPesananSelesai;
    }

    // Getter dipakai dashboard untuk membaca isi laporan.
    public String getIdLaporan() {
        return idLaporan;
    }

    public String getPeriodeBulan() {
        return periodeBulan;
    }

    public double getTotalPendapatan() {
        return totalPendapatan;
    }

    public int getJumlahPesananSelesai() {
        return jumlahPesananSelesai;
    }
}
