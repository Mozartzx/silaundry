package silaundry.util;

import java.io.FileInputStream;
import java.io.File;
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
        try (Connection connection = getConnection()) {
            return connection.isValid(2) ? "Koneksi database berhasil." : "Koneksi database tidak valid.";
        } catch (SQLException ex) {
            return "Koneksi database gagal: " + ex.getMessage();
        }
    }

    private static void loadProperties() {
        try (InputStream input = openConfig()) {
            PROPERTIES.load(input);
        } catch (IOException ex) {
            PROPERTIES.setProperty("db.url", "jdbc:mysql://localhost:3306/silaundry_db?useSSL=false&serverTimezone=Asia/Jakarta&allowPublicKeyRetrieval=true");
            PROPERTIES.setProperty("db.user", "root");
            PROPERTIES.setProperty("db.password", "");
            PROPERTIES.setProperty("db.driver", "com.mysql.cj.jdbc.Driver");
        }
    }

    private static InputStream openConfig() throws IOException {
        File file = new File(CONFIG_PATH);
        if (file.isFile()) {
            return new FileInputStream(file);
        }
        InputStream resource = DatabaseConnection.class.getClassLoader().getResourceAsStream("db.properties");
        if (resource != null) {
            return resource;
        }
        throw new IOException("File konfigurasi database tidak ditemukan.");
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
