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

    public Pembayaran simpanPembayaran(String idPesanan, String metode, double jumlah) throws SQLException {
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
        if (jumlah <= 0) {
            throw new IllegalArgumentException("Nominal pembayaran harus lebih dari Rp0.");
        }
        Pembayaran existing = pembayaranDAO.findByPesanan(idPesanan);
        if (existing != null && existing.getStatus() == StatusPembayaran.LUNAS) {
            throw new IllegalArgumentException("Pesanan ini sudah lunas.");
        }
        double totalSebelumnya = existing == null ? 0 : existing.getJumlah();
        double sisaTagihan = pesanan.getTotalBiaya() - totalSebelumnya;
        if (jumlah > sisaTagihan) {
            throw new IllegalArgumentException("Nominal melebihi sisa tagihan sebesar Rp"
                    + Math.round(sisaTagihan) + ".");
        }
        String idPembayaran = existing != null ? existing.getIdPembayaran() : IdGenerator.generate("PAY");
        double totalDibayar = totalSebelumnya + jumlah;
        Pembayaran pembayaran = new Pembayaran(
                idPembayaran, idPesanan, metode.trim(), totalDibayar, StatusPembayaran.BELUM_BAYAR);
        pembayaran.prosesPembayaran(pesanan.getTotalBiaya());
        pembayaranDAO.upsert(pembayaran);
        return pembayaran;
    }
}
