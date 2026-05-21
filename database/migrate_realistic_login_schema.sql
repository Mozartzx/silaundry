USE silaundry_db;

UPDATE pengguna
SET username = 'Mozart',
    kata_sandi = 'a665a45920422f9d417e4867efdc4fb8a04a1f3fff1fa07e998e86f7f7a27ae3'
WHERE role = 'PEMILIK';

ALTER TABLE pengguna
    MODIFY kata_sandi CHAR(64) NOT NULL,
    ADD COLUMN diperbarui_pada TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP AFTER dibuat_pada,
    ADD COLUMN terakhir_login DATETIME NULL AFTER diperbarui_pada,
    ADD COLUMN pemilik_guard TINYINT GENERATED ALWAYS AS (
        CASE WHEN role = 'PEMILIK' THEN 1 ELSE NULL END
    ) STORED AFTER terakhir_login,
    ADD UNIQUE KEY uq_satu_pemilik (pemilik_guard),
    ADD INDEX idx_pengguna_login (username, kata_sandi, role, aktif),
    ADD INDEX idx_pengguna_role_aktif (role, aktif),
    ADD CONSTRAINT chk_pengguna_password_sha CHECK (CHAR_LENGTH(kata_sandi) = 64),
    ADD CONSTRAINT chk_pengguna_telepon CHECK (CHAR_LENGTH(nomor_telepon) BETWEEN 8 AND 20);

ALTER TABLE pelanggan
    MODIFY alamat VARCHAR(255) NOT NULL,
    ADD CONSTRAINT chk_pelanggan_alamat CHECK (CHAR_LENGTH(alamat) >= 5);

ALTER TABLE karyawan
    ADD CONSTRAINT chk_karyawan_shift CHECK (CHAR_LENGTH(shift_kerja) >= 3);

ALTER TABLE mesin_cuci
    ADD CONSTRAINT chk_mesin_kapasitas CHECK (kapasitas > 0);

ALTER TABLE pesanan
    ADD INDEX idx_pesanan_pelanggan_status (id_pelanggan, status_pesanan),
    ADD INDEX idx_pesanan_karyawan_status (id_karyawan, status_pesanan),
    ADD INDEX idx_pesanan_tanggal (tanggal_masuk),
    ADD CONSTRAINT chk_pesanan_estimasi CHECK (estimasi_selesai >= tanggal_masuk),
    ADD CONSTRAINT chk_pesanan_total CHECK (total_biaya >= 0);

ALTER TABLE item_pakaian
    ADD INDEX idx_item_pesanan (id_pesanan),
    ADD INDEX idx_item_warna_group (kategori_warna, label_smart_group);

ALTER TABLE proses_laundry
    ADD INDEX idx_proses_pesanan (id_pesanan),
    ADD INDEX idx_proses_mesin (id_mesin),
    ADD CONSTRAINT chk_proses_waktu CHECK (waktu_selesai IS NULL OR waktu_selesai >= waktu_mulai);

ALTER TABLE pembayaran
    ADD INDEX idx_pembayaran_status (status),
    ADD CONSTRAINT chk_pembayaran_jumlah CHECK (jumlah >= 0);

ALTER TABLE detail_pembayaran
    ADD INDEX idx_detail_pembayaran (id_pembayaran);

ALTER TABLE notifikasi
    ADD INDEX idx_notifikasi_pesanan_baca (id_pesanan, sudah_dibaca);

DROP TRIGGER IF EXISTS trg_pengguna_owner_insert;
DROP TRIGGER IF EXISTS trg_pengguna_owner_update;
DROP TRIGGER IF EXISTS trg_pelanggan_role_insert;
DROP TRIGGER IF EXISTS trg_karyawan_role_insert;
DROP TRIGGER IF EXISTS trg_pemilik_role_insert;

DELIMITER $$

CREATE TRIGGER trg_pengguna_owner_insert
BEFORE INSERT ON pengguna
FOR EACH ROW
BEGIN
    IF NEW.role = 'PEMILIK' AND NEW.username <> 'Mozart' THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Akun pemilik hanya boleh menggunakan username Mozart.';
    END IF;
END$$

CREATE TRIGGER trg_pengguna_owner_update
BEFORE UPDATE ON pengguna
FOR EACH ROW
BEGIN
    IF NEW.role = 'PEMILIK' AND NEW.username <> 'Mozart' THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Akun pemilik hanya boleh menggunakan username Mozart.';
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
