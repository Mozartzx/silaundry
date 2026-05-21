package silaundry.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public final class DatabaseConnection {
    private static final String CONFIG_PATH = "config/db.properties";
    private static final Properties PROPERTIES = new Properties();

    static {
        loadProperties();
        loadDriver();
    }

    private DatabaseConnection() {
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
                PROPERTIES.getProperty("db.url"),
                PROPERTIES.getProperty("db.user"),
                PROPERTIES.getProperty("db.password"));
    }

    public static String testConnection() {
        try (Connection ignored = getConnection()) {
            return "Koneksi database berhasil.";
        } catch (SQLException ex) {
            return "Koneksi database gagal: " + ex.getMessage();
        }
    }

    private static void loadProperties() {
        try (InputStream input = new FileInputStream(CONFIG_PATH)) {
            PROPERTIES.load(input);
        } catch (IOException ex) {
            PROPERTIES.setProperty("db.url", "jdbc:mysql://localhost:3306/silaundry_db?useSSL=false&serverTimezone=Asia/Jakarta&allowPublicKeyRetrieval=true");
            PROPERTIES.setProperty("db.user", "root");
            PROPERTIES.setProperty("db.password", "");
            PROPERTIES.setProperty("db.driver", "com.mysql.cj.jdbc.Driver");
        }
    }

    private static void loadDriver() {
        String driver = PROPERTIES.getProperty("db.driver", "com.mysql.cj.jdbc.Driver");
        try {
            Class.forName(driver);
        } catch (ClassNotFoundException ex) {
            System.err.println("Driver JDBC belum ditemukan: " + driver);
        }
    }
}
