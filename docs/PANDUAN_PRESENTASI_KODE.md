# Panduan Singkat Presentasi Kode SiLaundry

Dokumen ini membantu menjelaskan source tanpa membaca setiap baris kode.

## Alur Umum

1. `Main` membuka `LoginFrame` melalui Swing Event Dispatch Thread.
2. View mengambil input pengguna dan memanggil controller.
3. Controller memeriksa aturan bisnis dan membentuk model.
4. DAO menjalankan SQL melalui `DatabaseConnection`.
5. Hasil dikembalikan ke view untuk ditampilkan pada form atau tabel.

## Bagian Penting

- `model`: bentuk data dan perilaku dasar objek seperti pesanan, pakaian, dan pembayaran.
- `controller`: validasi serta alur bisnis yang dipanggil oleh tampilan.
- `dao`: seluruh query database supaya SQL tidak bercampur dengan Swing.
- `service`: fitur khusus seperti smart grouping dan template WhatsApp.
- `view`: komponen Swing untuk pemilik, karyawan, dan pelanggan.
- `util`: koneksi database, pembuat ID, hash password, serta helper UI.

## Alur Pesanan

Karyawan memilih pelanggan dan paket. `LaundryController` mengambil tarif aktif,
menghitung total biaya, membuat objek `Pesanan`, lalu `LaundryDAO` menyimpannya.
Status hanya dapat bergerak berurutan sesuai aturan pada `StatusPesanan`.

## Smart Grouping

Karyawan mencatat kategori warna setiap pakaian. `SmartGroupingService` membaca
semua item pesanan, mengubah kategori menjadi label grup, lalu menyimpan hasilnya
dalam satu batch database.

## Pembayaran

Dropdown hanya menampilkan pesanan belum lunas. Ketika pembayaran dicatat,
jumlahnya mengikuti total pesanan dan status berubah menjadi `LUNAS`. Tabel
riwayat menggabungkan pesanan dan pembayaran agar tagihan belum lunas tetap terlihat.

## Notifikasi

Saat status menjadi siap diambil atau selesai, perubahan status dan notifikasi
aplikasi disimpan dalam satu transaksi. `WhatsAppNotifikasi` hanya membuat link
template, sehingga aplikasi tidak memerlukan API WhatsApp.

## Pengelolaan Pengguna

Pelanggan mendaftar melalui halaman login. Pemilik membuat dan menghapus akun
karyawan. Jika karyawan dihapus, pesanan lama tetap tersimpan karena foreign key
pesanan memakai aturan `ON DELETE SET NULL`.
