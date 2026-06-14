package silaundry.controller;

import java.sql.SQLException;
import java.util.List;
import silaundry.dao.TarifLaundryDAO;
import silaundry.model.TarifLaundry;
import silaundry.model.enums.PaketLaundry;

public class TarifController {
    private final TarifLaundryDAO tarifLaundryDAO = new TarifLaundryDAO();

    public List<TarifLaundry> getSemuaTarif() throws SQLException {
        return tarifLaundryDAO.findAll();
    }

    public List<TarifLaundry> getTarifAktif() throws SQLException {
        return tarifLaundryDAO.findActive();
    }

    public TarifLaundry getTarif(PaketLaundry paketLaundry) throws SQLException {
        return tarifLaundryDAO.findByPaket(paketLaundry);
    }

    public void updateHarga(PaketLaundry paketLaundry, double hargaPerKg) throws SQLException {
        if (hargaPerKg < 1000 || hargaPerKg > 1_000_000) {
            throw new IllegalArgumentException("Harga per kilo harus antara Rp1.000 dan Rp1.000.000.");
        }
        tarifLaundryDAO.updateHarga(paketLaundry, hargaPerKg);
    }
}
