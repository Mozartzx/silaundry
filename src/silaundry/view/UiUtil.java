package silaundry.view;

import java.awt.Component;
import java.awt.Color;
import java.text.NumberFormat;
import java.util.Locale;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

public final class UiUtil {
    private static final NumberFormat RUPIAH = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("id-ID"));

    private UiUtil() {
    }

    public static DefaultTableModel model(String... columns) {
        return new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    public static void applyTableStyle(JTable table) {
        table.setRowHeight(30);
        table.setAutoCreateRowSorter(true);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.setFillsViewportHeight(true);
        table.setShowGrid(false);
        table.setIntercellSpacing(new java.awt.Dimension(0, 0));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setSelectionBackground(new Color(217, 237, 239));
        table.setSelectionForeground(AppTheme.TEXT);
        table.setForeground(AppTheme.TEXT);
        table.setBackground(AppTheme.SURFACE);
        JTableHeader header = table.getTableHeader();
        header.setFont(AppTheme.SECTION_FONT.deriveFont(13f));
        header.setBackground(new Color(232, 240, 242));
        header.setForeground(AppTheme.PRIMARY_DARK);
        header.setReorderingAllowed(false);
        for (int i = 0; i < table.getColumnModel().getColumnCount(); i++) {
            TableColumn column = table.getColumnModel().getColumn(i);
            String name = column.getHeaderValue().toString();
            column.setPreferredWidth(preferredColumnWidth(name));
        }
    }

    public static String money(double amount) {
        return RUPIAH.format(amount);
    }

    public static void info(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "SILAUNDRY", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void error(Component parent, String message, Exception ex) {
        String detail = ex == null ? "" : "\n\nDetail: " + ex.getMessage();
        JOptionPane.showMessageDialog(parent, message + detail, "Terjadi Kesalahan", JOptionPane.ERROR_MESSAGE);
    }

    public static String selectedId(JTable table) {
        int row = table.getSelectedRow();
        if (row < 0) {
            return null;
        }
        int modelRow = table.convertRowIndexToModel(row);
        Object value = table.getModel().getValueAt(modelRow, 0);
        return value == null ? null : value.toString();
    }

    private static int preferredColumnWidth(String columnName) {
        return switch (columnName) {
            case "ID", "Pesanan" -> 130;
            case "Tanggal", "Estimasi", "Selesai/Estimasi" -> 115;
            case "Pelanggan", "Nama", "Username" -> 170;
            case "Paket" -> 150;
            case "Berat", "Harga/kg", "Total", "Jumlah", "Status", "Aktif", "Shift" -> 115;
            case "Telepon", "Kode QR" -> 135;
            case "Jenis", "Warna", "Kondisi", "Metode" -> 140;
            case "Smart Group" -> 155;
            case "Deskripsi Detail", "Catatan" -> 260;
            default -> 140;
        };
    }
}
