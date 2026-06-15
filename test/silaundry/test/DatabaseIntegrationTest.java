package silaundry.test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import silaundry.controller.ItemController;
import silaundry.controller.PembayaranController;
import silaundry.controller.PesananController;
import silaundry.dao.DashboardDAO;
import silaundry.model.Pembayaran;
import silaundry.model.enums.KategoriWarna;
import silaundry.model.enums.StatusPembayaran;
import silaundry.model.enums.StatusPesanan;
import silaundry.util.DatabaseConnection;
import silaundry.util.PasswordUtil;

public final class DatabaseIntegrationTest {
    private static final String CUSTOMER_ID = "TSTPLG";
    private static final String EMPLOYEE_ID = "TSTKRY";

    private DatabaseIntegrationTest() {
    }

    public static void main(String[] args) throws Exception {
        cleanup();
        try {
            setup();
            testPembayaranLunas();
            testSmartGroupingEksplisit();
            testPenguncianItem();
            testStatusDanNotifikasiAtomik();
            System.out.println("Semua database integration test berhasil.");
        } finally {
            cleanup();
        }
    }

    private static void testPembayaranLunas() throws Exception {
        PembayaranController controller = new PembayaranController();
        DashboardDAO dashboardDAO = new DashboardDAO();
        double pendapatanSebelum = dashboardDAO.getDataDasbor().getPendapatanDiterima();
        Pembayaran pembayaran = controller.catatPembayaran("TSTORD1", "Tunai");
        check(pembayaran.getJumlah() == 10_000 && pembayaran.getStatus() == StatusPembayaran.LUNAS,
                "Pembayaran harus langsung mengikuti total tagihan dan berstatus LUNAS");
        check(queryInt("SELECT COUNT(*) FROM pembayaran WHERE id_pesanan='TSTORD1'") == 1,
                "Satu pesanan hanya memiliki satu pembayaran");
        check(dashboardDAO.getDataDasbor().getPendapatanDiterima() == pendapatanSebelum + 10_000,
                "Pembayaran lunas harus masuk pendapatan dashboard");
        check(dashboardDAO.getLaporanBulanIni().getTotalPendapatan() == pendapatanSebelum + 10_000,
                "Laporan harus memakai nominal pembayaran lunas");

        expectIllegalArgument(() -> controller.catatPembayaran("TSTORD1", "QRIS"));
        expectIllegalArgument(() -> new PesananController().batalkanPesanan("TSTORD1"));
    }

    private static void testSmartGroupingEksplisit() throws Exception {
        ItemController controller = new ItemController();
        controller.tambahItem("TSTORD1", "Kemeja", KategoriWarna.PUTIH, "Normal", "Ukuran M");
        check("Belum Dikelompokkan".equals(queryString(
                "SELECT label_smart_group FROM item_pakaian WHERE id_pesanan='TSTORD1' LIMIT 1")),
                "Item baru tidak boleh langsung dianggap sudah dikelompokkan");
        check(controller.jalankanSmartGrouping("TSTORD1") == 1,
                "Smart grouping harus memproses satu item");
        check("Grup Putih".equals(queryString(
                "SELECT label_smart_group FROM item_pakaian WHERE id_pesanan='TSTORD1' LIMIT 1")),
                "Smart grouping harus menyimpan label berdasarkan kategori warna");
    }

    private static void testPenguncianItem() throws Exception {
        ItemController controller = new ItemController();
        expectIllegalArgument(() -> controller.tambahItem(
                "TSTORD2", "Kaos", KategoriWarna.GELAP, "Normal", "Kaos hitam ukuran L"));
        expectIllegalArgument(() -> controller.hapusItem("TSTITM2"));
    }

    private static void testStatusDanNotifikasiAtomik() throws Exception {
        PesananController controller = new PesananController();
        check(controller.updateStatus("TSTORD3", StatusPesanan.SIAP_DIAMBIL),
                "Status DISETRIKA harus dapat menjadi SIAP_DIAMBIL");
        check("SIAP_DIAMBIL".equals(queryString(
                "SELECT status_pesanan FROM pesanan WHERE id_pesanan='TSTORD3'")),
                "Status pesanan harus tersimpan");
        check(queryInt("SELECT COUNT(*) FROM notifikasi WHERE id_pesanan='TSTORD3'") == 1,
                "Perubahan status harus membuat satu notifikasi");
        check(!controller.updateStatus("TSTORD3", StatusPesanan.SIAP_DIAMBIL),
                "Update ke status yang sama tidak boleh dianggap perubahan");
        check(queryInt("SELECT COUNT(*) FROM notifikasi WHERE id_pesanan='TSTORD3'") == 1,
                "Retry status yang sama tidak boleh menggandakan notifikasi");
    }

