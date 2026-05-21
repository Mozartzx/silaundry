package silaundry.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import silaundry.model.TarifLaundry;
import silaundry.model.enums.PaketLaundry;
import silaundry.util.DatabaseConnection;

public class TarifLaundryDAO {
    public List<TarifLaundry> findAll() throws SQLException {
        String sql = "SELECT * FROM tarif_laundry ORDER BY estimasi_hari DESC, paket_laundry";
        List<TarifLaundry> rows = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection();
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                rows.add(mapTarif(resultSet));
            }
        }
        return rows;
    }

    public List<TarifLaundry> findActive() throws SQLException {
        String sql = "SELECT * FROM tarif_laundry WHERE aktif = TRUE ORDER BY estimasi_hari DESC, paket_laundry";
        List<TarifLaundry> rows = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection();
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                rows.add(mapTarif(resultSet));
            }
        }
        return rows;
    }

    public TarifLaundry findByPaket(PaketLaundry paketLaundry) throws SQLException {
        String sql = "SELECT * FROM tarif_laundry WHERE paket_laundry = ? AND aktif = TRUE LIMIT 1";
        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, paketLaundry.name());
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() ? mapTarif(resultSet) : null;
            }
        }
    }

    public void updateHarga(PaketLaundry paketLaundry, double hargaPerKg) throws SQLException {
        String sql = "UPDATE tarif_laundry SET harga_per_kg = ? WHERE paket_laundry = ?";
        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setDouble(1, hargaPerKg);
            statement.setString(2, paketLaundry.name());
            statement.executeUpdate();
        }
    }

    private TarifLaundry mapTarif(ResultSet resultSet) throws SQLException {
        return new TarifLaundry(
                resultSet.getString("id_tarif"),
                PaketLaundry.valueOf(resultSet.getString("paket_laundry")),
                resultSet.getString("nama_paket"),
                resultSet.getInt("estimasi_hari"),
                resultSet.getDouble("harga_per_kg"),
                resultSet.getBoolean("aktif"));
    }
}
