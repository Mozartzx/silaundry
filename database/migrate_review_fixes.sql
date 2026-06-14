USE silaundry_db;

-- Mencatat waktu perubahan status terakhir untuk laporan pesanan selesai.
ALTER TABLE pesanan
    ADD COLUMN IF NOT EXISTS status_diperbarui_pada DATETIME NULL AFTER status_pesanan;

UPDATE pesanan
SET status_diperbarui_pada = COALESCE(status_diperbarui_pada, tanggal_masuk);

ALTER TABLE pesanan
    MODIFY COLUMN status_diperbarui_pada DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP;

-- Setiap pembayaran/cicilan memiliki nominal, metode, dan waktu transaksi sendiri.
ALTER TABLE detail_pembayaran
    ADD COLUMN IF NOT EXISTS metode VARCHAR(30) NULL AFTER waktu_bayar,
    ADD COLUMN IF NOT EXISTS jumlah DECIMAL(12,2) NULL AFTER metode;

UPDATE detail_pembayaran d
JOIN pembayaran p ON p.id_pembayaran = d.id_pembayaran
SET d.metode = COALESCE(d.metode, p.metode),
    d.jumlah = CASE WHEN d.jumlah IS NULL OR d.jumlah <= 0 THEN p.jumlah ELSE d.jumlah END;

ALTER TABLE detail_pembayaran
    MODIFY COLUMN metode VARCHAR(30) NOT NULL,
    MODIFY COLUMN jumlah DECIMAL(12,2) NOT NULL;

CREATE INDEX IF NOT EXISTS idx_detail_waktu_bayar ON detail_pembayaran (waktu_bayar);
