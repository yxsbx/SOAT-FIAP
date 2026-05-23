ALTER TABLE demo_leads
    ADD COLUMN demo_profile VARCHAR(40) NOT NULL DEFAULT 'workshop';

CREATE INDEX idx_demo_leads_demo_profile ON demo_leads (demo_profile);
