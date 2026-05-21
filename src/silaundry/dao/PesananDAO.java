package silaundry.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import silaundry.model.Pesanan;
import silaundry.model.enums.StatusPesanan;
import silaundry.util.DatabaseConnection;

public class PesananDAO {
    public List<Pesanan> findAll() throws SQLException {
        String sql = baseQuery() + " ORDER BY ps.tanggal_masuk DESC, ps.id_pesanan DESC";
        List<Pesanan> pesanan = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection();
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                pesanan.add(mapPesanan(resultSet));
            }
        }
        return pesanan;
    }

    public List<Pesanan> findByPelanggan(String idPelanggan) throws SQLException {
        String sql = baseQuery() + " WHERE ps.id_pelanggan = ? ORDER BY ps.tanggal_masuk DESC";
        List<Pesanan> pesanan = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, idPelanggan);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    pesanan.add(mapPesanan(resultSet));
                }
            }
        }
        return pesanan;
    }

    public Pesanan findById(String idPesanan) throws SQLException {
        String sql = baseQuery() + " WHERE ps.id_pesanan = ?";
        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, idPesanan);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() ? mapPesanan(resultSet) : null;
            }
        }
    }

    public void create(Pesanan pesanan) throws SQLException {
        String sql = """
                INSERT INTO pesanan (id_pesanan, id_pelanggan, id_karyawan, tanggal_masuk,
                    estimasi_selesai, status_pesanan, total_biaya, catatan)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """;
        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, pesanan.getIdPesanan());
            statement.setString(2, pesanan.getIdPelanggan());
            statement.setString(3, pesanan.getIdKaryawan());
            statement.setDate(4, Date.valueOf(pesanan.getTanggalMasuk()));
            statement.setDate(5, Date.valueOf(pesanan.getEstimasiSelesai()));
            statement.setString(6, pesanan.getStatusPesanan().name());
            statement.setDouble(7, pesanan.getTotalBiaya());
            statement.setString(8, pesanan.getCatatan());
            statement.executeUpdate();
        }
    }

    public void updateStatus(String idPesanan, StatusPesanan statusPesanan) throws SQLException {
        String sql = "UPDATE pesanan SET status_pesanan = ? WHERE id_pesanan = ?";
        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, statusPesanan.name());
            statement.setString(2, idPesanan);
            statement.executeUpdate();
        }
    }

    public void delete(String idPesanan) throws SQLException {
        String sql = "DELETE FROM pesanan WHERE id_pesanan = ?";
        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, idPesanan);
            statement.executeUpdate();
        }
    }

    private String baseQuery() {
        return """
                SELECT ps.*, pp.nama_lengkap AS nama_pelanggan, pk.nama_lengkap AS nama_karyawan
                FROM pesanan ps
                JOIN pelanggan pl ON pl.id_pelanggan = ps.id_pelanggan
                JOIN pengguna pp ON pp.id_pengguna = pl.id_pengguna
                LEFT JOIN karyawan ky ON ky.id_karyawan = ps.id_karyawan
                LEFT JOIN pengguna pk ON pk.id_pengguna = ky.id_pengguna
                """;
    }

    private Pesanan mapPesanan(ResultSet resultSet) throws SQLException {
        Pesanan pesanan = new Pesanan(
                resultSet.getString("id_pesanan"),
                resultSet.getString("id_pelanggan"),
                resultSet.getString("id_karyawan"),
                resultSet.getDate("tanggal_masuk").toLocalDate(),
                resultSet.getDate("estimasi_selesai").toLocalDate(),
                StatusPesanan.valueOf(resultSet.getString("status_pesanan")),
                resultSet.getDouble("total_biaya"),
                resultSet.getString("catatan"));
        pesanan.setNamaPelanggan(resultSet.getString("nama_pelanggan"));
        pesanan.setNamaKaryawan(resultSet.getString("nama_karyawan"));
        return pesanan;
    }
}
