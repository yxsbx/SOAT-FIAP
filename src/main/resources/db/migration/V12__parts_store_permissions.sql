UPDATE users
SET employee_sub_role = 'ATTENDANT',
    permissions = 'MANAGE_STOCK,CREATE_BUDGET,VIEW_STATS'
WHERE username = 'loja.funcionario@autocarehub.com';

UPDATE users
SET permissions = 'VIEW_BILLING,MANAGE_STOCK,CREATE_BUDGET,EDIT_EMPLOYEES,VIEW_STATS'
WHERE username = 'loja.admin@autocarehub.com';
