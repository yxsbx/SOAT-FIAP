ALTER TABLE users
    ADD COLUMN company_name VARCHAR(160),
    ADD COLUMN company_type VARCHAR(60);

UPDATE users
SET company_name = CASE
        WHEN profile_type = 'WORKSHOP_ADMIN' THEN 'Oficina Central AutoCare'
        WHEN profile_type = 'PARTS_STORE_ADMIN' THEN 'Loja Pecas Prime'
        WHEN profile_type = 'WORKSHOP_EMPLOYEE' THEN 'Oficina Central AutoCare'
        WHEN profile_type = 'PARTS_STORE_EMPLOYEE' THEN 'Loja Pecas Prime'
        WHEN profile_type = 'MASTER_ADMIN' THEN 'AutoCare Hub'
        ELSE ''
    END,
    company_type = CASE
        WHEN profile_type IN ('WORKSHOP_ADMIN', 'WORKSHOP_EMPLOYEE') THEN 'WORKSHOP'
        WHEN profile_type IN ('PARTS_STORE_ADMIN', 'PARTS_STORE_EMPLOYEE') THEN 'PARTS_STORE'
        WHEN profile_type = 'MASTER_ADMIN' THEN 'PLATFORM'
        ELSE ''
    END;

ALTER TABLE users
    ALTER COLUMN company_name SET NOT NULL,
    ALTER COLUMN company_type SET NOT NULL;
