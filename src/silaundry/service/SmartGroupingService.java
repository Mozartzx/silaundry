package silaundry.service;

import java.util.ArrayList;
import java.util.List;
import silaundry.data.DataStore;
import silaundry.model.ItemPakaian;
import silaundry.model.Pesanan;
import silaundry.model.enums.KategoriWarna;

// Mengelompokkan item pakaian dan menunjukkan contoh method overloading.
public class SmartGroupingService {
    public static final String BELUM_DIKELOMPOKKAN = "Belum Dikelompokkan";
    private final DataStore dataStore = DataStore.getInstance();

    public String labelFor(KategoriWarna kategoriWarna) {
        return kategoriWarna.getSmartGroupLabel();
    }

    // Tiga method dengan nama sama ini menunjukkan konsep overloading.
    public void kelompokkanItem(Pesanan pesanan) {
        kelompokkanItem(pesanan.getDaftarItem());
    }

    public void kelompokkanItem(List<ItemPakaian> daftarItem) {
        for (ItemPakaian item : daftarItem) {
            item.terapkanGrupWarna();
        }
    }

    public int kelompokkanItem(String idPesanan) {
        List<ItemPakaian> items = new ArrayList<>();
        for (ItemPakaian item : dataStore.getDaftarItem()) {
            if (item.getIdPesanan().equals(idPesanan)) {
                items.add(item);
            }
        }
        kelompokkanItem(items);
        return items.size();
    }
}
