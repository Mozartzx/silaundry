package silaundry.view;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import silaundry.controller.DashboardController;
import silaundry.controller.PenggunaController;
import silaundry.model.DataDasbor;
import silaundry.model.Karyawan;
import silaundry.model.LaporanKeuangan;

public class PemilikPanel extends JPanel {
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel contentPanel = new JPanel(cardLayout);
    private final List<JButton> menuButtons = new ArrayList<>();
    private final DashboardController dashboardController = new DashboardController();
    private final PenggunaController penggunaController = new PenggunaController();
    private final JLabel aktifLabel = new JLabel("-");
    private final JLabel pendapatanLabel = new JLabel("-");
    private final JLabel itemLabel = new JLabel("-");
    private final JLabel pelangganLabel = new JLabel("-");
    private final JTextArea laporanArea = new JTextArea(8, 60);
    private final DefaultTableModel karyawanModel = UiUtil.model("ID", "Username", "Nama", "Telepon", "Shift");
    private final JTable karyawanTable = new JTable(karyawanModel);
    private final JTextField usernameField = new JTextField(10);
    private final JTextField namaField = new JTextField(16);
    private final JTextField teleponField = new JTextField(10);
    private final JTextField passwordField = new JTextField("karyawan123", 10);
    private final JTextField shiftField = new JTextField("Pagi", 8);

    public PemilikPanel() {
        setLayout(new BorderLayout());
        setBackground(AppTheme.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));
        styleInputs();
        contentPanel.setBackground(AppTheme.BACKGROUND);
        contentPanel.add(buildDashboardPanel(), "Dashboard");
        contentPanel.add(buildKaryawanPanel(), "Kelola Karyawan");
        add(buildLocalSidebar(), BorderLayout.WEST);
        add(contentPanel, BorderLayout.CENTER);
        refreshAll();
    }

    private JPanel buildLocalSidebar() {
        JPanel sidebar = AppTheme.compactSurface(new BorderLayout(0, 12));
        sidebar.setPreferredSize(new java.awt.Dimension(190, 0));
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
        addMenu(menu, "Kelola Karyawan");

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
        button.setHorizontalAlignment(JButton.LEFT);
        button.setFocusPainted(false);
        button.setBackground(AppTheme.SURFACE);
        button.setForeground(AppTheme.TEXT);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppTheme.BORDER),
                new EmptyBorder(10, 10, 10, 10)));
        button.setMaximumSize(new java.awt.Dimension(Integer.MAX_VALUE, 42));
        return button;
    }

    private void setActiveMenu(JButton activeButton) {
        for (JButton button : menuButtons) {
            boolean active = button == activeButton;
            button.setBackground(active ? new Color(217, 237, 239) : AppTheme.SURFACE);
            button.setForeground(active ? AppTheme.PRIMARY_DARK : AppTheme.TEXT);
        }
    }

    private JPanel buildDashboardPanel() {
        JPanel panel = AppTheme.page(new BorderLayout(12, 12));
        panel.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));
        JPanel header = AppTheme.surface(new BorderLayout(4, 4));
        header.add(AppTheme.sectionTitle("Dashboard Pemilik"), BorderLayout.NORTH);
        header.add(AppTheme.muted("Ringkasan performa transaksi dan laporan keuangan bulan berjalan."), BorderLayout.CENTER);

        JPanel metrics = new JPanel(new GridLayout(1, 4, 10, 10));
        metrics.setBackground(AppTheme.BACKGROUND);
        metrics.add(metricCard("Pesanan Aktif", aktifLabel));
        metrics.add(metricCard("Estimasi Pendapatan", pendapatanLabel));
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
        JPanel form = AppTheme.surface(new FlowLayout(FlowLayout.LEFT));
        form.add(AppTheme.sectionTitle("Kelola Karyawan"));
        form.add(new JLabel("Username"));
        form.add(usernameField);
        form.add(new JLabel("Nama"));
        form.add(namaField);
        form.add(new JLabel("Telepon"));
        form.add(teleponField);
        form.add(new JLabel("Password"));
        form.add(passwordField);
        form.add(new JLabel("Shift"));
        form.add(shiftField);
        JButton addButton = AppTheme.primaryButton("Tambah Karyawan");
        addButton.addActionListener(event -> addKaryawan());
        JButton deleteButton = AppTheme.dangerButton("Nonaktifkan");
        deleteButton.addActionListener(event -> deleteKaryawan());
        JButton refreshButton = AppTheme.secondaryButton("Refresh");
        refreshButton.addActionListener(event -> refreshKaryawan());
        form.add(addButton);
        form.add(deleteButton);
        form.add(refreshButton);

        panel.add(form, BorderLayout.NORTH);
        panel.add(AppTheme.scroll(karyawanTable), BorderLayout.CENTER);
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
    }

    private void refreshAll() {
        refreshDashboard();
        refreshKaryawan();
    }

    private void refreshDashboard() {
        try {
            DataDasbor data = dashboardController.getDataDasbor();
            LaporanKeuangan laporan = dashboardController.getLaporanBulanIni();
            aktifLabel.setText(String.valueOf(data.getTotalPesananAktif()));
            pendapatanLabel.setText(UiUtil.money(data.getEstimasiPendapatan()));
            itemLabel.setText(String.valueOf(data.getTotalItem()));
            pelangganLabel.setText(String.valueOf(data.getTotalPelanggan()));
            laporanArea.setText("Laporan bulan ini\n" + laporan.formatDataLaporan());
        } catch (SQLException ex) {
            UiUtil.error(this, "Gagal memuat dashboard.", ex);
        }
    }

    private void refreshKaryawan() {
        karyawanModel.setRowCount(0);
        try {
            for (Karyawan karyawan : penggunaController.getAllKaryawan()) {
                karyawanModel.addRow(new Object[] {
                        karyawan.getIdKaryawan(),
                        karyawan.getUsername(),
                        karyawan.getNamaLengkap(),
                        karyawan.getNomorTelepon(),
                        karyawan.getShiftKerja()
                });
            }
        } catch (SQLException ex) {
            UiUtil.error(this, "Gagal memuat data karyawan.", ex);
        }
    }

    private void addKaryawan() {
        if (usernameField.getText().trim().isEmpty() || namaField.getText().trim().isEmpty()) {
            UiUtil.info(this, "Username dan nama karyawan wajib diisi.");
            return;
        }
        try {
            penggunaController.tambahKaryawan(
                    usernameField.getText().trim(),
                    namaField.getText().trim(),
                    teleponField.getText().trim(),
                    passwordField.getText().trim(),
                    shiftField.getText().trim());
            usernameField.setText("");
            namaField.setText("");
            teleponField.setText("");
            refreshKaryawan();
        } catch (SQLException ex) {
            UiUtil.error(this, "Gagal menambah karyawan.", ex);
        }
    }

    private void deleteKaryawan() {
        String idKaryawan = UiUtil.selectedId(karyawanTable);
        if (idKaryawan == null) {
            UiUtil.info(this, "Pilih karyawan yang akan dinonaktifkan.");
            return;
        }
        try {
            penggunaController.hapusKaryawan(idKaryawan);
            refreshKaryawan();
        } catch (SQLException ex) {
            UiUtil.error(this, "Gagal menonaktifkan karyawan.", ex);
        }
    }
}
