package silaundry.controller;

import java.sql.SQLException;
import java.util.List;
import silaundry.dao.UserDAO;
import silaundry.model.Karyawan;
import silaundry.model.Pelanggan;
import silaundry.util.IdGenerator;
import silaundry.util.PasswordUtil;

public class PenggunaController {
    private final UserDAO userDAO = new UserDAO();

    public List<Pelanggan> getAllPelanggan() throws SQLException {
        return userDAO.findAllPelanggan();
    }

    public List<Karyawan> getAllKaryawan() throws SQLException {
        return userDAO.findAllKaryawan();
    }

    public String getDefaultKaryawanId() throws SQLException {
        return userDAO.findFirstKaryawanId();
    }

    public void tambahPelanggan(String username, String nama, String telepon, String password, String alamat)
            throws SQLException {
        Pelanggan pelanggan = new Pelanggan(
                IdGenerator.generate("USR"),
                username,
                nama,
                telepon,
                PasswordUtil.hash(password),
                IdGenerator.generate("PLG"),
                alamat);
        userDAO.createPelanggan(pelanggan);
    }

    public void tambahKaryawan(String username, String nama, String telepon, String password, String shift)
            throws SQLException {
        Karyawan karyawan = new Karyawan(
                IdGenerator.generate("USR"),
                username,
                nama,
                telepon,
                PasswordUtil.hash(password),
                IdGenerator.generate("KRY"),
                shift);
        userDAO.createKaryawan(karyawan);
    }

    public void hapusKaryawan(String idKaryawan) throws SQLException {
        userDAO.deleteKaryawan(idKaryawan);
    }
}
