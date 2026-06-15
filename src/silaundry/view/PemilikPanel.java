package silaundry.view;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import silaundry.controller.DashboardController;
import silaundry.controller.PenggunaController;
import silaundry.controller.PesananController;
import silaundry.controller.TarifController;
import silaundry.model.DataDasbor;
import silaundry.model.Karyawan;
import silaundry.model.LaporanKeuangan;
import silaundry.model.Pelanggan;
import silaundry.model.Pesanan;
import silaundry.model.TarifLaundry;
import silaundry.model.enums.StatusPesanan;

public class PemilikPanel extends JPanel {
    public enum Page {
        DASHBOARD, PESANAN, PELANGGAN, KARYAWAN, TARIF
    }

    private final Page page;
    private final DashboardController dashboardController = new DashboardController();
    private final PenggunaController penggunaController = new PenggunaController();
    private final PesananController pesananController = new PesananController();
    private final TarifController tarifController = new TarifController();

    private final JLabel aktifLabel = new JLabel("-");
    private final JLabel nilaiAktifLabel = new JLabel("-");
    private final JLabel pendapatanLabel = new JLabel("-");
    private final JLabel itemLabel = new JLabel("-");
    private final JLabel pelangganLabel = new JLabel("-");
    private final JTextArea laporanArea = new JTextArea(8, 60);

    private final DefaultTableModel orderModel = UiUtil.model("ID", "Pelanggan", "Karyawan", "Tanggal",
            "Estimasi", "Paket", "Berat", "Status", "Total", "Catatan");
    private final JTable orderTable = new JTable(orderModel);
    private final JTextField orderSearchField = new JTextField(16);
    private final JComboBox<String> orderFilterCombo = new JComboBox<>(
            new String[] { "Semua", "Aktif", "Selesai", "Dibatalkan" });
    private final JLabel orderSummaryLabel = AppTheme.muted("Memuat data pesanan...");
    private List<Pesanan> orderCache = new ArrayList<>();

    private final DefaultTableModel pelangganModel = UiUtil.model("ID", "Username", "Nama", "Telepon", "Alamat");
    private final JTable pelangganTable = new JTable(pelangganModel);
    private final JTextField pelangganSearchField = new JTextField(18);
    private final JLabel pelangganSummaryLabel = AppTheme.muted("Memuat data pelanggan...");

    private final DefaultTableModel karyawanModel = UiUtil.model("ID", "Username", "Nama", "Telepon", "Shift");
    private final JTable karyawanTable = new JTable(karyawanModel);
    private final JTextField karyawanSearchField = new JTextField(16);
    private final JTextField usernameField = new JTextField(10);
    private final JTextField namaField = new JTextField(16);
    private final JTextField teleponField = new JTextField(10);
    private final JPasswordField passwordField = new JPasswordField(10);
    private final JComboBox<String> shiftCombo = new JComboBox<>(new String[] { "Pagi", "Malam" });

    private final DefaultTableModel tarifModel = UiUtil.model("ID", "Paket", "Estimasi", "Harga/kg", "Aktif");
    private final JTable tarifTable = new JTable(tarifModel);
    private final JComboBox<TarifLaundry> tarifCombo = new JComboBox<>();
    private final JTextField hargaTarifField = new JTextField(10);

