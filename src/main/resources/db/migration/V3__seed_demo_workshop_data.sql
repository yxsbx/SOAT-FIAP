INSERT INTO customers (
    id, name, document_type, document_value, phone, email,
    address_street, address_number, address_complement, address_neighborhood,
    address_city, address_state, address_zip_code, active, created_at
) VALUES
    ('10000000-0000-0000-0000-000000000001', 'Mariana Costa', 'CPF', '12345678909', '11988887777', 'mariana.costa@email.com', 'Rua das Oficinas', '120', 'Casa 2', 'Mooca', 'Sao Paulo', 'SP', '03111000', TRUE, CURRENT_TIMESTAMP),
    ('10000000-0000-0000-0000-000000000002', 'Ricardo Almeida', 'CPF', '98765432100', '11977776666', 'ricardo.almeida@email.com', 'Avenida Brasil', '450', NULL, 'Jardins', 'Sao Paulo', 'SP', '01430000', TRUE, CURRENT_TIMESTAMP),
    ('10000000-0000-0000-0000-000000000003', 'Patricia Oliveira', 'CPF', '45678912300', '11966665555', 'patricia.oliveira@email.com', 'Rua Vergueiro', '2200', 'Apto 81', 'Vila Mariana', 'Sao Paulo', 'SP', '04102000', TRUE, CURRENT_TIMESTAMP),
    ('10000000-0000-0000-0000-000000000004', 'TechLog Transportes Ltda', 'CNPJ', '11222333000144', '1133332222', 'frota@techlog.com', 'Avenida Industrial', '900', 'Galpao B', 'Tambore', 'Barueri', 'SP', '06460000', TRUE, CURRENT_TIMESTAMP),
    ('10000000-0000-0000-0000-000000000005', 'Joao Pereira', 'CPF', '32165498701', '11955554444', 'joao.pereira@email.com', 'Rua Augusta', '1001', NULL, 'Consolacao', 'Sao Paulo', 'SP', '01305000', TRUE, CURRENT_TIMESTAMP)
ON CONFLICT (id) DO NOTHING;

INSERT INTO vehicles (
    id, customer_id, plate, brand, model, manufacture_year, mileage, active
) VALUES
    ('20000000-0000-0000-0000-000000000001', '10000000-0000-0000-0000-000000000001', 'MCA1D23', 'Honda', 'Civic Touring', 2020, 58200, TRUE),
    ('20000000-0000-0000-0000-000000000002', '10000000-0000-0000-0000-000000000001', 'MCA2E45', 'Jeep', 'Compass Limited', 2022, 31800, TRUE),
    ('20000000-0000-0000-0000-000000000003', '10000000-0000-0000-0000-000000000002', 'RCA3F67', 'Toyota', 'Corolla XEi', 2019, 74000, TRUE),
    ('20000000-0000-0000-0000-000000000004', '10000000-0000-0000-0000-000000000003', 'PTO4G89', 'Hyundai', 'HB20 Comfort', 2021, 42600, TRUE),
    ('20000000-0000-0000-0000-000000000005', '10000000-0000-0000-0000-000000000004', 'TLG5H10', 'Fiat', 'Fiorino Endurance', 2020, 112300, TRUE),
    ('20000000-0000-0000-0000-000000000006', '10000000-0000-0000-0000-000000000004', 'TLG6J32', 'Renault', 'Master Furgao', 2018, 156900, TRUE),
    ('20000000-0000-0000-0000-000000000007', '10000000-0000-0000-0000-000000000004', 'TLG7K54', 'Volkswagen', 'Delivery Express', 2021, 89000, TRUE),
    ('20000000-0000-0000-0000-000000000008', '10000000-0000-0000-0000-000000000005', 'JPR8L76', 'Chevrolet', 'Onix Premier', 2023, 18400, TRUE)
ON CONFLICT (id) DO NOTHING;

