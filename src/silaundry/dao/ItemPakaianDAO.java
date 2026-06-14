package silaundry.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import silaundry.model.ItemPakaian;
import silaundry.model.enums.KategoriWarna;
import silaundry.util.DatabaseConnection;

public class ItemPakaianDAO {
    public List<ItemPakaian> findAll() throws SQLException {
        String sql = "SELECT * FROM item_pakaian ORDER BY id_item DESC";
        List<ItemPakaian> items = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection();
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                items.add(mapItem(resultSet));
            }
        }
        return items;
    }

    public List<ItemPakaian> findByPesanan(String idPesanan) throws SQLException {
        String sql = "SELECT * FROM item_pakaian WHERE id_pesanan = ? ORDER BY id_item";
        List<ItemPakaian> items = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, idPesanan);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    items.add(mapItem(resultSet));
                }
            }
        }
        return items;
    }

    public ItemPakaian findByTrackingKey(String trackingKey) throws SQLException {
        String sql = """
                SELECT * FROM item_pakaian
                WHERE id_item = ? OR kode_qr = ?
                LIMIT 1
                """;
        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, trackingKey);
            statement.setString(2, trackingKey);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() ? mapItem(resultSet) : null;
            }
        }
    }

    public ItemPakaian findByTrackingKeyAndPelanggan(String trackingKey, String idPelanggan) throws SQLException {
        String sql = """
                SELECT ip.*
                FROM item_pakaian ip
                JOIN pesanan ps ON ps.id_pesanan = ip.id_pesanan
                WHERE (ip.id_item = ? OR ip.kode_qr = ?)
                  AND ps.id_pelanggan = ?
                LIMIT 1
                """;
        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, trackingKey);
            statement.setString(2, trackingKey);
            statement.setString(3, idPelanggan);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() ? mapItem(resultSet) : null;
            }
        }
    }

    public int countByPesanan(String idPesanan) throws SQLException {
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

    public void create(ItemPakaian item) throws SQLException {
        String sql = """
                INSERT INTO item_pakaian (id_item, id_pesanan, jenis_pakaian, kategori_warna,
                    kondisi_awal, deskripsi_detail, label_smart_group, kode_qr)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
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
            statement.setString(8, item.getKodeQR());
            statement.executeUpdate();
        }
    }

    public void updateSmartGroup(String idItem, String labelSmartGroup) throws SQLException {
        String sql = "UPDATE item_pakaian SET label_smart_group = ? WHERE id_item = ?";
        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, labelSmartGroup);
            statement.setString(2, idItem);
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

    public void delete(String idItem) throws SQLException {
        String sql = "DELETE FROM item_pakaian WHERE id_item = ?";
        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, idItem);
            statement.executeUpdate();
        }
    }

    private ItemPakaian mapItem(ResultSet resultSet) throws SQLException {
        return new ItemPakaian(
                resultSet.getString("id_item"),
                resultSet.getString("id_pesanan"),
                resultSet.getString("jenis_pakaian"),
                KategoriWarna.valueOf(resultSet.getString("kategori_warna")),
                resultSet.getString("kondisi_awal"),
                resultSet.getString("deskripsi_detail"),
                resultSet.getString("label_smart_group"),
                resultSet.getString("kode_qr"));
    }
}
