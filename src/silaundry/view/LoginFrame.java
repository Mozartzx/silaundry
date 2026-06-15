package silaundry.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JCheckBox;
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

// Halaman awal untuk memilih role, login, mendaftar pelanggan, dan menguji database.
public class LoginFrame extends JFrame {
    private final AuthController authController = new AuthController();
    private final PenggunaController penggunaController = new PenggunaController();
    private final JComboBox<Role> roleCombo = new JComboBox<>(Role.values());
    private final JTextField usernameField = new JTextField("Master", 24);
    private final JPasswordField passwordField = new JPasswordField(24);
    private final JLabel statusLabel = new JLabel("Koneksi database belum diuji");

    public LoginFrame() {
        setTitle("SILAUNDRY - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 680);
        setMinimumSize(new Dimension(960, 620));
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        add(buildContent(), BorderLayout.CENTER);
    }

    private JPanel buildContent() {
        // Halaman dibagi menjadi panel merek di kiri dan form login di kanan.
        JPanel root = AppTheme.page(new GridBagLayout());
        root.setBackground(new Color(205, 199, 188));

        JPanel shell = new JPanel(new GridBagLayout());
        shell.setBackground(new Color(18, 22, 24));
        shell.setBorder(BorderFactory.createLineBorder(new Color(18, 22, 24), 8));

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
        gbc.insets = new Insets(8, 8, 8, 8);
        root.add(shell, gbc);
        return root;
    }

    private JPanel buildBrandPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(15, 22, 24));
        panel.setBorder(BorderFactory.createEmptyBorder(36, 48, 36, 28));

