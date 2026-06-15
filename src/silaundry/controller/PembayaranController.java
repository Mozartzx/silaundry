package silaundry.controller;

import java.sql.SQLException;
import silaundry.dao.PembayaranDAO;
import silaundry.dao.PesananDAO;
import silaundry.model.Pembayaran;
import silaundry.model.Pesanan;
import silaundry.model.enums.StatusPembayaran;
import silaundry.util.IdGenerator;

public class PembayaranController {
    private final PembayaranDAO pembayaranDAO = new PembayaranDAO();
    private final PesananDAO pesananDAO = new PesananDAO();

    public Pembayaran getPembayaran(String idPesanan) throws SQLException {
        return pembayaranDAO.findByPesanan(idPesanan);
    }

    public Pembayaran catatPembayaran(String idPesanan, String metode) throws SQLException {
        Pesanan pesanan = pesananDAO.findById(idPesanan);
        if (pesanan == null) {
            throw new IllegalArgumentException("Pesanan tidak ditemukan.");
        }
        if (metode == null || metode.isBlank()) {
            throw new IllegalArgumentException("Metode pembayaran wajib diisi.");
        }
        if (!pesanan.getStatusPesanan().dapatMenerimaPembayaran()) {
            throw new IllegalArgumentException("Pesanan yang dibatalkan tidak dapat menerima pembayaran.");
        }
        Pembayaran existing = pembayaranDAO.findByPesanan(idPesanan);
        if (existing != null) {
            throw new IllegalArgumentException("Pesanan ini sudah lunas.");
        }
        Pembayaran pembayaran = new Pembayaran(
                IdGenerator.generate("PAY"), idPesanan, metode.trim(), pesanan.getTotalBiaya(),
                StatusPembayaran.BELUM_BAYAR);
        pembayaran.prosesPembayaran();
        pembayaranDAO.create(pembayaran);
        return pembayaran;
    }
}
