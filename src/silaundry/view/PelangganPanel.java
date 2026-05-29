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
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import silaundry.controller.NotifikasiController;
import silaundry.controller.PesananController;
import silaundry.model.Notifikasi;
import silaundry.model.Pelanggan;
import silaundry.model.Pesanan;
import silaundry.model.enums.StatusPesanan;

public class PelangganPanel extends JPanel {
    private final Pelanggan pelanggan;
    private final PesananController pesananController = new PesananController();
    private final NotifikasiController notifikasiController = new NotifikasiController();
    private final JLabel summaryLabel = AppTheme.muted("Memuat status laundry...");
    private final DefaultTableModel activeModel = UiUtil.model("ID", "Tanggal", "Estimasi", "Paket", "Berat",
            "Status", "Total", "Catatan");
    private final DefaultTableModel historyModel = UiUtil.model("ID", "Tanggal", "Selesai/Estimasi", "Paket",
            "Berat", "Status", "Total", "Catatan");
    private final DefaultTableModel notificationModel = UiUtil.model("Waktu", "Pesanan", "Pesan", "Status Baca");
    private final JTable activeTable = new JTable(activeModel);
    private final JTable historyTable = new JTable(historyModel);
    private final JTable notificationTable = new JTable(notificationModel);

    public PelangganPanel(Pelanggan pelanggan) {
        this.pelanggan = pelanggan;
        setLayout(new BorderLayout(12, 12));
        setBackground(AppTheme.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        UiUtil.applyTableStyle(activeTable);
        UiUtil.applyTableStyle(historyTable);
        UiUtil.applyTableStyle(notificationTable);
        add(buildHeader(), BorderLayout.NORTH);
        add(buildTables(), BorderLayout.CENTER);
        refresh();
    }

    private JPanel buildHeader() {
        JPanel header = AppTheme.surface(new BorderLayout(10, 8));

        JPanel title = new JPanel(new GridLayout(2, 1, 0, 4));
        title.setBackground(AppTheme.SURFACE);
        title.add(AppTheme.sectionTitle("Status Laundry Saya"));
        title.add(AppTheme.muted("Pelanggan: " + pelanggan.getNamaLengkap() + " | " + pelanggan.getAlamat()));

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actions.setBackground(AppTheme.SURFACE);
        JButton refreshButton = AppTheme.primaryButton("Refresh");
        refreshButton.addActionListener(event -> refresh());
        actions.add(refreshButton);

        header.add(title, BorderLayout.CENTER);
        header.add(summaryLabel, BorderLayout.SOUTH);
        header.add(actions, BorderLayout.EAST);
        return header;
    }

    private JPanel buildTables() {
        JPanel content = new JPanel(new GridLayout(3, 1, 0, 12));
        content.setBackground(AppTheme.BACKGROUND);
        content.add(buildTableSection("Status Saat Ini", "Pesanan yang masih diproses atau siap diambil.", activeTable));
        content.add(buildTableSection("Riwayat Pesanan", "Pesanan yang sudah selesai atau dibatalkan.", historyTable));
        content.add(buildTableSection("Notifikasi", "Informasi terbaru dari proses laundry.", notificationTable));
        return content;
    }

    private JPanel buildTableSection(String title, String subtitle, JTable table) {
        JPanel section = AppTheme.surface(new BorderLayout(0, 8));

        JPanel heading = new JPanel(new GridLayout(2, 1, 0, 2));
        heading.setBackground(AppTheme.SURFACE);
        heading.add(AppTheme.sectionTitle(title));
        heading.add(AppTheme.muted(subtitle));

        section.add(heading, BorderLayout.NORTH);
        section.add(AppTheme.scroll(table), BorderLayout.CENTER);
        return section;
    }

    private void refresh() {
        activeModel.setRowCount(0);
        historyModel.setRowCount(0);
        notificationModel.setRowCount(0);
        try {
            List<Pesanan> rows = pesananController.getPesananPelanggan(pelanggan.getIdPelanggan());
            int activeCount = 0;
            int historyCount = 0;
            for (Pesanan pesanan : rows) {
                Object[] row = toRow(pesanan);
                if (isHistory(pesanan)) {
                    historyModel.addRow(row);
                    historyCount++;
                } else {
                    activeModel.addRow(row);
                    activeCount++;
                }
            }
            int notificationCount = refreshNotifications();
            summaryLabel.setText(activeCount + " pesanan berjalan, " + historyCount
                    + " riwayat pesanan, " + notificationCount + " notifikasi.");
        } catch (SQLException ex) {
            UiUtil.error(this, "Gagal memuat status laundry pelanggan.", ex);
        }
    }

    private Object[] toRow(Pesanan pesanan) {
        return new Object[] {
                pesanan.getIdPesanan(),
                pesanan.getTanggalMasuk(),
                pesanan.getEstimasiSelesai(),
                pesanan.getPaketLaundry().getDisplayName(),
                String.format("%.2f kg", pesanan.getBeratKg()),
                pesanan.getStatusPesanan().getDisplayName(),
                UiUtil.money(pesanan.getTotalBiaya()),
                pesanan.getCatatan()
        };
    }

    private boolean isHistory(Pesanan pesanan) {
        return pesanan.getStatusPesanan() == StatusPesanan.SELESAI
                || pesanan.getStatusPesanan() == StatusPesanan.DIBATALKAN;
    }

    private int refreshNotifications() throws SQLException {
        List<Notifikasi> notifikasiList = notifikasiController.getNotifikasiPelanggan(pelanggan.getIdPelanggan());
        for (Notifikasi notifikasi : notifikasiList) {
            notificationModel.addRow(new Object[] {
                    notifikasi.getTanggalKirim(),
                    notifikasi.getIdPesanan(),
                    notifikasi.getPesan(),
                    notifikasi.isSudahDibaca() ? "Sudah dibaca" : "Belum dibaca"
            });
        }
        return notifikasiList.size();
    }
}
