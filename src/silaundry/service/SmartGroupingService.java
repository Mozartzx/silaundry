package silaundry.service;

import java.sql.SQLException;
import java.util.List;
import silaundry.dao.ItemPakaianDAO;
import silaundry.model.ItemPakaian;
import silaundry.model.Pesanan;
import silaundry.model.enums.KategoriWarna;

public class SmartGroupingService {
    private int id;
    private final ItemPakaianDAO itemPakaianDAO = new ItemPakaianDAO();

    public SmartGroupingService() {
        this(1);
    }

    public SmartGroupingService(int id) {
        this.id = id;
    }

    public String labelFor(KategoriWarna kategoriWarna) {
        return kategoriWarna.getSmartGroupLabel();
    }

    public void kelompokkanItem(Pesanan pesanan) {
        for (ItemPakaian item : pesanan.getDaftarItem()) {
            item.terapkanGrupWarna();
        }
    }

    public int kelompokkanItem(String idPesanan) throws SQLException {
        List<ItemPakaian> items = itemPakaianDAO.findByPesanan(idPesanan);
        for (ItemPakaian item : items) {
            item.terapkanGrupWarna();
            itemPakaianDAO.updateSmartGroup(item.getIdItem(), item.getLabelSmartGroup());
        }
        return items.size();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
