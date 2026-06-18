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

## Core Class Diagram

Diagram core menampilkan class domain yang paling penting untuk pembahasan OOP.

```mermaid
classDiagram
direction LR

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
  +getInformasiPeran()* String
}

class Admin {
  +kelolaPesanan() void
  +kelolaTarif() void
  +lihatDashboard() void
  +getInformasiPeran() String
}

class Pelanggan {
  -String idPelanggan
  -String alamat
  +lacakStatusCucian() void
  +lihatRiwayatPesanan() void
  +getInformasiPeran() String
}

class Pesanan {
  -String idPesanan
  -String idPelanggan
  -StatusPesanan statusPesanan
  -PaketLaundry paketLaundry
  -double beratKg
  -double hargaPerKg
  -double totalBiaya
  -List~ItemPakaian~ daftarItem
  +tambahItemPakaian(item: ItemPakaian) void
  +kalkulasiTotalBiaya() double
}

class ItemPakaian {
  -String idItem
  -String jenisPakaian
  -KategoriWarna kategoriWarna
  -String kondisiAwal
  -String deskripsiDetail
  -String labelSmartGroup
  +terapkanGrupWarna() void
}

class TarifLaundry {
  -PaketLaundry paketLaundry
  -int estimasiHari
  -double hargaPerKg
  +hitungTotal(beratKg: double) double
}

class Pembayaran {
  -String idPembayaran
  -String idPesanan
  -String metode
  -double jumlah
  -StatusPembayaran status
  +prosesPembayaran() void
}

class Notifikasi {
  -String idNotifikasi
  -String idPesanan
  -String pesan
  -boolean sudahDibaca
}

class INotifiable {
  <<interface>>
  +kirimNotifikasi(notifikasi: Notifikasi) void
}

class AppNotifikasi
class WhatsAppNotifikasi
class DataStore {
  -ArrayList~Pengguna~ daftarPengguna
  -ArrayList~Pelanggan~ daftarPelanggan
  -ArrayList~Pesanan~ daftarPesanan
  -ArrayList~ItemPakaian~ daftarItem
  -ArrayList~Pembayaran~ daftarPembayaran
  -ArrayList~Notifikasi~ daftarNotifikasi
  -ArrayList~TarifLaundry~ daftarTarif
}

Pengguna <|-- Admin
Pengguna <|-- Pelanggan
INotifiable <|.. AppNotifikasi
INotifiable <|.. WhatsAppNotifikasi
Pelanggan "1" --> "0..*" Pesanan : memantau
Pesanan "1" *-- "0..*" ItemPakaian : berisi
Pesanan --> TarifLaundry : memakai tarif
Pesanan "1" --> "0..1" Pembayaran : memiliki
Pesanan "1" --> "0..*" Notifikasi : menghasilkan
DataStore o-- Pengguna
DataStore o-- Pesanan
DataStore o-- ItemPakaian
DataStore o-- Pembayaran
DataStore o-- Notifikasi
DataStore o-- TarifLaundry
```

## Final Class Diagram

Diagram final menunjukkan hubungan model, collection, controller, service, dan GUI pada implementasi saat ini.

```mermaid
classDiagram
direction LR

class Pengguna {
  <<abstract>>
  +getInformasiPeran()* String
}
class Admin
class Pelanggan
class Pesanan
class ItemPakaian
class TarifLaundry
class Pembayaran
class RiwayatPembayaran
class Notifikasi
class DataDasbor
class LaporanKeuangan

class DataStore {
  <<singleton>>
  +getInstance() DataStore
  +reset() void
  +cariPengguna(username: String, password: String, role: Role) Pengguna
  +tambahPelanggan(pelanggan: Pelanggan) void
  +tambahPesanan(pesanan: Pesanan) void
  +tambahItem(item: ItemPakaian) void
  +simpanPembayaran(pembayaran: Pembayaran) void
  +tambahNotifikasi(notifikasi: Notifikasi) void
}

class AuthController {
  -DataStore dataStore
  +login(username: String, password: String, role: Role) Pengguna
}

class PenggunaController {
  -DataStore dataStore
  +getAllPelanggan() List~Pelanggan~
  +tambahPelanggan(username: String, nama: String, telepon: String, password: String, alamat: String) void
}

class LaundryController {
  -DataStore dataStore
  -SmartGroupingService smartGroupingService
  -INotifiable appNotifikasi
  +getAllPesanan() List~Pesanan~
  +getPesananPelanggan(idPelanggan: String) List~Pesanan~
  +tambahPesanan(idPelanggan: String, paket: PaketLaundry, berat: double, catatan: String) Pesanan
  +updateStatus(idPesanan: String, status: StatusPesanan) boolean
  +tambahItem(idPesanan: String, jenis: String, warna: KategoriWarna, kondisi: String, deskripsi: String) void
  +jalankanSmartGrouping(idPesanan: String) int
  +catatPembayaran(idPesanan: String, metode: String) Pembayaran
}

class AdminController {
  -DataStore dataStore
  +getDataDasbor() DataDasbor
  +getLaporanBulanIni() LaporanKeuangan
  +getSemuaTarif() List~TarifLaundry~
  +updateHarga(paket: PaketLaundry, harga: double) void
}

class SmartGroupingService {
  +kelompokkanItem(pesanan: Pesanan) void
  +kelompokkanItem(items: List~ItemPakaian~) void
  +kelompokkanItem(idPesanan: String) int
}

class INotifiable {
  <<interface>>
  +kirimNotifikasi(notifikasi: Notifikasi) void
}
class AppNotifikasi
class WhatsAppNotifikasi

class LoginFrame
class MainFrame
class AdminPanel
class PelangganPanel
class AppTheme
class UiUtil
class IdGenerator
class Main

class Role {
  <<enumeration>>
  ADMIN
  PELANGGAN
}
class StatusPesanan {
  <<enumeration>>
}
class PaketLaundry {
  <<enumeration>>
}
class KategoriWarna {
  <<enumeration>>
}
class StatusPembayaran {
  <<enumeration>>
}

Pengguna <|-- Admin
Pengguna <|-- Pelanggan
Pengguna --> Role

INotifiable <|.. AppNotifikasi
INotifiable <|.. WhatsAppNotifikasi

DataStore o-- Pengguna
DataStore o-- Pelanggan
DataStore o-- Pesanan
DataStore o-- ItemPakaian
DataStore o-- Pembayaran
DataStore o-- Notifikasi
DataStore o-- TarifLaundry

Pesanan "1" *-- "0..*" ItemPakaian
Pesanan "1" --> "0..1" Pembayaran
Pesanan "1" --> "0..*" Notifikasi
Pelanggan "1" --> "0..*" Pesanan

AuthController --> DataStore
PenggunaController --> DataStore
LaundryController --> DataStore
AdminController --> DataStore
LaundryController --> SmartGroupingService
LaundryController --> INotifiable
SmartGroupingService --> DataStore
AppNotifikasi --> DataStore

LoginFrame --> AuthController
LoginFrame --> PenggunaController
MainFrame --> AdminPanel
MainFrame --> PelangganPanel
AdminPanel --> AdminController
AdminPanel --> LaundryController
AdminPanel --> PenggunaController
PelangganPanel --> LaundryController
Main --> LoginFrame
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
