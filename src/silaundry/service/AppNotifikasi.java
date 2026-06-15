package silaundry.service;

import java.sql.SQLException;
import silaundry.dao.LaundryDAO;
import silaundry.model.INotifiable;
import silaundry.model.Notifikasi;

// Implementasi notifikasi yang menyimpan pesan agar dapat ditampilkan di aplikasi.
public class AppNotifikasi implements INotifiable {
    private final LaundryDAO laundryDAO = new LaundryDAO();

    @Override
    public void kirimNotifikasi(Notifikasi notifikasi) throws SQLException {
        laundryDAO.createNotifikasi(notifikasi);
    }

    public String tampilkanDiAplikasi(Notifikasi notifikasi) {
        return notifikasi.getPesan();
    }
}
