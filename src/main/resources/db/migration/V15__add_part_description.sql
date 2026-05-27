ALTER TABLE parts
    ADD COLUMN description VARCHAR(500);

UPDATE parts
SET description = COALESCE(NULLIF(name, ''), 'Peça ou insumo cadastrado')
WHERE description IS NULL;

ALTER TABLE parts
    ALTER COLUMN description SET NOT NULL;
