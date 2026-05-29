package silaundry.model;

import java.sql.SQLException;

public interface INotifiable {
    void kirimNotifikasi(Notifikasi notifikasi) throws SQLException;
}
