package silaundry.model;

import silaundry.model.enums.Role;

public class Pemilik extends Pengguna {
    private String id;
    private String idPemilik;

    public Pemilik(String idPengguna, String username, String namaLengkap, String nomorTelepon,
            String kataSandi, String idPemilik) {
        super(idPengguna, username, namaLengkap, nomorTelepon, kataSandi, Role.PEMILIK);
        this.id = idPemilik;
        this.idPemilik = idPemilik;
    }

    public void tinjauDasborAnalitik() {
        System.out.println("Pemilik meninjau dasbor analitik");
    }

    public void unduhLaporanKeuangan() {
        System.out.println("Pemilik mengunduh laporan keuangan");
    }

    public void kelolaDataKaryawan() {
        System.out.println("Pemilik mengelola data karyawan");
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
        this.idPemilik = id;
    }

    public String getIdPemilik() {
        return idPemilik;
    }

    public void setIdPemilik(String idPemilik) {
        this.idPemilik = idPemilik;
        this.id = idPemilik;
    }
}
