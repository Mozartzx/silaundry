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
import silaundry.service.TrackingResult;

public class TrackingPanel extends JPanel {
    private final ItemController itemController = new ItemController();
    private final JTextField trackingField = new JTextField(24);
    private final JTextArea resultArea = new JTextArea(12, 60);

    public TrackingPanel() {
        setLayout(new BorderLayout(8, 8));
        setBackground(AppTheme.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        JPanel content = AppTheme.surface(new BorderLayout(8, 12));
        JPanel form = new JPanel(new FlowLayout(FlowLayout.LEFT));
        form.setBackground(AppTheme.SURFACE);
        AppTheme.styleTextField(trackingField);
        JLabel title = AppTheme.sectionTitle("Item Tracking");
        form.add(title);
        form.add(AppTheme.muted("Masukkan ID item atau kode QR teks"));
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
        try {
            TrackingResult result = itemController.lacakItem(key);
            resultArea.setText(result.toSummary());
        } catch (SQLException ex) {
            UiUtil.error(this, "Gagal melacak item.", ex);
        }
    }
}
