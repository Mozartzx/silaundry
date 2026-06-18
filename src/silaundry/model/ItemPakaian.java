package silaundry.model;

import silaundry.model.enums.KategoriWarna;

// Menyimpan ciri setiap pakaian agar item pelanggan tidak mudah tertukar.
public class ItemPakaian {
    private String idItem;
    private String idPesanan;
    private String jenisPakaian;
    private KategoriWarna kategoriWarna;
    private String kondisiAwal;
    private String deskripsiDetail;
    private String labelSmartGroup;

    // Constructor menyimpan ciri pakaian agar mudah dibedakan dari pakaian lain.
    public ItemPakaian(String idItem, String idPesanan, String jenisPakaian, KategoriWarna kategoriWarna,
            String kondisiAwal, String deskripsiDetail, String labelSmartGroup) {
        this.idItem = idItem;
        this.idPesanan = idPesanan;
        this.jenisPakaian = jenisPakaian;
        this.kategoriWarna = kategoriWarna;
        this.kondisiAwal = kondisiAwal;
        this.deskripsiDetail = deskripsiDetail;
        this.labelSmartGroup = labelSmartGroup;
    }

    public void terapkanGrupWarna() {
        // Label grup diambil dari enum supaya aturan warna tidak tersebar di banyak tempat.
        this.labelSmartGroup = kategoriWarna.getSmartGroupLabel();
    }

    // Getter dan setter digunakan saat item ditampilkan atau diperbarui.
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

}
