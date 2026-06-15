package silaundry.model;

import silaundry.model.enums.Role;

// Representasi pengguna karyawan beserta ID kerja dan shift yang dijalankan.
public class Karyawan extends Pengguna {
    private String idKaryawan;
    private String shiftKerja;

    public Karyawan(String idPengguna, String username, String namaLengkap, String nomorTelepon,
            String kataSandi, String idKaryawan, String shiftKerja) {
        super(idPengguna, username, namaLengkap, nomorTelepon, kataSandi, Role.KARYAWAN);
        this.idKaryawan = idKaryawan;
        this.shiftKerja = shiftKerja;
    }

    public void buatPesananBaru() {
        System.out.println("Karyawan membuat pesanan baru");
    }

    public void perbaruiStatusPesanan() {
        System.out.println("Karyawan memperbarui status pesanan");
    }

    public void rekamDataPakaian() {
        System.out.println("Karyawan merekam data pakaian");
    }

    public void jalankanSmartGrouping() {
        System.out.println("Karyawan menjalankan smart grouping");
    }

    public String getIdKaryawan() {
        return idKaryawan;
    }

    public void setIdKaryawan(String idKaryawan) {
        this.idKaryawan = idKaryawan;
    }

    public String getShiftKerja() {
        return shiftKerja;
    }

    public void setShiftKerja(String shiftKerja) {
        this.shiftKerja = shiftKerja;
    }
}