INSERT INTO workshop_services (
    id, name, description, base_price, estimated_time_in_minutes, active
) VALUES
    ('30000000-0000-0000-0000-000000000001', 'Diagnostico eletronico', 'Leitura de scanner, inspecao de falhas e relatorio tecnico para o cliente.', 180.00, 60, TRUE),
    ('30000000-0000-0000-0000-000000000002', 'Troca de oleo e filtros', 'Substituicao de oleo do motor, filtro de oleo, filtro de ar e checklist visual.', 220.00, 75, TRUE),
    ('30000000-0000-0000-0000-000000000003', 'Revisao preventiva completa', 'Revisao de freios, suspensao, fluidos, correias, pneus, luzes e sistema eletrico.', 520.00, 240, TRUE),
    ('30000000-0000-0000-0000-000000000004', 'Servico de freios', 'Inspecao e substituicao de pastilhas, discos e sangria quando necessario.', 360.00, 150, TRUE),
    ('30000000-0000-0000-0000-000000000005', 'Alinhamento e balanceamento', 'Alinhamento computadorizado, balanceamento das rodas e calibragem.', 190.00, 90, TRUE),
    ('30000000-0000-0000-0000-000000000006', 'Higienizacao do ar-condicionado', 'Limpeza do sistema, troca de filtro de cabine e eliminacao de odores.', 160.00, 60, TRUE),
    ('30000000-0000-0000-0000-000000000007', 'Troca de correia dentada', 'Substituicao do kit de correia dentada e verificacao do sincronismo do motor.', 680.00, 300, TRUE),
    ('30000000-0000-0000-0000-000000000008', 'Diagnostico de suspensao', 'Avaliacao de buchas, amortecedores, bandejas, pivots e terminais.', 140.00, 60, TRUE)
ON CONFLICT (id) DO NOTHING;

INSERT INTO parts (
    id, name, sku, category, subcategory, brand, unit_price, stock_quantity, minimum_stock, active
) VALUES
    ('40000000-0000-0000-0000-000000000001', 'Oleo sintetico 5W30 1L', 'OLE-5W30-001', 'Lubrificantes', 'Motor', 'Mobil', 52.90, 24, 12, TRUE),
    ('40000000-0000-0000-0000-000000000002', 'Filtro de oleo Honda/Toyota', 'FIL-OLE-HT01', 'Filtros', 'Oleo', 'Tecfil', 38.50, 8, 10, TRUE),
    ('40000000-0000-0000-0000-000000000003', 'Filtro de ar compacto', 'FIL-AR-CMP01', 'Filtros', 'Ar', 'Mann', 46.00, 18, 8, TRUE),
    ('40000000-0000-0000-0000-000000000004', 'Filtro de cabine universal', 'FIL-CAB-UNI01', 'Filtros', 'Cabine', 'Wega', 42.00, 5, 10, TRUE),
    ('40000000-0000-0000-0000-000000000005', 'Pastilha de freio dianteira', 'FR-PAS-DIA01', 'Freios', 'Pastilhas', 'Fras-le', 185.00, 6, 8, TRUE),
    ('40000000-0000-0000-0000-000000000006', 'Disco de freio ventilado', 'FR-DIS-VEN01', 'Freios', 'Discos', 'Fremax', 240.00, 4, 6, TRUE),
    ('40000000-0000-0000-0000-000000000007', 'Fluido de freio DOT4 500ml', 'FLU-DOT4-500', 'Fluidos', 'Freio', 'Bosch', 39.90, 16, 10, TRUE),
    ('40000000-0000-0000-0000-000000000008', 'Amortecedor dianteiro', 'SUS-AMO-DIA01', 'Suspensao', 'Amortecedores', 'Monroe', 390.00, 3, 4, TRUE),
    ('40000000-0000-0000-0000-000000000009', 'Bucha bandeja dianteira', 'SUS-BUC-BAN01', 'Suspensao', 'Buchas', 'Axios', 72.00, 12, 8, TRUE),
    ('40000000-0000-0000-0000-000000000010', 'Bateria 60Ah', 'ELE-BAT-60AH', 'Eletrica', 'Bateria', 'Moura', 520.00, 2, 3, TRUE),
    ('40000000-0000-0000-0000-000000000011', 'Lampada H7', 'ELE-LMP-H7', 'Eletrica', 'Iluminacao', 'Osram', 49.00, 20, 8, TRUE),
    ('40000000-0000-0000-0000-000000000012', 'Vela de ignicao iridium', 'MOT-VEL-IRI01', 'Motor', 'Ignicao', 'NGK', 68.00, 22, 12, TRUE),
    ('40000000-0000-0000-0000-000000000013', 'Kit correia dentada', 'MOT-COR-KIT01', 'Motor', 'Correias', 'Gates', 410.00, 1, 3, TRUE),
    ('40000000-0000-0000-0000-000000000014', 'Aditivo radiador rosa 1L', 'ARR-ADT-ROS01', 'Arrefecimento', 'Aditivo', 'Petronas', 32.00, 14, 10, TRUE),
    ('40000000-0000-0000-0000-000000000015', 'Palheta limpador par', 'ACE-PAL-PAR01', 'Acessorios', 'Palhetas', 'Bosch', 89.90, 9, 6, TRUE),
    ('40000000-0000-0000-0000-000000000016', 'Pneu 205/55 R16', 'ROD-PNE-20555R16', 'Rodas', 'Pneus', 'Pirelli', 520.00, 7, 4, TRUE),
    ('40000000-0000-0000-0000-000000000017', 'Sensor ABS dianteiro', 'ELE-SEN-ABS01', 'Eletrica', 'Sensores', 'Magneti Marelli', 210.00, 2, 4, TRUE),
    ('40000000-0000-0000-0000-000000000018', 'Coxim do motor', 'MOT-COX-MOT01', 'Motor', 'Coxins', 'Mobensani', 160.00, 6, 4, TRUE),
    ('40000000-0000-0000-0000-000000000019', 'Kit embreagem utilitario', 'TRA-EMB-KIT01', 'Transmissao', 'Embreagem', 'Luk', 780.00, 1, 2, TRUE),
    ('40000000-0000-0000-0000-000000000020', 'Fluido de cambio ATF 1L', 'TRA-FLU-ATF01', 'Transmissao', 'Fluidos', 'Motul', 74.00, 10, 8, TRUE)
