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
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.DefaultTableModel;
import silaundry.controller.ItemController;
import silaundry.controller.PembayaranController;
import silaundry.controller.PenggunaController;
import silaundry.controller.PesananController;
import silaundry.model.ItemPakaian;
import silaundry.model.Karyawan;
import silaundry.model.Pelanggan;
import silaundry.model.Pesanan;
import silaundry.model.enums.KategoriWarna;
import silaundry.model.enums.StatusPembayaran;
import silaundry.model.enums.StatusPesanan;

public class KaryawanPanel extends JPanel {
    private final Karyawan karyawan;
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel contentPanel = new JPanel(cardLayout);
    private final List<JButton> menuButtons = new ArrayList<>();
    private final PesananController pesananController = new PesananController();
    private final PenggunaController penggunaController = new PenggunaController();
    private final ItemController itemController = new ItemController();
    private final PembayaranController pembayaranController = new PembayaranController();

    private final DefaultTableModel orderModel = UiUtil.model("ID", "Pelanggan", "Tanggal", "Estimasi", "Status", "Total", "Catatan");
    private final JTable orderTable = new JTable(orderModel);
    private final JComboBox<Pelanggan> pelangganCombo = new JComboBox<>();
    private final JComboBox<StatusPesanan> statusCombo = new JComboBox<>(StatusPesanan.values());
    private final JSpinner estimasiSpinner = new JSpinner(new SpinnerNumberModel(2, 1, 14, 1));
    private final JTextField totalField = new JTextField("30000", 9);
    private final JTextField catatanField = new JTextField(22);

    private final DefaultTableModel itemModel = UiUtil.model("ID", "Pesanan", "Jenis", "Warna", "Kondisi", "Smart Group", "Kode QR");
    private final JTable itemTable = new JTable(itemModel);
    private final JTextField itemOrderField = new JTextField(14);
    private final JTextField jenisField = new JTextField("Kaos", 12);
    private final JComboBox<KategoriWarna> warnaCombo = new JComboBox<>(KategoriWarna.values());
    private final JTextField kondisiField = new JTextField("Normal", 14);

    private final JTextField paymentOrderField = new JTextField(14);
    private final JTextField paymentMethodField = new JTextField("Tunai", 10);
    private final JTextField paymentAmountField = new JTextField("30000", 10);
    private final JComboBox<StatusPembayaran> paymentStatusCombo = new JComboBox<>(StatusPembayaran.values());

    public KaryawanPanel(Karyawan karyawan) {
        this.karyawan = karyawan;
        setLayout(new BorderLayout());
        setBackground(AppTheme.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));
        styleInputs();
        contentPanel.setBackground(AppTheme.BACKGROUND);
        contentPanel.add(buildOrderPanel(), "Pesanan");
        contentPanel.add(buildItemPanel(), "Item Pakaian");
        contentPanel.add(buildPaymentPanel(), "Pembayaran");
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
        header.add(AppTheme.sectionTitle("Operasional"), BorderLayout.NORTH);
        header.add(AppTheme.muted("Menu karyawan"), BorderLayout.CENTER);

        JPanel menu = new JPanel();
        menu.setBackground(AppTheme.SURFACE);
        menu.setLayout(new BoxLayout(menu, BoxLayout.Y_AXIS));
        addMenu(menu, "Pesanan");
        addMenu(menu, "Item Pakaian");
        addMenu(menu, "Pembayaran");

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

