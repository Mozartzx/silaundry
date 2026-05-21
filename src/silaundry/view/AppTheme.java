package silaundry.view;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Font;
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

    public static JButton primaryButton(String text) {
        JButton button = new JButton(text);
        styleButton(button, PRIMARY, Color.WHITE);
        return button;
    }

    public static JButton secondaryButton(String text) {
        JButton button = new JButton(text);
        styleButton(button, SURFACE, PRIMARY_DARK);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(183, 202, 207)),
                new EmptyBorder(8, 13, 8, 13)));
        return button;
    }

    public static JButton dangerButton(String text) {
        JButton button = new JButton(text);
        styleButton(button, DANGER, Color.WHITE);
        return button;
    }

    public static void styleButton(JButton button, Color background, Color foreground) {
        button.setBackground(background);
        button.setForeground(foreground);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(background.darker()),
                new EmptyBorder(8, 13, 8, 13)));
    }

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
