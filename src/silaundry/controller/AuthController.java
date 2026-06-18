package silaundry.controller;

import silaundry.data.DataStore;
import silaundry.model.Pengguna;
import silaundry.model.enums.Role;

// Menghubungkan form login dengan daftar akun yang tersimpan sementara di ArrayList.
public class AuthController {
    private final DataStore dataStore = DataStore.getInstance();

    // Login mencocokkan username, password, dan role dari ArrayList pengguna.
    public Pengguna login(String username, String password, Role role) {
        if (username == null || username.isBlank() || password == null || role == null) {
            return null;
        }
        Pengguna pengguna = dataStore.cariPengguna(username.trim(), password, role);
        if (pengguna != null) {
            pengguna.login();
        }
        return pengguna;
    }
}
