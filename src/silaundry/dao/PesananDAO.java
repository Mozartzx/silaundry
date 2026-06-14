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
import silaundry.model.Notifikasi;
import silaundry.model.enums.PaketLaundry;
import silaundry.model.enums.StatusPesanan;
import silaundry.util.DatabaseConnection;

public class PesananDAO {
    private final NotifikasiDAO notifikasiDAO = new NotifikasiDAO();
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
                    estimasi_selesai, status_pesanan, paket_laundry, berat_kg, harga_per_kg, total_biaya, catatan)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;
        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, pesanan.getIdPesanan());
            statement.setString(2, pesanan.getIdPelanggan());
            statement.setString(3, pesanan.getIdKaryawan());
            statement.setDate(4, Date.valueOf(pesanan.getTanggalMasuk()));
            statement.setDate(5, Date.valueOf(pesanan.getEstimasiSelesai()));
            statement.setString(6, pesanan.getStatusPesanan().name());
            statement.setString(7, pesanan.getPaketLaundry().name());
            statement.setDouble(8, pesanan.getBeratKg());
            statement.setDouble(9, pesanan.getHargaPerKg());
            statement.setDouble(10, pesanan.getTotalBiaya());
            statement.setString(11, pesanan.getCatatan());
            statement.executeUpdate();
        }
    }

    public void updateStatus(String idPesanan, StatusPesanan statusPesanan) throws SQLException {
        String sql = "UPDATE pesanan SET status_pesanan = ?, status_diperbarui_pada = NOW() WHERE id_pesanan = ?";
        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, statusPesanan.name());
            statement.setString(2, idPesanan);
            statement.executeUpdate();
        }
    }

    public void updateStatusDanNotifikasi(String idPesanan, StatusPesanan statusLama,
            StatusPesanan statusBaru, Notifikasi notifikasi) throws SQLException {
        String sql = """
                UPDATE pesanan
                SET status_pesanan = ?, status_diperbarui_pada = NOW()
                WHERE id_pesanan = ? AND status_pesanan = ?
                """;
        try (Connection connection = DatabaseConnection.getConnection()) {
            connection.setAutoCommit(false);
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, statusBaru.name());
                statement.setString(2, idPesanan);
                statement.setString(3, statusLama.name());
                if (statement.executeUpdate() != 1) {
                    throw new SQLException("Status pesanan berubah oleh proses lain. Silakan refresh data.");
                }
                if (notifikasi != null
                        && !notifikasiDAO.exists(connection, notifikasi.getIdPesanan(), notifikasi.getPesan())) {
                    notifikasiDAO.create(connection, notifikasi);
                }
                connection.commit();
            } catch (SQLException ex) {
                connection.rollback();
                throw ex;
            }
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
                PaketLaundry.valueOf(resultSet.getString("paket_laundry")),
                resultSet.getDouble("berat_kg"),
                resultSet.getDouble("harga_per_kg"),
                resultSet.getDouble("total_biaya"),
                resultSet.getString("catatan"));
        pesanan.setNamaPelanggan(resultSet.getString("nama_pelanggan"));
        pesanan.setNamaKaryawan(resultSet.getString("nama_karyawan"));
        return pesanan;
    }
}