ON CONFLICT (id) DO NOTHING;

INSERT INTO service_orders (
    id, customer_id, vehicle_id, status, diagnostic_notes, total_amount,
    created_at, budget_generated_at, approved_at, started_at, finished_at, delivered_at
) VALUES
    ('50000000-0000-0000-0000-000000000001', '10000000-0000-0000-0000-000000000001', '20000000-0000-0000-0000-000000000001', 'IN_PROGRESS', 'Cliente relata vibracao ao frear acima de 80 km/h. Nota: revisar discos dianteiros, pastilhas e fluido.', 807.00, CURRENT_TIMESTAMP - INTERVAL '5 days', CURRENT_TIMESTAMP - INTERVAL '4 days', CURRENT_TIMESTAMP - INTERVAL '4 days', CURRENT_TIMESTAMP - INTERVAL '3 days', NULL, NULL),
    ('50000000-0000-0000-0000-000000000002', '10000000-0000-0000-0000-000000000001', '20000000-0000-0000-0000-000000000002', 'WAITING_APPROVAL', 'Revisao de 30 mil km. Nota: cliente pediu priorizar troca de oleo, filtros e higienizacao do ar.', 656.50, CURRENT_TIMESTAMP - INTERVAL '2 days', CURRENT_TIMESTAMP - INTERVAL '1 day', NULL, NULL, NULL, NULL),
    ('50000000-0000-0000-0000-000000000003', '10000000-0000-0000-0000-000000000002', '20000000-0000-0000-0000-000000000003', 'FINISHED', 'Luz de injecao acesa e consumo elevado. Nota: scanner apontou falha intermitente em vela de ignicao.', 632.00, CURRENT_TIMESTAMP - INTERVAL '8 days', CURRENT_TIMESTAMP - INTERVAL '7 days', CURRENT_TIMESTAMP - INTERVAL '7 days', CURRENT_TIMESTAMP - INTERVAL '6 days', CURRENT_TIMESTAMP - INTERVAL '5 days', NULL),
    ('50000000-0000-0000-0000-000000000004', '10000000-0000-0000-0000-000000000003', '20000000-0000-0000-0000-000000000004', 'DELIVERED', 'Barulho na suspensao dianteira em piso irregular. Nota: substituida bucha de bandeja e feito alinhamento.', 334.00, CURRENT_TIMESTAMP - INTERVAL '14 days', CURRENT_TIMESTAMP - INTERVAL '13 days', CURRENT_TIMESTAMP - INTERVAL '13 days', CURRENT_TIMESTAMP - INTERVAL '12 days', CURRENT_TIMESTAMP - INTERVAL '11 days', CURRENT_TIMESTAMP - INTERVAL '10 days'),
    ('50000000-0000-0000-0000-000000000005', '10000000-0000-0000-0000-000000000004', '20000000-0000-0000-0000-000000000005', 'RECEIVED', 'Veiculo de frota chegou para revisao preventiva. Nota: motorista reportou dificuldade de partida pela manha.', 0.00, CURRENT_TIMESTAMP - INTERVAL '1 day', NULL, NULL, NULL, NULL, NULL),
    ('50000000-0000-0000-0000-000000000006', '10000000-0000-0000-0000-000000000004', '20000000-0000-0000-0000-000000000006', 'IN_DIAGNOSIS', 'Utilitario com ruido ao acionar embreagem. Nota: avaliar kit de embreagem antes de liberar orcamento.', 180.00, CURRENT_TIMESTAMP - INTERVAL '3 days', NULL, NULL, CURRENT_TIMESTAMP - INTERVAL '2 days', NULL, NULL),
    ('50000000-0000-0000-0000-000000000007', '10000000-0000-0000-0000-000000000005', '20000000-0000-0000-0000-000000000008', 'WAITING_APPROVAL', 'Primeira revisao pos-compra. Nota: cliente solicitou checagem completa antes de viagem.', 1056.90, CURRENT_TIMESTAMP - INTERVAL '4 days', CURRENT_TIMESTAMP - INTERVAL '3 days', NULL, NULL, NULL, NULL)
