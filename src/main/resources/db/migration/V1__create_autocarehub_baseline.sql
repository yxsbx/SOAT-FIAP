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

ALTER TABLE users
    ADD COLUMN customer_id UUID REFERENCES customers (id);

INSERT INTO users (id, username, password_hash, role, customer_id, active, created_at)
VALUES ('00000000-0000-0000-0000-000000000001', 'admin@autocarehub.com',
        '$2a$10$xAb5kI.uSxQkLo9n6tZTiuf8WbQcehwTGGk99zzc2QtY28sx9WFO.', 'ADMIN', NULL, TRUE, CURRENT_TIMESTAMP);

INSERT INTO customers (id, name, document_type, document_value, phone, email,
                       address_street, address_number, address_complement, address_neighborhood,
                       address_city, address_state, address_zip_code, active, created_at)
VALUES ('10000000-0000-0000-0000-000000000001', 'Mariana Costa', 'CPF', '12345678909', '11988887777',
        'mariana.costa@email.com', 'Rua das Oficinas', '120', 'Casa 2', 'Mooca', 'Sao Paulo', 'SP', '03111000', TRUE,
        CURRENT_TIMESTAMP),
       ('10000000-0000-0000-0000-000000000002', 'Ricardo Almeida', 'CPF', '98765432100', '11977776666',
        'ricardo.almeida@email.com', 'Avenida Brasil', '450', NULL, 'Jardins', 'Sao Paulo', 'SP', '01430000', TRUE,
        CURRENT_TIMESTAMP),
       ('10000000-0000-0000-0000-000000000003', 'Patricia Oliveira', 'CPF', '45678912300', '11966665555',
        'patricia.oliveira@email.com', 'Rua Vergueiro', '2200', 'Apto 81', 'Vila Mariana', 'Sao Paulo', 'SP',
        '04102000', TRUE, CURRENT_TIMESTAMP),
       ('10000000-0000-0000-0000-000000000004', 'TechLog Transportes Ltda', 'CNPJ', '11222333000144', '1133332222',
        'frota@techlog.com', 'Avenida Industrial', '900', 'Galpao B', 'Tambore', 'Barueri', 'SP', '06460000', TRUE,
        CURRENT_TIMESTAMP),
       ('10000000-0000-0000-0000-000000000005', 'Joao Pereira', 'CPF', '32165498701', '11955554444',
        'joao.pereira@email.com', 'Rua Augusta', '1001', NULL, 'Consolação', 'Sao Paulo', 'SP', '01305000', TRUE,
        CURRENT_TIMESTAMP) ON CONFLICT (id) DO NOTHING;

INSERT INTO vehicles (id, customer_id, plate, brand, model, manufacture_year, mileage, active)
VALUES ('20000000-0000-0000-0000-000000000001', '10000000-0000-0000-0000-000000000001', 'MCA1D23', 'Honda',
        'Civic Touring', 2020, 58200, TRUE),
       ('20000000-0000-0000-0000-000000000002', '10000000-0000-0000-0000-000000000001', 'MCA2E45', 'Jeep',
        'Compass Limited', 2022, 31800, TRUE),
       ('20000000-0000-0000-0000-000000000003', '10000000-0000-0000-0000-000000000002', 'RCA3F67', 'Toyota',
        'Corolla XEi', 2019, 74000, TRUE),
       ('20000000-0000-0000-0000-000000000004', '10000000-0000-0000-0000-000000000003', 'PTO4G89', 'Hyundai',
        'HB20 Comfort', 2021, 42600, TRUE),
       ('20000000-0000-0000-0000-000000000005', '10000000-0000-0000-0000-000000000004', 'TLG5H10', 'Fiat',
        'Fiorino Endurance', 2020, 112300, TRUE),
       ('20000000-0000-0000-0000-000000000006', '10000000-0000-0000-0000-000000000004', 'TLG6J32', 'Renault',
        'Master Furgao', 2018, 156900, TRUE),
       ('20000000-0000-0000-0000-000000000007', '10000000-0000-0000-0000-000000000004', 'TLG7K54', 'Volkswagen',
        'Delivery Express', 2021, 89000, TRUE),
       ('20000000-0000-0000-0000-000000000008', '10000000-0000-0000-0000-000000000005', 'JPR8L76', 'Chevrolet',
        'Onix Premier', 2023, 18400, TRUE) ON CONFLICT (id) DO NOTHING;

INSERT INTO workshop_services (id, name, description, base_price, estimated_time_in_minutes, active)
VALUES ('30000000-0000-0000-0000-000000000001', 'Diagnostico eletronico',
        'Leitura de scanner, inspeção de falhas e relatorio tecnico para o cliente.', 180.00, 60, TRUE),
       ('30000000-0000-0000-0000-000000000002', 'Troca de oleo e filtros',
        'Substituição de oleo do motor, filtro de oleo, filtro de ar e checklist visual.', 220.00, 75, TRUE),
       ('30000000-0000-0000-0000-000000000003', 'Revisao preventiva completa',
        'Revisao de freios, suspensao, fluidos, correias, pneus, luzes e sistema eletrico.', 520.00, 240, TRUE),
       ('30000000-0000-0000-0000-000000000004', 'Servico de freios',
        'Inspeção e substituição de pastilhas, discos e sangria quando necessario.', 360.00, 150, TRUE),
       ('30000000-0000-0000-0000-000000000005', 'Alinhamento e balanceamento',
        'Alinhamento computadorizado, balanceamento das rodas e calibragem.', 190.00, 90, TRUE),
       ('30000000-0000-0000-0000-000000000006', 'Higienização do ar-condicionado',
        'Limpeza do sistema, troca de filtro de cabine e eliminação de odores.', 160.00, 60, TRUE),
       ('30000000-0000-0000-0000-000000000007', 'Troca de correia dentada',
        'Substituição do kit de correia dentada e verificação do sincronismo do motor.', 680.00, 300, TRUE),
       ('30000000-0000-0000-0000-000000000008', 'Diagnostico de suspensao',
        'Avaliação de buchas, amortecedores, bandejas, pivots e terminais.', 140.00, 60,
        TRUE) ON CONFLICT (id) DO NOTHING;

