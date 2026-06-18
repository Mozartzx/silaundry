package silaundry.model;

// Kontrak sederhana agar notifikasi aplikasi dan WhatsApp memakai cara kirim yang seragam.
public interface INotifiable {
    void kirimNotifikasi(Notifikasi notifikasi);
}
