package silaundry.service;

import java.sql.SQLException;
import java.util.List;
import silaundry.dao.ItemPakaianDAO;
import silaundry.model.ItemPakaian;
import silaundry.model.Pesanan;
import silaundry.model.enums.KategoriWarna;

public class SmartGroupingService {
    public static final String BELUM_DIKELOMPOKKAN = "Belum Dikelompokkan";
    private final ItemPakaianDAO itemPakaianDAO = new ItemPakaianDAO();

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
        }
        itemPakaianDAO.updateSmartGroups(items);
        return items.size();
    }
}
