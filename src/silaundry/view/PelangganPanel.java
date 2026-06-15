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
import silaundry.controller.LaundryController;
import silaundry.model.Notifikasi;
import silaundry.model.Pelanggan;
import silaundry.model.Pesanan;
import silaundry.model.enums.StatusPesanan;

// Menampilkan pesanan aktif, riwayat, dan notifikasi milik pelanggan yang login.
public class PelangganPanel extends JPanel {
    private final Pelanggan pelanggan;
    private final LaundryController laundryController = new LaundryController();
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
        // Header berisi identitas pelanggan dan tombol aksi yang sering dipakai.
        JPanel header = AppTheme.surface(new BorderLayout(10, 8));

        JPanel title = new JPanel(new GridLayout(2, 1, 0, 4));
        title.setBackground(AppTheme.SURFACE);
        title.add(AppTheme.sectionTitle("Status Laundry Saya"));
        title.add(AppTheme.muted("Pelanggan: " + pelanggan.getNamaLengkap() + " | " + pelanggan.getAlamat()));

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actions.setBackground(AppTheme.SURFACE);
        JButton refreshButton = AppTheme.primaryButton("Refresh");
        refreshButton.addActionListener(event -> refresh());
        JButton readButton = AppTheme.secondaryButton("Tandai Notifikasi Dibaca");
        readButton.addActionListener(event -> markNotificationsRead());
        actions.add(readButton);
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
        // Pesanan dan notifikasi dimuat bersama agar ringkasan berasal dari data yang sama.
        UiUtil.runTask(this, () -> new CustomerData(
                laundryController.getPesananPelanggan(pelanggan.getIdPelanggan()),
                laundryController.getNotifikasiPelanggan(pelanggan.getIdPelanggan())), data -> {
            activeModel.setRowCount(0);
            historyModel.setRowCount(0);
            notificationModel.setRowCount(0);
            int activeCount = 0;
            int historyCount = 0;
            for (Pesanan pesanan : data.pesanan()) {
                Object[] row = toRow(pesanan);
                if (isHistory(pesanan)) {
                    historyModel.addRow(row);
                    historyCount++;
                } else {
                    activeModel.addRow(row);
                    activeCount++;
                }
            }
            for (Notifikasi notifikasi : data.notifikasi()) {
                notificationModel.addRow(new Object[] {
                        notifikasi.getTanggalKirim(),
                        notifikasi.getIdPesanan(),
                        notifikasi.getPesan(),
                        notifikasi.isSudahDibaca() ? "Sudah dibaca" : "Belum dibaca"
                });
            }
            // Ringkasan hanya menghitung notifikasi yang memang belum dibaca pelanggan.
            int unreadNotificationCount = 0;
            for (Notifikasi notifikasi : data.notifikasi()) {
                if (!notifikasi.isSudahDibaca()) {
                    unreadNotificationCount++;
                }
            }
            summaryLabel.setText(activeCount + " pesanan berjalan, " + historyCount
                    + " riwayat pesanan, " + unreadNotificationCount + " notifikasi belum dibaca.");
            if (activeCount == 0) {
                activeModel.addRow(new Object[] { "-", "-", "-", "-", "-", "Belum ada pesanan aktif", "-", "-" });
            }
            if (historyCount == 0) {
                historyModel.addRow(new Object[] { "-", "-", "-", "-", "-", "Belum ada riwayat", "-", "-" });
            }
            if (data.notifikasi().isEmpty()) {
                notificationModel.addRow(new Object[] { "-", "-", "Belum ada notifikasi", "-" });
            }
        }, "Gagal memuat status laundry pelanggan.");
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
        // Pesanan final dipindahkan dari tabel aktif ke tabel riwayat.
        return pesanan.getStatusPesanan() == StatusPesanan.SELESAI
                || pesanan.getStatusPesanan() == StatusPesanan.DIBATALKAN;
    }

    private void markNotificationsRead() {
        UiUtil.runTask(this,
                () -> laundryController.tandaiSemuaDibaca(pelanggan.getIdPelanggan()), changed -> {
            refresh();
            UiUtil.info(this, changed == 0 ? "Tidak ada notifikasi baru." : changed + " notifikasi ditandai dibaca.");
        }, "Gagal memperbarui notifikasi.");
    }

    private record CustomerData(List<Pesanan> pesanan, List<Notifikasi> notifikasi) {
    }
}