ON CONFLICT (id) DO NOTHING;

INSERT INTO service_order_services (
    id, service_order_id, service_id, name, quantity, unit_price, total_price
) VALUES
    ('60000000-0000-0000-0000-000000000001', '50000000-0000-0000-0000-000000000001', '30000000-0000-0000-0000-000000000004', 'Servico de freios', 1, 360.00, 360.00),
    ('60000000-0000-0000-0000-000000000002', '50000000-0000-0000-0000-000000000002', '30000000-0000-0000-0000-000000000002', 'Troca de oleo e filtros', 1, 220.00, 220.00),
    ('60000000-0000-0000-0000-000000000003', '50000000-0000-0000-0000-000000000002', '30000000-0000-0000-0000-000000000006', 'Higienizacao do ar-condicionado', 1, 160.00, 160.00),
    ('60000000-0000-0000-0000-000000000004', '50000000-0000-0000-0000-000000000003', '30000000-0000-0000-0000-000000000001', 'Diagnostico eletronico', 1, 180.00, 180.00),
    ('60000000-0000-0000-0000-000000000005', '50000000-0000-0000-0000-000000000004', '30000000-0000-0000-0000-000000000005', 'Alinhamento e balanceamento', 1, 190.00, 190.00),
    ('60000000-0000-0000-0000-000000000006', '50000000-0000-0000-0000-000000000006', '30000000-0000-0000-0000-000000000001', 'Diagnostico eletronico', 1, 180.00, 180.00),
    ('60000000-0000-0000-0000-000000000007', '50000000-0000-0000-0000-000000000007', '30000000-0000-0000-0000-000000000003', 'Revisao preventiva completa', 1, 520.00, 520.00)
