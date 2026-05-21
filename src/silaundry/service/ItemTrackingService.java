package silaundry.service;

import java.sql.SQLException;
import silaundry.dao.ItemPakaianDAO;
import silaundry.dao.PesananDAO;
import silaundry.model.ItemPakaian;
import silaundry.model.Pesanan;

public class ItemTrackingService {
    private String notif;
    private final ItemPakaianDAO itemPakaianDAO = new ItemPakaianDAO();
    private final PesananDAO pesananDAO = new PesananDAO();

    public void trackItem(String idItem) {
        this.notif = "Tracking item " + idItem;
        System.out.println(notif);
    }

    public TrackingResult trackItemResult(String trackingKey) throws SQLException {
        ItemPakaian item = itemPakaianDAO.findByTrackingKey(trackingKey.trim());
        if (item == null) {
            notif = "Item tidak ditemukan.";
            return new TrackingResult(null, null);
        }
        Pesanan pesanan = pesananDAO.findById(item.getIdPesanan());
        notif = "Item ditemukan pada pesanan " + item.getIdPesanan();
        return new TrackingResult(item, pesanan);
    }

    public void updateLokasiItem() {
        System.out.println("Lokasi item diperbarui mengikuti status pesanan.");
    }

    public String getNotif() {
        return notif;
    }

    public void setNotif(String notif) {
        this.notif = notif;
    }
}
