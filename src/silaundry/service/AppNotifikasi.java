package silaundry.service;

import java.sql.SQLException;
import silaundry.dao.NotifikasiDAO;
import silaundry.model.INotifiable;
import silaundry.model.Notifikasi;

public class AppNotifikasi implements INotifiable {
    private final NotifikasiDAO notifikasiDAO = new NotifikasiDAO();

    @Override
    public void kirimNotifikasi(Notifikasi notifikasi) throws SQLException {
        notifikasiDAO.create(notifikasi);
    }

    public String tampilkanDiAplikasi(Notifikasi notifikasi) {
        return notifikasi.getPesan();
    }
}