ON CONFLICT (id) DO NOTHING;

INSERT INTO service_order_parts (
    id, service_order_id, part_id, name, sku, quantity, unit_price, total_price
) VALUES
    ('70000000-0000-0000-0000-000000000001', '50000000-0000-0000-0000-000000000001', '40000000-0000-0000-0000-000000000005', 'Pastilha de freio dianteira', 'FR-PAS-DIA01', 1, 185.00, 185.00),
    ('70000000-0000-0000-0000-000000000002', '50000000-0000-0000-0000-000000000001', '40000000-0000-0000-0000-000000000006', 'Disco de freio ventilado', 'FR-DIS-VEN01', 1, 240.00, 240.00),
    ('70000000-0000-0000-0000-000000000003', '50000000-0000-0000-0000-000000000001', '40000000-0000-0000-0000-000000000007', 'Fluido de freio DOT4 500ml', 'FLU-DOT4-500', 1, 39.90, 39.90),
    ('70000000-0000-0000-0000-000000000004', '50000000-0000-0000-0000-000000000002', '40000000-0000-0000-0000-000000000001', 'Oleo sintetico 5W30 1L', 'OLE-5W30-001', 4, 52.90, 211.60),
    ('70000000-0000-0000-0000-000000000005', '50000000-0000-0000-0000-000000000002', '40000000-0000-0000-0000-000000000002', 'Filtro de oleo Honda/Toyota', 'FIL-OLE-HT01', 1, 38.50, 38.50),
    ('70000000-0000-0000-0000-000000000006', '50000000-0000-0000-0000-000000000002', '40000000-0000-0000-0000-000000000004', 'Filtro de cabine universal', 'FIL-CAB-UNI01', 1, 42.00, 42.00),
    ('70000000-0000-0000-0000-000000000007', '50000000-0000-0000-0000-000000000003', '40000000-0000-0000-0000-000000000012', 'Vela de ignicao iridium', 'MOT-VEL-IRI01', 4, 68.00, 272.00),
    ('70000000-0000-0000-0000-000000000008', '50000000-0000-0000-0000-000000000004', '40000000-0000-0000-0000-000000000009', 'Bucha bandeja dianteira', 'SUS-BUC-BAN01', 2, 72.00, 144.00),
    ('70000000-0000-0000-0000-000000000009', '50000000-0000-0000-0000-000000000007', '40000000-0000-0000-0000-000000000001', 'Oleo sintetico 5W30 1L', 'OLE-5W30-001', 5, 52.90, 264.50),
    ('70000000-0000-0000-0000-000000000010', '50000000-0000-0000-0000-000000000007', '40000000-0000-0000-0000-000000000003', 'Filtro de ar compacto', 'FIL-AR-CMP01', 1, 46.00, 46.00),
    ('70000000-0000-0000-0000-000000000011', '50000000-0000-0000-0000-000000000007', '40000000-0000-0000-0000-000000000015', 'Palheta limpador par', 'ACE-PAL-PAR01', 1, 89.90, 89.90)
ON CONFLICT (id) DO NOTHING;

INSERT INTO users (id, username, password_hash, role, customer_id, active, created_at)
VALUES
    ('00000000-0000-0000-0000-000000000002', 'funcionario@autocarehub.com', '$2a$10$xAb5kI.uSxQkLo9n6tZTiuf8WbQcehwTGGk99zzc2QtY28sx9WFO.', 'EMPLOYEE', NULL, TRUE, CURRENT_TIMESTAMP),
    ('00000000-0000-0000-0000-000000000003', 'cliente@autocarehub.com', '$2a$10$xAb5kI.uSxQkLo9n6tZTiuf8WbQcehwTGGk99zzc2QtY28sx9WFO.', 'CUSTOMER', '10000000-0000-0000-0000-000000000001', TRUE, CURRENT_TIMESTAMP)
ON CONFLICT (id) DO NOTHING;
