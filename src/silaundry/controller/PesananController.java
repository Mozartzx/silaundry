package silaundry.controller;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import silaundry.dao.PesananDAO;
import silaundry.dao.TarifLaundryDAO;
import silaundry.model.Pesanan;
import silaundry.model.TarifLaundry;
import silaundry.model.enums.PaketLaundry;
import silaundry.model.enums.StatusPesanan;
import silaundry.util.IdGenerator;

public class PesananController {
    private final PesananDAO pesananDAO = new PesananDAO();
    private final TarifLaundryDAO tarifLaundryDAO = new TarifLaundryDAO();
    private final NotifikasiController notifikasiController = new NotifikasiController();

    public List<Pesanan> getAllPesanan() throws SQLException {
        return pesananDAO.findAll();
    }

    public List<Pesanan> getPesananPelanggan(String idPelanggan) throws SQLException {
        return pesananDAO.findByPelanggan(idPelanggan);
    }

    public Pesanan getPesanan(String idPesanan) throws SQLException {
        return pesananDAO.findById(idPesanan);
    }

    public Pesanan tambahPesanan(String idPelanggan, String idKaryawan, PaketLaundry paketLaundry, double beratKg,
            String catatan) throws SQLException {
        if (beratKg <= 0) {
            throw new IllegalArgumentException("Berat laundry harus lebih dari 0 kg.");
        }
        TarifLaundry tarif = tarifLaundryDAO.findByPaket(paketLaundry);
        if (tarif == null) {
            throw new SQLException("Tarif paket " + paketLaundry.getDisplayName() + " belum tersedia.");
        }
        Pesanan pesanan = new Pesanan(
                IdGenerator.generate("ORD"),
                idPelanggan,
                idKaryawan,
                LocalDate.now(),
                LocalDate.now().plusDays(tarif.getEstimasiHari()),
                StatusPesanan.BARU,
                tarif.getPaketLaundry(),
                beratKg,
                tarif.getHargaPerKg(),
                tarif.hitungTotal(beratKg),
                catatan);
        pesananDAO.create(pesanan);
        return pesanan;
    }

    public void updateStatus(String idPesanan, StatusPesanan statusPesanan) throws SQLException {
        pesananDAO.updateStatus(idPesanan, statusPesanan);
        Pesanan pesanan = pesananDAO.findById(idPesanan);
        if (pesanan != null) {
            notifikasiController.kirimNotifikasiStatus(pesanan);
        }
    }

    public void hapusPesanan(String idPesanan) throws SQLException {
        pesananDAO.delete(idPesanan);
    }
}
