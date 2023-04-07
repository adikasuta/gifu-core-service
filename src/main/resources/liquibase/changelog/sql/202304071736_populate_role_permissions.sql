
INSERT INTO role_permission(role_id, permission_id, created_date, updated_date)
select r.id, p.id,current_timestamp,current_timestamp from "role" r
left join "permission" p on true
where
r.code = 'ADMIN'
and p.code in (
'historical_order_table',
'confirm_order',
'bill_order',
'product_search',
'product_add',
'product_edit',
'catalog_search',
'catalog_add',
'catalog_edit',
'catalog_delete',
'workflow_search',
'workflow_add',
'workflow_edit',
'workflow_delete',
'user_search',
'user_add',
'user_edit'
);

INSERT INTO role_permission(role_id, permission_id, created_date, updated_date)
select r.id, p.id,current_timestamp,current_timestamp from "role" r
left join "permission" p on true
where
r.code = 'STAFF'
and p.code in (
'historical_order_table',
'staff_assignment'
);

INSERT INTO role_permission(role_id, permission_id, created_date, updated_date)
select r.id, p.id,current_timestamp,current_timestamp from "role" r
left join "permission" p on true
where
r.code = 'SPV'
and p.code in (
'historical_order_table',
'staff_assignment',
'product_search',
'product_add',
'product_edit',
'catalog_search',
'catalog_add',
'catalog_edit',
'catalog_delete',
'workflow_search',
'workflow_add',
'workflow_edit',
'workflow_delete',
'user_search',
'user_add',
'user_edit',
'approve_step_status'
);
