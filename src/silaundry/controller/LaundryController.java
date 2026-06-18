package silaundry.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import silaundry.data.DataStore;
import silaundry.model.INotifiable;
import silaundry.model.ItemPakaian;
import silaundry.model.Notifikasi;
import silaundry.model.Pelanggan;
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

// Menangani aturan bisnis laundry dengan data yang tersimpan di ArrayList.
public class LaundryController {
    private final DataStore dataStore = DataStore.getInstance();
    private final SmartGroupingService smartGroupingService = new SmartGroupingService();
    private final INotifiable appNotifikasi = new AppNotifikasi();

    // Bagian pembacaan data pesanan untuk tabel admin dan pelanggan.
    public List<Pesanan> getAllPesanan() {
        List<Pesanan> pesanan = dataStore.getDaftarPesanan();
        pesanan.sort(Comparator.comparing(Pesanan::getTanggalMasuk)
                .thenComparing(Pesanan::getIdPesanan)
                .reversed());
        return pesanan;
    }

    public List<Pesanan> getPesananPelanggan(String idPelanggan) {
        List<Pesanan> hasil = new ArrayList<>();
        for (Pesanan pesanan : getAllPesanan()) {
            if (pesanan.getIdPelanggan().equals(idPelanggan)) {
                hasil.add(pesanan);
            }
        }
        return hasil;
    }

    public Pesanan getPesanan(String idPesanan) {
        return dataStore.cariPesanan(idPesanan);
    }

    public List<TarifLaundry> getTarifAktif() {
        List<TarifLaundry> hasil = new ArrayList<>();
        for (TarifLaundry tarif : dataStore.getDaftarTarif()) {
            if (tarif.isAktif()) {
                hasil.add(tarif);
            }
        }
        return hasil;
    }

    // Pembuatan pesanan memvalidasi input lalu mengambil snapshot tarif yang sedang berlaku.
    public Pesanan tambahPesanan(String idPelanggan, PaketLaundry paketLaundry, double beratKg, String catatan) {
        if (idPelanggan == null || idPelanggan.isBlank()) {
            throw new IllegalArgumentException("Pelanggan wajib dipilih.");
        }
        if (paketLaundry == null) {
            throw new IllegalArgumentException("Paket laundry wajib dipilih.");
        }
        if (beratKg <= 0) {
            throw new IllegalArgumentException("Berat laundry harus lebih dari 0 kg.");
        }
        Pelanggan pelanggan = dataStore.cariPelanggan(idPelanggan);
        if (pelanggan == null) {
            throw new IllegalArgumentException("Pelanggan tidak ditemukan.");
        }
        TarifLaundry tarif = dataStore.cariTarif(paketLaundry);
        if (tarif == null || !tarif.isAktif()) {
            throw new IllegalArgumentException("Tarif paket belum tersedia.");
        }
        Pesanan pesanan = new Pesanan(
                IdGenerator.generate("ORD"),
                idPelanggan,
                LocalDate.now(),
                LocalDate.now().plusDays(tarif.getEstimasiHari()),
                StatusPesanan.BARU,
                tarif.getPaketLaundry(),
                beratKg,
                tarif.getHargaPerKg(),
                tarif.hitungTotal(beratKg),
                catatan == null ? "" : catatan.trim());
        pesanan.setNamaPelanggan(pelanggan.getNamaLengkap());
        dataStore.tambahPesanan(pesanan);
        return pesanan;
    }

    // Perubahan status harus mengikuti urutan proses laundry pada enum StatusPesanan.
    public boolean updateStatus(String idPesanan, StatusPesanan statusBaru) {
        if (statusBaru == null) {
            throw new IllegalArgumentException("Status tujuan wajib dipilih.");
        }
        Pesanan pesanan = dataStore.cariPesanan(idPesanan);
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
        if (statusBaru.membutuhkanItemPakaian() && getItemsByPesanan(idPesanan).isEmpty()) {
            throw new IllegalArgumentException("Catat minimal satu item pakaian sebelum proses pencucian.");
        }
        pesanan.setStatusPesanan(statusBaru);
        kirimNotifikasiStatus(pesanan);
        return true;
    }

    public boolean batalkanPesanan(String idPesanan) {
        Pembayaran pembayaran = getPembayaran(idPesanan);
        if (pembayaran != null && pembayaran.getJumlah() > 0) {
            throw new IllegalArgumentException("Pesanan yang sudah dibayar tidak dapat dibatalkan.");
        }
        return updateStatus(idPesanan, StatusPesanan.DIBATALKAN);
    }

