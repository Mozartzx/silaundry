package silaundry.view;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.GridLayout;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JPasswordField;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import silaundry.controller.DashboardController;
import silaundry.controller.PenggunaController;
import silaundry.controller.PesananController;
import silaundry.controller.TarifController;
import silaundry.model.DataDasbor;
import silaundry.model.Karyawan;
import silaundry.model.LaporanKeuangan;
import silaundry.model.Pesanan;
import silaundry.model.TarifLaundry;
import silaundry.model.enums.StatusPesanan;

public class PemilikPanel extends JPanel {
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel contentPanel = new JPanel(cardLayout);
    private final List<JButton> menuButtons = new ArrayList<>();
    private final DashboardController dashboardController = new DashboardController();
    private final PenggunaController penggunaController = new PenggunaController();
    private final PesananController pesananController = new PesananController();
    private final TarifController tarifController = new TarifController();
    private final JLabel aktifLabel = new JLabel("-");
    private final JLabel pendapatanLabel = new JLabel("-");
    private final JLabel pendapatanDiterimaLabel = new JLabel("-");
    private final JLabel itemLabel = new JLabel("-");
    private final JLabel pelangganLabel = new JLabel("-");
    private final JTextArea laporanArea = new JTextArea(8, 60);
    private final JLabel pesananSummaryLabel = AppTheme.muted("Memuat data pesanan...");
    private final DefaultTableModel activeOrderModel = UiUtil.model("ID", "Pelanggan", "Karyawan", "Tanggal",
            "Estimasi", "Paket", "Berat", "Status", "Total", "Catatan");
    private final DefaultTableModel historyOrderModel = UiUtil.model("ID", "Pelanggan", "Karyawan", "Tanggal",
            "Estimasi", "Paket", "Berat", "Status", "Total", "Catatan");
    private final JTable activeOrderTable = new JTable(activeOrderModel);
    private final JTable historyOrderTable = new JTable(historyOrderModel);
    private final JTextField activeOrderSearchField = new JTextField(16);
    private final JTextField historyOrderSearchField = new JTextField(16);
    private final DefaultTableModel karyawanModel = UiUtil.model("ID", "Username", "Nama", "Telepon", "Shift");
    private final JTable karyawanTable = new JTable(karyawanModel);
    private final JTextField karyawanSearchField = new JTextField(16);
    private final JTextField usernameField = new JTextField(10);
    private final JTextField namaField = new JTextField(16);
    private final JTextField teleponField = new JTextField(10);
    private final JPasswordField passwordField = new JPasswordField(10);
    private final JTextField shiftField = new JTextField("Pagi", 8);
    private final DefaultTableModel tarifModel = UiUtil.model("ID", "Paket", "Estimasi", "Harga/kg", "Aktif");
    private final JTable tarifTable = new JTable(tarifModel);
    private final JComboBox<TarifLaundry> tarifCombo = new JComboBox<>();
    private final JTextField hargaTarifField = new JTextField(10);

