package silaundry.controller;

import java.sql.SQLException;
import java.util.List;
import silaundry.dao.ItemPakaianDAO;
import silaundry.model.ItemPakaian;
import silaundry.model.enums.KategoriWarna;
import silaundry.service.ItemTrackingService;
import silaundry.service.SmartGroupingService;
import silaundry.service.TrackingResult;
import silaundry.util.IdGenerator;

public class ItemController {
    private final ItemPakaianDAO itemPakaianDAO = new ItemPakaianDAO();
    private final SmartGroupingService smartGroupingService = new SmartGroupingService();
    private final ItemTrackingService itemTrackingService = new ItemTrackingService();

    public List<ItemPakaian> getAllItems() throws SQLException {
        return itemPakaianDAO.findAll();
    }

    public List<ItemPakaian> getItemsByPesanan(String idPesanan) throws SQLException {
        return itemPakaianDAO.findByPesanan(idPesanan);
    }

    public void tambahItem(String idPesanan, String jenisPakaian, KategoriWarna kategoriWarna, String kondisiAwal)
            throws SQLException {
        String idItem = IdGenerator.generate("ITM");
        ItemPakaian item = new ItemPakaian(
                idItem,
                idPesanan,
                jenisPakaian,
                kategoriWarna,
                kondisiAwal,
                smartGroupingService.labelFor(kategoriWarna),
                "QR-" + idItem);
        itemPakaianDAO.create(item);
    }

    public int jalankanSmartGrouping(String idPesanan) throws SQLException {
        return smartGroupingService.kelompokkanItem(idPesanan);
    }

    public TrackingResult lacakItem(String trackingKey) throws SQLException {
        return itemTrackingService.trackItemResult(trackingKey);
    }

    public void hapusItem(String idItem) throws SQLException {
        itemPakaianDAO.delete(idItem);
    }
}
