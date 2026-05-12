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

Sistem terdiri dari 3 modul utama:

### **Modul A: Aktor dan Manajemen Pengguna**
- `Pengguna` (Abstract Class)
  - `Pelanggan` (extends)
  - `Karyawan` (extends)
  - `Pemilik` (extends)

### **Modul B: Operasional Bisnis**
- `Pesanan` - Pusat data transaksi
- `ItemPakaian` - Representasi digital pakaian
- `ProsesLaundry` - Tahapan teknis
- `MesinCuci` - Representasi mesin fisik
- `SmartGroupingSystem` - Service pengelompokan
- `ItemTrackingService` - Service pelacakan

### **Modul C: Layanan Sistem**
- `Pembayaran` & `DetailPembayaran`
- `INotifiable` (Interface) & `Notifikasi` (implements)
- `DataDasbor` - Metrik harian
- `LaporanKeuangan` - Laporan bulanan

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
