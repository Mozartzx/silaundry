USE silaundry_db;

-- Bersihkan data operasional agar demonstrasi dimulai dari kondisi kosong.
SET FOREIGN_KEY_CHECKS = 0;
DELETE FROM notifikasi;
DELETE FROM pembayaran;
DELETE FROM item_pakaian;
DELETE FROM pesanan;
DELETE FROM pelanggan;
DELETE FROM karyawan;
DELETE FROM pemilik;
DELETE FROM pengguna;
SET FOREIGN_KEY_CHECKS = 1;

-- Pastikan aturan satu akun pemilik menggunakan identitas presentasi final.
DROP TRIGGER IF EXISTS trg_pengguna_owner_insert;
DROP TRIGGER IF EXISTS trg_pengguna_owner_update;

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

DELIMITER ;

-- Password 123 disimpan sebagai hash SHA-256.
INSERT INTO pengguna (id_pengguna, username, nama_lengkap, nomor_telepon, kata_sandi, role)
VALUES ('USR001', 'Master', 'Master Admin', '081234567890',
        'a665a45920422f9d417e4867efdc4fb8a04a1f3fff1fa07e998e86f7f7a27ae3', 'PEMILIK');

INSERT INTO pemilik (id_pemilik, id_pengguna)
VALUES ('OWN001', 'USR001');

-- Kembalikan konfigurasi tarif ke nilai awal presentasi.
INSERT INTO tarif_laundry
    (id_tarif, paket_laundry, nama_paket, estimasi_hari, harga_per_kg, aktif)
VALUES
    ('TRF001', 'STANDARD_2_HARI', 'Standard 2 Hari', 2, 7000, TRUE),
    ('TRF002', 'EXPRESS_1_HARI', 'Express 1 Hari', 1, 8000, TRUE)
ON DUPLICATE KEY UPDATE
    nama_paket = VALUES(nama_paket),
    estimasi_hari = VALUES(estimasi_hari),
    harga_per_kg = VALUES(harga_per_kg),
    aktif = VALUES(aktif);
