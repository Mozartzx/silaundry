package silaundry.view;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
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
import silaundry.controller.NotifikasiController;
import silaundry.controller.PembayaranController;
import silaundry.controller.PenggunaController;
import silaundry.controller.PesananController;
import silaundry.controller.TarifController;
import silaundry.model.ItemPakaian;
import silaundry.model.Karyawan;
import silaundry.model.Pelanggan;
import silaundry.model.Pesanan;
import silaundry.model.TarifLaundry;
import silaundry.model.enums.KategoriWarna;
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
    private final TarifController tarifController = new TarifController();
    private final NotifikasiController notifikasiController = new NotifikasiController();

    private final DefaultTableModel orderModel = UiUtil.model("ID", "Pelanggan", "Tanggal", "Estimasi", "Paket",
            "Berat", "Harga/kg", "Status", "Total", "Catatan");
    private final JTable orderTable = new JTable(orderModel);
    private final JTextField orderSearchField = new JTextField(18);
    private final JComboBox<Pelanggan> pelangganCombo = new JComboBox<>();
    private final JComboBox<StatusPesanan> statusCombo = new JComboBox<>(StatusPesanan.values());
    private final JComboBox<TarifLaundry> tarifCombo = new JComboBox<>();
    private final JSpinner beratSpinner = new JSpinner(new SpinnerNumberModel(3.0, 0.1, 100.0, 0.1));
    private final JLabel totalPreviewLabel = AppTheme.muted("Total otomatis: -");
    private final JTextField catatanField = new JTextField(22);

    private final DefaultTableModel itemModel = UiUtil.model("ID", "Pesanan", "Jenis", "Warna", "Kondisi",
            "Deskripsi Detail", "Smart Group", "Kode QR");
    private final JTable itemTable = new JTable(itemModel);
    private final JTextField itemSearchField = new JTextField(18);
    private final JTextField itemOrderField = new JTextField(14);
    private final JTextField jenisField = new JTextField(12);
    private final JComboBox<KategoriWarna> warnaCombo = new JComboBox<>(KategoriWarna.values());
    private final JTextField kondisiField = new JTextField("Normal", 14);
    private final JTextField deskripsiField = new JTextField(24);

    private final JTextField paymentOrderField = new JTextField(14);
    private final JComboBox<String> paymentMethodCombo = new JComboBox<>(new String[] { "Tunai", "QRIS", "Transfer" });
    private final JTextField paymentAmountField = new JTextField(10);
    private final JTextField paymentStatusField = new JTextField("Otomatis", 10);
    private final JLabel paymentSummaryLabel = AppTheme.muted("Total dibayar: - | Sisa tagihan: -");

    public KaryawanPanel(Karyawan karyawan) {
        this.karyawan = karyawan;
        setLayout(new BorderLayout());
        setBackground(AppTheme.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));
        styleInputs();
        itemOrderField.setEditable(false);
        paymentOrderField.setEditable(false);
        paymentStatusField.setEditable(false);
        tarifCombo.addActionListener(event -> updateTotalPreview());
        beratSpinner.addChangeListener(event -> updateTotalPreview());
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
        sidebar.setPreferredSize(new java.awt.Dimension(170, 0));
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
        AppTheme.styleLightMenuButton(button);
        button.setMaximumSize(new java.awt.Dimension(Integer.MAX_VALUE, 42));
        return button;
    }

    private void setActiveMenu(JButton activeButton) {
        for (JButton button : menuButtons) {
            AppTheme.setLightMenuButtonActive(button, button == activeButton);
        }
    }

    private JPanel buildOrderPanel() {
        JPanel panel = AppTheme.page(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));
        UiUtil.applyTableStyle(orderTable);
        UiUtil.installSearch(orderTable, orderSearchField);
        orderTable.getSelectionModel().addListSelectionListener(this::orderSelected);
        panel.add(buildOrderForm(), BorderLayout.NORTH);
        panel.add(AppTheme.scroll(orderTable), BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildOrderForm() {
        JPanel container = AppTheme.surface(new BorderLayout(0, 12));
        JPanel title = new JPanel(new BorderLayout(0, 4));
        title.setBackground(AppTheme.SURFACE);
        title.add(AppTheme.sectionTitle("Manajemen Pesanan"), BorderLayout.NORTH);
        title.add(AppTheme.muted("Karyawan: " + karyawan.getNamaLengkap() + " | Shift " + karyawan.getShiftKerja()),
                BorderLayout.CENTER);

        JPanel fields = AppTheme.formGrid();
        AppTheme.addField(fields, 0, 0, "Pelanggan", pelangganCombo);
        AppTheme.addField(fields, 0, 1, "Paket", tarifCombo);
        AppTheme.addField(fields, 1, 0, "Berat kg", beratSpinner);
        AppTheme.addField(fields, 1, 1, "Ubah status menjadi", statusCombo);
        AppTheme.addField(fields, 2, 0, "Total otomatis", totalPreviewLabel);
        AppTheme.addWideField(fields, 3, "Catatan", catatanField);
        AppTheme.addWideField(fields, 4, "Cari pesanan", orderSearchField);

        JButton createButton = AppTheme.primaryButton("Tambah");
        createButton.addActionListener(event -> createOrder());
        JButton statusButton = AppTheme.secondaryButton("Update Status");
        statusButton.addActionListener(event -> updateStatus());
        JButton whatsappButton = AppTheme.secondaryButton("Template WhatsApp");
        whatsappButton.addActionListener(event -> buatTemplateWhatsApp());
        JButton deleteButton = AppTheme.dangerButton("Batalkan Pesanan");
        deleteButton.addActionListener(event -> cancelOrder());
        JButton refreshButton = AppTheme.secondaryButton("Refresh");
        refreshButton.addActionListener(event -> refreshAll());

        JPanel actions = AppTheme.actionRow();
        actions.add(createButton);
        actions.add(statusButton);
        actions.add(whatsappButton);
        actions.add(deleteButton);
        actions.add(refreshButton);

        container.add(title, BorderLayout.NORTH);
        container.add(fields, BorderLayout.CENTER);
        container.add(actions, BorderLayout.SOUTH);
        return container;
    }

    private JPanel buildItemPanel() {
        JPanel panel = AppTheme.page(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));
        UiUtil.applyTableStyle(itemTable);
        UiUtil.installSearch(itemTable, itemSearchField);
        JPanel form = AppTheme.surface(new BorderLayout(0, 12));
        JPanel itemTitle = new JPanel(new BorderLayout(0, 4));
        itemTitle.setBackground(AppTheme.SURFACE);
        itemTitle.add(AppTheme.sectionTitle("Data Item"), BorderLayout.NORTH);
        itemTitle.add(AppTheme.muted(
                "Isi ciri pakaian secara spesifik, lalu kelompokkan berdasarkan warna sebelum proses pencucian."),
                BorderLayout.CENTER);
        form.add(itemTitle, BorderLayout.NORTH);

        JPanel fields = AppTheme.formGrid();
        AppTheme.addField(fields, 0, 0, "ID Pesanan", itemOrderField);
        AppTheme.addField(fields, 0, 1, "Jenis", jenisField);
        AppTheme.addField(fields, 1, 0, "Warna", warnaCombo);
        AppTheme.addField(fields, 1, 1, "Kondisi", kondisiField);
        AppTheme.addWideField(fields, 2, "Deskripsi", deskripsiField);
        AppTheme.addWideField(fields, 3, "Cari item", itemSearchField);

        JButton addButton = AppTheme.primaryButton("Tambah Item");
        addButton.addActionListener(event -> addItem());
        JButton groupButton = AppTheme.secondaryButton("Kelompokkan Warna");
        groupButton.addActionListener(event -> runSmartGrouping());
        JButton deleteButton = AppTheme.dangerButton("Hapus Item");
        deleteButton.addActionListener(event -> deleteItem());
        JButton refreshButton = AppTheme.secondaryButton("Refresh");
        refreshButton.addActionListener(event -> refreshItems());

        JPanel actions = AppTheme.actionRow();
        actions.add(addButton);
        actions.add(groupButton);
        actions.add(deleteButton);
        actions.add(refreshButton);
        form.add(fields, BorderLayout.CENTER);
        form.add(actions, BorderLayout.SOUTH);
        panel.add(form, BorderLayout.NORTH);
        panel.add(AppTheme.scroll(itemTable), BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildPaymentPanel() {
        JPanel page = AppTheme.page(new BorderLayout());
        page.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));
        JPanel panel = AppTheme.surface(new BorderLayout(0, 12));
        panel.add(AppTheme.sectionTitle("Pembayaran"), BorderLayout.NORTH);

        JPanel fields = AppTheme.formGrid();
        AppTheme.addField(fields, 0, 0, "ID Pesanan", paymentOrderField);
        AppTheme.addField(fields, 0, 1, "Metode", paymentMethodCombo);
        AppTheme.addField(fields, 1, 0, "Bayar sekarang", paymentAmountField);
        AppTheme.addField(fields, 1, 1, "Status otomatis", paymentStatusField);
        AppTheme.addWideField(fields, 2, "Ringkasan", paymentSummaryLabel);

        JButton fromSelectionButton = AppTheme.secondaryButton("Ambil Pesanan Terpilih");
        fromSelectionButton.addActionListener(event -> copySelectedOrderToPayment());
        JButton saveButton = AppTheme.primaryButton("Simpan Pembayaran");
        saveButton.addActionListener(event -> savePayment());
        JPanel actions = AppTheme.actionRow();
        actions.add(fromSelectionButton);
        actions.add(saveButton);
        panel.add(fields, BorderLayout.CENTER);
        panel.add(actions, BorderLayout.SOUTH);
        page.add(panel, BorderLayout.NORTH);
        page.add(AppTheme.muted(
                "Nominal yang dimasukkan adalah pembayaran baru. Sistem akan menambahkannya ke total pembayaran sebelumnya."),
                BorderLayout.CENTER);
        return page;
    }

    private void styleInputs() {
        AppTheme.styleComboBox(pelangganCombo);
        AppTheme.styleComboBox(statusCombo);
        AppTheme.styleComboBox(tarifCombo);
        AppTheme.styleComboBox(warnaCombo);
        AppTheme.styleComboBox(paymentMethodCombo);
        AppTheme.styleSpinner(beratSpinner);
        AppTheme.styleTextField(catatanField);
        AppTheme.styleTextField(itemOrderField);
        AppTheme.styleTextField(jenisField);
        AppTheme.styleTextField(kondisiField);
        AppTheme.styleTextField(deskripsiField);
        AppTheme.styleTextField(paymentOrderField);
        AppTheme.styleTextField(paymentAmountField);
        AppTheme.styleTextField(paymentStatusField);
        AppTheme.styleTextField(orderSearchField);
        AppTheme.styleTextField(itemSearchField);
    }

    private void refreshAll() {
        refreshCustomers();
        refreshTarif();
        refreshOrders();
        refreshItems();
    }

    private void refreshTarif() {
        TarifLaundry selectedTarif = (TarifLaundry) tarifCombo.getSelectedItem();
        UiUtil.runAsync(this, tarifController::getTarifAktif, rows -> {
            tarifCombo.removeAllItems();
            for (TarifLaundry tarif : rows) {
                tarifCombo.addItem(tarif);
                if (selectedTarif != null && tarif.getPaketLaundry() == selectedTarif.getPaketLaundry()) {
                    tarifCombo.setSelectedItem(tarif);
                }
            }
            updateTotalPreview();
        }, "Gagal memuat tarif laundry.");
    }

    private void refreshCustomers() {
        UiUtil.runAsync(this, penggunaController::getAllPelanggan, rows -> {
            pelangganCombo.removeAllItems();
            for (Pelanggan pelanggan : rows) {
                pelangganCombo.addItem(pelanggan);
            }
        }, "Gagal memuat pelanggan.");
    }

    private void refreshOrders() {
        UiUtil.runAsync(this, pesananController::getAllPesanan, rows -> {
            orderModel.setRowCount(0);
            for (Pesanan pesanan : rows) {
                orderModel.addRow(new Object[] {
                        pesanan.getIdPesanan(),
                        pesanan.getNamaPelanggan(),
                        pesanan.getTanggalMasuk(),
                        pesanan.getEstimasiSelesai(),
                        pesanan.getPaketLaundry().getDisplayName(),
                        String.format("%.2f kg", pesanan.getBeratKg()),
                        UiUtil.money(pesanan.getHargaPerKg()),
                        pesanan.getStatusPesanan().getDisplayName(),
                        UiUtil.money(pesanan.getTotalBiaya()),
                        pesanan.getCatatan()
                });
            }
        }, "Gagal memuat pesanan.");
    }

    private void refreshItems() {
        itemModel.setRowCount(0);
        String idPesanan = itemOrderField.getText().trim();
        if (idPesanan.isEmpty()) {
            return;
        }
        UiUtil.runAsync(this, () -> itemController.getItemsByPesanan(idPesanan), rows -> {
            if (!itemOrderField.getText().trim().equals(idPesanan)) {
                return;
            }
            itemModel.setRowCount(0);
            for (ItemPakaian item : rows) {
                itemModel.addRow(new Object[] {
                        item.getIdItem(),
                        item.getIdPesanan(),
                        item.getJenisPakaian(),
                        item.getKategoriWarna().getDisplayName(),
                        item.getKondisiAwal(),
                        item.getDeskripsiDetail(),
                        item.getLabelSmartGroup(),
                        item.getKodeQR()
                });
            }
        }, "Gagal memuat item pakaian.");
    }

    private void createOrder() {
        Pelanggan pelanggan = (Pelanggan) pelangganCombo.getSelectedItem();
        TarifLaundry tarif = (TarifLaundry) tarifCombo.getSelectedItem();
        if (pelanggan == null) {
            UiUtil.info(this, "Pilih pelanggan terlebih dahulu.");
            return;
        }
        if (tarif == null) {
            UiUtil.info(this, "Pilih paket laundry terlebih dahulu.");
            return;
        }
        double beratKg = getBeratKg();
        String catatan = catatanField.getText().trim();
        UiUtil.runAsync(this, () -> pesananController.tambahPesanan(
                pelanggan.getIdPelanggan(),
                karyawan.getIdKaryawan(),
                tarif.getPaketLaundry(),
                beratKg,
                catatan), pesananBaru -> {
            catatanField.setText("");
            itemOrderField.setText(pesananBaru.getIdPesanan());
            paymentOrderField.setText(pesananBaru.getIdPesanan());
            refreshOrders();
            refreshItems();
            UiUtil.info(this, "Pesanan berhasil dibuat. Lanjutkan dengan mencatat item pakaian.");
        }, "Gagal menambah pesanan.");
    }

    private void updateStatus() {
        String idPesanan = UiUtil.selectedId(orderTable);
        if (idPesanan == null) {
            UiUtil.info(this, "Pilih pesanan yang akan diupdate.");
            return;
        }
        StatusPesanan statusTujuan = (StatusPesanan) statusCombo.getSelectedItem();
        UiUtil.runAsync(this, () -> pesananController.updateStatus(idPesanan, statusTujuan), berubah -> {
            if (!berubah) {
                UiUtil.info(this, "Status pesanan tidak berubah.");
                return;
            }
            refreshOrders();
            loadOrderDetails(idPesanan);
            if (statusTujuan == StatusPesanan.SIAP_DIAMBIL || statusTujuan == StatusPesanan.SELESAI) {
                UiUtil.info(this, "Status diperbarui dan notifikasi aplikasi dibuat untuk pelanggan.");
            }
        }, "Gagal update status pesanan.");
    }

    private void buatTemplateWhatsApp() {
        String idPesanan = UiUtil.selectedId(orderTable);
        if (idPesanan == null) {
            UiUtil.info(this, "Pilih pesanan untuk membuat template WhatsApp.");
            return;
        }
        UiUtil.runAsync(this, () -> notifikasiController.buatLinkWhatsApp(idPesanan), link -> {
            Toolkit.getDefaultToolkit()
                    .getSystemClipboard()
                    .setContents(new StringSelection(link), null);
            UiUtil.info(this, "Template link WhatsApp sudah dibuat dan disalin ke clipboard:\n\n" + link);
        }, "Gagal membuat template WhatsApp.");
    }

    private void cancelOrder() {
        String idPesanan = UiUtil.selectedId(orderTable);
        if (idPesanan == null) {
            UiUtil.info(this, "Pilih pesanan yang akan dibatalkan.");
            return;
        }
        if (!UiUtil.confirm(this, "Batalkan pesanan " + idPesanan + "? Data transaksi tetap disimpan.")) {
            return;
        }
        UiUtil.runAsync(this, () -> pesananController.batalkanPesanan(idPesanan), dibatalkan -> {
            refreshOrders();
            loadOrderDetails(idPesanan);
            UiUtil.info(this, dibatalkan ? "Pesanan berhasil dibatalkan." : "Status pesanan tidak berubah.");
        }, "Gagal membatalkan pesanan.");
    }

    private void addItem() {
        String idPesanan = itemOrderField.getText().trim();
        if (idPesanan.isEmpty()) {
            UiUtil.info(this, "Pilih pesanan dari menu Pesanan terlebih dahulu.");
            return;
        }
        if (jenisField.getText().trim().isEmpty() || kondisiField.getText().trim().isEmpty()
                || deskripsiField.getText().trim().length() < 5) {
            UiUtil.info(this, "Jenis, kondisi, dan deskripsi item minimal 5 karakter wajib diisi.");
            return;
        }
        KategoriWarna warna = (KategoriWarna) warnaCombo.getSelectedItem();
        String jenis = jenisField.getText().trim();
        String kondisi = kondisiField.getText().trim();
        String deskripsi = deskripsiField.getText().trim();
        UiUtil.runAsync(this, () -> {
            itemController.tambahItem(
                    idPesanan,
                    jenis, warna, kondisi, deskripsi);
            return null;
        }, ignored -> {
            refreshItems();
            jenisField.setText("");
            kondisiField.setText("Normal");
            deskripsiField.setText("");
            UiUtil.info(this, "Item pakaian berhasil dicatat.");
        }, "Gagal menambah item pakaian.");
    }

    private void runSmartGrouping() {
        String idPesanan = itemOrderField.getText().trim();
        if (idPesanan.isEmpty()) {
            UiUtil.info(this, "Pilih pesanan dari menu Pesanan terlebih dahulu.");
            return;
        }
        UiUtil.runAsync(this, () -> itemController.jalankanSmartGrouping(idPesanan), count -> {
            refreshItems();
            UiUtil.info(this, count + " item berhasil dikelompokkan berdasarkan kategori warna.");
        }, "Gagal menjalankan smart grouping.");
    }

    private void deleteItem() {
        String idItem = UiUtil.selectedId(itemTable);
        if (idItem == null) {
            UiUtil.info(this, "Pilih item yang akan dihapus.");
            return;
        }
        if (!UiUtil.confirm(this, "Hapus item " + idItem + " dari pesanan?")) {
            return;
        }
        UiUtil.runAsync(this, () -> {
            itemController.hapusItem(idItem);
            return null;
        }, ignored -> {
            refreshItems();
        }, "Gagal menghapus item.");
    }

    private void copySelectedOrderToPayment() {
        String idPesanan = UiUtil.selectedId(orderTable);
        if (idPesanan == null) {
            UiUtil.info(this, "Pilih pesanan dari tab Pesanan.");
            return;
        }
        paymentOrderField.setText(idPesanan);
        itemOrderField.setText(idPesanan);
        loadOrderDetails(idPesanan);
    }

    private void savePayment() {
        String idPesanan = paymentOrderField.getText().trim();
        if (idPesanan.isEmpty()) {
            UiUtil.info(this, "Pilih pesanan dari menu Pesanan terlebih dahulu.");
            return;
        }
        final double nominal;
        try {
            nominal = Double.parseDouble(paymentAmountField.getText().trim());
        } catch (NumberFormatException ex) {
            UiUtil.info(this, "Jumlah pembayaran harus berupa angka.");
            return;
        }
        String metode = paymentMethodCombo.getSelectedItem().toString();
        UiUtil.runAsync(this,
                () -> pembayaranController.simpanPembayaran(idPesanan, metode, nominal), pembayaran -> {
            loadOrderDetails(idPesanan);
            UiUtil.info(this, "Pembayaran berhasil ditambahkan. Total dibayar "
                    + UiUtil.money(pembayaran.getJumlah()) + " dengan status "
                    + pembayaran.getStatus().getDisplayName() + ".");
        }, "Gagal menyimpan pembayaran.");
    }

    private void orderSelected(ListSelectionEvent event) {
        if (event.getValueIsAdjusting()) {
            return;
        }
        String idPesanan = UiUtil.selectedId(orderTable);
        if (idPesanan != null) {
            itemOrderField.setText(idPesanan);
            paymentOrderField.setText(idPesanan);
            loadOrderDetails(idPesanan);
            refreshItems();
        }
    }

    private void loadOrderDetails(String idPesanan) {
        UiUtil.runAsync(this, () -> {
            Pesanan pesanan = pesananController.getPesanan(idPesanan);
            silaundry.model.Pembayaran pembayaran = pembayaranController.getPembayaran(idPesanan);
            return new OrderDetails(pesanan, pembayaran);
        }, details -> {
            if (!paymentOrderField.getText().trim().equals(idPesanan)) {
                return;
            }
            Pesanan pesanan = details.pesanan();
            if (pesanan == null) {
                return;
            }
            refreshStatusOptions(pesanan.getStatusPesanan());
            silaundry.model.Pembayaran pembayaran = details.pembayaran();
            double totalDibayar = pembayaran == null ? 0 : pembayaran.getJumlah();
            double sisaTagihan = Math.max(0, pesanan.getTotalBiaya() - totalDibayar);
            if (pembayaran == null) {
                paymentStatusField.setText("Belum Bayar");
            } else {
                paymentMethodCombo.setSelectedItem(pembayaran.getMetode());
                paymentStatusField.setText(pembayaran.getStatus().getDisplayName());
            }
            paymentAmountField.setText(String.valueOf(Math.round(sisaTagihan)));
            paymentAmountField.setEditable(pesanan.getStatusPesanan().dapatMenerimaPembayaran()
                    && sisaTagihan > 0);
            paymentSummaryLabel.setText("Total dibayar: " + UiUtil.money(totalDibayar)
                    + " | Sisa tagihan: " + UiUtil.money(sisaTagihan));
        }, "Gagal mengambil detail pesanan.");
    }

    private void refreshStatusOptions(StatusPesanan statusSaatIni) {
        statusCombo.removeAllItems();
        statusCombo.addItem(statusSaatIni);
        for (StatusPesanan candidate : StatusPesanan.values()) {
            if (statusSaatIni.dapatBerubahKe(candidate)) {
                statusCombo.addItem(candidate);
            }
        }
        statusCombo.setSelectedItem(statusSaatIni);
    }

    private double getBeratKg() {
        Object value = beratSpinner.getValue();
        if (value instanceof Number number) {
            return number.doubleValue();
        }
        return Double.parseDouble(value.toString());
    }

    private void updateTotalPreview() {
        TarifLaundry tarif = (TarifLaundry) tarifCombo.getSelectedItem();
        if (tarif == null) {
            totalPreviewLabel.setText("Total otomatis: -");
            return;
        }
        double total = tarif.hitungTotal(getBeratKg());
        totalPreviewLabel.setText("Total otomatis: " + UiUtil.money(total));
    }

    private record OrderDetails(Pesanan pesanan, silaundry.model.Pembayaran pembayaran) {
    }
}
