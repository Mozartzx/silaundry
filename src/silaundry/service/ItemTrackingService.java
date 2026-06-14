package silaundry.service;

import java.sql.SQLException;
import silaundry.dao.ItemPakaianDAO;
import silaundry.dao.PesananDAO;
import silaundry.model.ItemPakaian;
import silaundry.model.Pelanggan;
import silaundry.model.Pesanan;
import silaundry.model.Pengguna;

public class ItemTrackingService {
    private final ItemPakaianDAO itemPakaianDAO = new ItemPakaianDAO();
    private final PesananDAO pesananDAO = new PesananDAO();

    public TrackingResult trackItemResult(String trackingKey) throws SQLException {
        return trackItemResult(trackingKey, null);
    }

    public TrackingResult trackItemResult(String trackingKey, Pengguna pengguna) throws SQLException {
        String key = trackingKey.trim();
        ItemPakaian item;
        if (pengguna instanceof Pelanggan pelanggan) {
            item = itemPakaianDAO.findByTrackingKeyAndPelanggan(key, pelanggan.getIdPelanggan());
        } else {
            item = itemPakaianDAO.findByTrackingKey(key);
        }
        if (item == null) {
            return new TrackingResult(null, null);
        }
        Pesanan pesanan = pesananDAO.findById(item.getIdPesanan());
        return new TrackingResult(item, pesanan);
    }
}
