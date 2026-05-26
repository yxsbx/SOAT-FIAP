ALTER TABLE demo_leads
    ADD COLUMN city VARCHAR(120),
    ADD COLUMN message VARCHAR(500);

UPDATE demo_leads
SET city = '',
    message = '';

ALTER TABLE demo_leads
    ALTER COLUMN city SET NOT NULL,
    ALTER COLUMN message SET NOT NULL;