INSERT INTO parts (id, name, sku, category, subcategory, brand, unit_price, stock_quantity, minimum_stock, active)
VALUES ('40000000-0000-0000-0000-000000000001', 'Oleo sintetico 5W30 1L', 'OLE-5W30-001', 'Lubrificantes', 'Motor',
        'Mobil', 52.90, 24, 12, TRUE),
       ('40000000-0000-0000-0000-000000000002', 'Filtro de oleo Honda/Toyota', 'FIL-OLE-HT01', 'Filtros', 'Oleo',
        'Tecfil', 38.50, 8, 10, TRUE),
       ('40000000-0000-0000-0000-000000000003', 'Filtro de ar compacto', 'FIL-AR-CMP01', 'Filtros', 'Ar', 'Mann', 46.00,
        18, 8, TRUE),
       ('40000000-0000-0000-0000-000000000004', 'Filtro de cabine universal', 'FIL-CAB-UNI01', 'Filtros', 'Cabine',
        'Wega', 42.00, 5, 10, TRUE),
       ('40000000-0000-0000-0000-000000000005', 'Pastilha de freio dianteira', 'FR-PAS-DIA01', 'Freios', 'Pastilhas',
        'Fras-le', 185.00, 6, 8, TRUE),
       ('40000000-0000-0000-0000-000000000006', 'Disco de freio ventilado', 'FR-DIS-VEN01', 'Freios', 'Discos',
        'Fremax', 240.00, 4, 6, TRUE),
       ('40000000-0000-0000-0000-000000000007', 'Fluido de freio DOT4 500ml', 'FLU-DOT4-500', 'Fluidos', 'Freio',
        'Bosch', 39.90, 16, 10, TRUE),
       ('40000000-0000-0000-0000-000000000008', 'Amortecedor dianteiro', 'SUS-AMO-DIA01', 'Suspensao', 'Amortecedores',
        'Monroe', 390.00, 3, 4, TRUE),
       ('40000000-0000-0000-0000-000000000009', 'Bucha bandeja dianteira', 'SUS-BUC-BAN01', 'Suspensao', 'Buchas',
        'Axios', 72.00, 12, 8, TRUE),
       ('40000000-0000-0000-0000-000000000010', 'Bateria 60Ah', 'ELE-BAT-60AH', 'Eletrica', 'Bateria', 'Moura', 520.00,
        2, 3, TRUE),
       ('40000000-0000-0000-0000-000000000011', 'Lampada H7', 'ELE-LMP-H7', 'Eletrica', 'Iluminação', 'Osram', 49.00,
        20, 8, TRUE),
       ('40000000-0000-0000-0000-000000000012', 'Vela de ignição iridium', 'MOT-VEL-IRI01', 'Motor', 'Ignição', 'NGK',
        68.00, 22, 12, TRUE),
       ('40000000-0000-0000-0000-000000000013', 'Kit correia dentada', 'MOT-COR-KIT01', 'Motor', 'Correias', 'Gates',
        410.00, 1, 3, TRUE),
       ('40000000-0000-0000-0000-000000000014', 'Aditivo radiador rosa 1L', 'ARR-ADT-ROS01', 'Arrefecimento', 'Aditivo',
        'Petronas', 32.00, 14, 10, TRUE),
       ('40000000-0000-0000-0000-000000000015', 'Palheta limpador par', 'ACE-PAL-PAR01', 'Acessorios', 'Palhetas',
        'Bosch', 89.90, 9, 6, TRUE),
       ('40000000-0000-0000-0000-000000000016', 'Pneu 205/55 R16', 'ROD-PNE-20555R16', 'Rodas', 'Pneus', 'Pirelli',
        520.00, 7, 4, TRUE),
       ('40000000-0000-0000-0000-000000000017', 'Sensor ABS dianteiro', 'ELE-SEN-ABS01', 'Eletrica', 'Sensores',
        'Magneti Marelli', 210.00, 2, 4, TRUE),
       ('40000000-0000-0000-0000-000000000018', 'Coxim do motor', 'MOT-COX-MOT01', 'Motor', 'Coxins', 'Mobensani',
        160.00, 6, 4, TRUE),
       ('40000000-0000-0000-0000-000000000019', 'Kit embreagem utilitario', 'TRA-EMB-KIT01', 'Transmissao', 'Embreagem',
        'Luk', 780.00, 1, 2, TRUE),
       ('40000000-0000-0000-0000-000000000020', 'Fluido de cambio ATF 1L', 'TRA-FLU-ATF01', 'Transmissao', 'Fluidos',
        'Motul', 74.00, 10, 8, TRUE) ON CONFLICT (id) DO NOTHING;

INSERT INTO service_orders (id, customer_id, vehicle_id, status, diagnostic_notes, total_amount,
                            created_at, budget_generated_at, approved_at, started_at, finished_at, delivered_at)
VALUES ('50000000-0000-0000-0000-000000000001', '10000000-0000-0000-0000-000000000001',
        '20000000-0000-0000-0000-000000000001', 'IN_PROGRESS',
        'Cliente relata vibração ao frear acima de 80 km/h. Nota: revisar discos dianteiros, pastilhas e fluido.',
        807.00, CURRENT_TIMESTAMP - INTERVAL '5 days', CURRENT_TIMESTAMP - INTERVAL '4 days',
        CURRENT_TIMESTAMP - INTERVAL '4 days', CURRENT_TIMESTAMP - INTERVAL '3 days', NULL, NULL),
       ('50000000-0000-0000-0000-000000000002', '10000000-0000-0000-0000-000000000001',
        '20000000-0000-0000-0000-000000000002', 'WAITING_APPROVAL',
        'Revisao de 30 mil km. Nota: cliente pediu priorizar troca de oleo, filtros e higienização do ar.', 656.50,
        CURRENT_TIMESTAMP - INTERVAL '2 days', CURRENT_TIMESTAMP - INTERVAL '1 day', NULL, NULL, NULL, NULL),
       ('50000000-0000-0000-0000-000000000003', '10000000-0000-0000-0000-000000000002',
        '20000000-0000-0000-0000-000000000003', 'FINISHED',
        'Luz de injeção acesa e consumo elevado. Nota: scanner apontou falha intermitente em vela de ignição.', 632.00,
        CURRENT_TIMESTAMP - INTERVAL '8 days', CURRENT_TIMESTAMP - INTERVAL '7 days',
        CURRENT_TIMESTAMP - INTERVAL '7 days', CURRENT_TIMESTAMP - INTERVAL '6 days',
        CURRENT_TIMESTAMP - INTERVAL '5 days', NULL),
       ('50000000-0000-0000-0000-000000000004', '10000000-0000-0000-0000-000000000003',
        '20000000-0000-0000-0000-000000000004', 'DELIVERED',
        'Barulho na suspensao dianteira em piso irregular. Nota: substituida bucha de bandeja e feito alinhamento.',
        334.00, CURRENT_TIMESTAMP - INTERVAL '14 days', CURRENT_TIMESTAMP - INTERVAL '13 days',
        CURRENT_TIMESTAMP - INTERVAL '13 days', CURRENT_TIMESTAMP - INTERVAL '12 days',
        CURRENT_TIMESTAMP - INTERVAL '11 days', CURRENT_TIMESTAMP - INTERVAL '10 days'),
       ('50000000-0000-0000-0000-000000000005', '10000000-0000-0000-0000-000000000004',
        '20000000-0000-0000-0000-000000000005', 'RECEIVED',
        'Veículo de frota chegou para revisao preventiva. Nota: motorista reportou dificuldade de partida pela manha.',
        0.00, CURRENT_TIMESTAMP - INTERVAL '1 day', NULL, NULL, NULL, NULL, NULL),
       ('50000000-0000-0000-0000-000000000006', '10000000-0000-0000-0000-000000000004',
        '20000000-0000-0000-0000-000000000006', 'IN_DIAGNOSIS',
        'Utilitario com ruido ao acionar embreagem. Nota: avaliar kit de embreagem antes de liberar orçamento.', 180.00,
        CURRENT_TIMESTAMP - INTERVAL '3 days', NULL, NULL, CURRENT_TIMESTAMP - INTERVAL '2 days', NULL, NULL),
       ('50000000-0000-0000-0000-000000000007', '10000000-0000-0000-0000-000000000005',
        '20000000-0000-0000-0000-000000000008', 'WAITING_APPROVAL',
        'Primeira revisao pos-compra. Nota: cliente solicitou checagem completa antes de viagem.', 1056.90,
        CURRENT_TIMESTAMP - INTERVAL '4 days', CURRENT_TIMESTAMP - INTERVAL '3 days', NULL, NULL, NULL,
        NULL) ON CONFLICT (id) DO NOTHING;

