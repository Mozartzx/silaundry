package silaundry;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import silaundry.view.AppTheme;
import silaundry.view.LoginFrame;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
                // Default Swing look and feel is still usable.
            }
            AppTheme.applyGlobalLook();
            new LoginFrame().setVisible(true);
        });
    }
}
