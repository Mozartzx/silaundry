package silaundry.controller;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import silaundry.dao.LaundryDAO;
import silaundry.dao.TarifDAO;
import silaundry.model.ItemPakaian;
import silaundry.model.Notifikasi;
import silaundry.model.Pembayaran;
import silaundry.model.Pesanan;
import silaundry.model.RiwayatPembayaran;
import silaundry.model.TarifLaundry;
import silaundry.model.enums.KategoriWarna;
import silaundry.model.enums.PaketLaundry;
import silaundry.model.enums.StatusPembayaran;
import silaundry.model.enums.StatusPesanan;
import silaundry.service.AppNotifikasi;
import silaundry.service.SmartGroupingService;
import silaundry.service.WhatsAppNotifikasi;
import silaundry.util.IdGenerator;

// Menangani aturan bisnis utama mulai dari pesanan sampai pembayaran dan notifikasi.
public class LaundryController {
    private final LaundryDAO laundryDAO = new LaundryDAO();
    private final TarifDAO tarifDAO = new TarifDAO();
    private final SmartGroupingService smartGroupingService = new SmartGroupingService();
    private final AppNotifikasi appNotifikasi = new AppNotifikasi();

    // Memuat seluruh pesanan untuk karyawan dan pemilik.
    public List<Pesanan> getAllPesanan() throws SQLException {
        return laundryDAO.findAllPesanan();
    }

    // Membatasi hasil pesanan berdasarkan pelanggan yang sedang login.
    public List<Pesanan> getPesananPelanggan(String idPelanggan) throws SQLException {
        return laundryDAO.findPesananByPelanggan(idPelanggan);
    }

    // Mencari satu pesanan saat view membutuhkan detail lengkap.
    public Pesanan getPesanan(String idPesanan) throws SQLException {
        return laundryDAO.findPesananById(idPesanan);
    }

    // Menyediakan paket aktif yang boleh dipilih saat membuat pesanan.
    public List<TarifLaundry> getTarifAktif() throws SQLException {
        return tarifDAO.findActive();
    }

    // Membuat pesanan dan menghitung estimasi serta total biaya dari tarif aktif.
    public Pesanan tambahPesanan(String idPelanggan, String idKaryawan, PaketLaundry paketLaundry,
            double beratKg, String catatan) throws SQLException {
        if (idPelanggan == null || idPelanggan.isBlank() || idKaryawan == null || idKaryawan.isBlank()) {
            throw new IllegalArgumentException("Pelanggan dan karyawan wajib dipilih.");
        }
        if (paketLaundry == null) {
            throw new IllegalArgumentException("Paket laundry wajib dipilih.");
        }
        if (beratKg <= 0) {
            throw new IllegalArgumentException("Berat laundry harus lebih dari 0 kg.");
        }
        TarifLaundry tarif = tarifDAO.findByPaket(paketLaundry);
        if (tarif == null) {
            throw new SQLException("Tarif paket " + paketLaundry.getDisplayName() + " belum tersedia.");
        }
        Pesanan pesanan = new Pesanan(
                IdGenerator.generate("ORD"),
                idPelanggan,
                idKaryawan,
                LocalDate.now(),
                LocalDate.now().plusDays(tarif.getEstimasiHari()),
                StatusPesanan.BARU,
                tarif.getPaketLaundry(),
                beratKg,
                tarif.getHargaPerKg(),
                tarif.hitungTotal(beratKg),
                catatan);
        laundryDAO.createPesanan(pesanan);
        return pesanan;
    }

    // Memastikan perpindahan status valid sebelum disimpan bersama notifikasinya.
    public boolean updateStatus(String idPesanan, StatusPesanan statusBaru) throws SQLException {
        if (statusBaru == null) {
            throw new IllegalArgumentException("Status tujuan wajib dipilih.");
        }
        Pesanan pesanan = laundryDAO.findPesananById(idPesanan);
        if (pesanan == null) {
            throw new IllegalArgumentException("Pesanan tidak ditemukan.");
        }
        StatusPesanan statusLama = pesanan.getStatusPesanan();
        if (statusLama == statusBaru) {
            kirimNotifikasiStatus(pesanan);
            return false;
        }
        if (!statusLama.dapatBerubahKe(statusBaru)) {
            throw new IllegalArgumentException("Status " + statusLama.getDisplayName()
                    + " tidak dapat langsung diubah menjadi " + statusBaru.getDisplayName() + ".");
        }
        if (statusBaru.membutuhkanItemPakaian() && laundryDAO.countItemsByPesanan(idPesanan) == 0) {
            throw new IllegalArgumentException("Catat minimal satu item pakaian sebelum proses pencucian.");
        }
        pesanan.setStatusPesanan(statusBaru);
        Notifikasi notifikasi = buatNotifikasiStatus(pesanan);
        laundryDAO.updateStatusDanNotifikasi(idPesanan, statusLama, statusBaru, notifikasi);
        return true;
    }