INSERT INTO service_order_services (id, service_order_id, service_id, name, quantity, unit_price, total_price)
VALUES ('60000000-0000-0000-0000-000000000001', '50000000-0000-0000-0000-000000000001',
        '30000000-0000-0000-0000-000000000004', 'Servico de freios', 1, 360.00, 360.00),
       ('60000000-0000-0000-0000-000000000002', '50000000-0000-0000-0000-000000000002',
        '30000000-0000-0000-0000-000000000002', 'Troca de oleo e filtros', 1, 220.00, 220.00),
       ('60000000-0000-0000-0000-000000000003', '50000000-0000-0000-0000-000000000002',
        '30000000-0000-0000-0000-000000000006', 'Higienização do ar-condicionado', 1, 160.00, 160.00),
       ('60000000-0000-0000-0000-000000000004', '50000000-0000-0000-0000-000000000003',
        '30000000-0000-0000-0000-000000000001', 'Diagnostico eletronico', 1, 180.00, 180.00),
       ('60000000-0000-0000-0000-000000000005', '50000000-0000-0000-0000-000000000004',
        '30000000-0000-0000-0000-000000000005', 'Alinhamento e balanceamento', 1, 190.00, 190.00),
       ('60000000-0000-0000-0000-000000000006', '50000000-0000-0000-0000-000000000006',
        '30000000-0000-0000-0000-000000000001', 'Diagnostico eletronico', 1, 180.00, 180.00),
       ('60000000-0000-0000-0000-000000000007', '50000000-0000-0000-0000-000000000007',
        '30000000-0000-0000-0000-000000000003', 'Revisao preventiva completa', 1, 520.00,
        520.00) ON CONFLICT (id) DO NOTHING;

INSERT INTO service_order_parts (id, service_order_id, part_id, name, sku, quantity, unit_price, total_price)
VALUES ('70000000-0000-0000-0000-000000000001', '50000000-0000-0000-0000-000000000001',
        '40000000-0000-0000-0000-000000000005', 'Pastilha de freio dianteira', 'FR-PAS-DIA01', 1, 185.00, 185.00),
       ('70000000-0000-0000-0000-000000000002', '50000000-0000-0000-0000-000000000001',
        '40000000-0000-0000-0000-000000000006', 'Disco de freio ventilado', 'FR-DIS-VEN01', 1, 240.00, 240.00),
       ('70000000-0000-0000-0000-000000000003', '50000000-0000-0000-0000-000000000001',
        '40000000-0000-0000-0000-000000000007', 'Fluido de freio DOT4 500ml', 'FLU-DOT4-500', 1, 39.90, 39.90),
       ('70000000-0000-0000-0000-000000000004', '50000000-0000-0000-0000-000000000002',
        '40000000-0000-0000-0000-000000000001', 'Oleo sintetico 5W30 1L', 'OLE-5W30-001', 4, 52.90, 211.60),
       ('70000000-0000-0000-0000-000000000005', '50000000-0000-0000-0000-000000000002',
        '40000000-0000-0000-0000-000000000002', 'Filtro de oleo Honda/Toyota', 'FIL-OLE-HT01', 1, 38.50, 38.50),
       ('70000000-0000-0000-0000-000000000006', '50000000-0000-0000-0000-000000000002',
        '40000000-0000-0000-0000-000000000004', 'Filtro de cabine universal', 'FIL-CAB-UNI01', 1, 42.00, 42.00),
       ('70000000-0000-0000-0000-000000000007', '50000000-0000-0000-0000-000000000003',
        '40000000-0000-0000-0000-000000000012', 'Vela de ignição iridium', 'MOT-VEL-IRI01', 4, 68.00, 272.00),
       ('70000000-0000-0000-0000-000000000008', '50000000-0000-0000-0000-000000000004',
        '40000000-0000-0000-0000-000000000009', 'Bucha bandeja dianteira', 'SUS-BUC-BAN01', 2, 72.00, 144.00),
       ('70000000-0000-0000-0000-000000000009', '50000000-0000-0000-0000-000000000007',
        '40000000-0000-0000-0000-000000000001', 'Oleo sintetico 5W30 1L', 'OLE-5W30-001', 5, 52.90, 264.50),
       ('70000000-0000-0000-0000-000000000010', '50000000-0000-0000-0000-000000000007',
        '40000000-0000-0000-0000-000000000003', 'Filtro de ar compacto', 'FIL-AR-CMP01', 1, 46.00, 46.00),
       ('70000000-0000-0000-0000-000000000011', '50000000-0000-0000-0000-000000000007',
        '40000000-0000-0000-0000-000000000015', 'Palheta limpador par', 'ACE-PAL-PAR01', 1, 89.90,
        89.90) ON CONFLICT (id) DO NOTHING;

