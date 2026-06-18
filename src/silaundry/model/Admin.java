package silaundry.model;

import silaundry.model.enums.Role;

// Admin adalah gabungan peran operasional dan pemantauan usaha supaya aplikasi lebih sederhana.
public class Admin extends Pengguna {
    // Constructor Admin langsung menetapkan role sebagai ADMIN.
    public Admin(String idPengguna, String username, String namaLengkap, String nomorTelepon,
            String kataSandi) {
        super(idPengguna, username, namaLengkap, nomorTelepon, kataSandi, Role.ADMIN);
    }

    public void kelolaPesanan() {
        System.out.println("Admin mengelola pesanan laundry");
    }

    public void kelolaTarif() {
        System.out.println("Admin mengatur tarif laundry");
    }

    public void lihatDashboard() {
        System.out.println("Admin melihat ringkasan usaha");
    }

    // Overriding memberi penjelasan hak akses khusus untuk Admin.
    @Override
    public String getInformasiPeran() {
        return "Mengelola pelanggan, pesanan, pembayaran, tarif, dan laporan.";
    }
}
