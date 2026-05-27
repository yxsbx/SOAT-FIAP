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
