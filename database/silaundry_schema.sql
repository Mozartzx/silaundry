DROP DATABASE IF EXISTS silaundry_db;
CREATE DATABASE silaundry_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE silaundry_db;

CREATE TABLE pengguna (
    id_pengguna VARCHAR(20) PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    nama_lengkap VARCHAR(100) NOT NULL,
    nomor_telepon VARCHAR(20) NOT NULL,
    kata_sandi CHAR(64) NOT NULL,
    role ENUM('PEMILIK','KARYAWAN','PELANGGAN') NOT NULL,
    aktif BOOLEAN NOT NULL DEFAULT TRUE,
    dibuat_pada TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    diperbarui_pada TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    terakhir_login DATETIME,
    pemilik_guard TINYINT GENERATED ALWAYS AS (
        CASE WHEN role = 'PEMILIK' THEN 1 ELSE NULL END
    ) STORED,
    UNIQUE KEY uq_pengguna_username (username),
    UNIQUE KEY uq_satu_pemilik (pemilik_guard),
    INDEX idx_pengguna_login (username, kata_sandi, role, aktif),
    INDEX idx_pengguna_role_aktif (role, aktif),
    CONSTRAINT chk_pengguna_password_sha CHECK (CHAR_LENGTH(kata_sandi) = 64),
    CONSTRAINT chk_pengguna_telepon CHECK (CHAR_LENGTH(nomor_telepon) BETWEEN 8 AND 20)
);

CREATE TABLE pelanggan (
    id_pelanggan VARCHAR(20) PRIMARY KEY,
    id_pengguna VARCHAR(20) NOT NULL UNIQUE,
    alamat VARCHAR(255) NOT NULL,
    FOREIGN KEY (id_pengguna) REFERENCES pengguna(id_pengguna)
        ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT chk_pelanggan_alamat CHECK (CHAR_LENGTH(alamat) >= 5)
);

CREATE TABLE karyawan (
    id_karyawan VARCHAR(20) PRIMARY KEY,
    id_pengguna VARCHAR(20) NOT NULL UNIQUE,
    shift_kerja VARCHAR(30) NOT NULL,
    FOREIGN KEY (id_pengguna) REFERENCES pengguna(id_pengguna)
        ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT chk_karyawan_shift CHECK (CHAR_LENGTH(shift_kerja) >= 3)
);

