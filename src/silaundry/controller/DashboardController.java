package silaundry.controller;

import java.sql.SQLException;
import silaundry.dao.DashboardDAO;
import silaundry.model.DataDasbor;
import silaundry.model.LaporanKeuangan;

public class DashboardController {
    private final DashboardDAO dashboardDAO = new DashboardDAO();

    public DataDasbor getDataDasbor() throws SQLException {
        return dashboardDAO.getDataDasbor();
    }

    public LaporanKeuangan getLaporanBulanIni() throws SQLException {
        return dashboardDAO.getLaporanBulanIni();
    }
}
