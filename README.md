# SILAUNDRY - Sistem Informasi Manajemen Laundry

![Java](https://img.shields.io/badge/Java-ED8B00?style=flat&logo=java&logoColor=white)
![NetBeans](https://img.shields.io/badge/NetBeans-1B6AC6?style=flat&logo=apachenetbeanside&logoColor=white)
![OOP](https://img.shields.io/badge/Paradigm-OOP-blue)

Sistem Informasi Manajemen Laundry berbasis desktop untuk UMKM dengan fitur **Item Tracking** dan **Smart Grouping**.

## 📋 Deskripsi Proyek

SILAUNDRY adalah aplikasi desktop Java yang dirancang untuk mengatasi masalah operasional utama usaha laundry UMKM: **tingginya risiko pakaian pelanggan yang tertukar**. Meskipun klaim layanan "1 mesin 1 pelanggan", keterbatasan kapasitas memaksa pencampuran pakaian dari berbagai pelanggan, yang mengakibatkan kerugian materi dan menurunnya kepercayaan pelanggan.

### 🎯 Solusi Teknis Utama

1. **Item Tracking System**
   - Identifikasi unik untuk setiap potong pakaian
   - Pelacakan relasional ke ID Pelanggan
   - Memastikan tidak ada identitas pakaian yang hilang meski dicampur dalam satu mesin

2. **Smart Grouping**
   - Sortir otomatis cucian berdasarkan kategori warna
   - Rekomendasi pengelompokan untuk mencegah kerusakan/kelunturan
   - Optimasi proses pencucian massal

## 🏗️ Domain & Ruang Lingkup

**Domain**: Sistem Informasi Manajemen Operasional Jasa  
**Fokus**: Manajemen alur kerja dan pelacakan aset/inventori sementara (pakaian pelanggan) dalam skala UMKM

### Ruang Lingkup Sistem

- ✅ **User Management**: Owner, Karyawan, Pelanggan
- ✅ **Order Management**: CRUD pesanan, pencatatan jenis pakaian, kalkulasi harga, status tracking
- ✅ **Item Tracking**: Identifikasi unik per pakaian dengan relasi ke pelanggan
- ✅ **Smart Grouping**: Logika back-end untuk sortir otomatis berdasarkan warna
- ✅ **Monitoring**: Akses informasi status bagi pelanggan dan performa bagi owner

### Batasan Sistem

- ❌ Tidak terintegrasi dengan hardware mesin cuci fisik
- ❌ Tidak mencakup modul manajemen kurir/pengiriman
- ❌ Tidak menyertakan manajemen gaji dan inventori bahan baku
- ❌ Fokus pada pemantauan transaksi, bukan payment gateway

## 🏛️ Arsitektur Class Diagram

```mermaid
classDiagram
    %% Abstract Class Pengguna
    class Pengguna {
        <<abstract>>
        -idPengguna: String
        -namaLengkap: String
        -nomorTelepon: String
        -kataSandi: String
        +login(): void
        +logout(): void
    }

    %% User Classes
    class Pelanggan {
        -idPelanggan: String
        -alamat: String
        +lacakStatusCucian(): void
        +lihatRiwayatPesanan(): void
    }

    class Karyawan {
        -idKaryawan: String
        -shiftKerja: String
        +buatPesananBaru(): void
        +perbaruiStatusPesanan(): void
        +rekamDataPakaian(): void
        +jalankanSmartGrouping(): void
    }

    class Pemilik {
        -id: String
        -namaLengkap: String
        +tinjauDasborAnalitik(): void
        +unduhLaporanKeuangan(): void
        +kelolaDataKaryawan(): void
    }

    %% Core Business Classes
    class Pesanan {
        -idPesanan: String
        -tanggalMasuk: String
        -estimasiSelesai: String
        -statusPesanan: String
        -paketLaundry: String
        -beratKg: double
        -hargaPerKg: double
        -totalBiaya: double
        +tambahItemPakaian(item: ItemPakaian): void
        +kalkulasiTotalBiaya(): double
        +kirimNotifikasiSelesai(): void
    }

    class ItemPakaian {
        -idItem: String
        -jenisPakaian: String
        -kategoriWarna: String
        -kondisiAwal: String
        -deskripsiDetail: String
        -labelSmartGroup: String
        -kodeQR: String
        +terapkanGrupWarna(): void
        +generateKodeQR(): void
    }

    class TarifLaundry {
        -idTarif: String
        -paketLaundry: String
        -namaPaket: String
        -estimasiHari: int
        -hargaPerKg: double
        +hitungTotal(beratKg: double): double
    }

    class ProsesLaundry {
        -idProses: String
        -tahap: String
        -waktuMulai: String
        -waktuSelesai: String
        +updateProses(): void
    }

    class MesinCuci {
        -idMesin: String
        -kapasitas: float
        -status: String
        +mulaiCuci(): void
        +selesaiCuci(): void
    }

    %% Service Classes
    class SmartGroupingService {
        -id: int
        +kelompokkanItem(pesanan: Pesanan): void
    }

    class ItemTrackingService {
        -notif: String
        +trackItem(idItem: String): void
        +updateLokasiItem(): void
    }

    %% Payment Classes
    class Pembayaran {
        -idPembayaran: String
        -metode: String
        -jumlah: double
        -status: String
        +prosesPembayaran(): void
        +konfirmasiPembayaran(): void
    }

    class DetailPembayaran {
        -idDetail: String
        -waktuBayar: String
        +generateStruk(): void
    }

    %% Monitoring Classes
    class LaporanKeuangan {
        -idLaporan: String
        -periodeBulan: String
        -totalPendapatan: double
        +cetakDataLaporan(): void
    }

    class DataDasbor {
        -idDasbor: String
        -totalPesananAktif: int
        -estimasiPendapatan: double
        +perbaruiMetrikHarian(): void
    }

    %% Notification Interface and Class
    class INotifiable {
        <<interface>>
        +kirimNotifikasi(notifikasi: Notifikasi): void
    }

    class Notifikasi {
        -idNotifikasi: String
        -idPesanan: String
        -pesan: String
        -tanggalKirim: String
        -sudahDibaca: boolean
    }

    class AppNotifikasi {
        +kirimNotifikasi(notifikasi: Notifikasi): void
        +tampilkanDiAplikasi(notifikasi: Notifikasi): String
    }

    class WhatsAppNotifikasi {
        -nomorTujuan: String
        -linkWhatsApp: String
        +kirimNotifikasi(notifikasi: Notifikasi): void
        +generateLinkWhatsApp(pesan: String): String
    }

    %% Relationships - Inheritance
    Pengguna <|-- Pelanggan : Extends
    Pengguna <|-- Karyawan : Extends
    Pengguna <|-- Pemilik : Extends

    %% Relationships - Interface Implementation
    INotifiable <|.. AppNotifikasi : Implements
    INotifiable <|.. WhatsAppNotifikasi : Implements

    %% Relationships - Associations (dari gambar)
    Pelanggan --> Pesanan : memantau
    Karyawan --> Pesanan : mengelola
    Pemilik --> TarifLaundry : mengatur
    Pemilik --> LaporanKeuangan : mengakses
    Pemilik --> DataDasbor : memantau

    %% Relationships - Core Business
    Pesanan --> TarifLaundry : memakai snapshot harga
    Pesanan *-- ItemPakaian : mengandung
    Pesanan --> Pembayaran : memiliki
    Pesanan --> Notifikasi : menghasilkan
    AppNotifikasi --> Notifikasi : menyimpan dan menampilkan
    WhatsAppNotifikasi --> Notifikasi : membuat template pesan
    WhatsAppNotifikasi --> Pelanggan : memakai nomor telepon
    Pesanan ..> SmartGroupingService : dikelompokkan
    
    ItemPakaian --> ItemTrackingService : dilacak
    ProsesLaundry --> MesinCuci : menggunakan
    
    Pembayaran --> DetailPembayaran : merinci
```


### Updated Class Diagram
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
}

class Pelanggan {
  -String idPelanggan
  -String alamat
  +lacakStatusCucian() void
  +lihatRiwayatPesanan() void
}

class Karyawan {
  -String idKaryawan
  -String shiftKerja
  +buatPesananBaru() void
  +perbaruiStatusPesanan() void
  +rekamDataPakaian() void
  +jalankanSmartGrouping() void
}

class Pemilik {
  -String id
  -String idPemilik
  +tinjauDasborAnalitik() void
  +unduhLaporanKeuangan() void
  +kelolaDataKaryawan() void
}

class Pesanan {
  -String idPesanan
  -String idPelanggan
  -String namaPelanggan
  -String idKaryawan
  -String namaKaryawan
  -LocalDate tanggalMasuk
  -LocalDate estimasiSelesai
  -StatusPesanan statusPesanan
  -PaketLaundry paketLaundry
  -double beratKg
  -double hargaPerKg
  -double totalBiaya
  -String catatan
  -List~ItemPakaian~ daftarItem
  +tambahItemPakaian(item: ItemPakaian) void
  +kalkulasiTotalBiaya() double
  +kirimNotifikasiSelesai() void
}

class TarifLaundry {
  -String idTarif
  -PaketLaundry paketLaundry
  -String namaPaket
  -int estimasiHari
  -double hargaPerKg
  -boolean aktif
  +hitungTotal(beratKg: double) double
}

class ItemPakaian {
  -String idItem
  -String idPesanan
  -String jenisPakaian
  -KategoriWarna kategoriWarna
  -String kondisiAwal
  -String deskripsiDetail
  -String labelSmartGroup
  -String kodeQR
  +terapkanGrupWarna() void
  +generateKodeQR() void
}

class ProsesLaundry {
  -String idProses
  -String idPesanan
  -String idMesin
  -TahapLaundry tahap
  -LocalDateTime waktuMulai
  -LocalDateTime waktuSelesai
  +updateProses() void
  +updateProses(tahap: TahapLaundry, waktuSelesai: LocalDateTime) void
}

class MesinCuci {
  -String idMesin
  -String namaMesin
  -float kapasitas
  -StatusMesin status
  +mulaiCuci() void
  +selesaiCuci() void
}

class Pembayaran {
  -String idPembayaran
  -String idPesanan
  -String metode
  -double jumlah
  -StatusPembayaran status
  +prosesPembayaran() void
  +konfirmasiPembayaran() void
}

class DetailPembayaran {
  -String idDetail
  -String idPembayaran
  -LocalDateTime waktuBayar
  -String keterangan
  +generateStruk() void
  +formatStruk() String
}

class INotifiable {
  <<interface>>
  +getId() int
  +kirimNotifikasi() void
}

class Notifikasi {
  -int id
  -String idNotifikasi
  -String idPesanan
  -String pesan
  -LocalDateTime tanggalKirim
  -boolean sudahDibaca
  +kirimNotifikasi() void
}

class DataDasbor {
  -String idDasbor
  -int totalPesananAktif
  -double estimasiPendapatan
  -int totalItem
  -int totalPelanggan
  +perbaruiMetrikHarian() void
}

class LaporanKeuangan {
  -String idLaporan
  -String periodeBulan
  -double totalPendapatan
  -int jumlahPesananSelesai
  +cetakDataLaporan() void
  +formatDataLaporan() String
}

class SmartGroupingService {
  -int id
  -ItemPakaianDAO itemPakaianDAO
  +labelFor(kategoriWarna: KategoriWarna) String
  +kelompokkanItem(pesanan: Pesanan) void
  +kelompokkanItem(idPesanan: String) int
}

class ItemTrackingService {
  -String notif
  -ItemPakaianDAO itemPakaianDAO
  -PesananDAO pesananDAO
  +trackItem(idItem: String) void
  +trackItemResult(trackingKey: String) TrackingResult
  +updateLokasiItem() void
}

class TrackingResult {
  -ItemPakaian itemPakaian
  -Pesanan pesanan
  +toSummary() String
}

class AuthController {
  -UserDAO userDAO
  +login(username: String, password: String, role: Role) Pengguna
  +testConnection() String
}

class PenggunaController {
  -UserDAO userDAO
  +getAllPelanggan() List~Pelanggan~
  +getAllKaryawan() List~Karyawan~
  +tambahPelanggan(...) void
  +tambahKaryawan(...) void
  +hapusKaryawan(idKaryawan: String) void
}

class PesananController {
  -PesananDAO pesananDAO
  -TarifLaundryDAO tarifLaundryDAO
  +getAllPesanan() List~Pesanan~
  +getPesananPelanggan(idPelanggan: String) List~Pesanan~
  +tambahPesanan(idPelanggan: String, idKaryawan: String, paketLaundry: PaketLaundry, beratKg: double, catatan: String) Pesanan
  +updateStatus(idPesanan: String, statusPesanan: StatusPesanan) void
  +hapusPesanan(idPesanan: String) void
}

class ItemController {
  -ItemPakaianDAO itemPakaianDAO
  -SmartGroupingService smartGroupingService
  -ItemTrackingService itemTrackingService
  +tambahItem(...) void
  +jalankanSmartGrouping(idPesanan: String) int
  +lacakItem(trackingKey: String) TrackingResult
  +hapusItem(idItem: String) void
}

class TarifController {
  -TarifLaundryDAO tarifLaundryDAO
  +getSemuaTarif() List~TarifLaundry~
  +getTarifAktif() List~TarifLaundry~
  +updateHarga(paketLaundry: PaketLaundry, hargaPerKg: double) void
}

class PembayaranController {
  -PembayaranDAO pembayaranDAO
  +getPembayaran(idPesanan: String) Pembayaran
  +simpanPembayaran(idPesanan: String, metode: String, jumlah: double, status: StatusPembayaran) void
}

class DashboardController {
  -DashboardDAO dashboardDAO
  +getDataDasbor() DataDasbor
  +getLaporanBulanIni() LaporanKeuangan
}

class UserDAO
class PesananDAO
class ItemPakaianDAO
class TarifLaundryDAO
class PembayaranDAO
class DashboardDAO
class DatabaseConnection
class PasswordUtil
class IdGenerator

class Role {
  <<enumeration>>
  PEMILIK
  KARYAWAN
  PELANGGAN
}

class StatusPesanan {
  <<enumeration>>
  BARU
  DITERIMA
  DIPROSES
  DICUCI
  DIKERINGKAN
  DISETRIKA
  SIAP_DIAMBIL
  SELESAI
  DIBATALKAN
}

class PaketLaundry {
  <<enumeration>>
  STANDARD_2_HARI
  EXPRESS_1_HARI
}

class KategoriWarna {
  <<enumeration>>
  PUTIH
  TERANG
  GELAP
  MUDAH_LUNTUR
}

class StatusPembayaran {
  <<enumeration>>
  BELUM_BAYAR
  SEBAGIAN
  LUNAS
  DIBATALKAN
}

class StatusMesin {
  <<enumeration>>
  TERSEDIA
  DIGUNAKAN
  PERAWATAN
}

class TahapLaundry {
  <<enumeration>>
  PENERIMAAN
  PENCUCIAN
  PENGERINGAN
  PENYETRIKAAN
  PENGEMASAN
  SELESAI
}

Pengguna <|-- Pelanggan
Pengguna <|-- Karyawan
Pengguna <|-- Pemilik
Pengguna --> Role

INotifiable <|.. Notifikasi

Pelanggan "1" --> "0..*" Pesanan : memantau
Karyawan "1" --> "0..*" Pesanan : menginput/mengelola
Pesanan "1" *-- "0..*" ItemPakaian : berisi
Pesanan "1" --> "0..1" Pembayaran : memiliki
Pesanan "1" --> "0..*" Notifikasi : menghasilkan
Pembayaran "1" --> "0..*" DetailPembayaran : detail
Pesanan --> StatusPesanan
Pesanan --> PaketLaundry
Pesanan ..> TarifLaundry : snapshot harga
TarifLaundry --> PaketLaundry
ItemPakaian --> KategoriWarna
ProsesLaundry --> Pesanan
ProsesLaundry --> MesinCuci
ProsesLaundry --> TahapLaundry
MesinCuci --> StatusMesin
Pembayaran --> StatusPembayaran
Notifikasi --> Pesanan

Pemilik ..> DataDasbor : memantau
Pemilik ..> LaporanKeuangan : mengakses
Pemilik ..> TarifController : mengatur tarif
TarifController --> TarifLaundryDAO
TarifLaundryDAO --> TarifLaundry

AuthController --> UserDAO
PenggunaController --> UserDAO
PesananController --> PesananDAO
PesananController --> TarifLaundryDAO
ItemController --> ItemPakaianDAO
ItemController --> SmartGroupingService
ItemController --> ItemTrackingService
PembayaranController --> PembayaranDAO
DashboardController --> DashboardDAO

UserDAO --> Pengguna
UserDAO --> Pelanggan
UserDAO --> Karyawan
UserDAO --> Pemilik
PesananDAO --> Pesanan
ItemPakaianDAO --> ItemPakaian
PembayaranDAO --> Pembayaran
DashboardDAO --> DataDasbor
DashboardDAO --> LaporanKeuangan

SmartGroupingService --> ItemPakaianDAO
SmartGroupingService ..> Pesanan
ItemTrackingService --> ItemPakaianDAO
ItemTrackingService --> PesananDAO
ItemTrackingService --> TrackingResult
TrackingResult --> ItemPakaian
TrackingResult --> Pesanan

UserDAO ..> DatabaseConnection
PesananDAO ..> DatabaseConnection
ItemPakaianDAO ..> DatabaseConnection
TarifLaundryDAO ..> DatabaseConnection
PembayaranDAO ..> DatabaseConnection
DashboardDAO ..> DatabaseConnection

AuthController ..> PasswordUtil
PenggunaController ..> PasswordUtil
PenggunaController ..> IdGenerator
PesananController ..> IdGenerator
ItemController ..> IdGenerator
```

### Penjelasan Modul

**Modul A: Aktor dan Manajemen Pengguna**
- `Pengguna` (Abstract Class) sebagai parent class
- `Pelanggan`, `Karyawan`, `Pemilik` sebagai child classes dengan fungsi spesifik

**Modul B: Operasional Bisnis**
- `Pesanan` - Pusat data transaksi laundry
- `ItemPakaian` - Representasi digital setiap pakaian
- `ProsesLaundry` - Tahapan proses pencucian
- `MesinCuci` - Representasi mesin fisik
- `SmartGroupingService` - Service pengelompokan otomatis
- `ItemTrackingService` - Service pelacakan item

**Modul C: Layanan Sistem**
- `Pembayaran` & `DetailPembayaran` - Manajemen transaksi
- `INotifiable`, `AppNotifikasi`, `WhatsAppNotifikasi`, dan `Notifikasi` - Sistem notifikasi aplikasi dan template link WhatsApp
- `DataDasbor` - Dashboard metrik real-time
- `LaporanKeuangan` - Laporan keuangan periodik



## 👥 Tim Pengembang

**Kelompok**: Asli Loh Yak

| Nama | Peran |
|------|-------|
| Fanan Agfian Mozart | Project Manager & Initiator |
| Ammar Farras Hanindhiya Bastian | Class Diagram Designer |
| Naufal Indra Washikita | Dokumentasi Tujuan & Modul B |
| Mikael Bramantyo Hastungkoro | Dokumentasi Modul A & C |
| Grace Jessica | Pendahuluan, Visualisasi, Relasi |

## 🛠️ Tech Stack

- **Language**: Java
- **IDE**: Apache NetBeans
- **Paradigm**: Object-Oriented Programming (OOP)
- **GUI**: Java Swing/AWT
- **Database**: MySQL/XAMPP via JDBC
- **Design Pattern**: MVC (Model-View-Controller)

## 🚀 Cara Menjalankan

1. **Clone repository**
```bash
   git clone https://github.com/Mozartzx/silaundry.git
   cd silaundry
```

2. **Siapkan database**
   - Jalankan MySQL dari XAMPP
   - Import file `database/silaundry_schema.sql` melalui phpMyAdmin atau MySQL client
   - Jika username/password MySQL berbeda, ubah `config/db.properties`
   - Jika database lama sudah pernah di-import dan tidak ingin drop ulang, jalankan `database/migrate_realistic_login_schema.sql`, lalu `database/migrate_tarif_per_kilo.sql`

3. **Siapkan JDBC driver**
   - Unduh MySQL Connector/J
   - Letakkan file `.jar` di folder `lib/`
   - Rename menjadi `mysql-connector-j.jar`

4. **Buka di NetBeans**
   - File → Open Project
   - Pilih folder SILAUNDRY

5. **Build & Run**
   - Klik kanan pada project → Clean and Build
   - Run Main File

### Akun Seed

| Role | Username | Password |
|------|----------|----------|
| Pemilik | `Mozart` | `123` |
| Karyawan | `karyawan` | `karyawan123` |
| Pelanggan | `pelanggan` | `pelanggan123` |

Catatan:
- Role pemilik hanya disediakan satu akun seed dan tidak dibuat dari menu register.
- Karyawan ditambahkan dari dashboard pemilik.
- Pelanggan dapat membuat akun sendiri melalui tombol **Daftar Pelanggan** di halaman login.
- Struktur database memakai `pengguna` sebagai tabel parent untuk login/role, sedangkan `pelanggan`, `karyawan`, dan `pemilik` hanya menyimpan atribut khusus masing-masing role.
- Tarif laundry dikelola oleh pemilik dari menu **Tarif Laundry**:
  - Standard 2 Hari: default Rp7.000/kg
  - Express 1 Hari: default Rp8.000/kg
- Total biaya pesanan dihitung otomatis dari `berat_kg x harga_per_kg`; karyawan dan pelanggan tidak menginput total manual.
- Karyawan dapat mencatat jenis pakaian, kategori warna, kondisi awal, dan deskripsi detail per item untuk mengurangi risiko pakaian tertukar.

## 📊 Fitur Utama

### Untuk Pelanggan
- 📱 Lacak status cucian secara real-time
- Lihat pesanan yang sedang berjalan
- Lihat riwayat pesanan selesai atau dibatalkan
- 🔔 Melihat notifikasi aplikasi saat pesanan siap diambil atau selesai

### Untuk Karyawan
- Hitung total otomatis berdasarkan paket dan berat kilo
- Catat deskripsi detail pakaian pelanggan
- ➕ CRUD manajemen pesanan
- 📦 Rekam data pakaian per item
- 🎨 Eksekusi Smart Grouping
- 🔄 Update status operasional
- Membuat template link WhatsApp dari pesanan terpilih

### Untuk Owner/Pemilik
- Kelola harga per kilo untuk paket Standard 2 Hari dan Express 1 Hari
- 📈 Dashboard analitik performa
- 💰 Laporan keuangan bulanan
- 👥 Kelola data karyawan

## 🔗 Relasi Kelas

- **Inheritance**: `Pengguna` → `Pelanggan`, `Karyawan`, `Pemilik`
- **Interface**: `INotifiable` ← implements `Notifikasi`
- **Association**: Aktor ↔ Pesanan, Pemilik ↔ Monitoring
- **Composition**: `Pesanan` ◆→ `ItemPakaian`, `Pembayaran`

## 📖 Dokumentasi

- [Class Diagram](docs/class-diagram.png)
- [Flowchart Operasional](docs/flowchart.png)
- [Laporan Lengkap](docs/laporan.pdf)

## 📚 Mata Kuliah

- **Mata Kuliah**: Pemrograman Berorientasi Objek (PBO)
- **Dosen Pengampu**: Miftahul Adnan Rasyid (MIU)
- **Semester**: 2
- **Institusi**: Telkom University
- **Tanggal Pengumpulan**: 4 Mei 2026

## 📝 Lisensi

Project ini dibuat untuk keperluan akademis Tugas Besar mata kuliah Pemrograman Berorientasi Objek.

## 🤝 Kontribusi

Kontribusi terbatas untuk anggota kelompok "Asli Loh Yak". Untuk pertanyaan atau saran, silakan hubungi salah satu anggota tim.

---

**© 2026 Kelompok Asli Loh Yak - Telkom University**
