# SILAUNDRY - Sistem Informasi Manajemen Laundry

SILAUNDRY adalah aplikasi desktop Java Swing untuk membantu simulasi operasional laundry. Project ini berfokus pada penerapan materi Pemrograman Berorientasi Objek dan menggunakan `ArrayList` sebagai penyimpanan sementara.

Program tidak memakai database. Seluruh akun pelanggan, pesanan, item pakaian, pembayaran, dan notifikasi akan kembali kosong ketika aplikasi ditutup.

## Role Pengguna

### Admin

- Login menggunakan akun awal.
- Melihat dashboard dan laporan.
- Melihat pelanggan yang sudah register.
- Membuat dan mengubah status pesanan.
- Mencatat detail pakaian.
- Menjalankan smart grouping.
- Mencatat pembayaran.
- Mengatur tarif laundry.
- Membuat template link WhatsApp.

### Pelanggan

- Membuat akun melalui menu register.
- Login menggunakan akun yang baru dibuat.
- Melihat pesanan aktif miliknya.
- Melihat riwayat pesanan.
- Melihat dan membaca notifikasi.

## Akun Awal

| Role | Nama | Username | Password |
|---|---|---|---|
| Admin | Master Admin | `masteradmin` | `123456` |

Saat aplikasi pertama kali dijalankan, hanya akun Admin tersebut yang tersedia. Akun pelanggan ditambahkan ke `ArrayList` melalui menu register.

## Penyimpanan Data

Class `DataStore` menyimpan collection berikut:

```java
ArrayList<Pengguna> daftarPengguna;
ArrayList<Pelanggan> daftarPelanggan;
ArrayList<Pesanan> daftarPesanan;
ArrayList<ItemPakaian> daftarItem;
ArrayList<Pembayaran> daftarPembayaran;
ArrayList<Notifikasi> daftarNotifikasi;
ArrayList<TarifLaundry> daftarTarif;
```

`DataStore` dibuat sebagai singleton agar semua controller dan halaman GUI memakai objek data yang sama selama aplikasi berjalan.

## Materi OOP

| Materi | Implementasi |
|---|---|
| Abstract class | `Pengguna` |
| Inheritance | `Admin` dan `Pelanggan` mewarisi `Pengguna` |
| Abstract method | `Pengguna.getInformasiPeran()` |
| Overriding | Implementasi `getInformasiPeran()`, `kirimNotifikasi()`, dan `toString()` |
| Overloading | Constructor `Pembayaran` dan method `SmartGroupingService.kelompokkanItem()` |
| Interface | `INotifiable` |
| Polymorphism | `INotifiable` dapat berisi `AppNotifikasi` atau `WhatsAppNotifikasi` |
| Encapsulation | Atribut private dengan getter dan setter |
| Collection | Data disimpan dalam beberapa `ArrayList` |
| Composition | `Pesanan` memiliki daftar `ItemPakaian` |
| Association | Pesanan terhubung dengan Pelanggan |
| Enum | Role, status pesanan, paket, warna, dan status pembayaran |
| Exception handling | Validasi input menggunakan `IllegalArgumentException` |
| GUI | Swing, event listener, table, form, sidebar, dan dialog |

Project mempunyai 27 class utama, 2 interface, dan 6 enum sehingga memenuhi ketentuan minimal 15 class.

## Final Class Diagram

Diagram ini menjadi satu-satunya class diagram final untuk presentasi. Isinya mengikuti project final SiLaundry saat ini: dua role (`Admin` dan `Pelanggan`), penyimpanan sementara memakai `ArrayList` pada `DataStore`, GUI Swing, controller, service, util, interface, enum, dan helper internal yang memang dipakai program.

