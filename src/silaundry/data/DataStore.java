package silaundry.data;

import java.util.ArrayList;
import java.util.List;
import silaundry.model.Admin;
import silaundry.model.ItemPakaian;
import silaundry.model.Notifikasi;
import silaundry.model.Pelanggan;
import silaundry.model.Pembayaran;
import silaundry.model.Pengguna;
import silaundry.model.Pesanan;
import silaundry.model.TarifLaundry;
import silaundry.model.enums.PaketLaundry;
import silaundry.model.enums.Role;

// Menyimpan seluruh data aplikasi di dalam ArrayList selama program masih berjalan.
public final class DataStore {
    private static final DataStore INSTANCE = new DataStore();

    private final ArrayList<Pengguna> daftarPengguna = new ArrayList<>();
    private final ArrayList<Pelanggan> daftarPelanggan = new ArrayList<>();
    private final ArrayList<Pesanan> daftarPesanan = new ArrayList<>();
    private final ArrayList<ItemPakaian> daftarItem = new ArrayList<>();
    private final ArrayList<Pembayaran> daftarPembayaran = new ArrayList<>();
    private final ArrayList<Notifikasi> daftarNotifikasi = new ArrayList<>();
    private final ArrayList<TarifLaundry> daftarTarif = new ArrayList<>();

    private DataStore() {
        reset();
    }

    public static DataStore getInstance() {
        return INSTANCE;
    }

    // Reset dipakai oleh pengujian dan menghasilkan kondisi awal yang sama seperti aplikasi baru dibuka.
    public void reset() {
        daftarPengguna.clear();
        daftarPelanggan.clear();
        daftarPesanan.clear();
        daftarItem.clear();
        daftarPembayaran.clear();
        daftarNotifikasi.clear();
        daftarTarif.clear();

        daftarPengguna.add(new Admin(
                "USR001",
                "masteradmin",
                "Master Admin",
                "081234567890",
                "123456"));
        daftarTarif.add(new TarifLaundry(
                "TRF001",
                PaketLaundry.STANDARD_2_HARI,
                "Standard 2 Hari",
                2,
                7000,
                true));
        daftarTarif.add(new TarifLaundry(
                "TRF002",
                PaketLaundry.EXPRESS_1_HARI,
                "Express 1 Hari",
                1,
                8000,
                true));
    }

    // Bagian akun digunakan untuk login dan pengecekan username.
    public Pengguna cariPengguna(String username, String password, Role role) {
        for (Pengguna pengguna : daftarPengguna) {
            boolean usernameCocok = pengguna.getUsername().equalsIgnoreCase(username);
            boolean passwordCocok = pengguna.getKataSandi().equals(password);
            if (usernameCocok && passwordCocok && pengguna.getRole() == role) {
                return pengguna;
            }
        }
        return null;
    }

    public boolean usernameSudahDipakai(String username) {
        for (Pengguna pengguna : daftarPengguna) {
            if (pengguna.getUsername().equalsIgnoreCase(username)) {
                return true;
            }
        }
        return false;
    }

    public void tambahPelanggan(Pelanggan pelanggan) {
        daftarPelanggan.add(pelanggan);
        daftarPengguna.add(pelanggan);
    }

    // Bagian pelanggan menyediakan salinan list dan pencarian berdasarkan ID.
    public List<Pelanggan> getDaftarPelanggan() {
        return new ArrayList<>(daftarPelanggan);
    }

    public Pelanggan cariPelanggan(String idPelanggan) {
        for (Pelanggan pelanggan : daftarPelanggan) {
            if (pelanggan.getIdPelanggan().equals(idPelanggan)) {
                return pelanggan;
            }
        }
        return null;
    }

    public void tambahPesanan(Pesanan pesanan) {
        daftarPesanan.add(pesanan);
    }

    // Bagian pesanan menyimpan transaksi dan mencari pesanan yang dipilih.
    public List<Pesanan> getDaftarPesanan() {
        return new ArrayList<>(daftarPesanan);
    }

    public Pesanan cariPesanan(String idPesanan) {
        for (Pesanan pesanan : daftarPesanan) {
            if (pesanan.getIdPesanan().equals(idPesanan)) {
                return pesanan;
            }
        }
        return null;
    }

    public void tambahItem(ItemPakaian item) {
        daftarItem.add(item);
        Pesanan pesanan = cariPesanan(item.getIdPesanan());
        if (pesanan != null) {
            pesanan.tambahItemPakaian(item);
        }
    }

    // Bagian item menjaga list utama dan list item di dalam pesanan tetap berhubungan.
    public List<ItemPakaian> getDaftarItem() {
        return new ArrayList<>(daftarItem);
    }

    public ItemPakaian cariItem(String idItem) {
        for (ItemPakaian item : daftarItem) {
            if (item.getIdItem().equals(idItem)) {
                return item;
            }
        }
        return null;
    }

    public boolean hapusItem(String idItem) {
        ItemPakaian item = cariItem(idItem);
        if (item == null) {
            return false;
        }
        daftarItem.remove(item);
        Pesanan pesanan = cariPesanan(item.getIdPesanan());
        if (pesanan != null) {
            pesanan.getDaftarItem().remove(item);
        }
        return true;
    }

    public void simpanPembayaran(Pembayaran pembayaran) {
        Pembayaran lama = cariPembayaran(pembayaran.getIdPesanan());
        if (lama != null) {
            daftarPembayaran.remove(lama);
        }
        daftarPembayaran.add(pembayaran);
    }

    // Bagian pembayaran memastikan satu pesanan hanya mempunyai satu pembayaran.
    public List<Pembayaran> getDaftarPembayaran() {
        return new ArrayList<>(daftarPembayaran);
    }

    public Pembayaran cariPembayaran(String idPesanan) {
        for (Pembayaran pembayaran : daftarPembayaran) {
            if (pembayaran.getIdPesanan().equals(idPesanan)) {
                return pembayaran;
            }
        }
        return null;
    }

    public void tambahNotifikasi(Notifikasi notifikasi) {
        daftarNotifikasi.add(notifikasi);
    }

    // Bagian notifikasi mencegah pesan status yang sama tersimpan dua kali.
    public List<Notifikasi> getDaftarNotifikasi() {
        return new ArrayList<>(daftarNotifikasi);
    }

    public boolean notifikasiSudahAda(String idPesanan, String pesan) {
        for (Notifikasi notifikasi : daftarNotifikasi) {
            if (notifikasi.getIdPesanan().equals(idPesanan)
                    && notifikasi.getPesan().equals(pesan)) {
                return true;
            }
        }
        return false;
    }

    public List<TarifLaundry> getDaftarTarif() {
        return new ArrayList<>(daftarTarif);
    }

    // Tarif dicari berdasarkan jenis paket yang dipilih oleh admin.
    public TarifLaundry cariTarif(PaketLaundry paketLaundry) {
        for (TarifLaundry tarif : daftarTarif) {
            if (tarif.getPaketLaundry() == paketLaundry) {
                return tarif;
            }
        }
        return null;
    }
}
