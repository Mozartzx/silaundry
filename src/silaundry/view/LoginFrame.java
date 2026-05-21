package silaundry.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.sql.SQLException;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import silaundry.controller.AuthController;
import silaundry.controller.PenggunaController;
import silaundry.model.Pengguna;
import silaundry.model.enums.Role;

public class LoginFrame extends JFrame {
    private final AuthController authController = new AuthController();
    private final PenggunaController penggunaController = new PenggunaController();
    private final JComboBox<Role> roleCombo = new JComboBox<>(Role.values());
    private final JTextField usernameField = new JTextField("Mozart", 24);
    private final JPasswordField passwordField = new JPasswordField("123", 24);
    private final JLabel statusLabel = new JLabel("Pemilik: Mozart / 123");

    public LoginFrame() {
        setTitle("SILAUNDRY - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1120, 700);
        setMinimumSize(new Dimension(960, 620));
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        add(buildContent(), BorderLayout.CENTER);
    }

    private JPanel buildContent() {
        JPanel root = AppTheme.page(new GridBagLayout());
        root.setBackground(new Color(205, 199, 188));

        JPanel shell = new JPanel(new GridBagLayout());
        shell.setBackground(new Color(18, 22, 24));
        shell.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(18, 22, 24), 8),
                BorderFactory.createEmptyBorder(0, 0, 0, 0)));

        GridBagConstraints left = new GridBagConstraints();
        left.gridx = 0;
        left.gridy = 0;
        left.weightx = 0.46;
        left.weighty = 1;
        left.fill = GridBagConstraints.BOTH;
        shell.add(buildBrandPanel(), left);

        GridBagConstraints right = new GridBagConstraints();
        right.gridx = 1;
        right.gridy = 0;
        right.weightx = 0.54;
        right.weighty = 1;
        right.fill = GridBagConstraints.BOTH;
        shell.add(buildLoginPanel(), right);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.insets = new Insets(24, 24, 24, 24);
        root.add(shell, gbc);
        return root;
    }

    private JPanel buildBrandPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(15, 22, 24));
        panel.setBorder(BorderFactory.createEmptyBorder(44, 48, 44, 48));

        JPanel stack = new JPanel(new GridBagLayout());
        stack.setOpaque(false);

        JLabel logo = new JLabel("S", JLabel.CENTER);
        logo.setPreferredSize(new Dimension(76, 76));
        logo.setOpaque(true);
        logo.setBackground(new Color(238, 172, 64));
        logo.setForeground(Color.WHITE);
        logo.setFont(new Font("Segoe UI", Font.BOLD, 42));
        logo.setBorder(BorderFactory.createLineBorder(new Color(244, 190, 90), 2));

        JLabel title = new JLabel("<html><div style='text-align:center'>Satu Sistem untuk<br>Operasional Laundry</div></html>", JLabel.CENTER);
        title.setForeground(Color.WHITE);
        title.setFont(AppTheme.TITLE_FONT.deriveFont(Font.BOLD, 28f));

        JLabel copy = new JLabel("<html><div style='text-align:center'>Kelola pesanan, item pakaian, smart grouping,<br>tracking, pembayaran, dan laporan dalam satu aplikasi.</div></html>", JLabel.CENTER);
        copy.setForeground(new Color(192, 203, 205));
        copy.setFont(AppTheme.SMALL_FONT);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 22, 0);
        stack.add(logo, gbc);
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 14, 0);
        stack.add(title, gbc);
        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, 0, 0);
        stack.add(copy, gbc);

        panel.add(stack);
        return panel;
    }

    private JPanel buildLoginPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(AppTheme.SURFACE);
        panel.setBorder(BorderFactory.createEmptyBorder(28, 38, 22, 38));

        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(AppTheme.SURFACE);
        JLabel miniLogo = AppTheme.sectionTitle("SILAUNDRY");
        JButton registerButton = AppTheme.secondaryButton("Daftar Pelanggan");
        registerButton.addActionListener(event -> showRegisterDialog());
        JPanel registerArea = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        registerArea.setBackground(AppTheme.SURFACE);
        registerArea.add(AppTheme.muted("Belum punya akun?"));
        registerArea.add(registerButton);
        top.add(miniLogo, BorderLayout.WEST);
        top.add(registerArea, BorderLayout.EAST);

        JPanel form = buildForm();
        JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(AppTheme.SURFACE);
        statusLabel.setForeground(AppTheme.MUTED);
        statusLabel.setFont(AppTheme.SMALL_FONT);
        footer.add(statusLabel, BorderLayout.WEST);
        footer.add(AppTheme.muted("MySQL/XAMPP + Java Swing"), BorderLayout.EAST);

        panel.add(top, BorderLayout.NORTH);
        panel.add(form, BorderLayout.CENTER);
        panel.add(footer, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel buildForm() {
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setBackground(AppTheme.SURFACE);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(AppTheme.SURFACE);
        panel.setPreferredSize(new Dimension(360, 430));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(6, 0, 6, 0);

        gbc.gridy = 0;
        JLabel title = AppTheme.title("Selamat datang kembali");
        title.setFont(AppTheme.TITLE_FONT.deriveFont(Font.BOLD, 22f));
        panel.add(center(title), gbc);

        gbc.gridy = 1;
        panel.add(center(AppTheme.muted("Pilih role lalu masuk dengan akun yang sesuai.")), gbc);

        styleAuthInputs();

        gbc.gridy = 2;
        gbc.insets = new Insets(18, 0, 5, 0);
        panel.add(new JLabel("Role"), gbc);
        gbc.gridy = 3;
        gbc.insets = new Insets(0, 0, 10, 0);
        panel.add(roleCombo, gbc);

        gbc.gridy = 4;
        gbc.insets = new Insets(4, 0, 5, 0);
        panel.add(new JLabel("Username"), gbc);
        gbc.gridy = 5;
        gbc.insets = new Insets(0, 0, 10, 0);
        panel.add(usernameField, gbc);

        gbc.gridy = 6;
        gbc.insets = new Insets(4, 0, 5, 0);
        panel.add(new JLabel("Password"), gbc);
        gbc.gridy = 7;
        gbc.insets = new Insets(0, 0, 16, 0);
        panel.add(passwordField, gbc);

        JButton loginButton = AppTheme.primaryButton("Masuk");
        loginButton.addActionListener(event -> login());
        JButton testButton = AppTheme.secondaryButton("Test DB");
        testButton.addActionListener(event -> statusLabel.setText(authController.testConnection()));

        gbc.gridy = 8;
        gbc.insets = new Insets(0, 0, 8, 0);
        panel.add(loginButton, gbc);
        gbc.gridy = 9;
        panel.add(testButton, gbc);

        roleCombo.addActionListener(event -> applyRolePreset());
        getRootPane().setDefaultButton(loginButton);

        wrapper.add(panel);
        return wrapper;
    }

    private Component center(JLabel label) {
        label.setHorizontalAlignment(JLabel.CENTER);
        return label;
    }

    private void styleAuthInputs() {
        AppTheme.styleComboBox(roleCombo);
        AppTheme.styleTextField(usernameField);
        AppTheme.styleTextField(passwordField);
    }

    private void applyRolePreset() {
        Role role = (Role) roleCombo.getSelectedItem();
        if (role == Role.PEMILIK) {
            usernameField.setText("Mozart");
            passwordField.setText("123");
            statusLabel.setText("Pemilik hanya satu akun: Mozart / 123");
        } else if (role == Role.KARYAWAN) {
            usernameField.setText("karyawan");
            passwordField.setText("karyawan123");
            statusLabel.setText("Karyawan dibuat oleh pemilik dari dashboard.");
        } else {
            usernameField.setText("pelanggan");
            passwordField.setText("pelanggan123");
            statusLabel.setText("Pelanggan bisa daftar sendiri lewat tombol Daftar Pelanggan.");
        }
    }

    private void login() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        Role role = (Role) roleCombo.getSelectedItem();
        if (username.isEmpty() || password.isEmpty()) {
            UiUtil.info(this, "Username dan password wajib diisi.");
            return;
        }
        try {
            Pengguna pengguna = authController.login(username, password, role);
            if (pengguna == null) {
                UiUtil.info(this, "Login gagal. Pastikan username, password, dan role sudah sesuai.");
                return;
            }
            dispose();
            new MainFrame(pengguna).setVisible(true);
        } catch (SQLException ex) {
            UiUtil.error(this, "Tidak bisa login karena koneksi database bermasalah.", ex);
        }
    }

    private void showRegisterDialog() {
        JTextField username = new JTextField(18);
        JTextField nama = new JTextField(18);
        JTextField telepon = new JTextField(18);
        JPasswordField password = new JPasswordField(18);
        JTextField alamat = new JTextField(18);
        AppTheme.styleTextField(username);
        AppTheme.styleTextField(nama);
        AppTheme.styleTextField(telepon);
        AppTheme.styleTextField(password);
        AppTheme.styleTextField(alamat);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(AppTheme.SURFACE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        gbc.insets = new Insets(5, 0, 5, 0);

        addRegisterRow(form, gbc, 0, "Username", username);
        addRegisterRow(form, gbc, 2, "Nama lengkap", nama);
        addRegisterRow(form, gbc, 4, "Nomor telepon", telepon);
        addRegisterRow(form, gbc, 6, "Password", password);
        addRegisterRow(form, gbc, 8, "Alamat", alamat);

        int option = JOptionPane.showConfirmDialog(
                this,
                form,
                "Daftar Akun Pelanggan",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);
        if (option != JOptionPane.OK_OPTION) {
            return;
        }

        String rawPassword = new String(password.getPassword());
        if (username.getText().trim().isEmpty() || nama.getText().trim().isEmpty()
                || telepon.getText().trim().isEmpty() || rawPassword.isEmpty()
                || alamat.getText().trim().isEmpty()) {
            UiUtil.info(this, "Semua field register pelanggan wajib diisi.");
            return;
        }

        try {
            penggunaController.tambahPelanggan(
                    username.getText().trim(),
                    nama.getText().trim(),
                    telepon.getText().trim(),
                    rawPassword,
                    alamat.getText().trim());
            roleCombo.setSelectedItem(Role.PELANGGAN);
            usernameField.setText(username.getText().trim());
            passwordField.setText(rawPassword);
            statusLabel.setText("Akun pelanggan berhasil dibuat. Silakan login.");
        } catch (SQLException ex) {
            UiUtil.error(this, "Gagal membuat akun pelanggan. Username mungkin sudah digunakan.", ex);
        }
    }

    private void addRegisterRow(JPanel form, GridBagConstraints gbc, int row, String label, Component field) {
        gbc.gridy = row;
        form.add(new JLabel(label), gbc);
        gbc.gridy = row + 1;
        form.add(field, gbc);
    }
}
