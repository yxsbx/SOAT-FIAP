ALTER TABLE parts
    ADD COLUMN cost_price NUMERIC(15, 2) NOT NULL DEFAULT 0,
    ADD COLUMN reserved_quantity INTEGER NOT NULL DEFAULT 0,
    ADD COLUMN reservation_days INTEGER NOT NULL DEFAULT 3,
    ADD COLUMN reservation_expires_at TIMESTAMP;

UPDATE parts
SET cost_price = ROUND(unit_price * 0.62, 2)
WHERE cost_price = 0;

CREATE TABLE stock_movements (
    id UUID PRIMARY KEY,
    part_id UUID NOT NULL REFERENCES parts (id),
    movement_type VARCHAR(30) NOT NULL,
    quantity INTEGER NOT NULL,
    unit_cost NUMERIC(15, 2),
    unit_price NUMERIC(15, 2),
    reason VARCHAR(500),
    created_at TIMESTAMP NOT NULL
);

CREATE INDEX idx_stock_movements_part_id ON stock_movements (part_id);
CREATE INDEX idx_stock_movements_type ON stock_movements (movement_type);
