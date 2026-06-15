package silaundry.view;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import silaundry.model.Karyawan;
import silaundry.model.Pelanggan;
import silaundry.model.Pemilik;
import silaundry.model.Pengguna;

// Kerangka dashboard yang membentuk sidebar dan halaman sesuai role pengguna.
public class MainFrame extends JFrame {
    private final Pengguna pengguna;
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel contentPanel = new JPanel(cardLayout);
    private final List<JButton> navButtons = new ArrayList<>();
    private JPanel navList;

    public MainFrame(Pengguna pengguna) {
        this.pengguna = pengguna;
        setTitle("SILAUNDRY - " + pengguna.getRole().getDisplayName());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1180, 760);
        setMinimumSize(new Dimension(980, 640));
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        add(buildShell(), BorderLayout.CENTER);
    }

    private JPanel buildShell() {
        // Menu yang didaftarkan ditentukan dari subclass pengguna hasil login.
        JPanel shell = new JPanel(new BorderLayout());
        shell.setBackground(AppTheme.BACKGROUND);
        shell.add(buildSidebar(), BorderLayout.WEST);
        shell.add(contentPanel, BorderLayout.CENTER);

        if (pengguna instanceof Pelanggan pelanggan) {
            addRoute("Status Laundry", "Pantau pesanan dan riwayat", new PelangganPanel(pelanggan));
        }
        if (pengguna instanceof Karyawan karyawan) {
            addRoute("Pesanan", "Tambah dan update pesanan",
                    new KaryawanPanel(karyawan, KaryawanPanel.Page.PESANAN));
            addRoute("Item Pakaian", "Catat ciri dan kelompok warna",
                    new KaryawanPanel(karyawan, KaryawanPanel.Page.ITEM_PAKAIAN));
            addRoute("Pembayaran", "Catat pembayaran lunas",
                    new KaryawanPanel(karyawan, KaryawanPanel.Page.PEMBAYARAN));
        }
        if (pengguna instanceof Pemilik) {
            addRoute("Dashboard", "Ringkasan usaha", new PemilikPanel(PemilikPanel.Page.DASHBOARD));
            addRoute("Pantau Pesanan", "Semua transaksi laundry", new PemilikPanel(PemilikPanel.Page.PESANAN));
            addRoute("Daftar Pelanggan", "Lihat pelanggan terdaftar", new PemilikPanel(PemilikPanel.Page.PELANGGAN));
            addRoute("Kelola Karyawan", "Tambah dan hapus akun", new PemilikPanel(PemilikPanel.Page.KARYAWAN));
            addRoute("Tarif Laundry", "Atur harga per kilo", new PemilikPanel(PemilikPanel.Page.TARIF));
        }
        selectFirstRoute();
        return shell;
    }

    private JPanel buildSidebar() {
        // Sidebar tetap sama, sedangkan daftar menunya menyesuaikan role.
        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setPreferredSize(new Dimension(220, 0));
        sidebar.setBackground(AppTheme.PRIMARY_DARK);
        sidebar.setBorder(new EmptyBorder(20, 16, 16, 16));

        JPanel brand = new JPanel();
        brand.setLayout(new BoxLayout(brand, BoxLayout.Y_AXIS));
        brand.setOpaque(false);

        JLabel appLabel = new JLabel("SILAUNDRY");
        appLabel.setFont(AppTheme.TITLE_FONT.deriveFont(Font.BOLD, 22f));
        appLabel.setForeground(Color.WHITE);
        JLabel roleLabel = new JLabel(pengguna.getRole().getDisplayName());
        roleLabel.setFont(AppTheme.SMALL_FONT);
        roleLabel.setForeground(new Color(202, 226, 230));
        JLabel userLabel = new JLabel(pengguna.getNamaLengkap());
        userLabel.setFont(AppTheme.BODY_FONT);
        userLabel.setForeground(new Color(235, 244, 246));
        brand.add(appLabel);
        brand.add(Box.createVerticalStrut(4));
        brand.add(roleLabel);
        brand.add(Box.createVerticalStrut(18));
        brand.add(userLabel);

        navList = new JPanel();
        navList.setLayout(new BoxLayout(navList, BoxLayout.Y_AXIS));
        navList.setOpaque(false);

        JButton logoutButton = sidebarButton("Logout", "Keluar dari aplikasi");
        logoutButton.addActionListener(event -> {
            if (!UiUtil.confirm(this, "Keluar dari akun " + pengguna.getNamaLengkap() + "?")) {
                return;
            }
            pengguna.logout();
            dispose();
            new LoginFrame().setVisible(true);
        });

        sidebar.add(brand, BorderLayout.NORTH);
        sidebar.add(navList, BorderLayout.CENTER);
        sidebar.add(logoutButton, BorderLayout.SOUTH);
        return sidebar;
    }

    private void addRoute(String title, String subtitle, Component content) {
        // Setiap route mempunyai tombol navigasi dan panel CardLayout dengan nama yang sama.
        contentPanel.add(content, title);
        JButton button = sidebarButton(title, subtitle);
        button.addActionListener(event -> {
            cardLayout.show(contentPanel, title);
            setActiveButton(button);
            if (content instanceof KaryawanPanel panel) {
                panel.refreshData();
            } else if (content instanceof PemilikPanel panel) {
                panel.refreshData();
            }
        });
        navList.add(Box.createVerticalStrut(10));
        navList.add(button);
        navButtons.add(button);
    }

    private JButton sidebarButton(String title, String subtitle) {
        JButton button = new JButton("<html><b>" + title + "</b><br><span style='font-size:10px'>" + subtitle + "</span></html>");
        AppTheme.styleSidebarButton(button);
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 58));
        return button;
    }

    private void selectFirstRoute() {
        if (!navButtons.isEmpty()) {
            navButtons.get(0).doClick();
        }
    }

    private void setActiveButton(JButton activeButton) {
        for (JButton button : navButtons) {
            AppTheme.setSidebarButtonActive(button, button == activeButton);
        }
    }
}
