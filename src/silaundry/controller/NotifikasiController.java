package silaundry.controller;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import silaundry.dao.NotifikasiDAO;
import silaundry.dao.PesananDAO;
import silaundry.model.Notifikasi;
import silaundry.model.Pesanan;
import silaundry.model.enums.StatusPesanan;
import silaundry.service.AppNotifikasi;
import silaundry.service.WhatsAppNotifikasi;
import silaundry.util.IdGenerator;

public class NotifikasiController {
    private final NotifikasiDAO notifikasiDAO = new NotifikasiDAO();
    private final PesananDAO pesananDAO = new PesananDAO();
    private final AppNotifikasi appNotifikasi = new AppNotifikasi();

    public List<Notifikasi> getNotifikasiPelanggan(String idPelanggan) throws SQLException {
        return notifikasiDAO.findByPelanggan(idPelanggan);
    }

    public void kirimNotifikasiStatus(Pesanan pesanan) throws SQLException {
        if (!perluNotifikasi(pesanan.getStatusPesanan())) {
            return;
        }
        appNotifikasi.kirimNotifikasi(buatNotifikasiStatus(pesanan));
    }

    public String buatLinkWhatsApp(String idPesanan) throws SQLException {
        Pesanan pesanan = pesananDAO.findById(idPesanan);
        if (pesanan == null) {
            throw new SQLException("Pesanan tidak ditemukan.");
        }
        String nomorTelepon = notifikasiDAO.findNomorTeleponByPesanan(idPesanan);
        if (nomorTelepon == null || nomorTelepon.isBlank()) {
            throw new SQLException("Nomor telepon pelanggan belum tersedia.");
        }
        WhatsAppNotifikasi whatsapp = new WhatsAppNotifikasi(nomorTelepon);
        Notifikasi notifikasi = buatNotifikasiStatus(pesanan);
        whatsapp.kirimNotifikasi(notifikasi);
        return whatsapp.getLinkWhatsApp();
    }

    private Notifikasi buatNotifikasiStatus(Pesanan pesanan) {
        return new Notifikasi(
                IdGenerator.generate("NTF"),
                pesanan.getIdPesanan(),
                buatPesanStatus(pesanan),
                LocalDateTime.now(),
                false);
    }

    private String buatPesanStatus(Pesanan pesanan) {
        if (pesanan.getStatusPesanan() == StatusPesanan.SIAP_DIAMBIL) {
            return "Halo " + pesanan.getNamaPelanggan() + ", pesanan laundry " + pesanan.getIdPesanan()
                    + " sudah siap diambil. Terima kasih - SILAUNDRY";
        }
        if (pesanan.getStatusPesanan() == StatusPesanan.SELESAI) {
            return "Halo " + pesanan.getNamaPelanggan() + ", pesanan laundry " + pesanan.getIdPesanan()
                    + " sudah selesai. Terima kasih - SILAUNDRY";
        }
        return "Halo " + pesanan.getNamaPelanggan() + ", status pesanan laundry " + pesanan.getIdPesanan()
                + " saat ini: " + pesanan.getStatusPesanan().getDisplayName() + ". Terima kasih - SILAUNDRY";
    }

    private boolean perluNotifikasi(StatusPesanan statusPesanan) {
        return statusPesanan == StatusPesanan.SIAP_DIAMBIL || statusPesanan == StatusPesanan.SELESAI;
    }
}
