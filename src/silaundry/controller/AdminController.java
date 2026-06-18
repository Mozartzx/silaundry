package silaundry.controller;

import java.time.YearMonth;
import java.util.List;
import silaundry.data.DataStore;
import silaundry.model.DataDasbor;
import silaundry.model.LaporanKeuangan;
import silaundry.model.Pembayaran;
import silaundry.model.Pesanan;
import silaundry.model.TarifLaundry;
import silaundry.model.enums.PaketLaundry;
import silaundry.model.enums.StatusPembayaran;
import silaundry.model.enums.StatusPesanan;

// Menghitung dashboard, laporan, dan tarif langsung dari collection aplikasi.
public class AdminController {
    private final DataStore dataStore = DataStore.getInstance();

    // Dashboard dihitung ulang dari isi ArrayList setiap kali halaman dibuka.
    public DataDasbor getDataDasbor() {
        int totalAktif = 0;
        double nilaiAktif = 0;
        int totalItem = dataStore.getDaftarItem().size();
        for (Pesanan pesanan : dataStore.getDaftarPesanan()) {
            if (!pesanan.getStatusPesanan().isFinal()) {
                totalAktif++;
                nilaiAktif += pesanan.getTotalBiaya();
            }
        }
        double pendapatan = 0;
        for (Pembayaran pembayaran : dataStore.getDaftarPembayaran()) {
            if (pembayaran.getStatus() == StatusPembayaran.LUNAS) {
                pendapatan += pembayaran.getJumlah();
            }
        }
        return new DataDasbor(
                "DSB-" + YearMonth.now(),
                totalAktif,
                nilaiAktif,
                pendapatan,
                totalItem,
                dataStore.getDaftarPelanggan().size());
    }

    public LaporanKeuangan getLaporanBulanIni() {
        double pendapatan = 0;
        int pesananSelesai = 0;
        for (Pembayaran pembayaran : dataStore.getDaftarPembayaran()) {
            if (pembayaran.getStatus() == StatusPembayaran.LUNAS) {
                pendapatan += pembayaran.getJumlah();
            }
        }
        for (Pesanan pesanan : dataStore.getDaftarPesanan()) {
            if (pesanan.getStatusPesanan() == StatusPesanan.SELESAI) {
                pesananSelesai++;
            }
        }
        YearMonth periode = YearMonth.now();
        return new LaporanKeuangan("LAP-" + periode, periode.toString(), pendapatan, pesananSelesai);
    }

    // Bagian tarif dipakai untuk menampilkan dan mengubah harga per kilogram.
    public List<TarifLaundry> getSemuaTarif() {
        return dataStore.getDaftarTarif();
    }

    public TarifLaundry getTarif(PaketLaundry paketLaundry) {
        return dataStore.cariTarif(paketLaundry);
    }

    public void updateHarga(PaketLaundry paketLaundry, double hargaPerKg) {
        if (paketLaundry == null) {
            throw new IllegalArgumentException("Paket tarif wajib dipilih.");
        }
        if (hargaPerKg <= 0) {
            throw new IllegalArgumentException("Harga per kilogram harus lebih dari 0.");
        }
        TarifLaundry tarif = dataStore.cariTarif(paketLaundry);
        if (tarif == null) {
            throw new IllegalArgumentException("Tarif laundry tidak ditemukan.");
        }
        tarif.setHargaPerKg(hargaPerKg);
    }
}
