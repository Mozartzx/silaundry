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
        +buatPesananOnline(): void
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
        -labelSmartGroup: String
        -kodeQR: String
        +terapkanGrupWarna(): void
        +generateKodeQR(): void
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
        -id: int
        +kirimNotifikasi(): void
    }

    class Notifikasi {
        -idNotifikasi: String
        -pesan: String
        -tanggalKirim: String
        +kirimNotifikasi(): void
    }

    %% Relationships - Inheritance
    Pengguna <|-- Pelanggan : Extends
    Pengguna <|-- Karyawan : Extends
    Pengguna <|-- Pemilik : Extends

    %% Relationships - Interface Implementation
    INotifiable <|.. Notifikasi : Implements

    %% Relationships - Associations (dari gambar)
    Pelanggan --> Pesanan : membuat
    Karyawan --> Pesanan : mengelola
    Pemilik --> LaporanKeuangan : mengakses
    Pemilik --> DataDasbor : memantau

    %% Relationships - Core Business
    Pesanan *-- ItemPakaian : mengandung
    Pesanan --> Pembayaran : memiliki
    Notifikasi --> Pesanan : mengirim
    Pesanan ..> SmartGroupingService : dikelompokkan
    
    ItemPakaian --> ItemTrackingService : dilacak
    ProsesLaundry --> MesinCuci : menggunakan
    
    Pembayaran --> DetailPembayaran : merinci
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
- `INotifiable` (Interface) & `Notifikasi` - Sistem notifikasi
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
- **Design Pattern**: MVC (Model-View-Controller)

## 🚀 Cara Menjalankan

1. **Clone repository**
```bash
   git clone https://github.com/[username]/SILAUNDRY.git
   cd SILAUNDRY
```

2. **Buka di NetBeans**
   - File → Open Project
   - Pilih folder SILAUNDRY

3. **Build & Run**
   - Klik kanan pada project → Clean and Build
   - Run Main File

## 📊 Fitur Utama

### Untuk Pelanggan
- 📱 Lacak status cucian secara real-time
- 🛒 Buat pesanan online
- 🔔 Notifikasi otomatis saat proses selesai

### Untuk Karyawan
- ➕ CRUD manajemen pesanan
- 📦 Rekam data pakaian per item
- 🎨 Eksekusi Smart Grouping
- 🔄 Update status operasional

### Untuk Owner/Pemilik
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