    public PemilikPanel(Page page) {
        this.page = page;
        setLayout(new BorderLayout());
        setBackground(AppTheme.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        styleInputs();
        tarifCombo.addActionListener(event -> tarifSelected());
        orderFilterCombo.addActionListener(event -> applyOrderFilter());
        add(switch (page) {
            case DASHBOARD -> buildDashboardPanel();
            case PESANAN -> buildMonitoringPanel();
            case PELANGGAN -> buildPelangganPanel();
            case KARYAWAN -> buildKaryawanPanel();
            case TARIF -> buildTarifPanel();
        }, BorderLayout.CENTER);
    }

    public void refreshData() {
        switch (page) {
            case DASHBOARD -> refreshDashboard();
            case PESANAN -> refreshPesanan();
            case PELANGGAN -> refreshPelanggan();
            case KARYAWAN -> refreshKaryawan();
            case TARIF -> refreshTarif();
        }
    }

    private JPanel buildDashboardPanel() {
        JPanel panel = AppTheme.page(new BorderLayout(12, 12));
        JPanel header = AppTheme.surface(new BorderLayout(4, 4));
        header.add(AppTheme.sectionTitle("Dashboard Pemilik"), BorderLayout.NORTH);
        header.add(AppTheme.muted("Ringkasan transaksi dan laporan bulan berjalan."), BorderLayout.CENTER);

        JPanel metrics = new JPanel(new GridLayout(2, 3, 10, 10));
        metrics.setBackground(AppTheme.BACKGROUND);
        metrics.add(metricCard("Pesanan Aktif", aktifLabel));
        metrics.add(metricCard("Nilai Pesanan Aktif", nilaiAktifLabel));
        metrics.add(metricCard("Pendapatan Bulan Ini", pendapatanLabel));
        metrics.add(metricCard("Total Item", itemLabel));
        metrics.add(metricCard("Total Pelanggan", pelangganLabel));

        laporanArea.setEditable(false);
        AppTheme.styleTextArea(laporanArea);
        JButton refreshButton = AppTheme.primaryButton("Refresh Dashboard");
        refreshButton.addActionListener(event -> refreshData());
        JPanel top = new JPanel(new BorderLayout(0, 12));
        top.setBackground(AppTheme.BACKGROUND);
        top.add(header, BorderLayout.NORTH);
        top.add(metrics, BorderLayout.CENTER);
        panel.add(top, BorderLayout.NORTH);
        panel.add(AppTheme.scroll(laporanArea), BorderLayout.CENTER);
        panel.add(refreshButton, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel buildMonitoringPanel() {
        JPanel panel = AppTheme.page(new BorderLayout(10, 10));
        UiUtil.applyTableStyle(orderTable);
        UiUtil.installSearch(orderTable, orderSearchField);

        JPanel header = AppTheme.surface(new BorderLayout(10, 8));
        JPanel title = new JPanel(new GridLayout(2, 1, 0, 3));
        title.setBackground(AppTheme.SURFACE);
        title.add(AppTheme.sectionTitle("Pantau Pesanan"));
        title.add(orderSummaryLabel);

        JPanel filters = AppTheme.formGrid();
        AppTheme.addField(filters, 0, 0, "Status", orderFilterCombo);
        AppTheme.addField(filters, 0, 1, "Cari", orderSearchField);
        JButton refreshButton = AppTheme.primaryButton("Refresh");
        refreshButton.addActionListener(event -> refreshData());
        JPanel right = new JPanel(new BorderLayout(8, 0));
        right.setBackground(AppTheme.SURFACE);
        right.add(filters, BorderLayout.CENTER);
        right.add(refreshButton, BorderLayout.EAST);

        header.add(title, BorderLayout.CENTER);
        header.add(right, BorderLayout.EAST);
        panel.add(header, BorderLayout.NORTH);
        panel.add(AppTheme.scroll(orderTable), BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildKaryawanPanel() {
        JPanel panel = AppTheme.page(new BorderLayout(10, 10));
        UiUtil.applyTableStyle(karyawanTable);
        UiUtil.installSearch(karyawanTable, karyawanSearchField);
        JPanel form = AppTheme.surface(new BorderLayout(0, 12));
        form.add(AppTheme.sectionTitle("Kelola Karyawan"), BorderLayout.NORTH);

        JPanel fields = AppTheme.formGrid();
        AppTheme.addField(fields, 0, 0, "Username", usernameField);
        AppTheme.addField(fields, 0, 1, "Nama", namaField);
        AppTheme.addField(fields, 1, 0, "Telepon", teleponField);
        AppTheme.addField(fields, 1, 1, "Password", passwordField);
        AppTheme.addField(fields, 2, 0, "Shift", shiftCombo);
        AppTheme.addField(fields, 2, 1, "Cari", karyawanSearchField);

        JButton addButton = AppTheme.primaryButton("Tambah Karyawan");
        addButton.addActionListener(event -> addKaryawan());
        JButton deleteButton = AppTheme.dangerButton("Nonaktifkan");
        deleteButton.addActionListener(event -> deleteKaryawan());
        JButton refreshButton = AppTheme.secondaryButton("Refresh");
        refreshButton.addActionListener(event -> refreshData());
        JPanel actions = AppTheme.actionRow();
        actions.add(addButton);
        actions.add(deleteButton);
        actions.add(refreshButton);

        form.add(fields, BorderLayout.CENTER);
        form.add(actions, BorderLayout.SOUTH);
        panel.add(form, BorderLayout.NORTH);
        panel.add(AppTheme.scroll(karyawanTable), BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildPelangganPanel() {
        JPanel panel = AppTheme.page(new BorderLayout(10, 10));
        UiUtil.applyTableStyle(pelangganTable);
        UiUtil.installSearch(pelangganTable, pelangganSearchField);

        JPanel header = AppTheme.surface(new BorderLayout(12, 8));
        JPanel title = new JPanel(new GridLayout(2, 1, 0, 3));
        title.setBackground(AppTheme.SURFACE);
        title.add(AppTheme.sectionTitle("Daftar Pelanggan"));
        title.add(pelangganSummaryLabel);

        JPanel search = AppTheme.formGrid();
        AppTheme.addField(search, 0, 0, "Cari", pelangganSearchField);
        JButton refreshButton = AppTheme.primaryButton("Refresh");
        refreshButton.addActionListener(event -> refreshData());
        JPanel right = new JPanel(new BorderLayout(8, 0));
        right.setBackground(AppTheme.SURFACE);
        right.add(search, BorderLayout.CENTER);
        right.add(refreshButton, BorderLayout.EAST);

        header.add(title, BorderLayout.CENTER);
        header.add(right, BorderLayout.EAST);
        panel.add(header, BorderLayout.NORTH);
        panel.add(AppTheme.scroll(pelangganTable), BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildTarifPanel() {
        JPanel panel = AppTheme.page(new BorderLayout(10, 10));
        UiUtil.applyTableStyle(tarifTable);
        JPanel form = AppTheme.surface(new BorderLayout(0, 12));
        JPanel title = new JPanel(new BorderLayout(0, 4));
        title.setBackground(AppTheme.SURFACE);
        title.add(AppTheme.sectionTitle("Tarif Laundry"), BorderLayout.NORTH);
        title.add(AppTheme.muted("Harga per kilo hanya dapat diubah oleh pemilik."), BorderLayout.CENTER);

        JPanel fields = AppTheme.formGrid();
        AppTheme.addField(fields, 0, 0, "Paket", tarifCombo);
        AppTheme.addField(fields, 0, 1, "Harga/kg", hargaTarifField);
        JButton updateButton = AppTheme.primaryButton("Update Harga");
        updateButton.addActionListener(event -> updateTarif());
        JButton refreshButton = AppTheme.secondaryButton("Refresh");
        refreshButton.addActionListener(event -> refreshData());
        JPanel actions = AppTheme.actionRow();
        actions.add(updateButton);
        actions.add(refreshButton);

        form.add(title, BorderLayout.NORTH);
        form.add(fields, BorderLayout.CENTER);
        form.add(actions, BorderLayout.SOUTH);
        panel.add(form, BorderLayout.NORTH);
        panel.add(AppTheme.scroll(tarifTable), BorderLayout.CENTER);
        return panel;
    }

    private JPanel metricCard(String title, JLabel valueLabel) {
        JPanel panel = AppTheme.compactSurface(new BorderLayout(4, 8));
        valueLabel.setFont(AppTheme.TITLE_FONT.deriveFont(22f));
        valueLabel.setForeground(AppTheme.PRIMARY_DARK);
        panel.add(AppTheme.muted(title), BorderLayout.NORTH);
        panel.add(valueLabel, BorderLayout.CENTER);
        return panel;
    }

    private void styleInputs() {
        AppTheme.styleTextField(orderSearchField);
        AppTheme.styleComboBox(orderFilterCombo);
        AppTheme.styleTextField(pelangganSearchField);
        AppTheme.styleTextField(usernameField);
        AppTheme.styleTextField(namaField);
        AppTheme.styleTextField(teleponField);
        AppTheme.styleTextField(passwordField);
        AppTheme.styleComboBox(shiftCombo);
        AppTheme.styleTextField(karyawanSearchField);
        AppTheme.styleComboBox(tarifCombo);
        AppTheme.styleTextField(hargaTarifField);
    }

    private void refreshDashboard() {
        UiUtil.runAsync(this,
                () -> new DashboardData(dashboardController.getDataDasbor(), dashboardController.getLaporanBulanIni()),
                result -> {
            DataDasbor data = result.dataDasbor();
            LaporanKeuangan laporan = result.laporan();
            aktifLabel.setText(String.valueOf(data.getTotalPesananAktif()));
            nilaiAktifLabel.setText(UiUtil.money(data.getEstimasiPendapatan()));
            pendapatanLabel.setText(UiUtil.money(data.getPendapatanDiterima()));
            itemLabel.setText(String.valueOf(data.getTotalItem()));
            pelangganLabel.setText(String.valueOf(data.getTotalPelanggan()));
            laporanArea.setText("Laporan bulan ini\n" + laporan.formatDataLaporan());
        }, "Gagal memuat dashboard.");
    }

    private void refreshPesanan() {
        UiUtil.runAsync(this, pesananController::getAllPesanan, rows -> {
            orderCache = rows;
            applyOrderFilter();
        }, "Gagal memuat data pesanan.");
    }

    private void applyOrderFilter() {
        if (orderModel == null) {
            return;
        }
        String filter = orderFilterCombo.getSelectedItem() == null ? "Semua"
                : orderFilterCombo.getSelectedItem().toString();
        orderModel.setRowCount(0);
        int shown = 0;
        for (Pesanan pesanan : orderCache) {
            if (matchesFilter(pesanan, filter)) {
                orderModel.addRow(new Object[] { pesanan.getIdPesanan(), pesanan.getNamaPelanggan(),
                        pesanan.getNamaKaryawan() == null ? "-" : pesanan.getNamaKaryawan(),
                        pesanan.getTanggalMasuk(), pesanan.getEstimasiSelesai(),
                        pesanan.getPaketLaundry().getDisplayName(), String.format("%.2f kg", pesanan.getBeratKg()),
                        pesanan.getStatusPesanan().getDisplayName(), UiUtil.money(pesanan.getTotalBiaya()),
                        pesanan.getCatatan() });
                shown++;
            }
        }
        orderSummaryLabel.setText(shown + " dari " + orderCache.size() + " pesanan ditampilkan.");
    }

    private boolean matchesFilter(Pesanan pesanan, String filter) {
        return switch (filter) {
            case "Aktif" -> !pesanan.getStatusPesanan().isFinal();
            case "Selesai" -> pesanan.getStatusPesanan() == StatusPesanan.SELESAI;
            case "Dibatalkan" -> pesanan.getStatusPesanan() == StatusPesanan.DIBATALKAN;
            default -> true;
        };
    }

    private void refreshKaryawan() {
        UiUtil.runAsync(this, penggunaController::getAllKaryawan, rows -> {
            karyawanModel.setRowCount(0);
            for (Karyawan karyawan : rows) {
                karyawanModel.addRow(new Object[] { karyawan.getIdKaryawan(), karyawan.getUsername(),
                        karyawan.getNamaLengkap(), karyawan.getNomorTelepon(), karyawan.getShiftKerja() });
            }
        }, "Gagal memuat data karyawan.");
    }

    private void refreshPelanggan() {
        UiUtil.runAsync(this, penggunaController::getAllPelanggan, rows -> {
            pelangganModel.setRowCount(0);
            for (Pelanggan pelanggan : rows) {
                pelangganModel.addRow(new Object[] { pelanggan.getIdPelanggan(), pelanggan.getUsername(),
                        pelanggan.getNamaLengkap(), pelanggan.getNomorTelepon(), pelanggan.getAlamat() });
            }
            pelangganSummaryLabel.setText(rows.size() + " pelanggan terdaftar.");
        }, "Gagal memuat data pelanggan.");
    }

    private void refreshTarif() {
        UiUtil.runAsync(this, tarifController::getSemuaTarif, rows -> {
            tarifModel.setRowCount(0);
            tarifCombo.removeAllItems();
            for (TarifLaundry tarif : rows) {
                tarifModel.addRow(new Object[] { tarif.getIdTarif(), tarif.getNamaPaket(),
                        tarif.getEstimasiHari() + " hari", UiUtil.money(tarif.getHargaPerKg()),
                        tarif.isAktif() ? "Aktif" : "Nonaktif" });
                tarifCombo.addItem(tarif);
            }
            tarifSelected();
        }, "Gagal memuat tarif laundry.");
    }

    private void addKaryawan() {
        String password = new String(passwordField.getPassword());
        UiUtil.runAsync(this, () -> {
            penggunaController.tambahKaryawan(usernameField.getText().trim(), namaField.getText().trim(),
                    teleponField.getText().trim(), password, shiftCombo.getSelectedItem().toString());
            return null;
        }, ignored -> {
            usernameField.setText("");
            namaField.setText("");
            teleponField.setText("");
            passwordField.setText("");
            shiftCombo.setSelectedItem("Pagi");
            refreshKaryawan();
            UiUtil.info(this, "Akun karyawan berhasil dibuat.");
        }, "Gagal menambah karyawan.");
    }

    private void deleteKaryawan() {
        String idKaryawan = UiUtil.selectedId(karyawanTable);
        if (idKaryawan == null || !UiUtil.confirm(this, "Nonaktifkan akun karyawan " + idKaryawan + "?")) {
            return;
        }
        UiUtil.runAsync(this, () -> {
            penggunaController.hapusKaryawan(idKaryawan);
            return null;
        }, ignored -> refreshKaryawan(), "Gagal menonaktifkan karyawan.");
    }

    private void tarifSelected() {
        TarifLaundry tarif = (TarifLaundry) tarifCombo.getSelectedItem();
        if (tarif != null) {
            hargaTarifField.setText(String.valueOf(Math.round(tarif.getHargaPerKg())));
        }
    }

    private void updateTarif() {
        TarifLaundry tarif = (TarifLaundry) tarifCombo.getSelectedItem();
        if (tarif == null) {
            UiUtil.info(this, "Pilih paket laundry terlebih dahulu.");
            return;
        }
        final double harga;
        try {
            harga = Double.parseDouble(hargaTarifField.getText().trim());
        } catch (NumberFormatException ex) {
            UiUtil.info(this, "Harga per kilo harus berupa angka.");
            return;
        }
        UiUtil.runAsync(this, () -> {
            tarifController.updateHarga(tarif.getPaketLaundry(), harga);
            return null;
        }, ignored -> {
            refreshTarif();
            UiUtil.info(this, "Harga " + tarif.getNamaPaket() + " berhasil diperbarui.");
        }, "Gagal memperbarui tarif laundry.");
    }

    private record DashboardData(DataDasbor dataDasbor, LaporanKeuangan laporan) {
    }
}