    // Bagian item pakaian menangani pencatatan, pengelompokan warna, dan penghapusan.
    public List<ItemPakaian> getAllItems() {
        return dataStore.getDaftarItem();
    }

    public List<ItemPakaian> getItemsByPesanan(String idPesanan) {
        List<ItemPakaian> hasil = new ArrayList<>();
        for (ItemPakaian item : dataStore.getDaftarItem()) {
            if (item.getIdPesanan().equals(idPesanan)) {
                hasil.add(item);
            }
        }
        return hasil;
    }

    public void tambahItem(String idPesanan, String jenisPakaian, KategoriWarna kategoriWarna,
            String kondisiAwal, String deskripsiDetail) {
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
        dataStore.tambahItem(item);
    }

    public int jalankanSmartGrouping(String idPesanan) {
        pesananDapatDiubah(idPesanan);
        int jumlahItem = smartGroupingService.kelompokkanItem(idPesanan);
        if (jumlahItem == 0) {
            throw new IllegalArgumentException("Belum ada item pakaian yang dapat dikelompokkan.");
        }
        return jumlahItem;
    }

    public void hapusItem(String idItem) {
        ItemPakaian item = dataStore.cariItem(idItem);
        if (item == null) {
            throw new IllegalArgumentException("Item pakaian tidak ditemukan.");
        }
        pesananDapatDiubah(item.getIdPesanan());
        dataStore.hapusItem(idItem);
    }

    public Pembayaran getPembayaran(String idPesanan) {
        return dataStore.cariPembayaran(idPesanan);
    }

    // Bagian pembayaran hanya menampilkan pesanan yang belum lunas.
    public List<Pesanan> getPesananBelumBayar() {
        List<Pesanan> hasil = new ArrayList<>();
        for (Pesanan pesanan : getAllPesanan()) {
            Pembayaran pembayaran = getPembayaran(pesanan.getIdPesanan());
            boolean belumLunas = pembayaran == null || pembayaran.getStatus() != StatusPembayaran.LUNAS;
            if (pesanan.getStatusPesanan().dapatMenerimaPembayaran() && belumLunas) {
                hasil.add(pesanan);
            }
        }
        return hasil;
    }

    public List<RiwayatPembayaran> getRiwayatPembayaran() {
        List<RiwayatPembayaran> hasil = new ArrayList<>();
        for (Pesanan pesanan : getAllPesanan()) {
            if (pesanan.getStatusPesanan() == StatusPesanan.DIBATALKAN) {
                continue;
            }
            Pembayaran pembayaran = getPembayaran(pesanan.getIdPesanan());
            StatusPembayaran status = pembayaran == null
                    ? StatusPembayaran.BELUM_BAYAR
                    : pembayaran.getStatus();
            hasil.add(new RiwayatPembayaran(
                    pesanan.getIdPesanan(),
                    pesanan.getNamaPelanggan(),
                    pesanan.getTanggalMasuk(),
                    pesanan.getTotalBiaya(),
                    pembayaran == null ? null : pembayaran.getMetode(),
                    pembayaran == null ? 0 : pembayaran.getJumlah(),
                    status,
                    pembayaran == null ? null : pembayaran.getTanggalBayar()));
        }
        hasil.sort(Comparator
                .comparing((RiwayatPembayaran riwayat) -> riwayat.getStatus() == StatusPembayaran.LUNAS)
                .thenComparing(RiwayatPembayaran::getTanggalPesanan, Comparator.reverseOrder()));
        return hasil;
    }

    public Pembayaran catatPembayaran(String idPesanan, String metode) {
        Pesanan pesanan = dataStore.cariPesanan(idPesanan);
        if (pesanan == null) {
            throw new IllegalArgumentException("Pesanan tidak ditemukan.");
        }
        if (metode == null || metode.isBlank()) {
            throw new IllegalArgumentException("Metode pembayaran wajib dipilih.");
        }
        if (!pesanan.getStatusPesanan().dapatMenerimaPembayaran()) {
            throw new IllegalArgumentException("Pesanan yang dibatalkan tidak dapat menerima pembayaran.");
        }
        Pembayaran existing = getPembayaran(idPesanan);
        if (existing != null && existing.getStatus() == StatusPembayaran.LUNAS) {
            throw new IllegalArgumentException("Pesanan ini sudah lunas.");
        }
        Pembayaran pembayaran = new Pembayaran(
                existing == null ? IdGenerator.generate("PAY") : existing.getIdPembayaran(),
                idPesanan,
                metode.trim(),
                pesanan.getTotalBiaya(),
                StatusPembayaran.BELUM_BAYAR);
        pembayaran.prosesPembayaran();
        dataStore.simpanPembayaran(pembayaran);
        return pembayaran;
    }

