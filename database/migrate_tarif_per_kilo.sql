USE silaundry_db;

CREATE TABLE IF NOT EXISTS tarif_laundry (
    id_tarif VARCHAR(20) PRIMARY KEY,
    paket_laundry ENUM('STANDARD_2_HARI','EXPRESS_1_HARI') NOT NULL UNIQUE,
    nama_paket VARCHAR(60) NOT NULL,
    estimasi_hari INT NOT NULL,
    harga_per_kg DECIMAL(12,2) NOT NULL,
    aktif BOOLEAN NOT NULL DEFAULT TRUE,
    diperbarui_pada TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_tarif_aktif (aktif)
);

INSERT INTO tarif_laundry (id_tarif, paket_laundry, nama_paket, estimasi_hari, harga_per_kg, aktif) VALUES
('TRF001', 'STANDARD_2_HARI', 'Standard 2 Hari', 2, 7000, TRUE),
('TRF002', 'EXPRESS_1_HARI', 'Express 1 Hari', 1, 8000, TRUE)
ON DUPLICATE KEY UPDATE
    nama_paket = VALUES(nama_paket),
    estimasi_hari = VALUES(estimasi_hari),
    aktif = VALUES(aktif);

ALTER TABLE pesanan
    ADD COLUMN IF NOT EXISTS paket_laundry ENUM('STANDARD_2_HARI','EXPRESS_1_HARI') NULL AFTER status_pesanan,
    ADD COLUMN IF NOT EXISTS berat_kg DECIMAL(6,2) NULL AFTER paket_laundry,
    ADD COLUMN IF NOT EXISTS harga_per_kg DECIMAL(12,2) NULL AFTER berat_kg;

UPDATE pesanan
SET paket_laundry = CASE
    WHEN DATEDIFF(estimasi_selesai, tanggal_masuk) <= 1 THEN 'EXPRESS_1_HARI'
    ELSE 'STANDARD_2_HARI'
END
WHERE paket_laundry IS NULL;

UPDATE pesanan
SET harga_per_kg = CASE
    WHEN paket_laundry = 'EXPRESS_1_HARI' THEN 8000
    ELSE 7000
END
WHERE harga_per_kg IS NULL OR harga_per_kg <= 0;

UPDATE pesanan
SET berat_kg = CASE
    WHEN total_biaya > 0 THEN ROUND(total_biaya / harga_per_kg, 2)
    ELSE 1
END
WHERE berat_kg IS NULL OR berat_kg <= 0;

UPDATE pesanan
SET total_biaya = ROUND(berat_kg * harga_per_kg, 0)
WHERE total_biaya IS NULL OR total_biaya <= 0;

ALTER TABLE pesanan
    MODIFY COLUMN paket_laundry ENUM('STANDARD_2_HARI','EXPRESS_1_HARI') NOT NULL,
    MODIFY COLUMN berat_kg DECIMAL(6,2) NOT NULL,
    MODIFY COLUMN harga_per_kg DECIMAL(12,2) NOT NULL;

ALTER TABLE item_pakaian
    ADD COLUMN IF NOT EXISTS deskripsi_detail VARCHAR(255) NOT NULL DEFAULT '' AFTER kondisi_awal;

UPDATE item_pakaian
SET deskripsi_detail = CONCAT(jenis_pakaian, ' - ', kondisi_awal)
WHERE deskripsi_detail = '';
