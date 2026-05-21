package silaundry.controller;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import silaundry.dao.PesananDAO;
import silaundry.model.Pesanan;
import silaundry.model.enums.StatusPesanan;
import silaundry.util.IdGenerator;

public class PesananController {
    private final PesananDAO pesananDAO = new PesananDAO();

    public List<Pesanan> getAllPesanan() throws SQLException {
        return pesananDAO.findAll();
    }

    public List<Pesanan> getPesananPelanggan(String idPelanggan) throws SQLException {
        return pesananDAO.findByPelanggan(idPelanggan);
    }

    public void tambahPesanan(String idPelanggan, String idKaryawan, int estimasiHari, double totalBiaya,
            String catatan) throws SQLException {
        Pesanan pesanan = new Pesanan(
                IdGenerator.generate("ORD"),
                idPelanggan,
                idKaryawan,
                LocalDate.now(),
                LocalDate.now().plusDays(estimasiHari),
                StatusPesanan.BARU,
                totalBiaya,
                catatan);
        pesananDAO.create(pesanan);
    }

    public void updateStatus(String idPesanan, StatusPesanan statusPesanan) throws SQLException {
        pesananDAO.updateStatus(idPesanan, statusPesanan);
    }

    public void hapusPesanan(String idPesanan) throws SQLException {
        pesananDAO.delete(idPesanan);
    }
}