INSERT INTO users (id, username, password_hash, role, customer_id, active, created_at)
VALUES ('00000000-0000-0000-0000-000000000002', 'funcionario@autocarehub.com',
        '$2a$10$xAb5kI.uSxQkLo9n6tZTiuf8WbQcehwTGGk99zzc2QtY28sx9WFO.', 'EMPLOYEE', NULL, TRUE, CURRENT_TIMESTAMP),
       ('00000000-0000-0000-0000-000000000003', 'cliente@autocarehub.com',
        '$2a$10$xAb5kI.uSxQkLo9n6tZTiuf8WbQcehwTGGk99zzc2QtY28sx9WFO.', 'CUSTOMER',
        '10000000-0000-0000-0000-000000000001', TRUE, CURRENT_TIMESTAMP) ON CONFLICT (id) DO NOTHING;

INSERT INTO parts (id, name, sku, category, subcategory, brand, unit_price, stock_quantity, minimum_stock, active)
VALUES ('40000000-0000-0000-0000-000000000021', 'Filtro de combustivel flex', 'FIL-COM-FLX01', 'Filtros', 'Combustivel',
        'Tecfil', 58.00, 26, 8, TRUE),
       ('40000000-0000-0000-0000-000000000022', 'Correia poly-v', 'MOT-COR-POL01', 'Motor', 'Correias', 'Gates', 96.00,
        15, 5, TRUE),
       ('40000000-0000-0000-0000-000000000023', 'Tensor da correia poly-v', 'MOT-TEN-POL01', 'Motor', 'Tensores', 'SKF',
        185.00, 9, 4, TRUE),
       ('40000000-0000-0000-0000-000000000024', 'Terminal de direção', 'DIR-TER-DIR01', 'Direção', 'Terminais', 'Axios',
        88.00, 14, 6, TRUE),
       ('40000000-0000-0000-0000-000000000025', 'Pivo de suspensao', 'SUS-PIV-DIA01', 'Suspensao', 'Pivos', 'Nakata',
        112.00, 11, 5, TRUE),
       ('40000000-0000-0000-0000-000000000026', 'Cabo de vela', 'MOT-CAB-VEL01', 'Motor', 'Ignição', 'NGK', 145.00, 10,
        4, TRUE),
       ('40000000-0000-0000-0000-000000000027', 'Sensor de oxigenio', 'ELE-SEN-OXI01', 'Eletrica', 'Sensores', 'Bosch',
        260.00, 8, 3, TRUE),
       ('40000000-0000-0000-0000-000000000028', 'Cilindro mestre de freio', 'FR-CIL-MES01', 'Freios', 'Hidraulico',
        'TRW', 310.00, 7, 3, TRUE),
       ('40000000-0000-0000-0000-000000000029', 'Jogo de tapetes automotivos', 'ACE-TAP-JOG01', 'Acessorios',
        'Interior', 'Borcol', 120.00, 18, 6, TRUE),
       ('40000000-0000-0000-0000-000000000030', 'Limpador de para-brisa aditivo', 'ACE-LIM-PAR01', 'Acessorios',
        'Limpeza', 'Wurth', 22.00, 35, 12, TRUE),
       ('40000000-0000-0000-0000-000000000031', 'Fluido para radiador concentrado', 'ARR-FLU-CON01', 'Arrefecimento',
        'Fluidos', 'Radiex', 48.00, 19, 8, TRUE),
       ('40000000-0000-0000-0000-000000000032', 'Graxa automotiva 500g', 'LUB-GRA-500G', 'Lubrificantes', 'Graxa',
        'Ipiranga', 36.00, 22, 8, TRUE) ON CONFLICT (id) DO NOTHING;

UPDATE customers
SET document_value = '45678912364'
WHERE id = '10000000-0000-0000-0000-000000000003';

UPDATE customers
SET document_value = '11222333000181'
WHERE id = '10000000-0000-0000-0000-000000000004';

UPDATE customers
SET document_value = '32165498791'
WHERE id = '10000000-0000-0000-0000-000000000005';

CREATE TABLE demo_leads
(
    id           UUID PRIMARY KEY,
    contact_name VARCHAR(120) NOT NULL,
    company_name VARCHAR(120) NOT NULL,
    email        VARCHAR(160) NOT NULL,
    phone        VARCHAR(30)  NOT NULL,
    cnpj         VARCHAR(40)  NOT NULL,
    created_at   TIMESTAMP    NOT NULL
);

CREATE INDEX idx_demo_leads_created_at ON demo_leads (created_at DESC);
CREATE INDEX idx_demo_leads_email ON demo_leads (email);

ALTER TABLE demo_leads
    ADD COLUMN demo_profile VARCHAR(40) NOT NULL DEFAULT 'workshop';

CREATE INDEX idx_demo_leads_demo_profile ON demo_leads (demo_profile);

INSERT INTO users (id, username, password_hash, role, customer_id, active, created_at)
VALUES ('00000000-0000-0000-0000-000000000010', 'master@autocarehub.com',
        '$2a$10$xAb5kI.uSxQkLo9n6tZTiuf8WbQcehwTGGk99zzc2QtY28sx9WFO.', 'ADMIN', NULL, TRUE, CURRENT_TIMESTAMP),
       ('00000000-0000-0000-0000-000000000011', 'oficina.admin@autocarehub.com',
        '$2a$10$xAb5kI.uSxQkLo9n6tZTiuf8WbQcehwTGGk99zzc2QtY28sx9WFO.', 'ADMIN', NULL, TRUE, CURRENT_TIMESTAMP),
       ('00000000-0000-0000-0000-000000000012', 'loja.admin@autocarehub.com',
        '$2a$10$xAb5kI.uSxQkLo9n6tZTiuf8WbQcehwTGGk99zzc2QtY28sx9WFO.', 'ADMIN', NULL, TRUE, CURRENT_TIMESTAMP),
       ('00000000-0000-0000-0000-000000000013', 'oficina.funcionario@autocarehub.com',
        '$2a$10$xAb5kI.uSxQkLo9n6tZTiuf8WbQcehwTGGk99zzc2QtY28sx9WFO.', 'EMPLOYEE', NULL, TRUE, CURRENT_TIMESTAMP),
       ('00000000-0000-0000-0000-000000000014', 'loja.funcionario@autocarehub.com',
        '$2a$10$xAb5kI.uSxQkLo9n6tZTiuf8WbQcehwTGGk99zzc2QtY28sx9WFO.', 'EMPLOYEE', NULL, TRUE,
        CURRENT_TIMESTAMP) ON CONFLICT (id) DO NOTHING;

