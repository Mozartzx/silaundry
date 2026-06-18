package silaundry.test;

import silaundry.controller.AdminController;
import silaundry.controller.AuthController;
import silaundry.controller.LaundryController;
import silaundry.controller.PenggunaController;
import silaundry.data.DataStore;
import silaundry.model.Admin;
import silaundry.model.INotifiable;
import silaundry.model.ItemPakaian;
import silaundry.model.Notifikasi;
import silaundry.model.Pelanggan;
import silaundry.model.Pembayaran;
import silaundry.model.Pengguna;
import silaundry.model.Pesanan;
import silaundry.model.enums.KategoriWarna;
import silaundry.model.enums.PaketLaundry;
import silaundry.model.enums.Role;
import silaundry.model.enums.StatusPembayaran;
import silaundry.model.enums.StatusPesanan;
import silaundry.service.WhatsAppNotifikasi;

public final class BusinessRulesTest {
    private BusinessRulesTest() {
    }

    public static void main(String[] args) {
        testAlurStatusPesanan();
        testStatusPembayaran();
        testSmartGrouping();
        testInterfaceDanOverriding();
        testAlurCollectionLengkap();
        System.out.println("Semua business rules test berhasil.");
    }

    // Menguji agar perubahan status selalu mengikuti urutan proses laundry.
    private static void testAlurStatusPesanan() {
        check(StatusPesanan.BARU.dapatBerubahKe(StatusPesanan.DIPROSES),
                "BARU harus dapat menjadi DIPROSES");
        check(!StatusPesanan.BARU.dapatBerubahKe(StatusPesanan.DICUCI),
                "BARU tidak boleh langsung menjadi DICUCI");
        check(StatusPesanan.DIPROSES.dapatBerubahKe(StatusPesanan.DIBATALKAN),
                "DIPROSES masih boleh dibatalkan");
        check(!StatusPesanan.DICUCI.dapatBerubahKe(StatusPesanan.DIBATALKAN),
                "DICUCI tidak boleh dibatalkan");
    }

    private static void testStatusPembayaran() {
        Pembayaran belumBayar = new Pembayaran(
                "PAY-TEST", "ORD-TEST", "Tunai", 0, StatusPembayaran.BELUM_BAYAR);
        belumBayar.prosesPembayaran();
        check(belumBayar.getStatus() == StatusPembayaran.BELUM_BAYAR,
                "Pembayaran tanpa nominal harus berstatus BELUM_BAYAR");

        Pembayaran lunas = new Pembayaran(
                "PAY-TEST-2", "ORD-TEST", "QRIS", 10_000, StatusPembayaran.BELUM_BAYAR);
        lunas.prosesPembayaran();
        check(lunas.getStatus() == StatusPembayaran.LUNAS,
                "Pembayaran bernominal positif harus berstatus LUNAS");
        check(lunas.getTanggalBayar() != null,
                "Pembayaran lunas harus menyimpan waktu pembayaran");
    }

    // Menguji pengelompokan warna pada item pakaian.
    private static void testSmartGrouping() {
        ItemPakaian item = new ItemPakaian(
                "ITM-TEST", "ORD-TEST", "Kemeja", KategoriWarna.PUTIH,
                "Normal", "Kemeja putih lengan panjang", "Belum Dikelompokkan");
        item.terapkanGrupWarna();
        check("Grup Putih".equals(item.getLabelSmartGroup()),
                "Kategori PUTIH harus masuk Grup Putih");
    }

    private static void testInterfaceDanOverriding() {
        WhatsAppNotifikasi whatsapp = new WhatsAppNotifikasi("081234567890");
        INotifiable channel = whatsapp;
        channel.kirimNotifikasi(new Notifikasi(
                "NTF-TEST", "ORD-TEST", "Pesanan siap diambil", java.time.LocalDateTime.now(), false));
        check(whatsapp.getLinkWhatsApp().startsWith("https://wa.me/6281234567890"),
                "Implementasi interface harus membuat link WhatsApp");
        WhatsAppNotifikasi whatsappTanpaNol = new WhatsAppNotifikasi("81234567890");
        check(whatsappTanpaNol.generateLinkWhatsApp("Tes").startsWith("https://wa.me/6281234567890"),
                "Nomor WhatsApp berawalan 8 harus ditambahkan kode negara 62");

        Pengguna admin = new Admin("USR-TEST", "admin", "Admin Test", "081234567890", "123456");
        check(admin.getInformasiPeran().contains("Mengelola"),
                "Method abstract harus dioverride oleh Admin");
    }

    // Menguji alur utama aplikasi dari login sampai pembayaran memakai ArrayList.
    private static void testAlurCollectionLengkap() {
        DataStore.getInstance().reset();
        AuthController authController = new AuthController();
        PenggunaController penggunaController = new PenggunaController();
        LaundryController laundryController = new LaundryController();
        AdminController adminController = new AdminController();

        Pengguna admin = authController.login("masteradmin", "123456", Role.ADMIN);
        check(admin instanceof Admin, "Akun awal harus berupa Admin");
        check(authController.login("masteradmin", "salah", Role.ADMIN) == null,
                "Password Admin yang salah harus ditolak");

        penggunaController.tambahPelanggan(
                "pelanggan1", "Pelanggan Satu", "081298765432", "rahasia", "Jalan Melati Nomor 1");
        check(penggunaController.getAllPelanggan().size() == 1,
                "Pelanggan hasil register harus masuk ArrayList");

        Pelanggan pelanggan = penggunaController.getAllPelanggan().get(0);
        check(authController.login("pelanggan1", "rahasia", Role.PELANGGAN) == pelanggan,
                "Pelanggan hasil register harus dapat login");

        Pesanan pesanan = laundryController.tambahPesanan(
                pelanggan.getIdPelanggan(), PaketLaundry.STANDARD_2_HARI, 2.0, "Jangan dicampur");
        check(laundryController.getPesananPelanggan(pelanggan.getIdPelanggan()).size() == 1,
                "Pelanggan hanya perlu melihat pesanan miliknya");

        laundryController.tambahItem(
                pesanan.getIdPesanan(), "Kemeja", KategoriWarna.PUTIH, "Normal", "Ukuran M");
        check(laundryController.jalankanSmartGrouping(pesanan.getIdPesanan()) == 1,
                "Smart grouping harus memproses item dalam ArrayList");

        laundryController.updateStatus(pesanan.getIdPesanan(), StatusPesanan.DIPROSES);
        laundryController.updateStatus(pesanan.getIdPesanan(), StatusPesanan.DICUCI);
        laundryController.updateStatus(pesanan.getIdPesanan(), StatusPesanan.DIKERINGKAN);
        laundryController.updateStatus(pesanan.getIdPesanan(), StatusPesanan.DISETRIKA);
        laundryController.updateStatus(pesanan.getIdPesanan(), StatusPesanan.SIAP_DIAMBIL);
        check(laundryController.getNotifikasiPelanggan(pelanggan.getIdPelanggan()).size() == 1,
                "Status siap diambil harus membuat notifikasi pelanggan");

        Pembayaran pembayaran = laundryController.catatPembayaran(pesanan.getIdPesanan(), "Tunai");
        check(pembayaran.getStatus() == StatusPembayaran.LUNAS,
                "Pembayaran harus tersimpan sebagai lunas");
        check(adminController.getDataDasbor().getPendapatanDiterima() == pesanan.getTotalBiaya(),
                "Dashboard harus menghitung pembayaran dari ArrayList");
    }

    // Helper check membuat test berhenti jika hasil tidak sesuai harapan.
    private static void check(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}
