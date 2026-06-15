package silaundry.controller;

import java.sql.SQLException;
import java.util.List;
import silaundry.dao.DashboardDAO;
import silaundry.dao.TarifDAO;
import silaundry.model.DataDasbor;
import silaundry.model.LaporanKeuangan;
import silaundry.model.TarifLaundry;
import silaundry.model.enums.PaketLaundry;

// Menyediakan data dashboard, laporan, dan pengaturan tarif khusus untuk pemilik.
public class PemilikController {
    private final DashboardDAO dashboardDAO = new DashboardDAO();
    private final TarifDAO tarifDAO = new TarifDAO();

    // Mengambil angka ringkas yang akan dipasang pada kartu dashboard.
    public DataDasbor getDataDasbor() throws SQLException {
        return dashboardDAO.getDataDasbor();
    }

    // Mengambil rekap keuangan untuk bulan yang sedang berjalan.
    public LaporanKeuangan getLaporanBulanIni() throws SQLException {
        return dashboardDAO.getLaporanBulanIni();
    }

    // Menampilkan semua tarif, termasuk yang sedang tidak aktif.
    public List<TarifLaundry> getSemuaTarif() throws SQLException {
        return tarifDAO.findAll();
    }

    // Mencari tarif aktif berdasarkan paket yang dipilih.
    public TarifLaundry getTarif(PaketLaundry paketLaundry) throws SQLException {
        return tarifDAO.findByPaket(paketLaundry);
    }

    // Membatasi harga agar tidak tersimpan dengan nilai yang tidak masuk akal.
    public void updateHarga(PaketLaundry paketLaundry, double hargaPerKg) throws SQLException {
        if (hargaPerKg < 1000 || hargaPerKg > 1_000_000) {
            throw new IllegalArgumentException("Harga per kilo harus antara Rp1.000 dan Rp1.000.000.");
        }
        tarifDAO.updateHarga(paketLaundry, hargaPerKg);
    }
}
