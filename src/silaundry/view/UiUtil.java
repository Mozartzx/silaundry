package silaundry.view;

import java.awt.Component;
import java.awt.Color;
import java.awt.Cursor;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.function.Consumer;
import javax.swing.JOptionPane;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JTextField;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableRowSorter;
import javax.swing.RowFilter;

// Berisi helper tabel, dialog, format rupiah, dropdown, dan eksekusi aksi dari view.
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
            if ("Status".equals(name)) {
                column.setCellRenderer(new StatusRenderer());
            }
        }
    }

    public static String money(double amount) {
        return RUPIAH.format(amount);
    }

    public static void info(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "SILAUNDRY", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void error(Component parent, String message, Exception ex) {
        if (ex != null) {
            ex.printStackTrace();
        }
        JOptionPane.showMessageDialog(parent, message + "\nSilakan periksa koneksi atau data yang dimasukkan.",
                "Terjadi Kesalahan", JOptionPane.ERROR_MESSAGE);
    }

    public static boolean confirm(Component parent, String message) {
        return JOptionPane.showConfirmDialog(parent, message, "Konfirmasi",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION;
    }

    public static <T> void runTask(Component parent, Task<T> task,
            Consumer<T> onSuccess, String errorMessage) {
        // Helper ini menyamakan cursor, penanganan error, dan callback sukses di semua view.
        parent.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        try {
            T result = task.run();
            parent.setCursor(Cursor.getDefaultCursor());
            onSuccess.accept(result);
        } catch (IllegalArgumentException ex) {
            parent.setCursor(Cursor.getDefaultCursor());
            info(parent, ex.getMessage());
        } catch (Exception ex) {
            parent.setCursor(Cursor.getDefaultCursor());
            error(parent, errorMessage, ex);
        } finally {
            parent.setCursor(Cursor.getDefaultCursor());
        }
    }

    public static void installSearch(JTable table, JTextField searchField) {
        // Isi tabel difilter secara langsung tanpa mengubah data aslinya.
        TableRowSorter<javax.swing.table.TableModel> sorter = new TableRowSorter<>(table.getModel());
        table.setRowSorter(sorter);
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            private void update() {
                String keyword = searchField.getText().trim();
                sorter.setRowFilter(keyword.isEmpty() ? null
                        : RowFilter.regexFilter("(?i)" + java.util.regex.Pattern.quote(keyword)));
            }

            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent event) {
                update();
            }

            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent event) {
                update();
            }

            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent event) {
                update();
            }
        });
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
            case "Tanggal", "Estimasi", "Selesai/Estimasi", "Waktu" -> 150;
            case "Pelanggan", "Nama", "Username" -> 170;
            case "Paket" -> 150;
            case "Berat", "Harga/kg", "Total", "Jumlah", "Status", "Aktif", "Shift", "Status Baca" -> 120;
            case "Telepon" -> 135;
            case "Jenis", "Warna", "Kondisi", "Metode" -> 140;
            case "Smart Group" -> 155;
            case "Deskripsi Detail", "Catatan", "Pesan" -> 300;
            default -> 140;
        };
    }

    public static void installComboPlaceholder(JComboBox<?> comboBox, String placeholder) {
        // Renderer ini menampilkan petunjuk tanpa memasukkan teks placeholder sebagai data pilihan.
        comboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                    boolean selected, boolean focused) {
                Component component = super.getListCellRendererComponent(list, value, index, selected, focused);
                if (value == null && index == -1) {
                    setText(placeholder);
                    if (!selected) {
                        setForeground(AppTheme.MUTED);
                    }
                }
                return component;
            }
        });
        comboBox.setSelectedIndex(-1);
    }

    private static final class StatusRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean selected,
                boolean focused, int row, int column) {
            Component component = super.getTableCellRendererComponent(table, value, selected, focused, row, column);
            if (!selected) {
                String status = value == null ? "" : value.toString().toLowerCase(Locale.ROOT);
                component.setBackground(status.contains("siap") || status.contains("lunas")
                        ? new Color(220, 245, 228)
                        : status.contains("batal") ? new Color(252, 226, 226)
                        : status.contains("selesai") ? new Color(232, 237, 243)
                        : Color.WHITE);
                component.setForeground(AppTheme.TEXT);
            }
            return component;
        }
    }

    @FunctionalInterface
    public interface Task<T> {
        T run() throws Exception;
    }
}