```mermaid
classDiagram
direction TB

class Main {
  +main(args: String[]) void
}

class Pengguna {
  <<abstract>>
  -String idPengguna
  -String username
  -String namaLengkap
  -String nomorTelepon
  -String kataSandi
  -Role role
  +login() void
  +logout() void
  +getInformasiPeran() String
}

class Admin {
  +Admin(idPengguna: String, username: String, namaLengkap: String, nomorTelepon: String, kataSandi: String)
  +kelolaPesanan() void
  +kelolaTarif() void
  +lihatDashboard() void
  +getInformasiPeran() String
}

class Pelanggan {
  -String idPelanggan
  -String alamat
  +Pelanggan(idPengguna: String, username: String, namaLengkap: String, nomorTelepon: String, kataSandi: String, idPelanggan: String, alamat: String)
  +lacakStatusCucian() void
  +lihatRiwayatPesanan() void
  +getInformasiPeran() String
  +toString() String
}

class Pesanan {
  -String idPesanan
  -String idPelanggan
  -String namaPelanggan
  -LocalDate tanggalMasuk
  -LocalDate estimasiSelesai
  -StatusPesanan statusPesanan
  -PaketLaundry paketLaundry
  -double beratKg
  -double hargaPerKg
  -double totalBiaya
  -String catatan
  -List~ItemPakaian~ daftarItem
  +Pesanan(idPesanan: String, idPelanggan: String, tanggalMasuk: LocalDate, estimasiSelesai: LocalDate, statusPesanan: StatusPesanan, paketLaundry: PaketLaundry, beratKg: double, hargaPerKg: double, totalBiaya: double, catatan: String)
  +tambahItemPakaian(item: ItemPakaian) void
  +kalkulasiTotalBiaya() double
  +toString() String
}

class ItemPakaian {
  -String idItem
  -String idPesanan
  -String jenisPakaian
  -KategoriWarna kategoriWarna
  -String kondisiAwal
  -String deskripsiDetail
  -String labelSmartGroup
  +ItemPakaian(idItem: String, idPesanan: String, jenisPakaian: String, kategoriWarna: KategoriWarna, kondisiAwal: String, deskripsiDetail: String, labelSmartGroup: String)
  +terapkanGrupWarna() void
}

class TarifLaundry {
  -String idTarif
  -PaketLaundry paketLaundry
  -String namaPaket
  -int estimasiHari
  -double hargaPerKg
  -boolean aktif
  +TarifLaundry(idTarif: String, paketLaundry: PaketLaundry, namaPaket: String, estimasiHari: int, hargaPerKg: double, aktif: boolean)
  +hitungTotal(beratKg: double) double
  +toString() String
}

class Pembayaran {
  -String idPembayaran
  -String idPesanan
  -String metode
  -double jumlah
  -StatusPembayaran status
  -LocalDateTime tanggalBayar
  +Pembayaran(idPembayaran: String, idPesanan: String, metode: String, jumlah: double, status: StatusPembayaran)
  +Pembayaran(idPembayaran: String, idPesanan: String, metode: String, jumlah: double, status: StatusPembayaran, tanggalBayar: LocalDateTime)
  +prosesPembayaran() void
}

class RiwayatPembayaran {
  -String idPesanan
  -String namaPelanggan
  -LocalDate tanggalPesanan
  -double totalTagihan
  -String metode
  -double jumlahBayar
  -StatusPembayaran status
  -LocalDateTime tanggalBayar
  +RiwayatPembayaran(idPesanan: String, namaPelanggan: String, tanggalPesanan: LocalDate, totalTagihan: double, metode: String, jumlahBayar: double, status: StatusPembayaran, tanggalBayar: LocalDateTime)
}

class Notifikasi {
  -String idNotifikasi
  -String idPesanan
  -String pesan
  -LocalDateTime tanggalKirim
  -boolean sudahDibaca
  +Notifikasi(idNotifikasi: String, idPesanan: String, pesan: String, tanggalKirim: LocalDateTime, sudahDibaca: boolean)
  +setSudahDibaca(sudahDibaca: boolean) void
}

class DataDasbor {
  -String idDasbor
  -int totalPesananAktif
  -double estimasiPendapatan
  -double pendapatanDiterima
  -int totalItem
  -int totalPelanggan
  +DataDasbor(idDasbor: String, totalPesananAktif: int, estimasiPendapatan: double, pendapatanDiterima: double, totalItem: int, totalPelanggan: int)
  +perbaruiMetrikHarian(totalPesananAktif: int, estimasiPendapatan: double, pendapatanDiterima: double, totalItem: int, totalPelanggan: int) void
}

class LaporanKeuangan {
  -String idLaporan
  -String periodeBulan
  -double totalPendapatan
  -int jumlahPesananSelesai
  +LaporanKeuangan(idLaporan: String, periodeBulan: String, totalPendapatan: double, jumlahPesananSelesai: int)
  +cetakDataLaporan() void
  +formatDataLaporan() String
}

class DataStore {
  <<singleton>>
  -DataStore INSTANCE
  -ArrayList~Pengguna~ daftarPengguna
  -ArrayList~Pelanggan~ daftarPelanggan
  -ArrayList~Pesanan~ daftarPesanan
  -ArrayList~ItemPakaian~ daftarItem
  -ArrayList~Pembayaran~ daftarPembayaran
  -ArrayList~Notifikasi~ daftarNotifikasi
  -ArrayList~TarifLaundry~ daftarTarif
  +getInstance() DataStore
  +reset() void
  +cariPengguna(username: String, password: String, role: Role) Pengguna
  +usernameSudahDipakai(username: String) boolean
  +tambahPelanggan(pelanggan: Pelanggan) void
  +getDaftarPelanggan() List~Pelanggan~
  +cariPelanggan(idPelanggan: String) Pelanggan
  +tambahPesanan(pesanan: Pesanan) void
  +getDaftarPesanan() List~Pesanan~
  +cariPesanan(idPesanan: String) Pesanan
  +tambahItem(item: ItemPakaian) void
  +getDaftarItem() List~ItemPakaian~
  +cariItem(idItem: String) ItemPakaian
  +hapusItem(idItem: String) boolean
  +simpanPembayaran(pembayaran: Pembayaran) void
  +getDaftarPembayaran() List~Pembayaran~
  +cariPembayaran(idPesanan: String) Pembayaran
  +tambahNotifikasi(notifikasi: Notifikasi) void
  +getDaftarNotifikasi() List~Notifikasi~
  +notifikasiSudahAda(idPesanan: String, pesan: String) boolean
  +getDaftarTarif() List~TarifLaundry~
  +cariTarif(paketLaundry: PaketLaundry) TarifLaundry
}

class AuthController {
  -DataStore dataStore
  +login(username: String, password: String, role: Role) Pengguna
}

class PenggunaController {
  -DataStore dataStore
  +getAllPelanggan() List~Pelanggan~
  +tambahPelanggan(username: String, nama: String, telepon: String, password: String, alamat: String) void
  -validasiPengguna(username: String, nama: String, telepon: String, password: String) void
}

class LaundryController {
  -DataStore dataStore
  -SmartGroupingService smartGroupingService
  -INotifiable appNotifikasi
  +getAllPesanan() List~Pesanan~
  +getPesananPelanggan(idPelanggan: String) List~Pesanan~
  +getPesanan(idPesanan: String) Pesanan
  +getTarifAktif() List~TarifLaundry~
  +tambahPesanan(idPelanggan: String, paketLaundry: PaketLaundry, beratKg: double, catatan: String) Pesanan
  +updateStatus(idPesanan: String, statusBaru: StatusPesanan) boolean
  +batalkanPesanan(idPesanan: String) boolean
  +getAllItems() List~ItemPakaian~
  +getItemsByPesanan(idPesanan: String) List~ItemPakaian~
  +tambahItem(idPesanan: String, jenisPakaian: String, kategoriWarna: KategoriWarna, kondisiAwal: String, deskripsiDetail: String) void
  +jalankanSmartGrouping(idPesanan: String) int
  +hapusItem(idItem: String) void
  +getPembayaran(idPesanan: String) Pembayaran
  +getPesananBelumBayar() List~Pesanan~
  +getRiwayatPembayaran() List~RiwayatPembayaran~
  +catatPembayaran(idPesanan: String, metode: String) Pembayaran
  +getNotifikasiPelanggan(idPelanggan: String) List~Notifikasi~
  +tandaiSemuaDibaca(idPelanggan: String) int
  +kirimNotifikasiStatus(pesanan: Pesanan) void
  +buatLinkWhatsApp(idPesanan: String) String
  +buatNotifikasiStatus(pesanan: Pesanan) Notifikasi
  -pesananDapatDiubah(idPesanan: String) Pesanan
  -buatPesanStatus(pesanan: Pesanan) String
  -perluNotifikasi(statusPesanan: StatusPesanan) boolean
}

class AdminController {
  -DataStore dataStore
  +getDataDasbor() DataDasbor
  +getLaporanBulanIni() LaporanKeuangan
  +getSemuaTarif() List~TarifLaundry~
  +getTarif(paketLaundry: PaketLaundry) TarifLaundry
  +updateHarga(paketLaundry: PaketLaundry, hargaPerKg: double) void
}

class SmartGroupingService {
  +BELUM_DIKELOMPOKKAN String
  -DataStore dataStore
  +labelFor(kategoriWarna: KategoriWarna) String
  +kelompokkanItem(pesanan: Pesanan) void
  +kelompokkanItem(daftarItem: List~ItemPakaian~) void
  +kelompokkanItem(idPesanan: String) int
}

class INotifiable {
  <<interface>>
  +kirimNotifikasi(notifikasi: Notifikasi) void
}

class AppNotifikasi {
  -DataStore dataStore
  +kirimNotifikasi(notifikasi: Notifikasi) void
  +tampilkanDiAplikasi(notifikasi: Notifikasi) String
}

class WhatsAppNotifikasi {
  -String nomorTujuan
  -String linkWhatsApp
  +WhatsAppNotifikasi(nomorTujuan: String)
  +kirimNotifikasi(notifikasi: Notifikasi) void
  +generateLinkWhatsApp(pesan: String) String
  +getLinkWhatsApp() String
  -normalisasiNomor(nomor: String) String
}

class LoginFrame {
  -AuthController authController
  -PenggunaController penggunaController
  -JComboBox~Role~ roleCombo
  -JTextField usernameField
  -JPasswordField passwordField
  +LoginFrame()
  -buildContent() JPanel
  -buildBrandPanel() JPanel
  -buildLoginPanel() JPanel
  -buildForm() JPanel
  -styleAuthInputs() void
  -applyRolePreset() void
  -login() void
  -showRegisterDialog() void
}

class MainFrame {
  -Pengguna pengguna
  -CardLayout cardLayout
  -JPanel contentPanel
  -List~JButton~ navButtons
  -JPanel navList
  +MainFrame(pengguna: Pengguna)
  -buildShell() JPanel
  -buildSidebar() JPanel
  -addRoute(title: String, subtitle: String, content: Component) void
  -sidebarButton(title: String, subtitle: String) JButton
  -selectFirstRoute() void
  -setActiveButton(activeButton: JButton) void
}

class AdminPanel {
  -Admin admin
  -AdminPanelPage page
  -AdminController adminController
  -LaundryController laundryController
  -PenggunaController penggunaController
  -boolean updatingOrderChoices
  +AdminPanel(admin: Admin, page: AdminPanelPage)
  +refreshData() void
  -buildDashboardPanel() JPanel
  -buildOrderPanel() JPanel
  -buildItemPanel() JPanel
  -buildPaymentPanel() JPanel
  -buildPelangganPanel() JPanel
  -buildTarifPanel() JPanel
  -refreshDashboard() void
  -refreshCustomers() void
  -refreshTarifChoices() void
  -refreshOrders() void
  -refreshPaymentData() void
  -refreshPaymentHistory() void
  -refreshUnpaidOrderChoices() void
  -refreshItems() void
  -refreshPelanggan() void
  -refreshTarifSettings() void
  -createOrder() void
  -updateStatus() void
  -cancelOrder() void
  -buatTemplateWhatsApp() void
  -addItem() void
  -runSmartGrouping() void
  -deleteItem() void
  -savePayment() void
  -updateTarif() void
  -updateTotalPreview() void
}

class AdminPanelPage {
  <<enumeration>>
  DASHBOARD
  PESANAN
  ITEM_PAKAIAN
  PEMBAYARAN
  PELANGGAN
  TARIF
}

class DashboardDataResult {
  <<record>>
  -DataDasbor dataDasbor
  -LaporanKeuangan laporan
}

class PelangganPanel {
  -Pelanggan pelanggan
  -LaundryController laundryController
  -JLabel summaryLabel
  -DefaultTableModel activeModel
  -DefaultTableModel historyModel
  -DefaultTableModel notificationModel
  +PelangganPanel(pelanggan: Pelanggan)
  -buildHeader() JPanel
  -buildTables() JPanel
  -buildTableSection(title: String, subtitle: String, table: JTable) JPanel
  -refresh() void
  -toRow(pesanan: Pesanan) Object[]
  -isHistory(pesanan: Pesanan) boolean
  -markNotificationsRead() void
}

class CustomerData {
  <<record>>
  -List~Pesanan~ pesanan
  -List~Notifikasi~ notifikasi
}

class AppTheme {
  <<utility>>
  +Color BACKGROUND
  +Color SURFACE
  +Color PRIMARY
  +Color PRIMARY_DARK
  +Color ACCENT
  +Color TEXT
  +Color MUTED
  +Color BORDER
  +Color DANGER
  +Font TITLE_FONT
  +Font SECTION_FONT
  +Font BODY_FONT
  +Font SMALL_FONT
  +applyGlobalLook() void
  +page(layout: LayoutManager) JPanel
  +surface(layout: LayoutManager) JPanel
  +compactSurface(layout: LayoutManager) JPanel
  +title(text: String) JLabel
  +sectionTitle(text: String) JLabel
  +muted(text: String) JLabel
  +primaryButton(text: String) JButton
  +secondaryButton(text: String) JButton
  +dangerButton(text: String) JButton
  +styleButton(button: JButton, background: Color, foreground: Color) void
  +styleLightMenuButton(button: JButton) void
  +setLightMenuButtonActive(button: JButton, active: boolean) void
  +styleSidebarButton(button: JButton) void
  +setSidebarButtonActive(button: JButton, active: boolean) void
  +formGrid() JPanel
  +addField(panel: JPanel, row: int, pairColumn: int, labelText: String, field: JComponent) void
  +addWideField(panel: JPanel, row: int, labelText: String, field: JComponent) void
  +actionRow() JPanel
  +styleTextField(field: JTextField) void
  +styleTextArea(area: JTextArea) void
  +styleComboBox(comboBox: JComboBox) void
  +styleSpinner(spinner: JSpinner) void
  +scroll(component: Component) JScrollPane
}

class UiUtil {
  <<utility>>
  -NumberFormat RUPIAH
  +model(columns: String[]) DefaultTableModel
  +applyTableStyle(table: JTable) void
  +money(amount: double) String
  +info(parent: Component, message: String) void
  +error(parent: Component, message: String, ex: Exception) void
  +confirm(parent: Component, message: String) boolean
  +runTask(parent: Component, task: UiUtilTask, onSuccess: Consumer, errorMessage: String) void
  +installSearch(table: JTable, searchField: JTextField) void
  +selectedId(table: JTable) String
  +installComboPlaceholder(comboBox: JComboBox, placeholder: String) void
}

class UiUtilTask {
  <<interface>>
  +run() T
}

class UiUtilStatusRenderer {
  <<private static class>>
  +getTableCellRendererComponent(table: JTable, value: Object, selected: boolean, focused: boolean, row: int, column: int) Component
}

class IdGenerator {
  <<utility>>
  -DateTimeFormatter FORMATTER
  -AtomicInteger SEQUENCE
  +generate(prefix: String) String
}

class Role {
  <<enumeration>>
  ADMIN
  PELANGGAN
  -String displayName
  +getDisplayName() String
  +toString() String
}
class StatusPesanan {
  <<enumeration>>
  BARU
  DIPROSES
  DICUCI
  DIKERINGKAN
  DISETRIKA
  SIAP_DIAMBIL
  SELESAI
  DIBATALKAN
  -String displayName
  +getDisplayName() String
  +dapatBerubahKe(statusBaru: StatusPesanan) boolean
  +membutuhkanItemPakaian() boolean
  +dapatMengubahItem() boolean
  +dapatMenerimaPembayaran() boolean
  +isFinal() boolean
  +toString() String
}
class PaketLaundry {
  <<enumeration>>
  STANDARD_2_HARI
  EXPRESS_1_HARI
  -String displayName
  -int estimasiHariDefault
  +getDisplayName() String
  +getEstimasiHariDefault() int
  +toString() String
}
class KategoriWarna {
  <<enumeration>>
  PUTIH
  TERANG
  GELAP
  MUDAH_LUNTUR
  -String displayName
  -String smartGroupLabel
  +getDisplayName() String
  +getSmartGroupLabel() String
  +toString() String
}
class StatusPembayaran {
  <<enumeration>>
  BELUM_BAYAR
  LUNAS
  -String displayName
  +getDisplayName() String
  +toString() String
}

class JFrame {
  <<Swing>>
}
class JPanel {
  <<Swing>>
}
class DefaultTableCellRenderer {
  <<Swing>>
}

Pengguna <|-- Admin
Pengguna <|-- Pelanggan
Pengguna --> Role

INotifiable <|.. AppNotifikasi
INotifiable <|.. WhatsAppNotifikasi

DataStore "1" o-- "0..*" Pengguna : daftarPengguna
DataStore "1" o-- "0..*" Pelanggan : daftarPelanggan
DataStore "1" o-- "0..*" Pesanan : daftarPesanan
DataStore "1" o-- "0..*" ItemPakaian : daftarItem
DataStore "1" o-- "0..*" Pembayaran : daftarPembayaran
DataStore "1" o-- "0..*" Notifikasi : daftarNotifikasi
DataStore "1" o-- "0..*" TarifLaundry : daftarTarif

Pelanggan "1" --> "0..*" Pesanan : memantau
Pesanan "1" *-- "0..*" ItemPakaian : daftarItem
Pesanan "1" --> "0..1" Pembayaran : pembayaran
Pesanan "1" --> "0..*" Notifikasi : status laundry
Pesanan --> StatusPesanan
Pesanan --> PaketLaundry
ItemPakaian --> KategoriWarna
TarifLaundry --> PaketLaundry
Pembayaran --> StatusPembayaran
RiwayatPembayaran --> StatusPembayaran

AuthController --> DataStore
PenggunaController --> DataStore
PenggunaController --> IdGenerator
LaundryController --> DataStore
AdminController --> DataStore
LaundryController --> SmartGroupingService
LaundryController --> INotifiable
LaundryController --> WhatsAppNotifikasi
LaundryController --> IdGenerator
LaundryController --> RiwayatPembayaran
SmartGroupingService --> DataStore
SmartGroupingService --> KategoriWarna
AppNotifikasi --> DataStore
WhatsAppNotifikasi --> Notifikasi
AdminController --> DataDasbor
AdminController --> LaporanKeuangan
AdminController --> TarifLaundry

LoginFrame --> AuthController
LoginFrame --> PenggunaController
LoginFrame --> Role
LoginFrame --> MainFrame
MainFrame --> AdminPanel
MainFrame --> PelangganPanel
MainFrame --> Pengguna
AdminPanel --> AdminController
AdminPanel --> LaundryController
AdminPanel --> PenggunaController
AdminPanel --> AdminPanelPage
AdminPanel --> DashboardDataResult
DashboardDataResult --> DataDasbor
DashboardDataResult --> LaporanKeuangan
PelangganPanel --> LaundryController
PelangganPanel --> CustomerData
CustomerData --> Pesanan
CustomerData --> Notifikasi
Main --> LoginFrame
JFrame <|-- LoginFrame
JFrame <|-- MainFrame
JPanel <|-- AdminPanel
JPanel <|-- PelangganPanel
UiUtil --> AppTheme
UiUtil --> UiUtilTask
UiUtil --> UiUtilStatusRenderer
DefaultTableCellRenderer <|-- UiUtilStatusRenderer
```