        // Wordmark dibuat sederhana seperti referensi agar panel kiri terasa lebih bersih.
        JLabel wordmark = new JLabel("Silaundry");
        wordmark.setForeground(Color.WHITE);
        wordmark.setFont(new Font("Segoe UI", Font.PLAIN, 72));
        wordmark.setHorizontalAlignment(JLabel.LEFT);
        wordmark.setVerticalAlignment(JLabel.CENTER);
        panel.add(wordmark, BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildLoginPanel() {
        // Footer Test DB dipisahkan dari form supaya bukan bagian dari proses login utama.
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(AppTheme.SURFACE);
        panel.setBorder(BorderFactory.createEmptyBorder(38, 54, 18, 54));

        JPanel footer = new JPanel(new GridBagLayout());
        footer.setBackground(AppTheme.SURFACE);
        JButton testButton = AppTheme.secondaryButton("Test DB");
        testButton.addActionListener(event -> UiUtil.runTask(
                this,
                authController::testConnection,
                statusLabel::setText,
                "Tidak dapat menguji koneksi database."));
        statusLabel.setForeground(AppTheme.MUTED);
        statusLabel.setFont(AppTheme.SMALL_FONT);
        GridBagConstraints footerGbc = new GridBagConstraints();
        footerGbc.gridx = 0;
        footerGbc.weightx = 0;
        footerGbc.anchor = GridBagConstraints.WEST;
        footer.add(testButton, footerGbc);
        footerGbc.gridx = 1;
        footerGbc.weightx = 1;
        footerGbc.insets = new Insets(0, 16, 0, 0);
        footer.add(statusLabel, footerGbc);

        panel.add(buildForm(), BorderLayout.CENTER);
        panel.add(footer, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel buildForm() {
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setBackground(AppTheme.SURFACE);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(AppTheme.SURFACE);
        panel.setPreferredSize(new Dimension(440, 510));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(6, 0, 6, 0);

        gbc.gridy = 0;
        JLabel title = AppTheme.title("Selamat datang kembali");
        title.setFont(AppTheme.TITLE_FONT.deriveFont(Font.BOLD, 28f));
        panel.add(title, gbc);

        gbc.gridy = 1;
        gbc.insets = new Insets(2, 0, 22, 0);
        panel.add(AppTheme.muted("Pilih role lalu masuk dengan akun yang sesuai."), gbc);

        styleAuthInputs();

        gbc.gridy = 2;
        gbc.insets = new Insets(6, 0, 5, 0);
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
        gbc.insets = new Insets(0, 0, 6, 0);
        panel.add(passwordField, gbc);

        JCheckBox showPassword = new JCheckBox("Tampilkan password");
        showPassword.setBackground(AppTheme.SURFACE);
        showPassword.setForeground(AppTheme.MUTED);
        char defaultEchoChar = passwordField.getEchoChar();
        showPassword.addActionListener(event -> passwordField.setEchoChar(
                showPassword.isSelected() ? '\0' : defaultEchoChar));
        gbc.gridy = 8;
        gbc.insets = new Insets(0, 0, 12, 0);
        panel.add(showPassword, gbc);

        JButton loginButton = AppTheme.primaryButton("Masuk");
        loginButton.addActionListener(event -> login());
        JButton registerButton = AppTheme.secondaryButton("Belum punya akun? Daftar sebagai pelanggan");
        registerButton.addActionListener(event -> showRegisterDialog());

        gbc.gridy = 9;
        gbc.insets = new Insets(0, 0, 8, 0);
        panel.add(loginButton, gbc);
        gbc.gridy = 10;
        gbc.insets = new Insets(0, 0, 0, 0);
        panel.add(registerButton, gbc);

        roleCombo.addActionListener(event -> applyRolePreset());
        roleCombo.setSelectedItem(Role.PEMILIK);
        applyRolePreset();
        getRootPane().setDefaultButton(loginButton);

        wrapper.add(panel);
        return wrapper;
    }

    private void styleAuthInputs() {
        AppTheme.styleComboBox(roleCombo);
        AppTheme.styleTextField(usernameField);
        AppTheme.styleTextField(passwordField);
    }

    private void applyRolePreset() {
        // Setiap role diberi arahan login yang berbeda supaya pengguna tidak perlu menebak akun.
        Role role = (Role) roleCombo.getSelectedItem();
        if (role == null) {
            usernameField.setText("");
            passwordField.setText("");
        } else if (role == Role.PEMILIK) {
            usernameField.setText("Master");
            passwordField.setText("");
        } else if (role == Role.KARYAWAN) {
            usernameField.setText("");
            passwordField.setText("");
        } else {
            usernameField.setText("");
            passwordField.setText("");
        }
    }

    private void login() {
        // View hanya mengambil input, sedangkan pengecekan akun dilakukan controller.
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        Role role = (Role) roleCombo.getSelectedItem();
        if (role == null) {
            UiUtil.info(this, "Pilih role pengguna terlebih dahulu.");
            return;
        }
        if (username.isEmpty() || password.isEmpty()) {
            UiUtil.info(this, "Username dan password wajib diisi.");
            return;
        }
        UiUtil.runTask(this, () -> authController.login(username, password, role), pengguna -> {
            if (pengguna == null) {
                UiUtil.info(this, "Login gagal. Pastikan username, password, dan role sudah sesuai.");
                return;
            }
            dispose();
            new MainFrame(pengguna).setVisible(true);
        }, "Tidak bisa login karena koneksi database bermasalah.");
    }

    private void showRegisterDialog() {
        // Register hanya membuat role pelanggan karena akun karyawan dibuat oleh pemilik.
        JTextField username = new JTextField(18);
        JTextField nama = new JTextField(18);
        JTextField telepon = new JTextField(18);
        JPasswordField password = new JPasswordField(18);
        JPasswordField konfirmasiPassword = new JPasswordField(18);
        JTextField alamat = new JTextField(18);
        AppTheme.styleTextField(username);
        AppTheme.styleTextField(nama);
        AppTheme.styleTextField(telepon);
        AppTheme.styleTextField(password);
        AppTheme.styleTextField(konfirmasiPassword);
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
        addRegisterRow(form, gbc, 8, "Konfirmasi password", konfirmasiPassword);
        addRegisterRow(form, gbc, 10, "Alamat", alamat);

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
        String rawKonfirmasi = new String(konfirmasiPassword.getPassword());
        if (username.getText().trim().isEmpty() || nama.getText().trim().isEmpty()
                || telepon.getText().trim().isEmpty() || rawPassword.isEmpty()
                || alamat.getText().trim().isEmpty()) {
            UiUtil.info(this, "Semua field register pelanggan wajib diisi.");
            return;
        }
        if (!rawPassword.equals(rawKonfirmasi)) {
            UiUtil.info(this, "Konfirmasi password tidak sama.");
            return;
        }

        String usernameBaru = username.getText().trim();
        String namaBaru = nama.getText().trim();
        String teleponBaru = telepon.getText().trim();
        String alamatBaru = alamat.getText().trim();
        UiUtil.runTask(this, () -> {
            penggunaController.tambahPelanggan(usernameBaru, namaBaru, teleponBaru, rawPassword, alamatBaru);
            return null;
        }, ignored -> {
            roleCombo.setSelectedItem(Role.PELANGGAN);
            usernameField.setText(usernameBaru);
            passwordField.setText(rawPassword);
            UiUtil.info(this, "Akun pelanggan berhasil dibuat. Silakan login.");
        }, "Gagal membuat akun pelanggan. Username mungkin sudah digunakan.");
    }

    private void addRegisterRow(JPanel form, GridBagConstraints gbc, int row, String label, Component field) {
        gbc.gridy = row;
        form.add(new JLabel(label), gbc);
        gbc.gridy = row + 1;
        form.add(field, gbc);
    }
}
