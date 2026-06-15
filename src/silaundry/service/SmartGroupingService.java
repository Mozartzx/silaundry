package silaundry.service;

import java.sql.SQLException;
import java.util.List;
import silaundry.dao.LaundryDAO;
import silaundry.model.ItemPakaian;
import silaundry.model.Pesanan;
import silaundry.model.enums.KategoriWarna;

// Mengelompokkan item pakaian berdasarkan kategori warna yang dipilih karyawan.
public class SmartGroupingService {
    public static final String BELUM_DIKELOMPOKKAN = "Belum Dikelompokkan";
    private final LaundryDAO laundryDAO = new LaundryDAO();

    // Mengambil nama grup yang sesuai dengan kategori warna.
    public String labelFor(KategoriWarna kategoriWarna) {
        return kategoriWarna.getSmartGroupLabel();
    }

    // Versi objek ini dipakai ketika daftar item sudah tersedia di dalam pesanan.
    public void kelompokkanItem(Pesanan pesanan) {
        for (ItemPakaian item : pesanan.getDaftarItem()) {
            item.terapkanGrupWarna();
        }
    }

    // Versi database membaca item, menerapkan grup, lalu menyimpan hasilnya sekaligus.
    public int kelompokkanItem(String idPesanan) throws SQLException {
        List<ItemPakaian> items = laundryDAO.findItemsByPesanan(idPesanan);
        for (ItemPakaian item : items) {
            item.terapkanGrupWarna();
        }
        laundryDAO.updateSmartGroups(items);
        return items.size();
    }
}
