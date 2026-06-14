package silaundry.controller;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import silaundry.dao.PesananDAO;
import silaundry.dao.PembayaranDAO;
import silaundry.dao.ItemPakaianDAO;
import silaundry.dao.TarifLaundryDAO;
import silaundry.model.Pesanan;
import silaundry.model.Pembayaran;
import silaundry.model.Notifikasi;
import silaundry.model.TarifLaundry;
import silaundry.model.enums.PaketLaundry;
import silaundry.model.enums.StatusPesanan;
import silaundry.util.IdGenerator;

public class PesananController {
    private final PesananDAO pesananDAO = new PesananDAO();
    private final TarifLaundryDAO tarifLaundryDAO = new TarifLaundryDAO();
    private final ItemPakaianDAO itemPakaianDAO = new ItemPakaianDAO();
    private final PembayaranDAO pembayaranDAO = new PembayaranDAO();
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
        if (idPelanggan == null || idPelanggan.isBlank() || idKaryawan == null || idKaryawan.isBlank()) {
            throw new IllegalArgumentException("Pelanggan dan karyawan wajib dipilih.");
        }
        if (paketLaundry == null) {
            throw new IllegalArgumentException("Paket laundry wajib dipilih.");
        }
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

    public boolean updateStatus(String idPesanan, StatusPesanan statusPesanan) throws SQLException {
        if (statusPesanan == null) {
            throw new IllegalArgumentException("Status tujuan wajib dipilih.");
        }
        Pesanan pesanan = pesananDAO.findById(idPesanan);
        if (pesanan == null) {
            throw new IllegalArgumentException("Pesanan tidak ditemukan.");
        }
        StatusPesanan statusLama = pesanan.getStatusPesanan();
        if (statusLama == statusPesanan) {
            notifikasiController.kirimNotifikasiStatus(pesanan);
            return false;
        }
        if (!statusLama.dapatBerubahKe(statusPesanan)) {
            throw new IllegalArgumentException("Status " + statusLama.getDisplayName()
                    + " tidak dapat langsung diubah menjadi " + statusPesanan.getDisplayName() + ".");
        }
        if (statusPesanan.membutuhkanItemPakaian() && itemPakaianDAO.countByPesanan(idPesanan) == 0) {
            throw new IllegalArgumentException("Catat minimal satu item pakaian sebelum proses pencucian.");
        }
        pesanan.setStatusPesanan(statusPesanan);
        Notifikasi notifikasi = notifikasiController.buatNotifikasiStatus(pesanan);
        pesananDAO.updateStatusDanNotifikasi(idPesanan, statusLama, statusPesanan, notifikasi);
        return true;
    }

    public boolean batalkanPesanan(String idPesanan) throws SQLException {
        Pembayaran pembayaran = pembayaranDAO.findByPesanan(idPesanan);
        if (pembayaran != null && pembayaran.getJumlah() > 0) {
            throw new IllegalArgumentException(
                    "Pesanan yang sudah memiliki pembayaran tidak dapat dibatalkan sebelum proses pengembalian dana.");
        }
        return updateStatus(idPesanan, StatusPesanan.DIBATALKAN);
    }
}
