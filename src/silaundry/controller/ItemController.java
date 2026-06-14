package silaundry.controller;

import java.sql.SQLException;
import java.util.List;
import silaundry.dao.ItemPakaianDAO;
import silaundry.dao.PesananDAO;
import silaundry.model.ItemPakaian;
import silaundry.model.Pengguna;
import silaundry.model.Pesanan;
import silaundry.model.enums.KategoriWarna;
import silaundry.service.ItemTrackingService;
import silaundry.service.SmartGroupingService;
import silaundry.service.TrackingResult;
import silaundry.util.IdGenerator;

public class ItemController {
    private final ItemPakaianDAO itemPakaianDAO = new ItemPakaianDAO();
    private final PesananDAO pesananDAO = new PesananDAO();
    private final SmartGroupingService smartGroupingService = new SmartGroupingService();
    private final ItemTrackingService itemTrackingService = new ItemTrackingService();

    public List<ItemPakaian> getAllItems() throws SQLException {
        return itemPakaianDAO.findAll();
    }

    public List<ItemPakaian> getItemsByPesanan(String idPesanan) throws SQLException {
        return itemPakaianDAO.findByPesanan(idPesanan);
    }

    public void tambahItem(String idPesanan, String jenisPakaian, KategoriWarna kategoriWarna, String kondisiAwal,
            String deskripsiDetail)
            throws SQLException {
        Pesanan pesanan = pesananDapatDiubah(idPesanan);
        if (jenisPakaian == null || jenisPakaian.isBlank() || kategoriWarna == null
                || kondisiAwal == null || kondisiAwal.isBlank()
                || deskripsiDetail == null || deskripsiDetail.trim().length() < 5) {
            throw new IllegalArgumentException("Data item pakaian belum lengkap.");
        }
        String idItem = IdGenerator.generate("ITM");
        ItemPakaian item = new ItemPakaian(
                idItem,
                pesanan.getIdPesanan(),
                jenisPakaian.trim(),
                kategoriWarna,
                kondisiAwal.trim(),
                deskripsiDetail.trim(),
                SmartGroupingService.BELUM_DIKELOMPOKKAN,
                "QR-" + idItem);
        itemPakaianDAO.create(item);
    }

    public int jalankanSmartGrouping(String idPesanan) throws SQLException {
        pesananDapatDiubah(idPesanan);
        int jumlahItem = smartGroupingService.kelompokkanItem(idPesanan);
        if (jumlahItem == 0) {
            throw new IllegalArgumentException("Belum ada item pakaian yang dapat dikelompokkan.");
        }
        return jumlahItem;
    }

    public TrackingResult lacakItem(String trackingKey) throws SQLException {
        return itemTrackingService.trackItemResult(trackingKey);
    }

    public TrackingResult lacakItem(String trackingKey, Pengguna pengguna) throws SQLException {
        return itemTrackingService.trackItemResult(trackingKey, pengguna);
    }

    public void hapusItem(String idItem) throws SQLException {
        ItemPakaian item = itemPakaianDAO.findByTrackingKey(idItem);
        if (item == null) {
            throw new IllegalArgumentException("Item pakaian tidak ditemukan.");
        }
        pesananDapatDiubah(item.getIdPesanan());
        itemPakaianDAO.delete(idItem);
    }

    private Pesanan pesananDapatDiubah(String idPesanan) throws SQLException {
        if (idPesanan == null || idPesanan.isBlank()) {
            throw new IllegalArgumentException("Pesanan wajib dipilih.");
        }
        Pesanan pesanan = pesananDAO.findById(idPesanan.trim());
        if (pesanan == null) {
            throw new IllegalArgumentException("Pesanan tidak ditemukan.");
        }
        if (!pesanan.getStatusPesanan().dapatMengubahItem()) {
            throw new IllegalArgumentException(
                    "Item tidak dapat diubah setelah proses pencucian dimulai atau pesanan ditutup.");
        }
        return pesanan;
    }
}
