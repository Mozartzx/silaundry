package silaundry.controller;

import java.sql.SQLException;
import silaundry.dao.UserDAO;
import silaundry.model.Pengguna;
import silaundry.model.enums.Role;
import silaundry.util.DatabaseConnection;
import silaundry.util.PasswordUtil;

public class AuthController {
    private final UserDAO userDAO = new UserDAO();

    public Pengguna login(String username, String password, Role role) throws SQLException {
        String passwordHash = PasswordUtil.hash(password);
        Pengguna pengguna = userDAO.authenticate(username, passwordHash, role);
        if (pengguna != null) {
            pengguna.login();
        }
        return pengguna;
    }

    public String testConnection() {
        return DatabaseConnection.testConnection();
    }
}