    // Pembatalan ditolak bila pesanan sudah mempunyai pembayaran.
    public boolean batalkanPesanan(String idPesanan) throws SQLException {
        Pembayaran pembayaran = laundryDAO.findPembayaranByPesanan(idPesanan);
        if (pembayaran != null && pembayaran.getJumlah() > 0) {
            throw new IllegalArgumentException(
                    "Pesanan yang sudah memiliki pembayaran tidak dapat dibatalkan sebelum proses pengembalian dana.");
        }
        return updateStatus(idPesanan, StatusPesanan.DIBATALKAN);
    }

    // Mengambil semua item untuk kebutuhan pemeriksaan data.
    public List<ItemPakaian> getAllItems() throws SQLException {
        return laundryDAO.findAllItems();
    }

    // Mengambil pakaian yang hanya terhubung dengan pesanan terpilih.
    public List<ItemPakaian> getItemsByPesanan(String idPesanan) throws SQLException {
        return laundryDAO.findItemsByPesanan(idPesanan);
    }

    // Mencatat ciri pakaian selama proses pencucian belum dimulai.
    public void tambahItem(String idPesanan, String jenisPakaian, KategoriWarna kategoriWarna,
            String kondisiAwal, String deskripsiDetail) throws SQLException {
        Pesanan pesanan = pesananDapatDiubah(idPesanan);
        if (jenisPakaian == null || jenisPakaian.isBlank() || kategoriWarna == null
                || kondisiAwal == null || kondisiAwal.isBlank()
                || deskripsiDetail == null || deskripsiDetail.trim().length() < 5) {
            throw new IllegalArgumentException("Data item pakaian belum lengkap.");
        }
        ItemPakaian item = new ItemPakaian(
                IdGenerator.generate("ITM"),
                pesanan.getIdPesanan(),
                jenisPakaian.trim(),
                kategoriWarna,
                kondisiAwal.trim(),
                deskripsiDetail.trim(),
                SmartGroupingService.BELUM_DIKELOMPOKKAN);
        laundryDAO.createItem(item);
    }

    // Menjalankan pengelompokan warna untuk seluruh item dalam satu pesanan.
    public int jalankanSmartGrouping(String idPesanan) throws SQLException {
        pesananDapatDiubah(idPesanan);
        int jumlahItem = smartGroupingService.kelompokkanItem(idPesanan);
        if (jumlahItem == 0) {
            throw new IllegalArgumentException("Belum ada item pakaian yang dapat dikelompokkan.");
        }
        return jumlahItem;
    }

    // Menghapus item hanya ketika pesanan masih boleh diedit.
    public void hapusItem(String idItem) throws SQLException {
        ItemPakaian item = laundryDAO.findItemById(idItem);
        if (item == null) {
            throw new IllegalArgumentException("Item pakaian tidak ditemukan.");
        }
        pesananDapatDiubah(item.getIdPesanan());
        laundryDAO.deleteItem(idItem);
    }

    // Membaca pembayaran milik satu pesanan.
    public Pembayaran getPembayaran(String idPesanan) throws SQLException {
        return laundryDAO.findPembayaranByPesanan(idPesanan);
    }

    // Menyediakan pilihan pesanan yang masih mempunyai tagihan.
    public List<Pesanan> getPesananBelumBayar() throws SQLException {
        return laundryDAO.findPesananBelumBayar();
    }

    // Menyediakan gabungan tagihan belum bayar dan pembayaran yang sudah lunas.
    public List<RiwayatPembayaran> getRiwayatPembayaran() throws SQLException {
        return laundryDAO.findRiwayatPembayaran();
    }

    // Mencatat pelunasan sesuai total pesanan dan metode yang dipilih karyawan.
    public Pembayaran catatPembayaran(String idPesanan, String metode) throws SQLException {
        Pesanan pesanan = laundryDAO.findPesananById(idPesanan);
        if (pesanan == null) {
            throw new IllegalArgumentException("Pesanan tidak ditemukan.");
        }
        if (metode == null || metode.isBlank()) {
            throw new IllegalArgumentException("Metode pembayaran wajib diisi.");
        }
        if (!pesanan.getStatusPesanan().dapatMenerimaPembayaran()) {
            throw new IllegalArgumentException("Pesanan yang dibatalkan tidak dapat menerima pembayaran.");
        }
        Pembayaran existing = laundryDAO.findPembayaranByPesanan(idPesanan);
        if (existing != null && existing.getStatus() == StatusPembayaran.LUNAS) {
            throw new IllegalArgumentException("Pesanan ini sudah lunas.");
        }
        Pembayaran pembayaran = new Pembayaran(
                existing == null ? IdGenerator.generate("PAY") : existing.getIdPembayaran(),
                idPesanan, metode.trim(), pesanan.getTotalBiaya(),
                StatusPembayaran.BELUM_BAYAR);
        pembayaran.prosesPembayaran();
        // Record lama yang masih Belum Bayar diperbarui, sedangkan tagihan baru dibuatkan record baru.
        if (existing == null) {
            laundryDAO.createPembayaran(pembayaran);
        } else {
            laundryDAO.updatePembayaran(pembayaran);
        }
        return pembayaran;
    }

