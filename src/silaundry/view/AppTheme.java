package silaundry.view;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.LayoutManager;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicButtonUI;

// Menyimpan warna, font, dan pembentuk komponen agar tampilan aplikasi konsisten.
public final class AppTheme {
    public static final Color BACKGROUND = new Color(245, 247, 250);
    public static final Color SURFACE = Color.WHITE;
    public static final Color PRIMARY = new Color(31, 111, 120);
    public static final Color PRIMARY_DARK = new Color(22, 82, 89);
    public static final Color ACCENT = new Color(233, 164, 73);
    public static final Color TEXT = new Color(31, 41, 55);
    public static final Color MUTED = new Color(107, 114, 128);
    public static final Color BORDER = new Color(222, 228, 236);
    public static final Color DANGER = new Color(190, 62, 62);

    public static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 26);
    public static final Font SECTION_FONT = new Font("Segoe UI", Font.BOLD, 16);
    public static final Font BODY_FONT = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font SMALL_FONT = new Font("Segoe UI", Font.PLAIN, 12);

    private AppTheme() {
    }

    // Pengaturan global membuat font dasar semua komponen terlihat konsisten.
    public static void applyGlobalLook() {
        UIManager.put("Panel.background", BACKGROUND);
        UIManager.put("Label.font", BODY_FONT);
        UIManager.put("Button.font", BODY_FONT);
        UIManager.put("TextField.font", BODY_FONT);
        UIManager.put("PasswordField.font", BODY_FONT);
        UIManager.put("ComboBox.font", BODY_FONT);
        UIManager.put("Table.font", BODY_FONT);
        UIManager.put("TabbedPane.font", BODY_FONT);
        UIManager.put("OptionPane.messageFont", BODY_FONT);
        UIManager.put("OptionPane.buttonFont", BODY_FONT);
    }

    public static JPanel page(LayoutManager layout) {
        JPanel panel = new JPanel(layout);
        panel.setBackground(BACKGROUND);
        panel.setBorder(new EmptyBorder(16, 16, 16, 16));
        return panel;
    }

    public static JPanel surface(LayoutManager layout) {
        JPanel panel = new JPanel(layout);
        panel.setBackground(SURFACE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                new EmptyBorder(14, 14, 14, 14)));
        return panel;
    }

    public static JPanel compactSurface(LayoutManager layout) {
        JPanel panel = surface(layout);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                new EmptyBorder(10, 12, 10, 12)));
        return panel;
    }

    public static JLabel title(String text) {
        JLabel label = new JLabel(text);
        label.setFont(TITLE_FONT);
        label.setForeground(TEXT);
        return label;
    }

    public static JLabel sectionTitle(String text) {
        JLabel label = new JLabel(text);
        label.setFont(SECTION_FONT);
        label.setForeground(TEXT);
        return label;
    }

    public static JLabel muted(String text) {
        JLabel label = new JLabel(text);
        label.setFont(SMALL_FONT);
        label.setForeground(MUTED);
        return label;
    }

    // Bagian tombol menyediakan gaya utama, sekunder, bahaya, dan sidebar.
    public static JButton primaryButton(String text) {
        JButton button = new JButton(text);
        button.setFont(BODY_FONT.deriveFont(Font.BOLD));
        styleButton(button, new Color(24, 96, 106), Color.WHITE);
        return button;
    }

    public static JButton secondaryButton(String text) {
        JButton button = new JButton(text);
        styleButton(button, SURFACE, PRIMARY_DARK);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(111, 159, 168)),
                new EmptyBorder(8, 13, 8, 13)));
        return button;
    }

    public static JButton dangerButton(String text) {
        JButton button = new JButton(text);
        button.setFont(BODY_FONT.deriveFont(Font.BOLD));
        styleButton(button, new Color(170, 45, 45), Color.WHITE);
        return button;
    }

    public static void styleButton(JButton button, Color background, Color foreground) {
        Color hoverBackground = isLight(background) ? new Color(217, 237, 239) : background.brighter();
        Color pressedBackground = isLight(background) ? new Color(198, 225, 229) : background.darker();
        Color stateForeground = isLight(background) ? PRIMARY_DARK : foreground;

        button.setUI(new BasicButtonUI());
        button.setBackground(background);
        button.setForeground(stateForeground);
        button.setFocusPainted(false);
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setRolloverEnabled(true);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(background.darker()),
                new EmptyBorder(8, 13, 8, 13)));
        button.setMinimumSize(new Dimension(96, 36));
        button.getModel().addChangeListener(event -> {
            boolean active = Boolean.TRUE.equals(button.getClientProperty("silaundry.active"));
            if (!button.isEnabled()) {
                button.setBackground(new Color(238, 241, 245));
                button.setForeground(MUTED);
            } else if (button.getModel().isPressed()) {
                button.setBackground(pressedBackground);
                button.setForeground(stateForeground);
            } else if (button.getModel().isRollover()) {
                button.setBackground(hoverBackground);
                button.setForeground(stateForeground);
            } else {
                button.setBackground(active ? hoverBackground : background);
                button.setForeground(stateForeground);
            }
        });
    }

    public static void styleLightMenuButton(JButton button) {
        styleButton(button, SURFACE, TEXT);
        button.putClientProperty("silaundry.active", false);
        button.setHorizontalAlignment(JButton.LEFT);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                new EmptyBorder(10, 10, 10, 10)));
    }

    public static void setLightMenuButtonActive(JButton button, boolean active) {
        button.putClientProperty("silaundry.active", active);
        button.setBackground(active ? new Color(217, 237, 239) : SURFACE);
        button.setForeground(active ? PRIMARY_DARK : TEXT);
    }

    public static void styleSidebarButton(JButton button) {
        Color normalBackground = new Color(27, 96, 104);
        Color hoverBackground = new Color(224, 241, 243);
        Color pressedBackground = new Color(198, 225, 229);

        button.setUI(new BasicButtonUI());
        button.putClientProperty("silaundry.active", false);
        button.setHorizontalAlignment(JButton.LEFT);
        button.setFocusPainted(false);
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setRolloverEnabled(true);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(42, 98, 105)),
                new EmptyBorder(10, 12, 10, 12)));
        button.setBackground(normalBackground);
        button.setForeground(Color.WHITE);
        button.getModel().addChangeListener(event -> {
            boolean active = Boolean.TRUE.equals(button.getClientProperty("silaundry.active"));
            if (button.getModel().isPressed()) {
                button.setBackground(pressedBackground);
                button.setForeground(PRIMARY_DARK);
            } else if (button.getModel().isRollover()) {
                button.setBackground(hoverBackground);
                button.setForeground(PRIMARY_DARK);
            } else if (active) {
                button.setBackground(ACCENT);
                button.setForeground(TEXT);
            } else {
                button.setBackground(normalBackground);
                button.setForeground(Color.WHITE);
            }
        });
    }

    public static void setSidebarButtonActive(JButton button, boolean active) {
        button.putClientProperty("silaundry.active", active);
        button.setBackground(active ? ACCENT : new Color(27, 96, 104));
        button.setForeground(active ? TEXT : Color.WHITE);
    }

    private static boolean isLight(Color color) {
        double brightness = (color.getRed() * 299 + color.getGreen() * 587 + color.getBlue() * 114) / 1000.0;
        return brightness >= 180;
    }

    // Bagian form membantu menyusun label dan input dengan GridBagLayout.
    public static JPanel formGrid() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(SURFACE);
        return panel;
    }

    public static void addField(JPanel panel, int row, int pairColumn, String labelText, JComponent field) {
        GridBagConstraints labelConstraints = new GridBagConstraints();
        labelConstraints.gridx = pairColumn * 2;
        labelConstraints.gridy = row;
        labelConstraints.insets = new Insets(5, pairColumn == 0 ? 0 : 18, 5, 8);
        labelConstraints.anchor = GridBagConstraints.LINE_START;
        JLabel label = new JLabel(labelText);
        label.setForeground(MUTED);
        label.setFont(SMALL_FONT);
        panel.add(label, labelConstraints);

        GridBagConstraints fieldConstraints = new GridBagConstraints();
        fieldConstraints.gridx = pairColumn * 2 + 1;
        fieldConstraints.gridy = row;
        fieldConstraints.insets = new Insets(5, 0, 5, 0);
        fieldConstraints.anchor = GridBagConstraints.LINE_START;
        fieldConstraints.fill = GridBagConstraints.HORIZONTAL;
        fieldConstraints.weightx = 1;
        panel.add(field, fieldConstraints);
    }

    public static void addWideField(JPanel panel, int row, String labelText, JComponent field) {
        GridBagConstraints labelConstraints = new GridBagConstraints();
        labelConstraints.gridx = 0;
        labelConstraints.gridy = row;
        labelConstraints.insets = new Insets(5, 0, 5, 8);
        labelConstraints.anchor = GridBagConstraints.LINE_START;
        JLabel label = new JLabel(labelText);
        label.setForeground(MUTED);
        label.setFont(SMALL_FONT);
        panel.add(label, labelConstraints);

        GridBagConstraints fieldConstraints = new GridBagConstraints();
        fieldConstraints.gridx = 1;
        fieldConstraints.gridy = row;
        fieldConstraints.gridwidth = 3;
        fieldConstraints.insets = new Insets(5, 0, 5, 0);
        fieldConstraints.fill = GridBagConstraints.HORIZONTAL;
        fieldConstraints.weightx = 1;
        panel.add(field, fieldConstraints);
    }

    public static JPanel actionRow() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        panel.setBackground(SURFACE);
        return panel;
    }

    // Bagian input menyamakan tampilan text field, text area, combo box, dan spinner.
    public static void styleTextField(JTextField field) {
        field.setFont(BODY_FONT);
        field.setForeground(TEXT);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                new EmptyBorder(7, 9, 7, 9)));
    }

    public static void styleTextArea(JTextArea area) {
        area.setFont(BODY_FONT);
        area.setForeground(TEXT);
        area.setBackground(SURFACE);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setBorder(new EmptyBorder(10, 10, 10, 10));
    }

    public static void styleComboBox(JComboBox<?> comboBox) {
        comboBox.setFont(BODY_FONT);
        comboBox.setBackground(SURFACE);
        comboBox.setForeground(TEXT);
    }

    public static void styleSpinner(JSpinner spinner) {
        spinner.setFont(BODY_FONT);
        JComponent editor = spinner.getEditor();
        if (editor instanceof JSpinner.DefaultEditor defaultEditor) {
            styleTextField(defaultEditor.getTextField());
        }
    }

    public static JScrollPane scroll(Component component) {
        JScrollPane scrollPane = new JScrollPane(component);
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER));
        scrollPane.getViewport().setBackground(SURFACE);
        return scrollPane;
    }
}
