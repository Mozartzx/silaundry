package silaundry.service;

import silaundry.data.DataStore;
import silaundry.model.INotifiable;
import silaundry.model.Notifikasi;

// Implementasi interface yang menyimpan notifikasi ke ArrayList aplikasi.
public class AppNotifikasi implements INotifiable {
    private final DataStore dataStore = DataStore.getInstance();

    @Override
    public void kirimNotifikasi(Notifikasi notifikasi) {
        dataStore.tambahNotifikasi(notifikasi);
    }

    public String tampilkanDiAplikasi(Notifikasi notifikasi) {
        return notifikasi.getPesan();
    }
}