INSERT INTO customers (id, name, document_type, document_value, phone, email,
                       address_street, address_number, address_complement, address_neighborhood,
                       address_city, address_state, address_zip_code, active, created_at)
VALUES ('10000000-0000-0000-0000-000000000006', 'Camila Rocha', 'CPF', '27463918050', '11944443333',
        'camila.rocha@email.com', 'Rua Guaicurus', '188', NULL, 'Lapa', 'Sao Paulo', 'SP', '05033001', TRUE,
        CURRENT_TIMESTAMP - INTERVAL '46 days'),
       ('10000000-0000-0000-0000-000000000007', 'Bruno Martins', 'CPF', '63891247087', '11933332222',
        'bruno.martins@email.com', 'Rua Turiassu', '640', 'Bloco B', 'Perdizes', 'Sao Paulo', 'SP', '05005001', TRUE,
        CURRENT_TIMESTAMP - INTERVAL '39 days'),
       ('10000000-0000-0000-0000-000000000008', 'Renata Figueiredo', 'CPF', '84521963005', '11922221111',
        'renata.figueiredo@email.com', 'Avenida Jabaquara', '1780', NULL, 'Mirandopolis', 'Sao Paulo', 'SP', '04046003',
        TRUE, CURRENT_TIMESTAMP - INTERVAL '32 days'),
       ('10000000-0000-0000-0000-000000000009', 'Rafael Nogueira', 'CPF', '50938416090', '11911110000',
        'rafael.nogueira@email.com', 'Rua das Palmeiras', '75', 'Casa', 'Santa Cecilia', 'Sao Paulo', 'SP', '01226010',
        TRUE, CURRENT_TIMESTAMP - INTERVAL '27 days'),
       ('10000000-0000-0000-0000-000000000010', 'Atlas Entregas Rapidas Ltda', 'CNPJ', '45891234000104', '1130304040',
        'manutenção@atlasentregas.com', 'Avenida dos Bandeirantes', '3100', 'Patio 4', 'Brooklin', 'Sao Paulo', 'SP',
        '04553000', TRUE, CURRENT_TIMESTAMP - INTERVAL '24 days'),
       ('10000000-0000-0000-0000-000000000011', 'Cooperativa Rota Azul', 'CNPJ', '62987451000111', '1140405050',
        'frota@rotaazul.com', 'Rodovia Anhanguera', '1200', 'Box 18', 'Pirituba', 'Sao Paulo', 'SP', '05112000', TRUE,
        CURRENT_TIMESTAMP - INTERVAL '19 days'),
       ('10000000-0000-0000-0000-000000000012', 'Helena Prado', 'CPF', '71352694034', '11900001111',
        'helena.prado@email.com', 'Rua Apucarana', '305', NULL, 'Tatuape', 'Sao Paulo', 'SP', '03311000', TRUE,
        CURRENT_TIMESTAMP - INTERVAL '12 days') ON CONFLICT (id) DO NOTHING;

INSERT INTO vehicles (id, customer_id, plate, brand, model, manufacture_year, mileage, active)
VALUES ('20000000-0000-0000-0000-000000000009', '10000000-0000-0000-0000-000000000006', 'CRH9M21', 'Yamaha',
        'MT-03 ABS', 2022, 16400, TRUE),
       ('20000000-0000-0000-0000-000000000010', '10000000-0000-0000-0000-000000000007', 'BRM0N34', 'Ford', 'Ranger XLS',
        2021, 68200, TRUE),
       ('20000000-0000-0000-0000-000000000011', '10000000-0000-0000-0000-000000000008', 'RFG1P56', 'Volkswagen',
        'Nivus Highline', 2023, 14200, TRUE),
       ('20000000-0000-0000-0000-000000000012', '10000000-0000-0000-0000-000000000009', 'RNG2Q78', 'BMW', 'G 310 GS',
        2020, 28100, TRUE),
       ('20000000-0000-0000-0000-000000000013', '10000000-0000-0000-0000-000000000010', 'ATL3R90', 'Mercedes-Benz',
        'Sprinter 416', 2019, 178450, TRUE),
       ('20000000-0000-0000-0000-000000000014', '10000000-0000-0000-0000-000000000010', 'ATL4S12', 'Iveco',
        'Daily 35-150', 2021, 126780, TRUE),
       ('20000000-0000-0000-0000-000000000015', '10000000-0000-0000-0000-000000000011', 'RAZ5T34', 'Volvo', 'VM 270',
        2018, 342100, TRUE),
       ('20000000-0000-0000-0000-000000000016', '10000000-0000-0000-0000-000000000011', 'RAZ6U56', 'Volkswagen',
        'Constellation 24.280', 2020, 288900, TRUE),
       ('20000000-0000-0000-0000-000000000017', '10000000-0000-0000-0000-000000000012', 'HPR7V78', 'Fiat',
        'Pulse Audace', 2022, 36600, TRUE),
       ('20000000-0000-0000-0000-000000000018', '10000000-0000-0000-0000-000000000001', 'MCA9W90', 'Honda', 'PCX 160',
        2024, 4800, TRUE) ON CONFLICT (id) DO NOTHING;

INSERT INTO workshop_services (id, name, description, base_price, estimated_time_in_minutes, active)
VALUES ('30000000-0000-0000-0000-000000000009', 'Revisao de motocicleta',
        'Checklist de freios, relação, pneus, fluidos e sistema eletrico para motocicletas.', 280.00, 150, TRUE),
       ('30000000-0000-0000-0000-000000000010', 'Manutenção de frota leve',
        'Inspeção preventiva para vans, utilitarios e veículos comerciais.', 640.00, 300, TRUE),
       ('30000000-0000-0000-0000-000000000011', 'Diagnóstico diesel',
        'Scanner, avaliação de alimentação, sensores e sistema de emissao para veículos diesel.', 420.00, 180, TRUE),
       ('30000000-0000-0000-0000-000000000012', 'Troca de pneus e geometria',
        'Substituição, balanceamento, alinhamento e calibragem com relatorio.', 340.00, 120, TRUE),
       ('30000000-0000-0000-0000-000000000013', 'Revisao eletrica completa',
        'Teste de bateria, alternador, partida, chicotes e pontos de consumo.', 360.00, 180, TRUE),
       ('30000000-0000-0000-0000-000000000014', 'Manutenção de arrefecimento',
        'Limpeza, teste de estanqueidade, troca de aditivo e avaliação de mangueiras.', 260.00, 120,
        TRUE) ON CONFLICT (id) DO NOTHING;