    public PemilikPanel() {
        setLayout(new BorderLayout());
        setBackground(AppTheme.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));
        styleInputs();
        tarifCombo.addActionListener(event -> tarifSelected());
        contentPanel.setBackground(AppTheme.BACKGROUND);
        contentPanel.add(buildDashboardPanel(), "Dashboard");
        contentPanel.add(buildMonitoringPanel(), "Pantau Pesanan");
        contentPanel.add(buildKaryawanPanel(), "Kelola Karyawan");
        contentPanel.add(buildTarifPanel(), "Tarif Laundry");
        add(buildLocalSidebar(), BorderLayout.WEST);
        add(contentPanel, BorderLayout.CENTER);
        refreshAll();
    }

    private JPanel buildLocalSidebar() {
        JPanel sidebar = AppTheme.compactSurface(new BorderLayout(0, 12));
        sidebar.setPreferredSize(new java.awt.Dimension(170, 0));
        sidebar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppTheme.BORDER),
                new EmptyBorder(12, 10, 12, 10)));

        JPanel header = new JPanel(new BorderLayout(0, 4));
        header.setBackground(AppTheme.SURFACE);
        header.add(AppTheme.sectionTitle("Pemilik"), BorderLayout.NORTH);
        header.add(AppTheme.muted("Menu utama"), BorderLayout.CENTER);

        JPanel menu = new JPanel();
        menu.setBackground(AppTheme.SURFACE);
        menu.setLayout(new BoxLayout(menu, BoxLayout.Y_AXIS));
        addMenu(menu, "Dashboard");
        addMenu(menu, "Pantau Pesanan");
        addMenu(menu, "Kelola Karyawan");
        addMenu(menu, "Tarif Laundry");

        sidebar.add(header, BorderLayout.NORTH);
        sidebar.add(menu, BorderLayout.CENTER);
        if (!menuButtons.isEmpty()) {
            menuButtons.get(0).doClick();
        }
        return sidebar;
    }

    private void addMenu(JPanel menu, String name) {
        JButton button = localMenuButton(name);
        button.addActionListener(event -> {
            cardLayout.show(contentPanel, name);
            setActiveMenu(button);
        });
        menu.add(button);
        menu.add(Box.createVerticalStrut(8));
        menuButtons.add(button);
    }

    private JButton localMenuButton(String text) {
        JButton button = new JButton(text);
        AppTheme.styleLightMenuButton(button);
        button.setMaximumSize(new java.awt.Dimension(Integer.MAX_VALUE, 42));
        return button;
    }

    private void setActiveMenu(JButton activeButton) {
        for (JButton button : menuButtons) {
            AppTheme.setLightMenuButtonActive(button, button == activeButton);
        }
    }

    private JPanel buildDashboardPanel() {
        JPanel panel = AppTheme.page(new BorderLayout(12, 12));
        panel.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));
        JPanel header = AppTheme.surface(new BorderLayout(4, 4));
        header.add(AppTheme.sectionTitle("Dashboard Pemilik"), BorderLayout.NORTH);
        header.add(AppTheme.muted("Ringkasan performa transaksi dan laporan keuangan bulan berjalan."), BorderLayout.CENTER);

        JPanel metrics = new JPanel(new GridLayout(2, 3, 10, 10));
        metrics.setBackground(AppTheme.BACKGROUND);
        metrics.add(metricCard("Pesanan Aktif", aktifLabel));
        metrics.add(metricCard("Nilai Pesanan Aktif", pendapatanLabel));
        metrics.add(metricCard("Pendapatan Bulan Ini", pendapatanDiterimaLabel));
        metrics.add(metricCard("Total Item", itemLabel));
        metrics.add(metricCard("Total Pelanggan", pelangganLabel));

        laporanArea.setEditable(false);
        AppTheme.styleTextArea(laporanArea);
        JButton refreshButton = AppTheme.primaryButton("Refresh Dashboard");
        refreshButton.addActionListener(event -> refreshDashboard());
        JPanel top = new JPanel(new BorderLayout(0, 12));
        top.setBackground(AppTheme.BACKGROUND);
        top.add(header, BorderLayout.NORTH);
        top.add(metrics, BorderLayout.CENTER);
        panel.add(top, BorderLayout.NORTH);
        panel.add(AppTheme.scroll(laporanArea), BorderLayout.CENTER);
        panel.add(refreshButton, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel buildKaryawanPanel() {
        JPanel panel = AppTheme.page(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));
        UiUtil.applyTableStyle(karyawanTable);
        UiUtil.installSearch(karyawanTable, karyawanSearchField);
        JPanel form = AppTheme.surface(new BorderLayout(0, 12));
        form.add(AppTheme.sectionTitle("Kelola Karyawan"), BorderLayout.NORTH);

        JPanel fields = AppTheme.formGrid();
        AppTheme.addField(fields, 0, 0, "Username", usernameField);
        AppTheme.addField(fields, 0, 1, "Nama", namaField);
        AppTheme.addField(fields, 1, 0, "Telepon", teleponField);
        AppTheme.addField(fields, 1, 1, "Password", passwordField);
        AppTheme.addField(fields, 2, 0, "Shift", shiftField);
        AppTheme.addField(fields, 2, 1, "Cari", karyawanSearchField);

        JButton addButton = AppTheme.primaryButton("Tambah Karyawan");
        addButton.addActionListener(event -> addKaryawan());
        JButton deleteButton = AppTheme.dangerButton("Nonaktifkan");
        deleteButton.addActionListener(event -> deleteKaryawan());
        JButton refreshButton = AppTheme.secondaryButton("Refresh");
        refreshButton.addActionListener(event -> refreshKaryawan());
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

    private JPanel buildMonitoringPanel() {
        JPanel panel = AppTheme.page(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));
        UiUtil.applyTableStyle(activeOrderTable);
        UiUtil.applyTableStyle(historyOrderTable);
        UiUtil.installSearch(activeOrderTable, activeOrderSearchField);
        UiUtil.installSearch(historyOrderTable, historyOrderSearchField);

        JPanel header = AppTheme.surface(new BorderLayout(10, 6));
        JPanel title = new JPanel(new GridLayout(2, 1, 0, 3));
        title.setBackground(AppTheme.SURFACE);
        title.add(AppTheme.sectionTitle("Pantau Pesanan"));
        title.add(pesananSummaryLabel);
        JButton refreshButton = AppTheme.primaryButton("Refresh Pesanan");
        refreshButton.addActionListener(event -> refreshPesanan());
        header.add(title, BorderLayout.CENTER);
        header.add(refreshButton, BorderLayout.EAST);

        JPanel tables = new JPanel(new GridLayout(2, 1, 0, 10));
        tables.setBackground(AppTheme.BACKGROUND);
        tables.add(buildOrderSection("Pesanan Aktif", "Pesanan yang masih diproses atau siap diambil.",
                activeOrderTable, activeOrderSearchField));
        tables.add(buildOrderSection("Riwayat Pesanan", "Pesanan yang selesai atau dibatalkan.",
                historyOrderTable, historyOrderSearchField));

        panel.add(header, BorderLayout.NORTH);
        panel.add(tables, BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildOrderSection(String titleText, String subtitle, JTable table, JTextField searchField) {
        JPanel section = AppTheme.surface(new BorderLayout(0, 8));
        JPanel heading = new JPanel(new BorderLayout(12, 0));
        heading.setBackground(AppTheme.SURFACE);
        JPanel labels = new JPanel(new GridLayout(2, 1, 0, 2));
        labels.setBackground(AppTheme.SURFACE);
        labels.add(AppTheme.sectionTitle(titleText));
        labels.add(AppTheme.muted(subtitle));
        JPanel search = AppTheme.formGrid();
        AppTheme.addField(search, 0, 0, "Cari", searchField);
        heading.add(labels, BorderLayout.CENTER);
        heading.add(search, BorderLayout.EAST);
        section.add(heading, BorderLayout.NORTH);
        section.add(AppTheme.scroll(table), BorderLayout.CENTER);
        return section;
    }

    private JPanel buildTarifPanel() {
        JPanel panel = AppTheme.page(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));
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
        refreshButton.addActionListener(event -> refreshTarif());
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
        JLabel titleLabel = AppTheme.muted(title);
        valueLabel.setFont(AppTheme.TITLE_FONT.deriveFont(22f));
        valueLabel.setForeground(AppTheme.PRIMARY_DARK);
        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(valueLabel, BorderLayout.CENTER);
        return panel;
    }

    private void styleInputs() {
        AppTheme.styleTextField(usernameField);
        AppTheme.styleTextField(namaField);
        AppTheme.styleTextField(teleponField);
        AppTheme.styleTextField(passwordField);
        AppTheme.styleTextField(shiftField);
        AppTheme.styleTextField(karyawanSearchField);
        AppTheme.styleTextField(activeOrderSearchField);
        AppTheme.styleTextField(historyOrderSearchField);
        AppTheme.styleComboBox(tarifCombo);
        AppTheme.styleTextField(hargaTarifField);
    }

    private void refreshAll() {
        refreshDashboard();
        refreshPesanan();
        refreshKaryawan();
        refreshTarif();
    }

    private void refreshDashboard() {
        UiUtil.runAsync(this,
                () -> new DashboardData(dashboardController.getDataDasbor(), dashboardController.getLaporanBulanIni()),
                hasil -> {
            DataDasbor data = hasil.dataDasbor();
            LaporanKeuangan laporan = hasil.laporan();
            aktifLabel.setText(String.valueOf(data.getTotalPesananAktif()));
            pendapatanLabel.setText(UiUtil.money(data.getEstimasiPendapatan()));
            pendapatanDiterimaLabel.setText(UiUtil.money(data.getPendapatanDiterima()));
            itemLabel.setText(String.valueOf(data.getTotalItem()));
            pelangganLabel.setText(String.valueOf(data.getTotalPelanggan()));
            laporanArea.setText("Laporan bulan ini\n" + laporan.formatDataLaporan());
        }, "Gagal memuat dashboard.");
    }

    private void refreshKaryawan() {
        UiUtil.runAsync(this, penggunaController::getAllKaryawan, rows -> {
            karyawanModel.setRowCount(0);
            for (Karyawan karyawan : rows) {
                karyawanModel.addRow(new Object[] {
                        karyawan.getIdKaryawan(),
                        karyawan.getUsername(),
                        karyawan.getNamaLengkap(),
                        karyawan.getNomorTelepon(),
                        karyawan.getShiftKerja()
                });
            }
        }, "Gagal memuat data karyawan.");
    }

    private void refreshPesanan() {
        UiUtil.runAsync(this, pesananController::getAllPesanan, rows -> {
            activeOrderModel.setRowCount(0);
            historyOrderModel.setRowCount(0);
            int activeCount = 0;
            int historyCount = 0;
            for (Pesanan pesanan : rows) {
                if (isOrderHistory(pesanan)) {
                    historyOrderModel.addRow(toOrderRow(pesanan));
                    historyCount++;
                } else {
                    activeOrderModel.addRow(toOrderRow(pesanan));
                    activeCount++;
                }
            }
            pesananSummaryLabel.setText(activeCount + " pesanan aktif dan " + historyCount
                    + " pesanan dalam riwayat.");
        }, "Gagal memuat data pesanan untuk pemilik.");
    }

    private Object[] toOrderRow(Pesanan pesanan) {
        return new Object[] {
                pesanan.getIdPesanan(),
                pesanan.getNamaPelanggan(),
                pesanan.getNamaKaryawan() == null ? "-" : pesanan.getNamaKaryawan(),
                pesanan.getTanggalMasuk(),
                pesanan.getEstimasiSelesai(),
                pesanan.getPaketLaundry().getDisplayName(),
                String.format("%.2f kg", pesanan.getBeratKg()),
                pesanan.getStatusPesanan().getDisplayName(),
                UiUtil.money(pesanan.getTotalBiaya()),
                pesanan.getCatatan()
        };
    }

    private boolean isOrderHistory(Pesanan pesanan) {
        return pesanan.getStatusPesanan() == StatusPesanan.SELESAI
                || pesanan.getStatusPesanan() == StatusPesanan.DIBATALKAN;
    }

    private void refreshTarif() {
        TarifLaundry selectedTarif = (TarifLaundry) tarifCombo.getSelectedItem();
        UiUtil.runAsync(this, tarifController::getSemuaTarif, rows -> {
            tarifModel.setRowCount(0);
            tarifCombo.removeAllItems();
            for (TarifLaundry tarif : rows) {
                tarifModel.addRow(new Object[] {
                        tarif.getIdTarif(),
                        tarif.getNamaPaket(),
                        tarif.getEstimasiHari() + " hari",
                        UiUtil.money(tarif.getHargaPerKg()),
                        tarif.isAktif() ? "Aktif" : "Nonaktif"
                });
                tarifCombo.addItem(tarif);
                if (selectedTarif != null && tarif.getPaketLaundry() == selectedTarif.getPaketLaundry()) {
                    tarifCombo.setSelectedItem(tarif);
                }
            }
            tarifSelected();
        }, "Gagal memuat tarif laundry.");
    }

    private void addKaryawan() {
        String username = usernameField.getText().trim();
        String nama = namaField.getText().trim();
        String telepon = teleponField.getText().trim();
        String password = new String(passwordField.getPassword());
        String shift = shiftField.getText().trim();
        UiUtil.runAsync(this, () -> {
            penggunaController.tambahKaryawan(
                    username, nama, telepon, password, shift);
            return null;
        }, ignored -> {
            usernameField.setText("");
            namaField.setText("");
            teleponField.setText("");
            passwordField.setText("");
            shiftField.setText("Pagi");
            refreshKaryawan();
            UiUtil.info(this, "Akun karyawan berhasil dibuat.");
        }, "Gagal menambah karyawan.");
    }

    private void deleteKaryawan() {
        String idKaryawan = UiUtil.selectedId(karyawanTable);
        if (idKaryawan == null) {
            UiUtil.info(this, "Pilih karyawan yang akan dinonaktifkan.");
            return;
        }
        if (!UiUtil.confirm(this, "Nonaktifkan akun karyawan " + idKaryawan + "?")) {
            return;
        }
        UiUtil.runAsync(this, () -> {
            penggunaController.hapusKaryawan(idKaryawan);
            return null;
        }, ignored -> {
            refreshKaryawan();
        }, "Gagal menonaktifkan karyawan.");
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
            UiUtil.info(this, "Pilih paket laundry yang akan diubah.");
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
            refreshDashboard();
            UiUtil.info(this, "Harga " + tarif.getNamaPaket() + " berhasil diperbarui.");
        }, "Gagal memperbarui tarif laundry.");
    }

    private record DashboardData(DataDasbor dataDasbor, LaporanKeuangan laporan) {
    }
}
