package silaundry.view;

import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
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
import silaundry.model.Pembayaran;
import silaundry.model.Pesanan;
import silaundry.model.TarifLaundry;
import silaundry.model.enums.KategoriWarna;
import silaundry.model.enums.StatusPesanan;

public class KaryawanPanel extends JPanel {
    public enum Page {
        PESANAN, ITEM_PAKAIAN, PEMBAYARAN
    }

    private final Karyawan karyawan;
    private final Page page;
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

    private final JComboBox<Pesanan> itemOrderCombo = new JComboBox<>();
    private final DefaultTableModel itemModel = UiUtil.model("ID", "Pesanan", "Jenis", "Warna", "Kondisi",
            "Deskripsi Detail", "Smart Group");
    private final JTable itemTable = new JTable(itemModel);
    private final JTextField itemSearchField = new JTextField(18);
    private final JTextField jenisField = new JTextField(12);
    private final JComboBox<KategoriWarna> warnaCombo = new JComboBox<>(KategoriWarna.values());
    private final JTextField kondisiField = new JTextField("Normal", 14);
    private final JTextField deskripsiField = new JTextField(24);

    private final JComboBox<Pesanan> paymentOrderCombo = new JComboBox<>();
    private final JComboBox<String> paymentMethodCombo = new JComboBox<>(new String[] { "Tunai", "QRIS", "Transfer" });
    private final JLabel paymentTotalLabel = AppTheme.muted("-");
    private final JLabel paymentStatusLabel = AppTheme.muted("Pilih pesanan");
    private final JButton paymentSaveButton = AppTheme.primaryButton("Catat Pembayaran Lunas");
    private boolean updatingOrderChoices;