    private JPanel buildOrderPanel() {
        JPanel panel = AppTheme.page(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));
        UiUtil.applyTableStyle(orderTable);
        orderTable.getSelectionModel().addListSelectionListener(this::orderSelected);
        panel.add(buildOrderForm(), BorderLayout.NORTH);
        panel.add(AppTheme.scroll(orderTable), BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildOrderForm() {
        JPanel container = AppTheme.surface(new GridLayout(2, 1, 0, 8));
        JPanel title = new JPanel(new FlowLayout(FlowLayout.LEFT));
        title.setBackground(AppTheme.SURFACE);
        title.add(AppTheme.sectionTitle("Manajemen Pesanan"));
        title.add(AppTheme.muted("Karyawan: " + karyawan.getNamaLengkap() + " | Shift " + karyawan.getShiftKerja()));

        JPanel form = new JPanel(new FlowLayout(FlowLayout.LEFT));
        form.setBackground(AppTheme.SURFACE);
        form.add(new JLabel("Pelanggan"));
        form.add(pelangganCombo);
        form.add(new JLabel("Estimasi"));
        form.add(estimasiSpinner);
        form.add(new JLabel("Total"));
        form.add(totalField);
        form.add(new JLabel("Catatan"));
        form.add(catatanField);

        JButton createButton = AppTheme.primaryButton("Tambah");
        createButton.addActionListener(event -> createOrder());
        JButton statusButton = AppTheme.secondaryButton("Update Status");
        statusButton.addActionListener(event -> updateStatus());
        JButton deleteButton = AppTheme.dangerButton("Hapus");
        deleteButton.addActionListener(event -> deleteOrder());
        JButton refreshButton = AppTheme.secondaryButton("Refresh");
        refreshButton.addActionListener(event -> refreshAll());

        form.add(statusCombo);
        form.add(createButton);
        form.add(statusButton);
        form.add(deleteButton);
        form.add(refreshButton);

        container.add(title);
        container.add(form);
        return container;
    }

    private JPanel buildItemPanel() {
        JPanel panel = AppTheme.page(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));
        UiUtil.applyTableStyle(itemTable);
        JPanel form = AppTheme.surface(new FlowLayout(FlowLayout.LEFT));
        form.add(AppTheme.sectionTitle("Data Item"));
        form.add(new JLabel("ID Pesanan"));
        form.add(itemOrderField);
        form.add(new JLabel("Jenis"));
        form.add(jenisField);
        form.add(new JLabel("Warna"));
        form.add(warnaCombo);
        form.add(new JLabel("Kondisi"));
        form.add(kondisiField);

        JButton addButton = AppTheme.primaryButton("Tambah Item");
        addButton.addActionListener(event -> addItem());
        JButton groupButton = AppTheme.secondaryButton("Smart Grouping");
        groupButton.addActionListener(event -> runSmartGrouping());
        JButton deleteButton = AppTheme.dangerButton("Hapus Item");
        deleteButton.addActionListener(event -> deleteItem());
        JButton refreshButton = AppTheme.secondaryButton("Refresh");
        refreshButton.addActionListener(event -> refreshItems());

        form.add(addButton);
        form.add(groupButton);
        form.add(deleteButton);
        form.add(refreshButton);
        panel.add(form, BorderLayout.NORTH);
        panel.add(AppTheme.scroll(itemTable), BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildPaymentPanel() {
        JPanel page = AppTheme.page(new BorderLayout());
        page.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));
        JPanel panel = AppTheme.surface(new FlowLayout(FlowLayout.LEFT));
        panel.add(AppTheme.sectionTitle("Pembayaran"));
        panel.add(new JLabel("ID Pesanan"));
        panel.add(paymentOrderField);
        panel.add(new JLabel("Metode"));
        panel.add(paymentMethodField);
        panel.add(new JLabel("Jumlah"));
        panel.add(paymentAmountField);
        panel.add(paymentStatusCombo);
        JButton fromSelectionButton = AppTheme.secondaryButton("Ambil Pesanan Terpilih");
        fromSelectionButton.addActionListener(event -> copySelectedOrderToPayment());
        JButton saveButton = AppTheme.primaryButton("Simpan Pembayaran");
        saveButton.addActionListener(event -> savePayment());
        panel.add(fromSelectionButton);
        panel.add(saveButton);
        page.add(panel, BorderLayout.NORTH);
        page.add(AppTheme.muted("Pilih pesanan di menu Pesanan untuk mengisi ID otomatis, lalu simpan status pembayaran."), BorderLayout.CENTER);
        return page;
    }

    private void styleInputs() {
        AppTheme.styleComboBox(pelangganCombo);
        AppTheme.styleComboBox(statusCombo);
        AppTheme.styleComboBox(warnaCombo);
        AppTheme.styleComboBox(paymentStatusCombo);
        AppTheme.styleSpinner(estimasiSpinner);
        AppTheme.styleTextField(totalField);
        AppTheme.styleTextField(catatanField);
        AppTheme.styleTextField(itemOrderField);
        AppTheme.styleTextField(jenisField);
        AppTheme.styleTextField(kondisiField);
        AppTheme.styleTextField(paymentOrderField);
        AppTheme.styleTextField(paymentMethodField);
        AppTheme.styleTextField(paymentAmountField);
    }

    private void refreshAll() {
        refreshCustomers();
        refreshOrders();
        refreshItems();
    }

    private void refreshCustomers() {
        pelangganCombo.removeAllItems();
        try {
            for (Pelanggan pelanggan : penggunaController.getAllPelanggan()) {
                pelangganCombo.addItem(pelanggan);
            }
        } catch (SQLException ex) {
            UiUtil.error(this, "Gagal memuat pelanggan.", ex);
        }
    }

    private void refreshOrders() {
        orderModel.setRowCount(0);
        try {
            List<Pesanan> rows = pesananController.getAllPesanan();
            for (Pesanan pesanan : rows) {
                orderModel.addRow(new Object[] {
                        pesanan.getIdPesanan(),
                        pesanan.getNamaPelanggan(),
                        pesanan.getTanggalMasuk(),
                        pesanan.getEstimasiSelesai(),
                        pesanan.getStatusPesanan().getDisplayName(),
                        UiUtil.money(pesanan.getTotalBiaya()),
                        pesanan.getCatatan()
                });
            }
        } catch (SQLException ex) {
            UiUtil.error(this, "Gagal memuat pesanan.", ex);
        }
    }