CREATE TABLE pemilik (
    id_pemilik VARCHAR(20) PRIMARY KEY,
    id_pengguna VARCHAR(20) NOT NULL UNIQUE,
    FOREIGN KEY (id_pengguna) REFERENCES pengguna(id_pengguna)
        ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE mesin_cuci (
    id_mesin VARCHAR(20) PRIMARY KEY,
    nama_mesin VARCHAR(50) NOT NULL,
    kapasitas DECIMAL(5,2) NOT NULL,
    status ENUM('TERSEDIA','DIGUNAKAN','PERAWATAN') NOT NULL DEFAULT 'TERSEDIA',
    CONSTRAINT chk_mesin_kapasitas CHECK (kapasitas > 0)
);

CREATE TABLE tarif_laundry (
    id_tarif VARCHAR(20) PRIMARY KEY,
    paket_laundry ENUM('STANDARD_2_HARI','EXPRESS_1_HARI') NOT NULL UNIQUE,
    nama_paket VARCHAR(60) NOT NULL,
    estimasi_hari INT NOT NULL,
    harga_per_kg DECIMAL(12,2) NOT NULL,
    aktif BOOLEAN NOT NULL DEFAULT TRUE,
    diperbarui_pada TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_tarif_aktif (aktif),
    CONSTRAINT chk_tarif_estimasi CHECK (estimasi_hari BETWEEN 1 AND 14),
    CONSTRAINT chk_tarif_harga CHECK (harga_per_kg > 0)
);

CREATE TABLE pesanan (
    id_pesanan VARCHAR(20) PRIMARY KEY,
    id_pelanggan VARCHAR(20) NOT NULL,
    id_karyawan VARCHAR(20),
    tanggal_masuk DATE NOT NULL,
    estimasi_selesai DATE NOT NULL,
    status_pesanan ENUM('BARU','DITERIMA','DIPROSES','DICUCI','DIKERINGKAN','DISETRIKA','SIAP_DIAMBIL','SELESAI','DIBATALKAN') NOT NULL DEFAULT 'BARU',
    status_diperbarui_pada DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    paket_laundry ENUM('STANDARD_2_HARI','EXPRESS_1_HARI') NOT NULL,
    berat_kg DECIMAL(6,2) NOT NULL,
    harga_per_kg DECIMAL(12,2) NOT NULL,
    total_biaya DECIMAL(12,2) NOT NULL,
    catatan TEXT,
    FOREIGN KEY (id_pelanggan) REFERENCES pelanggan(id_pelanggan)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    FOREIGN KEY (id_karyawan) REFERENCES karyawan(id_karyawan)
        ON UPDATE CASCADE ON DELETE SET NULL,
    INDEX idx_pesanan_pelanggan_status (id_pelanggan, status_pesanan),
    INDEX idx_pesanan_karyawan_status (id_karyawan, status_pesanan),
    INDEX idx_pesanan_tanggal (tanggal_masuk),
    CONSTRAINT chk_pesanan_estimasi CHECK (estimasi_selesai >= tanggal_masuk),
    CONSTRAINT chk_pesanan_berat CHECK (berat_kg > 0),
    CONSTRAINT chk_pesanan_harga_per_kg CHECK (harga_per_kg > 0),
    CONSTRAINT chk_pesanan_total CHECK (total_biaya >= 0)
);

CREATE TABLE item_pakaian (
    id_item VARCHAR(20) PRIMARY KEY,
    id_pesanan VARCHAR(20) NOT NULL,
    jenis_pakaian VARCHAR(60) NOT NULL,
    kategori_warna ENUM('PUTIH','TERANG','GELAP','MUDAH_LUNTUR') NOT NULL,
    kondisi_awal VARCHAR(120) NOT NULL,
    deskripsi_detail VARCHAR(255) NOT NULL,
    label_smart_group VARCHAR(60) NOT NULL,
    kode_qr VARCHAR(60) NOT NULL UNIQUE,
    FOREIGN KEY (id_pesanan) REFERENCES pesanan(id_pesanan)
        ON UPDATE CASCADE ON DELETE CASCADE,
    INDEX idx_item_pesanan (id_pesanan),
    INDEX idx_item_warna_group (kategori_warna, label_smart_group)
);

CREATE TABLE proses_laundry (
    id_proses VARCHAR(20) PRIMARY KEY,
    id_pesanan VARCHAR(20) NOT NULL,
    id_mesin VARCHAR(20),
    tahap ENUM('PENERIMAAN','PENCUCIAN','PENGERINGAN','PENYETRIKAAN','PENGEMASAN','SELESAI') NOT NULL,
    waktu_mulai DATETIME NOT NULL,
    waktu_selesai DATETIME,
    FOREIGN KEY (id_pesanan) REFERENCES pesanan(id_pesanan)
        ON UPDATE CASCADE ON DELETE CASCADE,
    FOREIGN KEY (id_mesin) REFERENCES mesin_cuci(id_mesin)
        ON UPDATE CASCADE ON DELETE SET NULL,
    INDEX idx_proses_pesanan (id_pesanan),
    INDEX idx_proses_mesin (id_mesin),
    CONSTRAINT chk_proses_waktu CHECK (waktu_selesai IS NULL OR waktu_selesai >= waktu_mulai)
);

CREATE TABLE pembayaran (
    id_pembayaran VARCHAR(20) PRIMARY KEY,
    id_pesanan VARCHAR(20) NOT NULL UNIQUE,
    metode VARCHAR(30) NOT NULL,
    jumlah DECIMAL(12,2) NOT NULL,
    status ENUM('BELUM_BAYAR','SEBAGIAN','LUNAS','DIBATALKAN') NOT NULL DEFAULT 'BELUM_BAYAR',
    FOREIGN KEY (id_pesanan) REFERENCES pesanan(id_pesanan)
        ON UPDATE CASCADE ON DELETE CASCADE,
    INDEX idx_pembayaran_status (status),
    CONSTRAINT chk_pembayaran_jumlah CHECK (jumlah >= 0)
);

CREATE TABLE detail_pembayaran (
    id_detail VARCHAR(20) PRIMARY KEY,
    id_pembayaran VARCHAR(20) NOT NULL,
    waktu_bayar DATETIME NOT NULL,
    metode VARCHAR(30) NOT NULL,
    jumlah DECIMAL(12,2) NOT NULL,
    keterangan VARCHAR(150),
    FOREIGN KEY (id_pembayaran) REFERENCES pembayaran(id_pembayaran)
        ON UPDATE CASCADE ON DELETE CASCADE,
    INDEX idx_detail_pembayaran (id_pembayaran),
    INDEX idx_detail_waktu_bayar (waktu_bayar),
    CONSTRAINT chk_detail_jumlah CHECK (jumlah > 0)
);

CREATE TABLE notifikasi (
    id_notifikasi VARCHAR(20) PRIMARY KEY,
    id_pesanan VARCHAR(20) NOT NULL,
    pesan VARCHAR(255) NOT NULL,
    tanggal_kirim DATETIME NOT NULL,
    sudah_dibaca BOOLEAN NOT NULL DEFAULT FALSE,
    FOREIGN KEY (id_pesanan) REFERENCES pesanan(id_pesanan)
        ON UPDATE CASCADE ON DELETE CASCADE,
    INDEX idx_notifikasi_pesanan_baca (id_pesanan, sudah_dibaca)
);

DELIMITER $$

CREATE TRIGGER trg_pengguna_owner_insert
BEFORE INSERT ON pengguna
FOR EACH ROW
BEGIN
    IF NEW.role = 'PEMILIK' AND NEW.username <> 'Master' THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Akun pemilik hanya boleh menggunakan username Master.';
    END IF;
END$$

CREATE TRIGGER trg_pengguna_owner_update
BEFORE UPDATE ON pengguna
FOR EACH ROW
BEGIN
    IF NEW.role = 'PEMILIK' AND NEW.username <> 'Master' THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Akun pemilik hanya boleh menggunakan username Master.';
    END IF;
END$$

CREATE TRIGGER trg_pelanggan_role_insert
BEFORE INSERT ON pelanggan
FOR EACH ROW
BEGIN
    IF COALESCE((SELECT role FROM pengguna WHERE id_pengguna = NEW.id_pengguna), '') <> 'PELANGGAN' THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Data pelanggan harus terhubung ke pengguna role PELANGGAN.';
    END IF;
END$$

CREATE TRIGGER trg_karyawan_role_insert
BEFORE INSERT ON karyawan
FOR EACH ROW
BEGIN
    IF COALESCE((SELECT role FROM pengguna WHERE id_pengguna = NEW.id_pengguna), '') <> 'KARYAWAN' THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Data karyawan harus terhubung ke pengguna role KARYAWAN.';
    END IF;
END$$

CREATE TRIGGER trg_pemilik_role_insert
BEFORE INSERT ON pemilik
FOR EACH ROW
BEGIN
    IF COALESCE((SELECT role FROM pengguna WHERE id_pengguna = NEW.id_pengguna), '') <> 'PEMILIK' THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Data pemilik harus terhubung ke pengguna role PEMILIK.';
    END IF;
END$$

DELIMITER ;

INSERT INTO pengguna (id_pengguna, username, nama_lengkap, nomor_telepon, kata_sandi, role) VALUES
('USR001', 'Master', 'Master Admin', '081234567890', 'a665a45920422f9d417e4867efdc4fb8a04a1f3fff1fa07e998e86f7f7a27ae3', 'PEMILIK');

INSERT INTO pemilik (id_pemilik, id_pengguna) VALUES
('OWN001', 'USR001');

INSERT INTO mesin_cuci (id_mesin, nama_mesin, kapasitas, status) VALUES
('MSN001', 'Mesin A', 8.00, 'TERSEDIA'),
('MSN002', 'Mesin B', 10.00, 'TERSEDIA'),
('MSN003', 'Mesin C', 12.00, 'PERAWATAN');

INSERT INTO tarif_laundry (id_tarif, paket_laundry, nama_paket, estimasi_hari, harga_per_kg, aktif) VALUES
('TRF001', 'STANDARD_2_HARI', 'Standard 2 Hari', 2, 7000, TRUE),
('TRF002', 'EXPRESS_1_HARI', 'Express 1 Hari', 1, 8000, TRUE);
