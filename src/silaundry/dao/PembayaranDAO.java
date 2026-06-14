package silaundry.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import silaundry.model.Pembayaran;
import silaundry.model.enums.StatusPembayaran;
import silaundry.util.DatabaseConnection;
import silaundry.util.IdGenerator;

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

    public void upsert(Pembayaran pembayaran) throws SQLException {
        String update = """
                UPDATE pembayaran
                SET metode = ?, jumlah = ?, status = ?
                WHERE id_pesanan = ?
                """;
        String insert = """
                INSERT INTO pembayaran (id_pembayaran, id_pesanan, metode, jumlah, status)
                VALUES (?, ?, ?, ?, ?)
                """;
        try (Connection connection = DatabaseConnection.getConnection()) {
            connection.setAutoCommit(false);
            try {
                PaymentSnapshot sebelumnya = findForUpdate(connection, pembayaran.getIdPesanan());
                if (sebelumnya != null && sebelumnya.status() == StatusPembayaran.LUNAS) {
                    throw new SQLException("Pembayaran yang sudah lunas tidak dapat diubah.");
                }
                double jumlahSebelumnya = sebelumnya == null ? 0 : sebelumnya.jumlah();
                double pembayaranBaru = pembayaran.getJumlah() - jumlahSebelumnya;
                if (pembayaranBaru <= 0) {
                    throw new SQLException("Total pembayaran baru harus lebih besar dari pembayaran sebelumnya.");
                }
                try (PreparedStatement updateStatement = connection.prepareStatement(update)) {
                    updateStatement.setString(1, pembayaran.getMetode());
                    updateStatement.setDouble(2, pembayaran.getJumlah());
                    updateStatement.setString(3, pembayaran.getStatus().name());
                    updateStatement.setString(4, pembayaran.getIdPesanan());
                    int changed = updateStatement.executeUpdate();
                    if (changed == 0) {
                        try (PreparedStatement insertStatement = connection.prepareStatement(insert)) {
                            insertStatement.setString(1, pembayaran.getIdPembayaran());
                            insertStatement.setString(2, pembayaran.getIdPesanan());
                            insertStatement.setString(3, pembayaran.getMetode());
                            insertStatement.setDouble(4, pembayaran.getJumlah());
                            insertStatement.setString(5, pembayaran.getStatus().name());
                            insertStatement.executeUpdate();
                        }
                    }
                    insertDetail(connection, pembayaran.getIdPembayaran(), pembayaran.getMetode(), pembayaranBaru,
                            pembayaran.getStatus());
                    connection.commit();
                }
            } catch (SQLException ex) {
                connection.rollback();
                throw ex;
            }
        }
    }

    private PaymentSnapshot findForUpdate(Connection connection, String idPesanan) throws SQLException {
        String sql = "SELECT jumlah, status FROM pembayaran WHERE id_pesanan = ? FOR UPDATE";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, idPesanan);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next()
                        ? new PaymentSnapshot(resultSet.getDouble("jumlah"),
                                StatusPembayaran.valueOf(resultSet.getString("status")))
                        : null;
            }
        }
    }

    private void insertDetail(Connection connection, String idPembayaran, String metode, double jumlah,
            StatusPembayaran status) throws SQLException {
        String sql = """
                INSERT INTO detail_pembayaran
                    (id_detail, id_pembayaran, waktu_bayar, metode, jumlah, keterangan)
                VALUES (?, ?, ?, ?, ?, ?)
                """;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, IdGenerator.generate("DPY"));
            statement.setString(2, idPembayaran);
            statement.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
            statement.setString(4, metode);
            statement.setDouble(5, jumlah);
            statement.setString(6, status == StatusPembayaran.LUNAS
                    ? "Pembayaran melunasi tagihan"
                    : "Pembayaran sebagian");
            statement.executeUpdate();
        }
    }

    private record PaymentSnapshot(double jumlah, StatusPembayaran status) {
    }
}
