ALTER TABLE users
    ADD COLUMN full_name VARCHAR(160),
    ADD COLUMN profile_type VARCHAR(60);

UPDATE users
SET full_name = CASE
        WHEN username = 'master@autocarehub.com' THEN 'Marina AutoCare Hub'
        WHEN username = 'oficina.admin@autocarehub.com' THEN 'Ana Oficina Central'
        WHEN username = 'loja.admin@autocarehub.com' THEN 'Bruno Loja de Peças'
        WHEN username = 'oficina.funcionario@autocarehub.com' THEN 'Carlos Atendimento Oficina'
        WHEN username = 'loja.funcionario@autocarehub.com' THEN 'Daniel Estoque Peças'
        WHEN username = 'cliente@autocarehub.com' THEN 'Eduardo Cliente Veículos'
        ELSE username
    END,
    profile_type = CASE
        WHEN username = 'master@autocarehub.com' THEN 'MASTER_ADMIN'
        WHEN username = 'oficina.admin@autocarehub.com' THEN 'WORKSHOP_ADMIN'
        WHEN username = 'loja.admin@autocarehub.com' THEN 'PARTS_STORE_ADMIN'
        WHEN username = 'oficina.funcionario@autocarehub.com' THEN 'WORKSHOP_EMPLOYEE'
        WHEN username = 'loja.funcionario@autocarehub.com' THEN 'PARTS_STORE_EMPLOYEE'
        WHEN username = 'cliente@autocarehub.com' THEN 'CUSTOMER_OWNER'
        ELSE role
    END;

ALTER TABLE users
    ALTER COLUMN full_name SET NOT NULL,
    ALTER COLUMN profile_type SET NOT NULL;

CREATE TABLE user_preferences
(
    user_id    UUID         NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    pref_key   VARCHAR(80)  NOT NULL,
    value_json TEXT         NOT NULL,
    updated_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (user_id, pref_key)
);

INSERT INTO user_preferences (user_id, pref_key, value_json)
SELECT id,
       'home',
       '{"widgets":["orders-progress","services-catalog","active-customers","vehicles-in-service","pending-budgets","waiting-contact","ready-pickup"],"showAlertsOnHome":false}'
FROM users
ON CONFLICT DO NOTHING;
