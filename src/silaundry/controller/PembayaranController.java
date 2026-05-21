package silaundry.controller;

import java.sql.SQLException;
import silaundry.dao.PembayaranDAO;
import silaundry.model.Pembayaran;
import silaundry.model.enums.StatusPembayaran;
import silaundry.util.IdGenerator;

public class PembayaranController {
    private final PembayaranDAO pembayaranDAO = new PembayaranDAO();

    public Pembayaran getPembayaran(String idPesanan) throws SQLException {
        return pembayaranDAO.findByPesanan(idPesanan);
    }

    public void simpanPembayaran(String idPesanan, String metode, double jumlah, StatusPembayaran status)
            throws SQLException {
        Pembayaran existing = pembayaranDAO.findByPesanan(idPesanan);
        String idPembayaran = existing != null ? existing.getIdPembayaran() : IdGenerator.generate("PAY");
        pembayaranDAO.upsert(new Pembayaran(idPembayaran, idPesanan, metode, jumlah, status));
    }
}