INSERT INTO parts (id, name, sku, category, subcategory, brand, unit_price, stock_quantity, minimum_stock, active)
VALUES ('40000000-0000-0000-0000-000000000033', 'Pastilha traseira motocicleta', 'MOT-FR-PAS-TR01', 'Freios',
        'Motocicleta', 'Cobreq', 96.00, 12, 5, TRUE),
       ('40000000-0000-0000-0000-000000000034', 'Kit relação 520H', 'MOT-REL-520H', 'Transmissao', 'Motocicleta', 'DID',
        310.00, 5, 3, TRUE),
       ('40000000-0000-0000-0000-000000000035', 'Oleo diesel 15W40 1L', 'OLE-15W40-DIE', 'Lubrificantes', 'Diesel',
        'Shell', 42.00, 36, 16, TRUE),
       ('40000000-0000-0000-0000-000000000036', 'Filtro separador de agua', 'FIL-SEP-DIE01', 'Filtros', 'Diesel',
        'Mann', 148.00, 3, 6, TRUE),
       ('40000000-0000-0000-0000-000000000037', 'Pastilha utilitario pesada', 'FR-PAS-UTP01', 'Freios', 'Utilitario',
        'Fras-le', 260.00, 2, 5, TRUE),
       ('40000000-0000-0000-0000-000000000038', 'Sensor MAP', 'ELE-SEN-MAP01', 'Eletrica', 'Sensores', 'Bosch', 190.00,
        9, 4, TRUE),
       ('40000000-0000-0000-0000-000000000039', 'Bateria 95Ah', 'ELE-BAT-95AH', 'Eletrica', 'Bateria', 'Moura', 890.00,
        1, 3, TRUE),
       ('40000000-0000-0000-0000-000000000040', 'Pneu 225/75 R16 carga', 'ROD-PNE-22575R16', 'Rodas', 'Pneus',
        'Goodyear', 780.00, 6, 4, TRUE),
       ('40000000-0000-0000-0000-000000000041', 'Aditivo longa duração 5L', 'ARR-ADT-5L', 'Arrefecimento', 'Aditivo',
        'Radiex', 126.00, 4, 5, TRUE),
       ('40000000-0000-0000-0000-000000000042', 'Correia de acessorios diesel', 'MOT-COR-DIE01', 'Motor', 'Correias',
        'Gates', 220.00, 7, 3, TRUE) ON CONFLICT (id) DO NOTHING;

INSERT INTO service_orders (id, customer_id, vehicle_id, status, diagnostic_notes, total_amount,
                            created_at, budget_generated_at, approved_at, started_at, finished_at, delivered_at)
VALUES ('50000000-0000-0000-0000-000000000008', '10000000-0000-0000-0000-000000000001',
        '20000000-0000-0000-0000-000000000018', 'RECEIVED',
        'Motocicleta recebida para primeira revisao. Cliente pediu retorno por WhatsApp antes de qualquer troca adicional.',
        0.00, CURRENT_TIMESTAMP - INTERVAL '8 hours', NULL, NULL, NULL, NULL, NULL),
       ('50000000-0000-0000-0000-000000000009', '10000000-0000-0000-0000-000000000001',
        '20000000-0000-0000-0000-000000000001', 'FINISHED',
        'Servico concluido apos revisao de freios. Aguardando retirada do cliente no fim do dia.', 679.90,
        CURRENT_TIMESTAMP - INTERVAL '12 days', CURRENT_TIMESTAMP - INTERVAL '11 days',
        CURRENT_TIMESTAMP - INTERVAL '11 days', CURRENT_TIMESTAMP - INTERVAL '10 days',
        CURRENT_TIMESTAMP - INTERVAL '9 days', NULL),
       ('50000000-0000-0000-0000-000000000010', '10000000-0000-0000-0000-000000000006',
        '20000000-0000-0000-0000-000000000009', 'IN_PROGRESS',
        'Revisao de motocicleta em execução. Relação apresenta folga e pastilha traseira esta no limite.', 686.00,
        CURRENT_TIMESTAMP - INTERVAL '4 days', CURRENT_TIMESTAMP - INTERVAL '3 days',
        CURRENT_TIMESTAMP - INTERVAL '3 days', CURRENT_TIMESTAMP - INTERVAL '2 days', NULL, NULL),
       ('50000000-0000-0000-0000-000000000011', '10000000-0000-0000-0000-000000000007',
        '20000000-0000-0000-0000-000000000010', 'WAITING_APPROVAL',
        'Picape com ruído em arrancadas. Orçamento enviado com embreagem, coxim e fluido.', 1574.00,
        CURRENT_TIMESTAMP - INTERVAL '5 days', CURRENT_TIMESTAMP - INTERVAL '4 days', NULL, NULL, NULL, NULL),
       ('50000000-0000-0000-0000-000000000012', '10000000-0000-0000-0000-000000000008',
        '20000000-0000-0000-0000-000000000011', 'IN_DIAGNOSIS',
        'Cliente relata falha intermitente ao ligar pela manha. Equipe iniciou diagnostico eletrico.', 360.00,
        CURRENT_TIMESTAMP - INTERVAL '1 day', NULL, NULL, CURRENT_TIMESTAMP - INTERVAL '20 hours', NULL, NULL),
       ('50000000-0000-0000-0000-000000000013', '10000000-0000-0000-0000-000000000009',
        '20000000-0000-0000-0000-000000000012', 'DELIVERED',
        'Revisao completa entregue com troca de pneus, fluido e ajuste de freios.', 1218.00,
        CURRENT_TIMESTAMP - INTERVAL '25 days', CURRENT_TIMESTAMP - INTERVAL '24 days',
        CURRENT_TIMESTAMP - INTERVAL '23 days', CURRENT_TIMESTAMP - INTERVAL '22 days',
        CURRENT_TIMESTAMP - INTERVAL '21 days', CURRENT_TIMESTAMP - INTERVAL '20 days'),
       ('50000000-0000-0000-0000-000000000014', '10000000-0000-0000-0000-000000000010',
        '20000000-0000-0000-0000-000000000013', 'IN_PROGRESS',
        'Van de entrega em manutenção preventiva. Prioridade alta para retorno a operação amanha.', 2324.00,
        CURRENT_TIMESTAMP - INTERVAL '6 days', CURRENT_TIMESTAMP - INTERVAL '5 days',
        CURRENT_TIMESTAMP - INTERVAL '5 days', CURRENT_TIMESTAMP - INTERVAL '4 days', NULL, NULL),
       ('50000000-0000-0000-0000-000000000015', '10000000-0000-0000-0000-000000000010',
        '20000000-0000-0000-0000-000000000014', 'RECEIVED',
        'Utilitario chegou sem agendamento por alerta de temperatura. Aguardando triagem.', 0.00,
        CURRENT_TIMESTAMP - INTERVAL '3 hours', NULL, NULL, NULL, NULL, NULL),
       ('50000000-0000-0000-0000-000000000016', '10000000-0000-0000-0000-000000000011',
        '20000000-0000-0000-0000-000000000015', 'WAITING_APPROVAL',
        'Caminhao com perda de potencia em subida. Orçamento de diagnostico diesel e filtros enviado ao gestor da frota.',
        1030.00, CURRENT_TIMESTAMP - INTERVAL '7 days', CURRENT_TIMESTAMP - INTERVAL '6 days', NULL, NULL, NULL, NULL),
       ('50000000-0000-0000-0000-000000000017', '10000000-0000-0000-0000-000000000011',
        '20000000-0000-0000-0000-000000000016', 'FINISHED',
        'Revisao de arrefecimento finalizada. Aguardando conferência documental da frota para retirada.', 586.00,
        CURRENT_TIMESTAMP - INTERVAL '10 days', CURRENT_TIMESTAMP - INTERVAL '9 days',
        CURRENT_TIMESTAMP - INTERVAL '9 days', CURRENT_TIMESTAMP - INTERVAL '8 days',
        CURRENT_TIMESTAMP - INTERVAL '7 days', NULL),
       ('50000000-0000-0000-0000-000000000018', '10000000-0000-0000-0000-000000000012',
        '20000000-0000-0000-0000-000000000017', 'IN_DIAGNOSIS',
        'Veículo com barulho na dianteira ao esterçar. Diagnostico de suspensao em andamento.', 140.00,
        CURRENT_TIMESTAMP - INTERVAL '2 days', NULL, NULL, CURRENT_TIMESTAMP - INTERVAL '1 day', NULL, NULL),
       ('50000000-0000-0000-0000-000000000019', '10000000-0000-0000-0000-000000000004',
        '20000000-0000-0000-0000-000000000007', 'DELIVERED',
        'Frota: revisao preventiva e troca de filtros entregues dentro do SLA combinado.', 892.00,
        CURRENT_TIMESTAMP - INTERVAL '35 days', CURRENT_TIMESTAMP - INTERVAL '34 days',
        CURRENT_TIMESTAMP - INTERVAL '34 days', CURRENT_TIMESTAMP - INTERVAL '33 days',
        CURRENT_TIMESTAMP - INTERVAL '32 days', CURRENT_TIMESTAMP - INTERVAL '31 days') ON CONFLICT (id) DO NOTHING;

