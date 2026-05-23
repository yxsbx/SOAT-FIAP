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
