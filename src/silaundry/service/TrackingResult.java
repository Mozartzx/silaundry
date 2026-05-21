package silaundry.service;

import silaundry.model.ItemPakaian;
import silaundry.model.Pesanan;

public class TrackingResult {
    private final ItemPakaian itemPakaian;
    private final Pesanan pesanan;

    public TrackingResult(ItemPakaian itemPakaian, Pesanan pesanan) {
        this.itemPakaian = itemPakaian;
        this.pesanan = pesanan;
    }

    public ItemPakaian getItemPakaian() {
        return itemPakaian;
    }

    public Pesanan getPesanan() {
        return pesanan;
    }

    public String toSummary() {
        if (itemPakaian == null || pesanan == null) {
            return "Item tidak ditemukan.";
        }
        return "Item " + itemPakaian.getIdItem()
                + " (" + itemPakaian.getJenisPakaian() + ") milik pesanan "
                + pesanan.getIdPesanan() + " - status: "
                + pesanan.getStatusPesanan().getDisplayName()
                + ", pelanggan: " + pesanan.getNamaPelanggan();
    }
}
