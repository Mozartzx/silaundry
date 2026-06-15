package silaundry.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import silaundry.model.Pembayaran;
import silaundry.model.enums.StatusPembayaran;
import silaundry.util.DatabaseConnection;

public class PembayaranDAO {
    public Pembayaran findByPesanan(String idPesanan) throws SQLException {
        String sql = "SELECT * FROM pembayaran WHERE id_pesanan = ?";
        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, idPesanan);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    return null;
                }
                return new Pembayaran(
                        resultSet.getString("id_pembayaran"),
                        resultSet.getString("id_pesanan"),
                        resultSet.getString("metode"),
                        resultSet.getDouble("jumlah"),
                        StatusPembayaran.valueOf(resultSet.getString("status")));
            }
        }
    }

    public void create(Pembayaran pembayaran) throws SQLException {
        String sql = """
                INSERT INTO pembayaran (id_pembayaran, id_pesanan, metode, jumlah, status)
                VALUES (?, ?, ?, ?, ?)
                """;
        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, pembayaran.getIdPembayaran());
            statement.setString(2, pembayaran.getIdPesanan());
            statement.setString(3, pembayaran.getMetode());
            statement.setDouble(4, pembayaran.getJumlah());
            statement.setString(5, pembayaran.getStatus().name());
            statement.executeUpdate();
        }
    }
}