## Struktur Project

```text
src/silaundry/
  controller/
  data/
  model/
  model/enums/
  service/
  util/
  view/
test/silaundry/test/
```

## Cara Menjalankan di NetBeans

1. Buka Apache NetBeans.
2. Pilih **File > Open Project**.
3. Pilih folder project SILAUNDRY.
4. Klik kanan project lalu pilih **Clean and Build**.
5. Jalankan main class `silaundry.Main`.

Program tidak membutuhkan XAMPP, MySQL, Connector/J, atau konfigurasi tambahan.

## Alur Presentasi

1. Jalankan aplikasi.
2. Tunjukkan bahwa role Admin dan Pelanggan tersedia.
3. Register satu akun pelanggan.
4. Login sebagai Admin menggunakan `masteradmin` / `123456`.
5. Buat pesanan untuk pelanggan yang baru register.
6. Tambahkan detail item pakaian.
7. Jalankan smart grouping.
8. Ubah status pesanan secara berurutan.
9. Catat pembayaran.
10. Logout dan login sebagai pelanggan.
11. Tampilkan pesanan aktif, riwayat, dan notifikasi pelanggan.
12. Jelaskan bahwa seluruh data berada di `ArrayList` dan hilang saat aplikasi ditutup.

## Pengujian

Compile melalui PowerShell:

```powershell
$files = @(rg --files src test | Where-Object { $_ -like '*.java' })
javac -encoding UTF-8 -d build\classes $files
```

Jalankan test:

```powershell
java -cp build\classes silaundry.test.BusinessRulesTest
```

Test mensimulasikan login Admin, register dan login pelanggan, pesanan, item pakaian, smart grouping, status, notifikasi, pembayaran, dan dashboard.

## Batasan

- Data hanya tersedia selama aplikasi berjalan.
- Data tidak disimpan ke file atau database.
- Aplikasi ditujukan sebagai simulasi tugas besar, bukan sistem produksi.
- Link WhatsApp hanya berupa template dan tidak dikirim otomatis melalui API.

## Mata Kuliah

- Mata Kuliah: Pemrograman Berorientasi Objek
- Institusi: Telkom University
- Tahun: 2026

Project ini dibuat untuk keperluan akademis Tugas Besar Pemrograman Berorientasi Objek.