    private static void setup() throws SQLException {
        String hash = PasswordUtil.hash("testing123");
        try (Connection connection = DatabaseConnection.getConnection()) {
            connection.setAutoCommit(false);
            try (PreparedStatement user = connection.prepareStatement(
                    "INSERT INTO pengguna (id_pengguna,username,nama_lengkap,nomor_telepon,kata_sandi,role) "
                            + "VALUES (?,?,?,?,?,?)")) {
                insertUser(user, "TSTUSR1", "testpegawai", "Test Pegawai", "081111111111", hash, "KARYAWAN");
                insertUser(user, "TSTUSR2", "testpelanggan", "Test Pelanggan", "082222222222", hash, "PELANGGAN");
            }
            execute(connection, "INSERT INTO karyawan VALUES ('" + EMPLOYEE_ID + "','TSTUSR1','Pagi')");
            execute(connection, "INSERT INTO pelanggan VALUES ('" + CUSTOMER_ID + "','TSTUSR2','Alamat pengujian')");
            insertOrder(connection, "TSTORD1", "BARU", 10_000);
            insertOrder(connection, "TSTORD2", "DICUCI", 12_000);
            insertOrder(connection, "TSTORD3", "DISETRIKA", 15_000);
            execute(connection, "INSERT INTO item_pakaian VALUES "
                    + "('TSTITM2','TSTORD2','Kaos','GELAP','Normal','Kaos hitam ukuran L',"
                    + "'Grup Gelap'),"
                    + "('TSTITM3','TSTORD3','Kemeja','PUTIH','Normal','Kemeja putih lengan panjang',"
                    + "'Grup Putih')");
            connection.commit();
        }
    }

    private static void insertUser(PreparedStatement statement, String id, String username, String nama,
            String telepon, String hash, String role) throws SQLException {
        statement.setString(1, id);
        statement.setString(2, username);
        statement.setString(3, nama);
        statement.setString(4, telepon);
        statement.setString(5, hash);
        statement.setString(6, role);
        statement.executeUpdate();
    }

    private static void insertOrder(Connection connection, String id, String status, double total)
            throws SQLException {
        String sql = "INSERT INTO pesanan (id_pesanan,id_pelanggan,id_karyawan,tanggal_masuk,estimasi_selesai,"
                + "status_pesanan,paket_laundry,berat_kg,harga_per_kg,total_biaya,catatan) "
                + "VALUES (?,?,?,CURRENT_DATE(),DATE_ADD(CURRENT_DATE(),INTERVAL 2 DAY),"
                + "?,'STANDARD_2_HARI',2,5000,?,'Data integration test')";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, id);
            statement.setString(2, CUSTOMER_ID);
            statement.setString(3, EMPLOYEE_ID);
            statement.setString(4, status);
            statement.setDouble(5, total);
            statement.executeUpdate();
        }
    }

    private static void cleanup() throws SQLException {
        try (Connection connection = DatabaseConnection.getConnection()) {
            execute(connection, "DELETE FROM pesanan WHERE id_pesanan LIKE 'TSTORD%'");
            execute(connection, "DELETE FROM pelanggan WHERE id_pelanggan='" + CUSTOMER_ID + "'");
            execute(connection, "DELETE FROM karyawan WHERE id_karyawan='" + EMPLOYEE_ID + "'");
            execute(connection, "DELETE FROM pengguna WHERE id_pengguna IN ('TSTUSR1','TSTUSR2')");
        }
    }

    private static void execute(Connection connection, String sql) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(sql);
        }
    }

    private static int queryInt(String sql) throws SQLException {
        return (int) queryDouble(sql);
    }

    private static double queryDouble(String sql) throws SQLException {
        try (Connection connection = DatabaseConnection.getConnection();
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(sql)) {
            resultSet.next();
            return resultSet.getDouble(1);
        }
    }

    private static String queryString(String sql) throws SQLException {
        try (Connection connection = DatabaseConnection.getConnection();
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(sql)) {
            resultSet.next();
            return resultSet.getString(1);
        }
    }

    private static void expectIllegalArgument(ThrowingAction action) throws Exception {
        try {
            action.run();
            throw new AssertionError("Operasi seharusnya ditolak");
        } catch (IllegalArgumentException expected) {
            // Expected business-rule rejection.
        }
    }

    private static void check(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }

    @FunctionalInterface
    private interface ThrowingAction {
        void run() throws Exception;
    }
}
