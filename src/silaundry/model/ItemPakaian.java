package silaundry.model;

import silaundry.model.enums.KategoriWarna;

public class ItemPakaian {
    private String idItem;
    private String idPesanan;
    private String jenisPakaian;
    private KategoriWarna kategoriWarna;
    private String kondisiAwal;
    private String deskripsiDetail;
    private String labelSmartGroup;
    private String kodeQR;

    public ItemPakaian(String idItem, String idPesanan, String jenisPakaian, KategoriWarna kategoriWarna,
            String kondisiAwal, String deskripsiDetail, String labelSmartGroup, String kodeQR) {
        this.idItem = idItem;
        this.idPesanan = idPesanan;
        this.jenisPakaian = jenisPakaian;
        this.kategoriWarna = kategoriWarna;
        this.kondisiAwal = kondisiAwal;
        this.deskripsiDetail = deskripsiDetail;
        this.labelSmartGroup = labelSmartGroup;
        this.kodeQR = kodeQR;
    }

    public void terapkanGrupWarna() {
        this.labelSmartGroup = kategoriWarna.getSmartGroupLabel();
    }

    public void generateKodeQR() {
        this.kodeQR = "QR-" + idItem;
    }

    public String getIdItem() {
        return idItem;
    }

    public void setIdItem(String idItem) {
        this.idItem = idItem;
    }

    public String getIdPesanan() {
        return idPesanan;
    }

    public void setIdPesanan(String idPesanan) {
        this.idPesanan = idPesanan;
    }

    public String getJenisPakaian() {
        return jenisPakaian;
    }

    public void setJenisPakaian(String jenisPakaian) {
        this.jenisPakaian = jenisPakaian;
    }

    public KategoriWarna getKategoriWarna() {
        return kategoriWarna;
    }

    public void setKategoriWarna(KategoriWarna kategoriWarna) {
        this.kategoriWarna = kategoriWarna;
    }

    public String getKondisiAwal() {
        return kondisiAwal;
    }

    public void setKondisiAwal(String kondisiAwal) {
        this.kondisiAwal = kondisiAwal;
    }

    public String getDeskripsiDetail() {
        return deskripsiDetail;
    }

    public void setDeskripsiDetail(String deskripsiDetail) {
        this.deskripsiDetail = deskripsiDetail;
    }

    public String getLabelSmartGroup() {
        return labelSmartGroup;
    }

    public void setLabelSmartGroup(String labelSmartGroup) {
        this.labelSmartGroup = labelSmartGroup;
    }

    public String getKodeQR() {
        return kodeQR;
    }

    public void setKodeQR(String kodeQR) {
        this.kodeQR = kodeQR;
    }
}
