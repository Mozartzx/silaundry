package silaundry.model;

import silaundry.model.enums.StatusMesin;

public class MesinCuci {
    private String idMesin;
    private String namaMesin;
    private float kapasitas;
    private StatusMesin status;

    public MesinCuci(String idMesin, String namaMesin, float kapasitas, StatusMesin status) {
        this.idMesin = idMesin;
        this.namaMesin = namaMesin;
        this.kapasitas = kapasitas;
        this.status = status;
    }

    public void mulaiCuci() {
        status = StatusMesin.DIGUNAKAN;
    }

    public void selesaiCuci() {
        status = StatusMesin.TERSEDIA;
    }

    public String getIdMesin() {
        return idMesin;
    }

    public String getNamaMesin() {
        return namaMesin;
    }

    public float getKapasitas() {
        return kapasitas;
    }

    public StatusMesin getStatus() {
        return status;
    }
}
