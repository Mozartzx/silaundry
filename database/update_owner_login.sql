USE silaundry_db;

UPDATE pengguna
SET username = 'Mozart',
    kata_sandi = 'a665a45920422f9d417e4867efdc4fb8a04a1f3fff1fa07e998e86f7f7a27ae3'
WHERE role = 'PEMILIK';

-- Untuk upgrade schema lengkap, jalankan migrate_realistic_login_schema.sql.
