package silaundry.controller;

import java.sql.SQLException;
import silaundry.dao.UserDAO;
import silaundry.model.Pengguna;
import silaundry.model.enums.Role;
import silaundry.util.DatabaseConnection;
import silaundry.util.PasswordUtil;

// Menghubungkan proses login pada view dengan data akun yang tersimpan di database.
public class AuthController {
    private final UserDAO userDAO = new UserDAO();

    // Password di-hash dahulu lalu dicocokkan bersama username dan role.
    public Pengguna login(String username, String password, Role role) throws SQLException {
        String passwordHash = PasswordUtil.hash(password);
        Pengguna pengguna = userDAO.authenticate(username, passwordHash, role);
        if (pengguna != null) {
            pengguna.login();
        }
        return pengguna;
    }

    // Dipakai tombol Test DB untuk memastikan konfigurasi MySQL dapat digunakan.
    public String testConnection() {
        return DatabaseConnection.testConnection();
    }
}
