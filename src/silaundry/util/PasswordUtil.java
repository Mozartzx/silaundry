package silaundry.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

// Mengubah password menjadi hash SHA-256 sebelum dibandingkan atau disimpan.
public final class PasswordUtil {
    private PasswordUtil() {
    }

    public static String hash(String plainText) {
        // Hasil byte SHA-256 diubah menjadi teks heksadesimal agar mudah disimpan di database.
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] bytes = digest.digest(plainText.getBytes(StandardCharsets.UTF_8));
            StringBuilder builder = new StringBuilder();
            for (byte b : bytes) {
                builder.append(String.format("%02x", b));
            }
            return builder.toString();
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("SHA-256 tidak tersedia", ex);
        }
    }

    public static boolean matches(String plainText, String hashed) {
        // Password input tidak dibandingkan langsung, tetapi dibandingkan hasil hash-nya.
        return hash(plainText).equalsIgnoreCase(hashed);
    }
}
