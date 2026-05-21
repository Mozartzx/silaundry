package silaundry.view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.sql.SQLException;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.table.DefaultTableModel;
import silaundry.controller.PenggunaController;
import silaundry.controller.PesananController;
import silaundry.model.Pelanggan;
import silaundry.model.Pesanan;

public class PelangganPanel extends JPanel {
    private final Pelanggan pelanggan;
    private final PesananController pesananController = new PesananController();
    private final PenggunaController penggunaController = new PenggunaController();
    private final DefaultTableModel tableModel = UiUtil.model("ID", "Tanggal", "Estimasi", "Status", "Total", "Catatan");
    private final JTable table = new JTable(tableModel);
    private final JSpinner estimasiSpinner = new JSpinner(new SpinnerNumberModel(2, 1, 14, 1));
    private final JTextField totalField = new JTextField("25000", 10);
    private final JTextField catatanField = new JTextField(28);

    public PelangganPanel(Pelanggan pelanggan) {
        this.pelanggan = pelanggan;
        setLayout(new BorderLayout(8, 8));
        setBackground(AppTheme.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        UiUtil.applyTableStyle(table);
        add(buildForm(), BorderLayout.NORTH);
        add(AppTheme.scroll(table), BorderLayout.CENTER);
        refresh();
    }

    private JPanel buildForm() {
        JPanel panel = AppTheme.surface(new GridLayout(2, 1, 0, 8));
        JPanel info = new JPanel(new FlowLayout(FlowLayout.LEFT));
        info.setBackground(AppTheme.SURFACE);
        info.add(AppTheme.sectionTitle("Pesanan Saya"));
        info.add(AppTheme.muted("Pelanggan: " + pelanggan.getNamaLengkap() + " | " + pelanggan.getAlamat()));

        JPanel form = new JPanel(new FlowLayout(FlowLayout.LEFT));
        form.setBackground(AppTheme.SURFACE);
        AppTheme.styleSpinner(estimasiSpinner);
        AppTheme.styleTextField(totalField);
        AppTheme.styleTextField(catatanField);
        form.add(new JLabel("Estimasi hari"));
        form.add(estimasiSpinner);
        form.add(new JLabel("Total"));
        form.add(totalField);
        form.add(new JLabel("Catatan"));
        form.add(catatanField);
        JButton createButton = AppTheme.primaryButton("Buat Pesanan");
        createButton.addActionListener(event -> createOrder());
        JButton refreshButton = AppTheme.secondaryButton("Refresh");
        refreshButton.addActionListener(event -> refresh());
        form.add(createButton);
        form.add(refreshButton);

        panel.add(info);
        panel.add(form);
        return panel;
    }

    private void refresh() {
        tableModel.setRowCount(0);
        try {
            List<Pesanan> rows = pesananController.getPesananPelanggan(pelanggan.getIdPelanggan());
            for (Pesanan pesanan : rows) {
                tableModel.addRow(new Object[] {
                        pesanan.getIdPesanan(),
                        pesanan.getTanggalMasuk(),
                        pesanan.getEstimasiSelesai(),
                        pesanan.getStatusPesanan().getDisplayName(),
                        UiUtil.money(pesanan.getTotalBiaya()),
                        pesanan.getCatatan()
                });
            }
        } catch (SQLException ex) {
            UiUtil.error(this, "Gagal memuat pesanan pelanggan.", ex);
        }
    }

    private void createOrder() {
        try {
            String karyawanId = penggunaController.getDefaultKaryawanId();
            if (karyawanId == null) {
                UiUtil.info(this, "Belum ada karyawan untuk menerima pesanan.");
                return;
            }
            pesananController.tambahPesanan(
                    pelanggan.getIdPelanggan(),
                    karyawanId,
                    (Integer) estimasiSpinner.getValue(),
                    Double.parseDouble(totalField.getText().trim()),
                    catatanField.getText().trim());
            catatanField.setText("");
            refresh();
        } catch (NumberFormatException ex) {
            UiUtil.info(this, "Total biaya harus berupa angka.");
        } catch (SQLException ex) {
            UiUtil.error(this, "Gagal membuat pesanan.", ex);
        }
    }
}
