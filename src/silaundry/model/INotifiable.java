package silaundry.model;

import java.sql.SQLException;

// Kontrak sederhana agar notifikasi aplikasi dan WhatsApp memakai cara kirim yang seragam.
public interface INotifiable {
    void kirimNotifikasi(Notifikasi notifikasi) throws SQLException;
}
