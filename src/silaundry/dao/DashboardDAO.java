package silaundry.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.YearMonth;
import silaundry.model.DataDasbor;
import silaundry.model.LaporanKeuangan;
import silaundry.util.DatabaseConnection;

// Mengambil hasil perhitungan dashboard dan laporan langsung dari transaksi database.
public class DashboardDAO {
    // Query ini menghitung metrik dashboard tanpa menyimpan angka ringkasan manual.
    public DataDasbor getDataDasbor() throws SQLException {
        String sql = """
                SELECT
                    (SELECT COUNT(*) FROM pesanan WHERE status_pesanan NOT IN ('SELESAI','DIBATALKAN')) AS aktif,
                    (SELECT COALESCE(SUM(total_biaya), 0) FROM pesanan
                        WHERE status_pesanan NOT IN ('SELESAI','DIBATALKAN')) AS nilai_aktif,
                    (SELECT COALESCE(SUM(p.jumlah), 0)
                        FROM pembayaran p
                        WHERE YEAR(p.tanggal_bayar) = YEAR(CURRENT_DATE())
                          AND MONTH(p.tanggal_bayar) = MONTH(CURRENT_DATE())) AS pendapatan_diterima,
                    (SELECT COUNT(*) FROM item_pakaian) AS item_count,
                    (SELECT COUNT(*) FROM pelanggan) AS pelanggan_count
                """;
        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet resultSet = statement.executeQuery()) {
            resultSet.next();
            return new DataDasbor(
                    "DSB-" + YearMonth.now(),
                    resultSet.getInt("aktif"),
                    resultSet.getDouble("nilai_aktif"),
                    resultSet.getDouble("pendapatan_diterima"),
                    resultSet.getInt("item_count"),
                    resultSet.getInt("pelanggan_count"));
        }
    }

    // Laporan hanya menjumlahkan pembayaran lunas pada bulan yang sedang berjalan.
    public LaporanKeuangan getLaporanBulanIni() throws SQLException {
        String sql = """
                SELECT
                    (SELECT COALESCE(SUM(p.jumlah), 0)
                     FROM pembayaran p
                     WHERE YEAR(p.tanggal_bayar) = YEAR(CURRENT_DATE())
                       AND MONTH(p.tanggal_bayar) = MONTH(CURRENT_DATE())) AS total_pendapatan,
                    (SELECT COUNT(*)
                     FROM pesanan ps
                     WHERE ps.status_pesanan = 'SELESAI'
                       AND YEAR(ps.status_diperbarui_pada) = YEAR(CURRENT_DATE())
                       AND MONTH(ps.status_diperbarui_pada) = MONTH(CURRENT_DATE())) AS selesai
                """;
        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet resultSet = statement.executeQuery()) {
            resultSet.next();
            YearMonth periode = YearMonth.now();
            return new LaporanKeuangan(
                    "LAP-" + periode,
                    periode.toString(),
                    resultSet.getDouble("total_pendapatan"),
                    resultSet.getInt("selesai"));
        }
    }
}
