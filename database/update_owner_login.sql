USE silaundry_db;

DROP TRIGGER IF EXISTS trg_pengguna_owner_insert;
DROP TRIGGER IF EXISTS trg_pengguna_owner_update;

UPDATE pengguna
SET username = 'Master',
    nama_lengkap = 'Master Admin',
    kata_sandi = 'a665a45920422f9d417e4867efdc4fb8a04a1f3fff1fa07e998e86f7f7a27ae3'
WHERE role = 'PEMILIK';

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

-- Untuk upgrade schema lengkap, jalankan migrate_realistic_login_schema.sql.
