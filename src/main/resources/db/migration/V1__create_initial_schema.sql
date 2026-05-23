CREATE TABLE customers
(
    id                   UUID PRIMARY KEY,
    name                 VARCHAR(120) NOT NULL,
    document_type        VARCHAR(10)  NOT NULL,
    document_value       VARCHAR(14)  NOT NULL UNIQUE,
    phone                VARCHAR(20)  NOT NULL,
    email                VARCHAR(120) NOT NULL,
    address_street       VARCHAR(120),
    address_number       VARCHAR(20),
    address_complement   VARCHAR(80),
    address_neighborhood VARCHAR(80),
    address_city         VARCHAR(80),
    address_state        VARCHAR(2),
    address_zip_code     VARCHAR(8),
    active               BOOLEAN      NOT NULL,
    created_at           TIMESTAMP    NOT NULL
);

CREATE TABLE vehicles
(
    id               UUID PRIMARY KEY,
    customer_id      UUID        NOT NULL REFERENCES customers (id),
    plate            VARCHAR(8)  NOT NULL UNIQUE,
    brand            VARCHAR(60) NOT NULL,
    model            VARCHAR(80) NOT NULL,
    manufacture_year INTEGER     NOT NULL,
    mileage          INTEGER     NOT NULL,
    active           BOOLEAN     NOT NULL
);

CREATE TABLE workshop_services
(
    id                        UUID PRIMARY KEY,
    name                      VARCHAR(100)   NOT NULL,
    description               VARCHAR(500)   NOT NULL,
    base_price                NUMERIC(15, 2) NOT NULL,
    estimated_time_in_minutes INTEGER        NOT NULL,
    active                    BOOLEAN        NOT NULL
);

CREATE TABLE parts
(
    id             UUID PRIMARY KEY,
    name           VARCHAR(120)   NOT NULL,
    sku            VARCHAR(60)    NOT NULL UNIQUE,
    category       VARCHAR(80)    NOT NULL,
    subcategory    VARCHAR(80),
    brand          VARCHAR(80)    NOT NULL,
    unit_price     NUMERIC(15, 2) NOT NULL,
    stock_quantity INTEGER        NOT NULL,
    minimum_stock  INTEGER        NOT NULL,
    active         BOOLEAN        NOT NULL
);

CREATE TABLE service_orders
(
    id                  UUID PRIMARY KEY,
    customer_id         UUID           NOT NULL REFERENCES customers (id),
    vehicle_id          UUID           NOT NULL REFERENCES vehicles (id),
    status              VARCHAR(30)    NOT NULL,
    diagnostic_notes    VARCHAR(2000)  NOT NULL,
    total_amount        NUMERIC(15, 2) NOT NULL,
    created_at          TIMESTAMP      NOT NULL,
    budget_generated_at TIMESTAMP,
    approved_at         TIMESTAMP,
    started_at          TIMESTAMP,
    finished_at         TIMESTAMP,
    delivered_at        TIMESTAMP
);

CREATE TABLE service_order_services
(
    id               UUID PRIMARY KEY,
    service_order_id UUID           NOT NULL REFERENCES service_orders (id) ON DELETE CASCADE,
    service_id       UUID           NOT NULL REFERENCES workshop_services (id),
    name             VARCHAR(100)   NOT NULL,
    quantity         INTEGER        NOT NULL,
    unit_price       NUMERIC(15, 2) NOT NULL,
    total_price      NUMERIC(15, 2) NOT NULL
);

CREATE TABLE service_order_parts
(
    id               UUID PRIMARY KEY,
    service_order_id UUID           NOT NULL REFERENCES service_orders (id) ON DELETE CASCADE,
    part_id          UUID           NOT NULL REFERENCES parts (id),
    name             VARCHAR(120)   NOT NULL,
    sku              VARCHAR(60)    NOT NULL,
    quantity         INTEGER        NOT NULL,
    unit_price       NUMERIC(15, 2) NOT NULL,
    total_price      NUMERIC(15, 2) NOT NULL
);

CREATE TABLE users
(
    id            UUID PRIMARY KEY,
    username      VARCHAR(120) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role          VARCHAR(50)  NOT NULL,
    active        BOOLEAN      NOT NULL,
    created_at    TIMESTAMP    NOT NULL
);

CREATE INDEX idx_vehicles_customer_id ON vehicles (customer_id);
CREATE INDEX idx_service_orders_customer_id ON service_orders (customer_id);
CREATE INDEX idx_service_orders_vehicle_id ON service_orders (vehicle_id);
CREATE INDEX idx_service_orders_status ON service_orders (status);
CREATE INDEX idx_service_order_services_order_id ON service_order_services (service_order_id);
CREATE INDEX idx_service_order_parts_order_id ON service_order_parts (service_order_id);