    public KaryawanPanel(Karyawan karyawan, Page page) {
        this.karyawan = karyawan;
        this.page = page;
        statusCombo.setSelectedIndex(-1);
        paymentSaveButton.setEnabled(false);
        setLayout(new BorderLayout());
        setBackground(AppTheme.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        styleInputs();
        tarifCombo.addActionListener(event -> updateTotalPreview());
        beratSpinner.addChangeListener(event -> updateTotalPreview());
        itemOrderCombo.addActionListener(event -> {
            if (!updatingOrderChoices) {
                refreshItems();
            }
        });
        paymentOrderCombo.addActionListener(event -> {
            if (!updatingOrderChoices) {
                loadPaymentDetails();
            }
        });
        add(switch (page) {
            case PESANAN -> buildOrderPanel();
            case ITEM_PAKAIAN -> buildItemPanel();
            case PEMBAYARAN -> buildPaymentPanel();
        }, BorderLayout.CENTER);
    }

    public void refreshData() {
        switch (page) {
            case PESANAN -> {
                refreshCustomers();
                refreshTarif();
                refreshOrders();
            }
            case ITEM_PAKAIAN -> refreshOrderChoices(itemOrderCombo, this::refreshItems);
            case PEMBAYARAN -> refreshOrderChoices(paymentOrderCombo, this::loadPaymentDetails);
        }
    }

    private JPanel buildOrderPanel() {
        JPanel panel = AppTheme.page(new BorderLayout(10, 10));
        UiUtil.applyTableStyle(orderTable);
        UiUtil.installSearch(orderTable, orderSearchField);
        orderTable.getSelectionModel().addListSelectionListener(this::orderSelected);

        JPanel form = AppTheme.surface(new BorderLayout(0, 12));
        JPanel title = new JPanel(new BorderLayout(0, 4));
        title.setBackground(AppTheme.SURFACE);
        title.add(AppTheme.sectionTitle("Manajemen Pesanan"), BorderLayout.NORTH);
        title.add(AppTheme.muted("Karyawan: " + karyawan.getNamaLengkap() + " | Shift " + karyawan.getShiftKerja()),
                BorderLayout.CENTER);

        JPanel fields = AppTheme.formGrid();
        AppTheme.addField(fields, 0, 0, "Pelanggan", pelangganCombo);
        AppTheme.addField(fields, 0, 1, "Paket", tarifCombo);
        AppTheme.addField(fields, 1, 0, "Berat kg", beratSpinner);
        AppTheme.addField(fields, 1, 1, "Status berikutnya", statusCombo);
        AppTheme.addField(fields, 2, 0, "Total otomatis", totalPreviewLabel);
        AppTheme.addWideField(fields, 3, "Catatan", catatanField);
        AppTheme.addWideField(fields, 4, "Cari pesanan", orderSearchField);

        JButton createButton = AppTheme.primaryButton("Tambah Pesanan");
        createButton.addActionListener(event -> createOrder());
        JButton statusButton = AppTheme.secondaryButton("Update Status");
        statusButton.addActionListener(event -> updateStatus());
        JButton whatsappButton = AppTheme.secondaryButton("Template WhatsApp");
        whatsappButton.addActionListener(event -> buatTemplateWhatsApp());
        JButton cancelButton = AppTheme.dangerButton("Batalkan");
        cancelButton.addActionListener(event -> cancelOrder());
        JButton refreshButton = AppTheme.secondaryButton("Refresh");
        refreshButton.addActionListener(event -> refreshData());
        JPanel actions = AppTheme.actionRow();
        actions.add(createButton);
        actions.add(statusButton);
        actions.add(whatsappButton);
        actions.add(cancelButton);
        actions.add(refreshButton);

        form.add(title, BorderLayout.NORTH);
        form.add(fields, BorderLayout.CENTER);
        form.add(actions, BorderLayout.SOUTH);
        panel.add(form, BorderLayout.NORTH);
        panel.add(AppTheme.scroll(orderTable), BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildItemPanel() {
        JPanel panel = AppTheme.page(new BorderLayout(10, 10));
        UiUtil.applyTableStyle(itemTable);
        UiUtil.installSearch(itemTable, itemSearchField);
        JPanel form = AppTheme.surface(new BorderLayout(0, 12));
        JPanel title = new JPanel(new BorderLayout(0, 4));
        title.setBackground(AppTheme.SURFACE);
        title.add(AppTheme.sectionTitle("Data Item Pakaian"), BorderLayout.NORTH);
        title.add(AppTheme.muted("Pilih pesanan, catat ciri pakaian, lalu kelompokkan berdasarkan warna."),
                BorderLayout.CENTER);

        JPanel fields = AppTheme.formGrid();
        AppTheme.addWideField(fields, 0, "Pesanan", itemOrderCombo);
        AppTheme.addField(fields, 1, 0, "Jenis", jenisField);
        AppTheme.addField(fields, 1, 1, "Warna", warnaCombo);
        AppTheme.addField(fields, 2, 0, "Kondisi", kondisiField);
        AppTheme.addField(fields, 2, 1, "Cari item", itemSearchField);
        AppTheme.addWideField(fields, 3, "Deskripsi", deskripsiField);

        JButton addButton = AppTheme.primaryButton("Tambah Item");
        addButton.addActionListener(event -> addItem());
        JButton groupButton = AppTheme.secondaryButton("Kelompokkan Warna");
        groupButton.addActionListener(event -> runSmartGrouping());
        JButton deleteButton = AppTheme.dangerButton("Hapus Item");
        deleteButton.addActionListener(event -> deleteItem());
        JButton refreshButton = AppTheme.secondaryButton("Refresh");
        refreshButton.addActionListener(event -> refreshData());
        JPanel actions = AppTheme.actionRow();
        actions.add(addButton);
        actions.add(groupButton);
        actions.add(deleteButton);
        actions.add(refreshButton);

        form.add(title, BorderLayout.NORTH);
        form.add(fields, BorderLayout.CENTER);
        form.add(actions, BorderLayout.SOUTH);
        panel.add(form, BorderLayout.NORTH);
        panel.add(AppTheme.scroll(itemTable), BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildPaymentPanel() {
        JPanel pagePanel = AppTheme.page(new BorderLayout(0, 12));
        JPanel panel = AppTheme.surface(new BorderLayout(0, 12));
        JPanel title = new JPanel(new BorderLayout(0, 4));
        title.setBackground(AppTheme.SURFACE);
        title.add(AppTheme.sectionTitle("Pembayaran"), BorderLayout.NORTH);
        title.add(AppTheme.muted("Pembayaran dicatat satu kali sesuai total tagihan pesanan."), BorderLayout.CENTER);

        JPanel fields = AppTheme.formGrid();
        AppTheme.addWideField(fields, 0, "Pesanan", paymentOrderCombo);
        AppTheme.addField(fields, 1, 0, "Metode", paymentMethodCombo);
        AppTheme.addField(fields, 1, 1, "Total", paymentTotalLabel);
        AppTheme.addWideField(fields, 2, "Status", paymentStatusLabel);

        paymentSaveButton.addActionListener(event -> savePayment());
        JButton refreshButton = AppTheme.secondaryButton("Refresh");
        refreshButton.addActionListener(event -> refreshData());
        JPanel actions = AppTheme.actionRow();
        actions.add(paymentSaveButton);
        actions.add(refreshButton);

        panel.add(title, BorderLayout.NORTH);
        panel.add(fields, BorderLayout.CENTER);
        panel.add(actions, BorderLayout.SOUTH);
        pagePanel.add(panel, BorderLayout.NORTH);
        return pagePanel;
    }

    private void styleInputs() {
        AppTheme.styleComboBox(pelangganCombo);
        AppTheme.styleComboBox(statusCombo);
        AppTheme.styleComboBox(tarifCombo);
        AppTheme.styleSpinner(beratSpinner);
        AppTheme.styleTextField(catatanField);
        AppTheme.styleTextField(orderSearchField);
        AppTheme.styleComboBox(itemOrderCombo);
        AppTheme.styleTextField(jenisField);
        AppTheme.styleComboBox(warnaCombo);
        AppTheme.styleTextField(kondisiField);
        AppTheme.styleTextField(deskripsiField);
        AppTheme.styleTextField(itemSearchField);
        AppTheme.styleComboBox(paymentOrderCombo);
        AppTheme.styleComboBox(paymentMethodCombo);
    }

    private void refreshCustomers() {
        UiUtil.runAsync(this, penggunaController::getAllPelanggan, rows -> {
            pelangganCombo.removeAllItems();
            rows.forEach(pelangganCombo::addItem);
        }, "Gagal memuat pelanggan.");
    }

    private void refreshTarif() {
        UiUtil.runAsync(this, tarifController::getTarifAktif, rows -> {
            tarifCombo.removeAllItems();
            rows.forEach(tarifCombo::addItem);
            updateTotalPreview();
        }, "Gagal memuat tarif laundry.");
    }

    private void refreshOrders() {
        UiUtil.runAsync(this, pesananController::getAllPesanan, rows -> {
            orderModel.setRowCount(0);
            statusCombo.setSelectedIndex(-1);
            for (Pesanan pesanan : rows) {
                orderModel.addRow(toOrderRow(pesanan));
            }
        }, "Gagal memuat pesanan.");
    }

    private Object[] toOrderRow(Pesanan pesanan) {
        return new Object[] { pesanan.getIdPesanan(), pesanan.getNamaPelanggan(), pesanan.getTanggalMasuk(),
                pesanan.getEstimasiSelesai(), pesanan.getPaketLaundry().getDisplayName(),
                String.format("%.2f kg", pesanan.getBeratKg()), UiUtil.money(pesanan.getHargaPerKg()),
                pesanan.getStatusPesanan().getDisplayName(), UiUtil.money(pesanan.getTotalBiaya()),
                pesanan.getCatatan() };
    }

    private void refreshOrderChoices(JComboBox<Pesanan> combo, Runnable afterRefresh) {
        String selectedId = selectedOrderId(combo);
        UiUtil.runAsync(this, pesananController::getAllPesanan, rows -> {
            updatingOrderChoices = true;
            combo.removeAllItems();
            for (Pesanan pesanan : rows) {
                if (pesanan.getStatusPesanan() != StatusPesanan.DIBATALKAN) {
                    combo.addItem(pesanan);
                    if (pesanan.getIdPesanan().equals(selectedId)) {
                        combo.setSelectedItem(pesanan);
                    }
                }
            }
            updatingOrderChoices = false;
            afterRefresh.run();
        }, "Gagal memuat pilihan pesanan.");
    }

    private void refreshItems() {
        Pesanan pesanan = (Pesanan) itemOrderCombo.getSelectedItem();
        itemModel.setRowCount(0);
        if (pesanan == null) {
            return;
        }
        UiUtil.runAsync(this, () -> itemController.getItemsByPesanan(pesanan.getIdPesanan()), rows -> {
            itemModel.setRowCount(0);
            for (ItemPakaian item : rows) {
                itemModel.addRow(new Object[] { item.getIdItem(), item.getIdPesanan(), item.getJenisPakaian(),
                        item.getKategoriWarna().getDisplayName(), item.getKondisiAwal(), item.getDeskripsiDetail(),
                        item.getLabelSmartGroup() });
            }
        }, "Gagal memuat item pakaian.");
    }

    private void createOrder() {
        Pelanggan pelanggan = (Pelanggan) pelangganCombo.getSelectedItem();
        TarifLaundry tarif = (TarifLaundry) tarifCombo.getSelectedItem();
        if (pelanggan == null || tarif == null) {
            UiUtil.info(this, "Pilih pelanggan dan paket laundry terlebih dahulu.");
            return;
        }
        double beratKg = ((Number) beratSpinner.getValue()).doubleValue();
        UiUtil.runAsync(this, () -> pesananController.tambahPesanan(pelanggan.getIdPelanggan(),
                karyawan.getIdKaryawan(), tarif.getPaketLaundry(), beratKg, catatanField.getText().trim()), created -> {
            catatanField.setText("");
            refreshOrders();
            UiUtil.info(this, "Pesanan " + created.getIdPesanan()
                    + " berhasil dibuat. Lanjutkan melalui menu Item Pakaian.");
        }, "Gagal menambah pesanan.");
    }

    private void orderSelected(ListSelectionEvent event) {
        if (event.getValueIsAdjusting()) {
            return;
        }
        String idPesanan = UiUtil.selectedId(orderTable);
        if (idPesanan == null) {
            return;
        }
        UiUtil.runAsync(this, () -> pesananController.getPesanan(idPesanan), pesanan -> {
            if (pesanan == null) {
                return;
            }
            for (StatusPesanan candidate : StatusPesanan.values()) {
                if (candidate != StatusPesanan.DIBATALKAN
                        && pesanan.getStatusPesanan().dapatBerubahKe(candidate)) {
                    statusCombo.setSelectedItem(candidate);
                    break;
                }
            }
        }, "Gagal membaca detail pesanan.");
    }

    private void updateStatus() {
        String idPesanan = UiUtil.selectedId(orderTable);
        StatusPesanan status = (StatusPesanan) statusCombo.getSelectedItem();
        if (idPesanan == null) {
            UiUtil.info(this, "Pilih pesanan yang akan diperbarui.");
            return;
        }
        if (status == null) {
            UiUtil.info(this, "Pilih status berikutnya.");
            return;
        }
        UiUtil.runAsync(this, () -> pesananController.updateStatus(idPesanan, status), changed -> {
            refreshOrders();
            UiUtil.info(this, changed ? "Status pesanan berhasil diperbarui." : "Status pesanan tidak berubah.");
        }, "Gagal memperbarui status pesanan.");
    }

    private void cancelOrder() {
        String idPesanan = UiUtil.selectedId(orderTable);
        if (idPesanan == null || !UiUtil.confirm(this, "Batalkan pesanan " + idPesanan + "?")) {
            return;
        }
        UiUtil.runAsync(this, () -> pesananController.batalkanPesanan(idPesanan), changed -> {
            refreshOrders();
            UiUtil.info(this, changed ? "Pesanan berhasil dibatalkan." : "Status pesanan tidak berubah.");
        }, "Gagal membatalkan pesanan.");
    }

    private void buatTemplateWhatsApp() {
        String idPesanan = UiUtil.selectedId(orderTable);
        if (idPesanan == null) {
            UiUtil.info(this, "Pilih pesanan terlebih dahulu.");
            return;
        }
        UiUtil.runAsync(this, () -> notifikasiController.buatLinkWhatsApp(idPesanan), link -> {
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(link), null);
            UiUtil.info(this, "Link WhatsApp disalin ke clipboard:\n\n" + link);
        }, "Gagal membuat template WhatsApp.");
    }

    private void addItem() {
        Pesanan pesanan = (Pesanan) itemOrderCombo.getSelectedItem();
        if (pesanan == null) {
            UiUtil.info(this, "Pilih pesanan terlebih dahulu.");
            return;
        }
        String jenis = jenisField.getText().trim();
        String kondisi = kondisiField.getText().trim();
        String deskripsi = deskripsiField.getText().trim();
        KategoriWarna warna = (KategoriWarna) warnaCombo.getSelectedItem();
        UiUtil.runAsync(this, () -> {
            itemController.tambahItem(pesanan.getIdPesanan(), jenis, warna, kondisi, deskripsi);
            return null;
        }, ignored -> {
            jenisField.setText("");
            kondisiField.setText("Normal");
            deskripsiField.setText("");
            refreshItems();
            UiUtil.info(this, "Item pakaian berhasil dicatat.");
        }, "Gagal menambah item pakaian.");
    }

    private void runSmartGrouping() {
        Pesanan pesanan = (Pesanan) itemOrderCombo.getSelectedItem();
        if (pesanan == null) {
            UiUtil.info(this, "Pilih pesanan terlebih dahulu.");
            return;
        }
        UiUtil.runAsync(this, () -> itemController.jalankanSmartGrouping(pesanan.getIdPesanan()), count -> {
            refreshItems();
            UiUtil.info(this, count + " item berhasil dikelompokkan berdasarkan warna.");
        }, "Gagal menjalankan smart grouping.");
    }

    private void deleteItem() {
        String idItem = UiUtil.selectedId(itemTable);
        if (idItem == null || !UiUtil.confirm(this, "Hapus item " + idItem + "?")) {
            return;
        }
        UiUtil.runAsync(this, () -> {
            itemController.hapusItem(idItem);
            return null;
        }, ignored -> refreshItems(), "Gagal menghapus item.");
    }

    private void loadPaymentDetails() {
        Pesanan pesanan = (Pesanan) paymentOrderCombo.getSelectedItem();
        if (pesanan == null) {
            paymentTotalLabel.setText("-");
            paymentStatusLabel.setText("Pilih pesanan");
            paymentSaveButton.setEnabled(false);
            return;
        }
        paymentTotalLabel.setText(UiUtil.money(pesanan.getTotalBiaya()));
        paymentSaveButton.setEnabled(false);
        UiUtil.runAsync(this, () -> pembayaranController.getPembayaran(pesanan.getIdPesanan()), payment -> {
            paymentStatusLabel.setText(payment == null ? "Belum dibayar" : "Lunas melalui " + payment.getMetode());
            paymentSaveButton.setEnabled(payment == null);
        }, "Gagal membaca pembayaran.");
    }

    private void savePayment() {
        Pesanan pesanan = (Pesanan) paymentOrderCombo.getSelectedItem();
        if (pesanan == null) {
            UiUtil.info(this, "Pilih pesanan terlebih dahulu.");
            return;
        }
        String metode = paymentMethodCombo.getSelectedItem().toString();
        if (!UiUtil.confirm(this, "Catat pembayaran lunas sebesar " + UiUtil.money(pesanan.getTotalBiaya()) + "?")) {
            return;
        }
        UiUtil.runAsync(this, () -> pembayaranController.catatPembayaran(pesanan.getIdPesanan(), metode), payment -> {
            loadPaymentDetails();
            UiUtil.info(this, "Pembayaran lunas berhasil dicatat.");
        }, "Gagal mencatat pembayaran.");
    }

    private void updateTotalPreview() {
        TarifLaundry tarif = (TarifLaundry) tarifCombo.getSelectedItem();
        if (tarif == null) {
            totalPreviewLabel.setText("Total otomatis: -");
            return;
        }
        double berat = ((Number) beratSpinner.getValue()).doubleValue();
        totalPreviewLabel.setText(UiUtil.money(tarif.hitungTotal(berat)));
    }

    private String selectedOrderId(JComboBox<Pesanan> combo) {
        Pesanan selected = (Pesanan) combo.getSelectedItem();
        return selected == null ? null : selected.getIdPesanan();
    }
}