INSERT INTO service_order_services (id, service_order_id, service_id, name, quantity, unit_price, total_price)
VALUES ('60000000-0000-0000-0000-000000000008', '50000000-0000-0000-0000-000000000009',
        '30000000-0000-0000-0000-000000000004', 'Servico de freios', 1, 360.00, 360.00),
       ('60000000-0000-0000-0000-000000000009', '50000000-0000-0000-0000-000000000010',
        '30000000-0000-0000-0000-000000000009', 'Revisao de motocicleta', 1, 280.00, 280.00),
       ('60000000-0000-0000-0000-000000000010', '50000000-0000-0000-0000-000000000011',
        '30000000-0000-0000-0000-000000000003', 'Revisao preventiva completa', 1, 520.00, 520.00),
       ('60000000-0000-0000-0000-000000000011', '50000000-0000-0000-0000-000000000012',
        '30000000-0000-0000-0000-000000000013', 'Revisao eletrica completa', 1, 360.00, 360.00),
       ('60000000-0000-0000-0000-000000000012', '50000000-0000-0000-0000-000000000013',
        '30000000-0000-0000-0000-000000000012', 'Troca de pneus e geometria', 1, 340.00, 340.00),
       ('60000000-0000-0000-0000-000000000013', '50000000-0000-0000-0000-000000000014',
        '30000000-0000-0000-0000-000000000010', 'Manutenção de frota leve', 1, 640.00, 640.00),
       ('60000000-0000-0000-0000-000000000014', '50000000-0000-0000-0000-000000000016',
        '30000000-0000-0000-0000-000000000011', 'Diagnostico diesel', 1, 420.00, 420.00),
       ('60000000-0000-0000-0000-000000000015', '50000000-0000-0000-0000-000000000017',
        '30000000-0000-0000-0000-000000000014', 'Manutenção de arrefecimento', 1, 260.00, 260.00),
       ('60000000-0000-0000-0000-000000000016', '50000000-0000-0000-0000-000000000018',
        '30000000-0000-0000-0000-000000000008', 'Diagnostico de suspensao', 1, 140.00, 140.00),
       ('60000000-0000-0000-0000-000000000017', '50000000-0000-0000-0000-000000000019',
        '30000000-0000-0000-0000-000000000010', 'Manutenção de frota leve', 1, 640.00,
        640.00) ON CONFLICT (id) DO NOTHING;

INSERT INTO service_order_parts (id, service_order_id, part_id, name, sku, quantity, unit_price, total_price)
VALUES ('70000000-0000-0000-0000-000000000012', '50000000-0000-0000-0000-000000000009',
        '40000000-0000-0000-0000-000000000005', 'Pastilha de freio dianteira', 'FR-PAS-DIA01', 1, 185.00, 185.00),
       ('70000000-0000-0000-0000-000000000013', '50000000-0000-0000-0000-000000000010',
        '40000000-0000-0000-0000-000000000033', 'Pastilha traseira motocicleta', 'MOT-FR-PAS-TR01', 1, 96.00, 96.00),
       ('70000000-0000-0000-0000-000000000014', '50000000-0000-0000-0000-000000000010',
        '40000000-0000-0000-0000-000000000034', 'Kit relação 520H', 'MOT-REL-520H', 1, 310.00, 310.00),
       ('70000000-0000-0000-0000-000000000015', '50000000-0000-0000-0000-000000000011',
        '40000000-0000-0000-0000-000000000019', 'Kit embreagem utilitario', 'TRA-EMB-KIT01', 1, 780.00, 780.00),
       ('70000000-0000-0000-0000-000000000016', '50000000-0000-0000-0000-000000000013',
        '40000000-0000-0000-0000-000000000033', 'Pastilha traseira motocicleta', 'MOT-FR-PAS-TR01', 1, 96.00, 96.00),
       ('70000000-0000-0000-0000-000000000017', '50000000-0000-0000-0000-000000000013',
        '40000000-0000-0000-0000-000000000034', 'Kit relação 520H', 'MOT-REL-520H', 1, 310.00, 310.00),
       ('70000000-0000-0000-0000-000000000018', '50000000-0000-0000-0000-000000000014',
        '40000000-0000-0000-0000-000000000035', 'Oleo diesel 15W40 1L', 'OLE-15W40-DIE', 8, 42.00, 336.00),
       ('70000000-0000-0000-0000-000000000019', '50000000-0000-0000-0000-000000000014',
        '40000000-0000-0000-0000-000000000040', 'Pneu 225/75 R16 carga', 'ROD-PNE-22575R16', 1, 780.00, 780.00),
       ('70000000-0000-0000-0000-000000000020', '50000000-0000-0000-0000-000000000016',
        '40000000-0000-0000-0000-000000000036', 'Filtro separador de agua', 'FIL-SEP-DIE01', 2, 148.00, 296.00),
       ('70000000-0000-0000-0000-000000000021', '50000000-0000-0000-0000-000000000017',
        '40000000-0000-0000-0000-000000000041', 'Aditivo longa duração 5L', 'ARR-ADT-5L', 1, 126.00, 126.00),
       ('70000000-0000-0000-0000-000000000022', '50000000-0000-0000-0000-000000000019',
        '40000000-0000-0000-0000-000000000035', 'Oleo diesel 15W40 1L', 'OLE-15W40-DIE', 6, 42.00,
        252.00) ON CONFLICT (id) DO NOTHING;

