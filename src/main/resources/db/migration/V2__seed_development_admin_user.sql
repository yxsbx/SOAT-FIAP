ALTER TABLE users
    ADD COLUMN customer_id UUID REFERENCES customers (id);

INSERT INTO users (id, username, password_hash, role, customer_id, active, created_at)
VALUES ('00000000-0000-0000-0000-000000000001', 'admin@autocarehub.com', '$2a$10$xAb5kI.uSxQkLo9n6tZTiuf8WbQcehwTGGk99zzc2QtY28sx9WFO.', 'ADMIN', NULL, TRUE, CURRENT_TIMESTAMP);
