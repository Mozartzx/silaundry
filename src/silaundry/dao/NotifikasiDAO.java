package silaundry.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import silaundry.model.Notifikasi;
import silaundry.util.DatabaseConnection;

public class NotifikasiDAO {
    public void create(Notifikasi notifikasi) throws SQLException {
        String sql = """
                INSERT INTO notifikasi (id_notifikasi, id_pesanan, pesan, tanggal_kirim, sudah_dibaca)
                VALUES (?, ?, ?, ?, ?)
                """;
        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, notifikasi.getIdNotifikasi());
            statement.setString(2, notifikasi.getIdPesanan());
            statement.setString(3, notifikasi.getPesan());
            statement.setTimestamp(4, Timestamp.valueOf(notifikasi.getTanggalKirim()));
            statement.setBoolean(5, notifikasi.isSudahDibaca());
            statement.executeUpdate();
        }
    }

    public List<Notifikasi> findByPelanggan(String idPelanggan) throws SQLException {
        String sql = """
                SELECT n.*
                FROM notifikasi n
                JOIN pesanan ps ON ps.id_pesanan = n.id_pesanan
                WHERE ps.id_pelanggan = ?
                ORDER BY n.tanggal_kirim DESC, n.id_notifikasi DESC
                """;
        List<Notifikasi> rows = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, idPelanggan);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    rows.add(mapNotifikasi(resultSet));
                }
            }
        }
        return rows;
    }

    public String findNomorTeleponByPesanan(String idPesanan) throws SQLException {
        String sql = """
                SELECT pg.nomor_telepon
                FROM pesanan ps
                JOIN pelanggan pl ON pl.id_pelanggan = ps.id_pelanggan
                JOIN pengguna pg ON pg.id_pengguna = pl.id_pengguna
                WHERE ps.id_pesanan = ?
                """;
        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, idPesanan);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() ? resultSet.getString("nomor_telepon") : null;
            }
        }
    }

    private Notifikasi mapNotifikasi(ResultSet resultSet) throws SQLException {
        return new Notifikasi(
                resultSet.getString("id_notifikasi"),
                resultSet.getString("id_pesanan"),
                resultSet.getString("pesan"),
                resultSet.getTimestamp("tanggal_kirim").toLocalDateTime(),
                resultSet.getBoolean("sudah_dibaca"));
    }
}
