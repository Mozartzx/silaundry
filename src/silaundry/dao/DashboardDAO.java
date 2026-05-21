package silaundry.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.YearMonth;
import silaundry.model.DataDasbor;
import silaundry.model.LaporanKeuangan;
import silaundry.util.DatabaseConnection;

public class DashboardDAO {
    public DataDasbor getDataDasbor() throws SQLException {
        String sql = """
                SELECT
                    (SELECT COUNT(*) FROM pesanan WHERE status_pesanan NOT IN ('SELESAI','DIBATALKAN')) AS aktif,
                    (SELECT COALESCE(SUM(total_biaya), 0) FROM pesanan WHERE status_pesanan <> 'DIBATALKAN') AS pendapatan,
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
                    resultSet.getDouble("pendapatan"),
                    resultSet.getInt("item_count"),
                    resultSet.getInt("pelanggan_count"));
        }
    }

    public LaporanKeuangan getLaporanBulanIni() throws SQLException {
        String sql = """
                SELECT COALESCE(SUM(p.jumlah), 0) AS total_pendapatan,
                       COUNT(CASE WHEN ps.status_pesanan = 'SELESAI' THEN 1 END) AS selesai
                FROM pesanan ps
                LEFT JOIN pembayaran p ON p.id_pesanan = ps.id_pesanan AND p.status = 'LUNAS'
                WHERE YEAR(ps.tanggal_masuk) = YEAR(CURRENT_DATE())
                  AND MONTH(ps.tanggal_masuk) = MONTH(CURRENT_DATE())
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