ALTER TABLE parts
    ADD COLUMN cost_price NUMERIC(15, 2) NOT NULL DEFAULT 0,
    ADD COLUMN reserved_quantity INTEGER NOT NULL DEFAULT 0,
    ADD COLUMN reservation_days INTEGER NOT NULL DEFAULT 3,
    ADD COLUMN reservation_expires_at TIMESTAMP;

UPDATE parts
SET cost_price = ROUND(unit_price * 0.62, 2)
WHERE cost_price = 0;

CREATE TABLE stock_movements
(
    id            UUID PRIMARY KEY,
    part_id       UUID        NOT NULL REFERENCES parts (id),
    movement_type VARCHAR(30) NOT NULL,
    quantity      INTEGER     NOT NULL,
    unit_cost     NUMERIC(15, 2),
    unit_price    NUMERIC(15, 2),
    reason        VARCHAR(500),
    created_at    TIMESTAMP   NOT NULL
);

CREATE INDEX idx_stock_movements_part_id ON stock_movements (part_id);
CREATE INDEX idx_stock_movements_type ON stock_movements (movement_type);

ALTER TABLE users
    ADD COLUMN full_name VARCHAR(160),
    ADD COLUMN profile_type VARCHAR(60);

UPDATE users
SET full_name    = CASE
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
ALTER
COLUMN profile_type SET NOT NULL;

CREATE TABLE user_preferences
(
    user_id    UUID        NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    pref_key   VARCHAR(80) NOT NULL,
    value_json TEXT        NOT NULL,
    updated_at TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (user_id, pref_key)
);

INSERT INTO user_preferences (user_id, pref_key, value_json)
SELECT id,
       'home',
       '{"widgets":["orders-progress","services-catalog","active-customers","vehicles-in-service","pending-budgets","waiting-contact","ready-pickup"],"showAlertsOnHome":false}'
FROM users ON CONFLICT DO NOTHING;

ALTER TABLE users
    ADD COLUMN employee_sub_role VARCHAR(60),
    ADD COLUMN permissions TEXT;

UPDATE users
SET employee_sub_role = CASE
                            WHEN username = 'oficina.funcionario@autocarehub.com' THEN 'MECHANIC'
                            WHEN username = 'loja.funcionario@autocarehub.com' THEN 'UNSPECIFIED'
                            ELSE ''
    END,
    permissions       = CASE
                            WHEN username = 'oficina.admin@autocarehub.com'
                                THEN 'VIEW_BILLING,CREATE_ORDER,EDIT_ORDER,MANAGE_STOCK,CREATE_BUDGET,EDIT_EMPLOYEES,VIEW_STATS'
                            WHEN username = 'oficina.funcionario@autocarehub.com'
                                THEN 'CREATE_ORDER,EDIT_ORDER,CREATE_BUDGET,VIEW_STATS'
                            WHEN username = 'loja.funcionario@autocarehub.com' THEN 'MANAGE_STOCK'
                            WHEN role = 'ADMIN'
                                THEN 'VIEW_BILLING,CREATE_ORDER,EDIT_ORDER,MANAGE_STOCK,CREATE_BUDGET,EDIT_EMPLOYEES,VIEW_STATS'
                            ELSE ''
        END;

ALTER TABLE users
    ALTER COLUMN employee_sub_role SET NOT NULL,
ALTER
COLUMN permissions SET NOT NULL;

UPDATE users
SET employee_sub_role = 'ATTENDANT',
    permissions       = 'MANAGE_STOCK,CREATE_BUDGET,VIEW_STATS'
WHERE username = 'loja.funcionario@autocarehub.com';

UPDATE users
SET permissions = 'VIEW_BILLING,MANAGE_STOCK,CREATE_BUDGET,EDIT_EMPLOYEES,VIEW_STATS'
WHERE username = 'loja.admin@autocarehub.com';

ALTER TABLE users
    ADD COLUMN company_name VARCHAR(160),
    ADD COLUMN company_type VARCHAR(60);

UPDATE users
SET company_name = CASE
                       WHEN profile_type = 'WORKSHOP_ADMIN' THEN 'Oficina Central AutoCare'
                       WHEN profile_type = 'PARTS_STORE_ADMIN' THEN 'Loja peças Prime'
                       WHEN profile_type = 'WORKSHOP_EMPLOYEE' THEN 'Oficina Central AutoCare'
                       WHEN profile_type = 'PARTS_STORE_EMPLOYEE' THEN 'Loja peças Prime'
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
ALTER
COLUMN company_type SET NOT NULL;

ALTER TABLE demo_leads
    ADD COLUMN city VARCHAR(120),
    ADD COLUMN message VARCHAR(500);

UPDATE demo_leads
SET city    = '',
    message = '';

ALTER TABLE demo_leads
    ALTER COLUMN city SET NOT NULL,
ALTER
COLUMN message SET NOT NULL;

ALTER TABLE parts
    ADD COLUMN description VARCHAR(500);

UPDATE parts
SET description = COALESCE(NULLIF(name, ''), 'Peça ou insumo cadastrado')
WHERE description IS NULL;

ALTER TABLE parts
    ALTER COLUMN description SET NOT NULL;

UPDATE customers
SET email = 'manutencao@atlasentregas.com'
WHERE email = 'manutenção@atlasentregas.com';