    // Bagian notifikasi membatasi pesan agar hanya dapat dibaca oleh pemilik pesanan.
    public List<Notifikasi> getNotifikasiPelanggan(String idPelanggan) {
        List<Notifikasi> hasil = new ArrayList<>();
        for (Notifikasi notifikasi : dataStore.getDaftarNotifikasi()) {
            Pesanan pesanan = dataStore.cariPesanan(notifikasi.getIdPesanan());
            if (pesanan != null && pesanan.getIdPelanggan().equals(idPelanggan)) {
                hasil.add(notifikasi);
            }
        }
        hasil.sort(Comparator.comparing(Notifikasi::getTanggalKirim).reversed());
        return hasil;
    }

    public int tandaiSemuaDibaca(String idPelanggan) {
        int jumlah = 0;
        for (Notifikasi notifikasi : getNotifikasiPelanggan(idPelanggan)) {
            if (!notifikasi.isSudahDibaca()) {
                notifikasi.setSudahDibaca(true);
                jumlah++;
            }
        }
        return jumlah;
    }

    public void kirimNotifikasiStatus(Pesanan pesanan) {
        Notifikasi notifikasi = buatNotifikasiStatus(pesanan);
        if (notifikasi != null
                && !dataStore.notifikasiSudahAda(notifikasi.getIdPesanan(), notifikasi.getPesan())) {
            appNotifikasi.kirimNotifikasi(notifikasi);
        }
    }

    public String buatLinkWhatsApp(String idPesanan) {
        Pesanan pesanan = dataStore.cariPesanan(idPesanan);
        if (pesanan == null) {
            throw new IllegalArgumentException("Pesanan tidak ditemukan.");
        }
        if (!perluNotifikasi(pesanan.getStatusPesanan())) {
            throw new IllegalArgumentException(
                    "Template WhatsApp tersedia saat pesanan Siap Diambil atau Selesai.");
        }
        Pelanggan pelanggan = dataStore.cariPelanggan(pesanan.getIdPelanggan());
        if (pelanggan == null || pelanggan.getNomorTelepon().isBlank()) {
            throw new IllegalArgumentException("Nomor telepon pelanggan belum tersedia.");
        }
        WhatsAppNotifikasi whatsapp = new WhatsAppNotifikasi(pelanggan.getNomorTelepon());
        whatsapp.kirimNotifikasi(buatNotifikasiStatus(pesanan));
        return whatsapp.getLinkWhatsApp();
    }

    // Helper berikut membentuk isi notifikasi berdasarkan status pesanan.
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

    private Pesanan pesananDapatDiubah(String idPesanan) {
        if (idPesanan == null || idPesanan.isBlank()) {
            throw new IllegalArgumentException("Pesanan wajib dipilih.");
        }
        Pesanan pesanan = dataStore.cariPesanan(idPesanan.trim());
        if (pesanan == null) {
            throw new IllegalArgumentException("Pesanan tidak ditemukan.");
        }
        if (!pesanan.getStatusPesanan().dapatMengubahItem()) {
            throw new IllegalArgumentException(
                    "Item tidak dapat diubah setelah proses pencucian dimulai atau pesanan ditutup.");
        }
        return pesanan;
    }

    private String buatPesanStatus(Pesanan pesanan) {
        if (pesanan.getStatusPesanan() == StatusPesanan.SIAP_DIAMBIL) {
            return "Halo " + pesanan.getNamaPelanggan() + ", pesanan laundry " + pesanan.getIdPesanan()
                    + " sudah siap diambil. Terima kasih - SILAUNDRY";
        }
        return "Halo " + pesanan.getNamaPelanggan() + ", pesanan laundry " + pesanan.getIdPesanan()
                + " sudah selesai. Terima kasih - SILAUNDRY";
    }

    private boolean perluNotifikasi(StatusPesanan statusPesanan) {
        return statusPesanan == StatusPesanan.SIAP_DIAMBIL || statusPesanan == StatusPesanan.SELESAI;
    }
}
