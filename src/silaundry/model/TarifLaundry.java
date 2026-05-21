package silaundry.model;

import silaundry.model.enums.PaketLaundry;

public class TarifLaundry {
    private String idTarif;
    private PaketLaundry paketLaundry;
    private String namaPaket;
    private int estimasiHari;
    private double hargaPerKg;
    private boolean aktif;

    public TarifLaundry(String idTarif, PaketLaundry paketLaundry, String namaPaket, int estimasiHari,
            double hargaPerKg, boolean aktif) {
        this.idTarif = idTarif;
        this.paketLaundry = paketLaundry;
        this.namaPaket = namaPaket;
        this.estimasiHari = estimasiHari;
        this.hargaPerKg = hargaPerKg;
        this.aktif = aktif;
    }

    public String getIdTarif() {
        return idTarif;
    }

    public void setIdTarif(String idTarif) {
        this.idTarif = idTarif;
    }

    public PaketLaundry getPaketLaundry() {
        return paketLaundry;
    }

    public void setPaketLaundry(PaketLaundry paketLaundry) {
        this.paketLaundry = paketLaundry;
    }

    public String getNamaPaket() {
        return namaPaket;
    }

    public void setNamaPaket(String namaPaket) {
        this.namaPaket = namaPaket;
    }

    public int getEstimasiHari() {
        return estimasiHari;
    }

    public void setEstimasiHari(int estimasiHari) {
        this.estimasiHari = estimasiHari;
    }

    public double getHargaPerKg() {
        return hargaPerKg;
    }

    public void setHargaPerKg(double hargaPerKg) {
        this.hargaPerKg = hargaPerKg;
    }

    public boolean isAktif() {
        return aktif;
    }

    public void setAktif(boolean aktif) {
        this.aktif = aktif;
    }

    public double hitungTotal(double beratKg) {
        return Math.round(beratKg * hargaPerKg);
    }

    @Override
    public String toString() {
        return namaPaket + " - " + estimasiHari + " hari";
    }
}