    private void refreshItems() {
        itemModel.setRowCount(0);
        try {
            for (ItemPakaian item : itemController.getAllItems()) {
                itemModel.addRow(new Object[] {
                        item.getIdItem(),
                        item.getIdPesanan(),
                        item.getJenisPakaian(),
                        item.getKategoriWarna().getDisplayName(),
                        item.getKondisiAwal(),
                        item.getLabelSmartGroup(),
                        item.getKodeQR()
                });
            }
        } catch (SQLException ex) {
            UiUtil.error(this, "Gagal memuat item pakaian.", ex);
        }
    }

    private void createOrder() {
        Pelanggan pelanggan = (Pelanggan) pelangganCombo.getSelectedItem();
        if (pelanggan == null) {
            UiUtil.info(this, "Pilih pelanggan terlebih dahulu.");
            return;
        }
        try {
            pesananController.tambahPesanan(
                    pelanggan.getIdPelanggan(),
                    karyawan.getIdKaryawan(),
                    (Integer) estimasiSpinner.getValue(),
                    Double.parseDouble(totalField.getText().trim()),
                    catatanField.getText().trim());
            catatanField.setText("");
            refreshOrders();
        } catch (NumberFormatException ex) {
            UiUtil.info(this, "Total biaya harus berupa angka.");
        } catch (SQLException ex) {
            UiUtil.error(this, "Gagal menambah pesanan.", ex);
        }
    }

    private void updateStatus() {
        String idPesanan = UiUtil.selectedId(orderTable);
        if (idPesanan == null) {
            UiUtil.info(this, "Pilih pesanan yang akan diupdate.");
            return;
        }
        try {
            pesananController.updateStatus(idPesanan, (StatusPesanan) statusCombo.getSelectedItem());
            refreshOrders();
        } catch (SQLException ex) {
            UiUtil.error(this, "Gagal update status pesanan.", ex);
        }
    }

    private void deleteOrder() {
        String idPesanan = UiUtil.selectedId(orderTable);
        if (idPesanan == null) {
            UiUtil.info(this, "Pilih pesanan yang akan dihapus.");
            return;
        }
        try {
            pesananController.hapusPesanan(idPesanan);
            refreshOrders();
            refreshItems();
        } catch (SQLException ex) {
            UiUtil.error(this, "Gagal menghapus pesanan.", ex);
        }
    }

    private void addItem() {
        String idPesanan = itemOrderField.getText().trim();
        if (idPesanan.isEmpty()) {
            UiUtil.info(this, "Isi ID pesanan untuk item.");
            return;
        }
        try {
            itemController.tambahItem(
                    idPesanan,
                    jenisField.getText().trim(),
                    (KategoriWarna) warnaCombo.getSelectedItem(),
                    kondisiField.getText().trim());
            refreshItems();
        } catch (SQLException ex) {
            UiUtil.error(this, "Gagal menambah item pakaian.", ex);
        }
    }

    private void runSmartGrouping() {
        String idPesanan = itemOrderField.getText().trim();
        if (idPesanan.isEmpty()) {
            UiUtil.info(this, "Isi ID pesanan untuk smart grouping.");
            return;
        }
        try {
            int count = itemController.jalankanSmartGrouping(idPesanan);
            refreshItems();
            UiUtil.info(this, "Smart grouping diterapkan ke " + count + " item.");
        } catch (SQLException ex) {
            UiUtil.error(this, "Gagal menjalankan smart grouping.", ex);
        }
    }

    private void deleteItem() {
        String idItem = UiUtil.selectedId(itemTable);
        if (idItem == null) {
            UiUtil.info(this, "Pilih item yang akan dihapus.");
            return;
        }
        try {
            itemController.hapusItem(idItem);
            refreshItems();
        } catch (SQLException ex) {
            UiUtil.error(this, "Gagal menghapus item.", ex);
        }
    }

    private void copySelectedOrderToPayment() {
        String idPesanan = UiUtil.selectedId(orderTable);
        if (idPesanan == null) {
            UiUtil.info(this, "Pilih pesanan dari tab Pesanan.");
            return;
        }
        paymentOrderField.setText(idPesanan);
        itemOrderField.setText(idPesanan);
    }

    private void savePayment() {
        String idPesanan = paymentOrderField.getText().trim();
        if (idPesanan.isEmpty()) {
            UiUtil.info(this, "Isi ID pesanan untuk pembayaran.");
            return;
        }
        try {
            pembayaranController.simpanPembayaran(
                    idPesanan,
                    paymentMethodField.getText().trim(),
                    Double.parseDouble(paymentAmountField.getText().trim()),
                    (StatusPembayaran) paymentStatusCombo.getSelectedItem());
            UiUtil.info(this, "Pembayaran tersimpan.");
        } catch (NumberFormatException ex) {
            UiUtil.info(this, "Jumlah pembayaran harus berupa angka.");
        } catch (SQLException ex) {
            UiUtil.error(this, "Gagal menyimpan pembayaran.", ex);
        }
    }

    private void orderSelected(ListSelectionEvent event) {
        if (event.getValueIsAdjusting()) {
            return;
        }
        String idPesanan = UiUtil.selectedId(orderTable);
        if (idPesanan != null) {
            itemOrderField.setText(idPesanan);
            paymentOrderField.setText(idPesanan);
        }
    }
}