    // Mengambil seluruh notifikasi yang dimiliki pelanggan.
    public List<Notifikasi> getNotifikasiPelanggan(String idPelanggan) throws SQLException {
        return laundryDAO.findNotifikasiByPelanggan(idPelanggan);
    }

    // Mengubah semua notifikasi pelanggan menjadi sudah dibaca.
    public int tandaiSemuaDibaca(String idPelanggan) throws SQLException {
        return laundryDAO.markAllNotifikasiRead(idPelanggan);
    }

    // Menyimpan notifikasi aplikasi bila pesan dengan isi sama belum pernah dibuat.
    public void kirimNotifikasiStatus(Pesanan pesanan) throws SQLException {
        Notifikasi notifikasi = buatNotifikasiStatus(pesanan);
        if (notifikasi != null
                && !laundryDAO.notifikasiExists(notifikasi.getIdPesanan(), notifikasi.getPesan())) {
            appNotifikasi.kirimNotifikasi(notifikasi);
        }
    }

    // Membentuk link WhatsApp dari nomor pelanggan dan template status akhir.
    public String buatLinkWhatsApp(String idPesanan) throws SQLException {
        Pesanan pesanan = laundryDAO.findPesananById(idPesanan);
        if (pesanan == null) {
            throw new SQLException("Pesanan tidak ditemukan.");
        }
        if (!perluNotifikasi(pesanan.getStatusPesanan())) {
            throw new IllegalArgumentException(
                    "Template WhatsApp tersedia saat pesanan Siap Diambil atau Selesai.");
        }
        String nomorTelepon = laundryDAO.findNomorTeleponByPesanan(idPesanan);
        if (nomorTelepon == null || nomorTelepon.isBlank()) {
            throw new SQLException("Nomor telepon pelanggan belum tersedia.");
        }
        WhatsAppNotifikasi whatsapp = new WhatsAppNotifikasi(nomorTelepon);
        whatsapp.kirimNotifikasi(buatNotifikasiStatus(pesanan));
        return whatsapp.getLinkWhatsApp();
    }

    // Notifikasi otomatis hanya dibuat untuk status siap diambil atau selesai.
    public Notifikasi buatNotifikasiStatus(Pesanan pesanan) {
        if (!perluNotifikasi(pesanan.getStatusPesanan())) {
            return null;
        }
        return new Notifikasi(
                IdGenerator.generate("NTF"),
                pesanan.getIdPesanan(),
                buatPesanStatus(pesanan),
                LocalDateTime.now(),
                false);
    }

    // Pemeriksaan bersama ini menjaga item tidak berubah setelah pencucian dimulai.
    private Pesanan pesananDapatDiubah(String idPesanan) throws SQLException {
        if (idPesanan == null || idPesanan.isBlank()) {
            throw new IllegalArgumentException("Pesanan wajib dipilih.");
        }
        Pesanan pesanan = laundryDAO.findPesananById(idPesanan.trim());
        if (pesanan == null) {
            throw new IllegalArgumentException("Pesanan tidak ditemukan.");
        }
        if (!pesanan.getStatusPesanan().dapatMengubahItem()) {
            throw new IllegalArgumentException(
                    "Item tidak dapat diubah setelah proses pencucian dimulai atau pesanan ditutup.");
        }
        return pesanan;
    }

    // Menyusun kalimat notifikasi berdasarkan status terakhir pesanan.
    private String buatPesanStatus(Pesanan pesanan) {
        if (pesanan.getStatusPesanan() == StatusPesanan.SIAP_DIAMBIL) {
            return "Halo " + pesanan.getNamaPelanggan() + ", pesanan laundry " + pesanan.getIdPesanan()
                    + " sudah siap diambil. Terima kasih - SILAUNDRY";
        }
        if (pesanan.getStatusPesanan() == StatusPesanan.SELESAI) {
            return "Halo " + pesanan.getNamaPelanggan() + ", pesanan laundry " + pesanan.getIdPesanan()
                    + " sudah selesai. Terima kasih - SILAUNDRY";
        }
        return "Halo " + pesanan.getNamaPelanggan() + ", status pesanan laundry " + pesanan.getIdPesanan()
                + " saat ini: " + pesanan.getStatusPesanan().getDisplayName() + ". Terima kasih - SILAUNDRY";
    }

    // Menentukan status yang memang perlu diberitahukan kepada pelanggan.
    private boolean perluNotifikasi(StatusPesanan statusPesanan) {
        return statusPesanan == StatusPesanan.SIAP_DIAMBIL || statusPesanan == StatusPesanan.SELESAI;
    }
}
