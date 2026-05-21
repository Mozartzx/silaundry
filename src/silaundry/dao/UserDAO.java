package silaundry.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import silaundry.model.Karyawan;
import silaundry.model.Pelanggan;
import silaundry.model.Pemilik;
import silaundry.model.Pengguna;
import silaundry.model.enums.Role;
import silaundry.util.DatabaseConnection;

public class UserDAO {
    public Pengguna authenticate(String username, String passwordHash, Role role) throws SQLException {
        String sql = """
                SELECT p.*, pl.id_pelanggan, pl.alamat, k.id_karyawan, k.shift_kerja, pm.id_pemilik
                FROM pengguna p
                LEFT JOIN pelanggan pl ON pl.id_pengguna = p.id_pengguna
                LEFT JOIN karyawan k ON k.id_pengguna = p.id_pengguna
                LEFT JOIN pemilik pm ON pm.id_pengguna = p.id_pengguna
                WHERE p.username = ? AND p.kata_sandi = ? AND p.role = ? AND p.aktif = TRUE
                """;
        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, username);
            statement.setString(2, passwordHash);
            statement.setString(3, role.name());
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    Pengguna pengguna = mapPengguna(resultSet);
                    updateLastLogin(connection, pengguna.getIdPengguna());
                    return pengguna;
                }
                return null;
            }
        }
    }

    public List<Pelanggan> findAllPelanggan() throws SQLException {
        String sql = """
                SELECT p.*, pl.id_pelanggan, pl.alamat
                FROM pelanggan pl
                JOIN pengguna p ON p.id_pengguna = pl.id_pengguna
                WHERE p.aktif = TRUE
                ORDER BY p.nama_lengkap
                """;
        List<Pelanggan> pelanggan = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection();
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                pelanggan.add(new Pelanggan(
                        resultSet.getString("id_pengguna"),
                        resultSet.getString("username"),
                        resultSet.getString("nama_lengkap"),
                        resultSet.getString("nomor_telepon"),
                        resultSet.getString("kata_sandi"),
                        resultSet.getString("id_pelanggan"),
                        resultSet.getString("alamat")));
            }
        }
        return pelanggan;
    }

    public List<Karyawan> findAllKaryawan() throws SQLException {
        String sql = """
                SELECT p.*, k.id_karyawan, k.shift_kerja
                FROM karyawan k
                JOIN pengguna p ON p.id_pengguna = k.id_pengguna
                WHERE p.aktif = TRUE
                ORDER BY p.nama_lengkap
                """;
        List<Karyawan> karyawan = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection();
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                karyawan.add(new Karyawan(
                        resultSet.getString("id_pengguna"),
                        resultSet.getString("username"),
                        resultSet.getString("nama_lengkap"),
                        resultSet.getString("nomor_telepon"),
                        resultSet.getString("kata_sandi"),
                        resultSet.getString("id_karyawan"),
                        resultSet.getString("shift_kerja")));
            }
        }
        return karyawan;
    }

    public void createPelanggan(Pelanggan pelanggan) throws SQLException {
        String insertPengguna = """
                INSERT INTO pengguna (id_pengguna, username, nama_lengkap, nomor_telepon, kata_sandi, role)
                VALUES (?, ?, ?, ?, ?, 'PELANGGAN')
                """;
        String insertPelanggan = "INSERT INTO pelanggan (id_pelanggan, id_pengguna, alamat) VALUES (?, ?, ?)";
        try (Connection connection = DatabaseConnection.getConnection()) {
            connection.setAutoCommit(false);
            try (PreparedStatement penggunaStatement = connection.prepareStatement(insertPengguna);
                    PreparedStatement pelangganStatement = connection.prepareStatement(insertPelanggan)) {
                fillPenggunaStatement(penggunaStatement, pelanggan);
                penggunaStatement.executeUpdate();

                pelangganStatement.setString(1, pelanggan.getIdPelanggan());
                pelangganStatement.setString(2, pelanggan.getIdPengguna());
                pelangganStatement.setString(3, pelanggan.getAlamat());
                pelangganStatement.executeUpdate();
                connection.commit();
            } catch (SQLException ex) {
                connection.rollback();
                throw ex;
            }
        }
    }

    public void createKaryawan(Karyawan karyawan) throws SQLException {
        String insertPengguna = """
                INSERT INTO pengguna (id_pengguna, username, nama_lengkap, nomor_telepon, kata_sandi, role)
                VALUES (?, ?, ?, ?, ?, 'KARYAWAN')
                """;
        String insertKaryawan = "INSERT INTO karyawan (id_karyawan, id_pengguna, shift_kerja) VALUES (?, ?, ?)";
        try (Connection connection = DatabaseConnection.getConnection()) {
            connection.setAutoCommit(false);
            try (PreparedStatement penggunaStatement = connection.prepareStatement(insertPengguna);
                    PreparedStatement karyawanStatement = connection.prepareStatement(insertKaryawan)) {
                fillPenggunaStatement(penggunaStatement, karyawan);
                penggunaStatement.executeUpdate();

                karyawanStatement.setString(1, karyawan.getIdKaryawan());
                karyawanStatement.setString(2, karyawan.getIdPengguna());
                karyawanStatement.setString(3, karyawan.getShiftKerja());
                karyawanStatement.executeUpdate();
                connection.commit();
            } catch (SQLException ex) {
                connection.rollback();
                throw ex;
            }
        }
    }

    public void deleteKaryawan(String idKaryawan) throws SQLException {
        String sql = """
                UPDATE pengguna p
                JOIN karyawan k ON k.id_pengguna = p.id_pengguna
                SET p.aktif = FALSE
                WHERE k.id_karyawan = ?
                """;
        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, idKaryawan);
            statement.executeUpdate();
        }
    }

    public String findFirstKaryawanId() throws SQLException {
        String sql = "SELECT id_karyawan FROM karyawan ORDER BY id_karyawan LIMIT 1";
        try (Connection connection = DatabaseConnection.getConnection();
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(sql)) {
            return resultSet.next() ? resultSet.getString("id_karyawan") : null;
        }
    }

    private void fillPenggunaStatement(PreparedStatement statement, Pengguna pengguna) throws SQLException {
        statement.setString(1, pengguna.getIdPengguna());
        statement.setString(2, pengguna.getUsername());
        statement.setString(3, pengguna.getNamaLengkap());
        statement.setString(4, pengguna.getNomorTelepon());
        statement.setString(5, pengguna.getKataSandi());
    }

    private void updateLastLogin(Connection connection, String idPengguna) {
        String sql = "UPDATE pengguna SET terakhir_login = NOW() WHERE id_pengguna = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, idPengguna);
            statement.executeUpdate();
        } catch (SQLException ex) {
            System.err.println("Kolom terakhir_login belum tersedia: " + ex.getMessage());
        }
    }

    private Pengguna mapPengguna(ResultSet resultSet) throws SQLException {
        Role role = Role.fromDb(resultSet.getString("role"));
        return switch (role) {
            case PELANGGAN -> new Pelanggan(
                    resultSet.getString("id_pengguna"),
                    resultSet.getString("username"),
                    resultSet.getString("nama_lengkap"),
                    resultSet.getString("nomor_telepon"),
                    resultSet.getString("kata_sandi"),
                    resultSet.getString("id_pelanggan"),
                    resultSet.getString("alamat"));
            case KARYAWAN -> new Karyawan(
                    resultSet.getString("id_pengguna"),
                    resultSet.getString("username"),
                    resultSet.getString("nama_lengkap"),
                    resultSet.getString("nomor_telepon"),
                    resultSet.getString("kata_sandi"),
                    resultSet.getString("id_karyawan"),
                    resultSet.getString("shift_kerja"));
            case PEMILIK -> new Pemilik(
                    resultSet.getString("id_pengguna"),
                    resultSet.getString("username"),
                    resultSet.getString("nama_lengkap"),
                    resultSet.getString("nomor_telepon"),
                    resultSet.getString("kata_sandi"),
                    resultSet.getString("id_pemilik"));
        };
    }
}
