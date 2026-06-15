package silaundry.test;

import silaundry.model.ItemPakaian;
import silaundry.model.Pembayaran;
import silaundry.model.enums.KategoriWarna;
import silaundry.model.enums.StatusPembayaran;
import silaundry.model.enums.StatusPesanan;
import silaundry.service.WhatsAppNotifikasi;
import silaundry.util.PasswordUtil;

public final class BusinessRulesTest {
    private BusinessRulesTest() {
    }

    public static void main(String[] args) {
        testAlurStatusPesanan();
        testAturanPerubahanItem();
        testStatusPembayaran();
        testSmartGrouping();
        testWhatsAppDanPassword();
        System.out.println("Semua business rules test berhasil.");
    }

    private static void testAlurStatusPesanan() {
        check(StatusPesanan.BARU.dapatBerubahKe(StatusPesanan.DIPROSES), "BARU harus dapat menjadi DIPROSES");
        check(!StatusPesanan.BARU.dapatBerubahKe(StatusPesanan.DICUCI),
                "BARU tidak boleh langsung menjadi DICUCI");
        check(StatusPesanan.DIPROSES.dapatBerubahKe(StatusPesanan.DIBATALKAN),
                "DIPROSES masih boleh dibatalkan");
        check(!StatusPesanan.DICUCI.dapatBerubahKe(StatusPesanan.DIBATALKAN),
                "DICUCI tidak boleh dibatalkan");
    }

    private static void testAturanPerubahanItem() {
        check(StatusPesanan.BARU.dapatMengubahItem(), "Item pesanan BARU harus dapat diubah");
        check(StatusPesanan.DIPROSES.dapatMengubahItem(), "Item pesanan DIPROSES harus dapat diubah");
        check(!StatusPesanan.DICUCI.dapatMengubahItem(), "Item tidak boleh diubah setelah mulai dicuci");
        check(!StatusPesanan.DIBATALKAN.dapatMenerimaPembayaran(),
                "Pesanan batal tidak boleh menerima pembayaran");
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
                "Pembayaran sebesar tagihan harus berstatus LUNAS");
    }

    private static void testSmartGrouping() {
        ItemPakaian item = new ItemPakaian(
                "ITM-TEST", "ORD-TEST", "Kemeja", KategoriWarna.PUTIH,
                "Normal", "Kemeja putih lengan panjang", "Belum Dikelompokkan");
        item.terapkanGrupWarna();
        check("Grup Putih".equals(item.getLabelSmartGroup()),
                "Kategori PUTIH harus masuk Grup Putih");
    }

    private static void testWhatsAppDanPassword() {
        WhatsAppNotifikasi whatsapp = new WhatsAppNotifikasi("081234567890");
        String link = whatsapp.generateLinkWhatsApp("Pesanan siap diambil");
        check(link.startsWith("https://wa.me/6281234567890"), "Nomor WhatsApp lokal harus dinormalisasi");

        String hash = PasswordUtil.hash("rahasia");
        check(PasswordUtil.matches("rahasia", hash), "Password harus cocok dengan hash-nya");
        check(!PasswordUtil.matches("salah", hash), "Password salah tidak boleh cocok");
    }

    private static void check(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}
