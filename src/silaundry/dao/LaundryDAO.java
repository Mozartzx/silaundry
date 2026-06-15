package silaundry.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import silaundry.model.ItemPakaian;
import silaundry.model.Notifikasi;
import silaundry.model.Pembayaran;
import silaundry.model.Pesanan;
import silaundry.model.RiwayatPembayaran;
import silaundry.model.enums.KategoriWarna;
import silaundry.model.enums.PaketLaundry;
import silaundry.model.enums.StatusPembayaran;
import silaundry.model.enums.StatusPesanan;
import silaundry.util.DatabaseConnection;

// Menyimpan seluruh query operasional pesanan, item, pembayaran, dan notifikasi.
public class LaundryDAO {
    // Bagian pesanan: query dasar, pembuatan transaksi, dan perubahan status.
    public List<Pesanan> findAllPesanan() throws SQLException {
        String sql = basePesananQuery() + " ORDER BY ps.tanggal_masuk DESC, ps.id_pesanan DESC";
        List<Pesanan> rows = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection();
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                rows.add(mapPesanan(resultSet));
            }
        }
        return rows;
    }

    public List<Pesanan> findPesananByPelanggan(String idPelanggan) throws SQLException {
        String sql = basePesananQuery() + " WHERE ps.id_pelanggan = ? ORDER BY ps.tanggal_masuk DESC";
        List<Pesanan> rows = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, idPelanggan);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    rows.add(mapPesanan(resultSet));
                }
            }
        }
        return rows;
    }

    public Pesanan findPesananById(String idPesanan) throws SQLException {
        String sql = basePesananQuery() + " WHERE ps.id_pesanan = ?";
        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, idPesanan);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() ? mapPesanan(resultSet) : null;
            }
        }
    }

    public void createPesanan(Pesanan pesanan) throws SQLException {
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
                        && !notifikasiExists(connection, notifikasi.getIdPesanan(), notifikasi.getPesan())) {
                    createNotifikasi(connection, notifikasi);
                }
                connection.commit();
            } catch (SQLException ex) {
                connection.rollback();
                throw ex;
            }
        }
    }

    // Bagian item pakaian: pencatatan ciri, pengecekan jumlah, dan smart grouping.
    public List<ItemPakaian> findAllItems() throws SQLException {
        String sql = "SELECT * FROM item_pakaian ORDER BY id_item DESC";
        List<ItemPakaian> rows = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection();
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                rows.add(mapItem(resultSet));
            }
        }
        return rows;
    }

    public List<ItemPakaian> findItemsByPesanan(String idPesanan) throws SQLException {
        String sql = "SELECT * FROM item_pakaian WHERE id_pesanan = ? ORDER BY id_item";
        List<ItemPakaian> rows = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, idPesanan);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    rows.add(mapItem(resultSet));
                }
            }
        }
        return rows;
    }

    public ItemPakaian findItemById(String idItem) throws SQLException {
        String sql = "SELECT * FROM item_pakaian WHERE id_item = ?";
        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, idItem);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() ? mapItem(resultSet) : null;
            }
        }
    }

    public int countItemsByPesanan(String idPesanan) throws SQLException {
        String sql = "SELECT COUNT(*) FROM item_pakaian WHERE id_pesanan = ?";
        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, idPesanan);
            try (ResultSet resultSet = statement.executeQuery()) {
                resultSet.next();
                return resultSet.getInt(1);
            }
        }
    }

    public void createItem(ItemPakaian item) throws SQLException {
        String sql = """
                INSERT INTO item_pakaian (id_item, id_pesanan, jenis_pakaian, kategori_warna,
                    kondisi_awal, deskripsi_detail, label_smart_group)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;
        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, item.getIdItem());
            statement.setString(2, item.getIdPesanan());
            statement.setString(3, item.getJenisPakaian());
            statement.setString(4, item.getKategoriWarna().name());
            statement.setString(5, item.getKondisiAwal());
            statement.setString(6, item.getDeskripsiDetail());
            statement.setString(7, item.getLabelSmartGroup());
            statement.executeUpdate();
        }
    }

    public void updateSmartGroups(List<ItemPakaian> items) throws SQLException {
        String sql = "UPDATE item_pakaian SET label_smart_group = ? WHERE id_item = ?";
        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            connection.setAutoCommit(false);
            try {
                for (ItemPakaian item : items) {
                    statement.setString(1, item.getLabelSmartGroup());
                    statement.setString(2, item.getIdItem());
                    statement.addBatch();
                }
                statement.executeBatch();
                connection.commit();
            } catch (SQLException ex) {
                connection.rollback();
                throw ex;
            }
        }
    }

    public void deleteItem(String idItem) throws SQLException {
        String sql = "DELETE FROM item_pakaian WHERE id_item = ?";
        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, idItem);
            statement.executeUpdate();
        }
    }

    // Bagian pembayaran: daftar tagihan, pelunasan, dan riwayat pembayaran.
    public Pembayaran findPembayaranByPesanan(String idPesanan) throws SQLException {
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

    public List<Pesanan> findPesananBelumBayar() throws SQLException {
        String sql = basePesananQuery() + """
                LEFT JOIN pembayaran byr ON byr.id_pesanan = ps.id_pesanan
                WHERE ps.status_pesanan <> 'DIBATALKAN'
                    AND (byr.id_pembayaran IS NULL OR byr.status = 'BELUM_BAYAR')
                ORDER BY ps.tanggal_masuk, ps.id_pesanan
                """;
        List<Pesanan> rows = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection();
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                rows.add(mapPesanan(resultSet));
            }
        }
        return rows;
    }

    public List<RiwayatPembayaran> findRiwayatPembayaran() throws SQLException {
        // Pesanan yang belum memiliki pembayaran tetap ditampilkan sebagai Belum Bayar.
        String sql = """
                SELECT ps.id_pesanan, pg.nama_lengkap, ps.tanggal_masuk, ps.total_biaya,
                    byr.metode, byr.jumlah, byr.status, byr.tanggal_bayar
                FROM pesanan ps
                JOIN pelanggan pl ON pl.id_pelanggan = ps.id_pelanggan
                JOIN pengguna pg ON pg.id_pengguna = pl.id_pengguna
                LEFT JOIN pembayaran byr ON byr.id_pesanan = ps.id_pesanan
                WHERE ps.status_pesanan <> 'DIBATALKAN'
                ORDER BY CASE
                    WHEN byr.id_pembayaran IS NULL OR byr.status = 'BELUM_BAYAR' THEN 0
                    ELSE 1
                END, ps.tanggal_masuk DESC, ps.id_pesanan DESC
                """;
        List<RiwayatPembayaran> rows = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection();
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                Timestamp tanggalBayar = resultSet.getTimestamp("tanggal_bayar");
                String status = resultSet.getString("status");
                rows.add(new RiwayatPembayaran(
                        resultSet.getString("id_pesanan"),
                        resultSet.getString("nama_lengkap"),
                        resultSet.getDate("tanggal_masuk").toLocalDate(),
                        resultSet.getDouble("total_biaya"),
                        resultSet.getString("metode"),
                        resultSet.getDouble("jumlah"),
                        status == null ? StatusPembayaran.BELUM_BAYAR : StatusPembayaran.valueOf(status),
                        tanggalBayar == null ? null : tanggalBayar.toLocalDateTime()));
            }
        }
        return rows;
    }

    public void createPembayaran(Pembayaran pembayaran) throws SQLException {
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

    public void updatePembayaran(Pembayaran pembayaran) throws SQLException {
        String sql = """
                UPDATE pembayaran
                SET metode = ?, jumlah = ?, status = ?, tanggal_bayar = NOW()
                WHERE id_pembayaran = ?
                """;
        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, pembayaran.getMetode());
            statement.setDouble(2, pembayaran.getJumlah());
            statement.setString(3, pembayaran.getStatus().name());
            statement.setString(4, pembayaran.getIdPembayaran());
            statement.executeUpdate();
        }
    }

    // Bagian notifikasi: penyimpanan pesan aplikasi dan pencarian nomor WhatsApp.
    public void createNotifikasi(Notifikasi notifikasi) throws SQLException {
        try (Connection connection = DatabaseConnection.getConnection()) {
            createNotifikasi(connection, notifikasi);
        }
    }

    public List<Notifikasi> findNotifikasiByPelanggan(String idPelanggan) throws SQLException {
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

    public boolean notifikasiExists(String idPesanan, String pesan) throws SQLException {
        try (Connection connection = DatabaseConnection.getConnection()) {
            return notifikasiExists(connection, idPesanan, pesan);
        }
    }

    public int markAllNotifikasiRead(String idPelanggan) throws SQLException {
        String sql = """
                UPDATE notifikasi n
                JOIN pesanan ps ON ps.id_pesanan = n.id_pesanan
                SET n.sudah_dibaca = TRUE
                WHERE ps.id_pelanggan = ? AND n.sudah_dibaca = FALSE
                """;
        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, idPelanggan);
            return statement.executeUpdate();
        }
    }

    private void createNotifikasi(Connection connection, Notifikasi notifikasi) throws SQLException {
        String sql = """
                INSERT INTO notifikasi (id_notifikasi, id_pesanan, pesan, tanggal_kirim, sudah_dibaca)
                VALUES (?, ?, ?, ?, ?)
                """;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, notifikasi.getIdNotifikasi());
            statement.setString(2, notifikasi.getIdPesanan());
            statement.setString(3, notifikasi.getPesan());
            statement.setTimestamp(4, Timestamp.valueOf(notifikasi.getTanggalKirim()));
            statement.setBoolean(5, notifikasi.isSudahDibaca());
            statement.executeUpdate();
        }
    }

    private boolean notifikasiExists(Connection connection, String idPesanan, String pesan) throws SQLException {
        String sql = "SELECT 1 FROM notifikasi WHERE id_pesanan = ? AND pesan = ? LIMIT 1";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, idPesanan);
            statement.setString(2, pesan);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        }
    }

    // Query dan mapper di bawah dipakai ulang supaya hasil objek tetap konsisten.
    private String basePesananQuery() {
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

    private ItemPakaian mapItem(ResultSet resultSet) throws SQLException {
        return new ItemPakaian(
                resultSet.getString("id_item"),
                resultSet.getString("id_pesanan"),
                resultSet.getString("jenis_pakaian"),
                KategoriWarna.valueOf(resultSet.getString("kategori_warna")),
                resultSet.getString("kondisi_awal"),
                resultSet.getString("deskripsi_detail"),
                resultSet.getString("label_smart_group"));
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
