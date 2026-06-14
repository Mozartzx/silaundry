package silaundry.view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.sql.SQLException;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import silaundry.controller.ItemController;
import silaundry.model.Pelanggan;
import silaundry.model.Pengguna;
import silaundry.service.TrackingResult;

public class TrackingPanel extends JPanel {
    private final ItemController itemController = new ItemController();
    private final Pengguna pengguna;
    private final JTextField trackingField = new JTextField(24);
    private final JTextArea resultArea = new JTextArea(12, 60);

    public TrackingPanel(Pengguna pengguna) {
        this.pengguna = pengguna;
        setLayout(new BorderLayout(8, 8));
        setBackground(AppTheme.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        JPanel content = AppTheme.surface(new BorderLayout(8, 12));
        JPanel form = new JPanel(new FlowLayout(FlowLayout.LEFT));
        form.setBackground(AppTheme.SURFACE);
        AppTheme.styleTextField(trackingField);
        JLabel title = AppTheme.sectionTitle("Item Tracking");
        form.add(title);
        form.add(AppTheme.muted(pengguna instanceof Pelanggan
                ? "Masukkan ID item atau QR dari pesanan Anda"
                : "Masukkan ID item atau kode QR teks"));
        form.add(trackingField);
        JButton trackButton = AppTheme.primaryButton("Lacak");
        trackButton.addActionListener(event -> track());
        form.add(trackButton);
        content.add(form, BorderLayout.NORTH);

        resultArea.setEditable(false);
        resultArea.setText("Hasil tracking akan tampil di sini.");
        AppTheme.styleTextArea(resultArea);
        content.add(AppTheme.scroll(resultArea), BorderLayout.CENTER);
        add(content, BorderLayout.CENTER);
    }

    private void track() {
        String key = trackingField.getText().trim();
        if (key.isEmpty()) {
            UiUtil.info(this, "Isi ID item atau kode QR teks.");
            return;
        }
        UiUtil.runAsync(
                this,
                () -> itemController.lacakItem(key, pengguna),
                result -> resultArea.setText(result.toSummary()),
                "Gagal melacak item.");
    }
}
