INSERT INTO public.ref_status_pattern
(pattern_code, name, created_date, updated_date)
VALUES('STRAIGHT_FORWARD', 'Straight forward status flow', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO public.ref_status
(id, name, next_status_id, created_date, updated_date, permission_code, pattern_code_id)
VALUES(1, 'To do', 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, null,1);

INSERT INTO public.ref_status
(id, name, next_status_id, created_date, updated_date, permission_code, pattern_code_id)
VALUES(2, 'In progress', 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, null,1);

INSERT INTO public.ref_status
(id,  name, next_status_id, created_date, updated_date, permission_code, pattern_code_id)
VALUES(3,  'Done', null, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, null, 1);


INSERT INTO public.ref_status_pattern
(pattern_code, name, created_date, updated_date)
VALUES('NEED_APPROVAL', 'Need approval status flow', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO public.ref_status
(id, name, next_status_id, created_date, updated_date, permission_code, pattern_code_id)
VALUES(4, 'To do', 5, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, null,2);

INSERT INTO public.ref_status
(id, name, next_status_id, created_date, updated_date, permission_code, pattern_code_id)
VALUES(5, 'In progress', 6, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, null,2);

INSERT INTO public.ref_status
(id, name, next_status_id, created_date, updated_date, permission_code, pattern_code_id)
VALUES(6, 'Waiting for approval', 7, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'APPROVE_STEP_STATUS',2);

INSERT INTO public.ref_status
(id, name, next_status_id, created_date, updated_date, permission_code, pattern_code_id)
VALUES(7, 'Done', null, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, null,2);

CREATE SEQUENCE ref_status_pattern_id_seq
MINVALUE 1
MAXVALUE 999999999
INCREMENT BY 1
START WITH 1;

CREATE SEQUENCE ref_status_id_seq
MINVALUE 1
MAXVALUE 999999999
INCREMENT BY 1
START WITH 1;

ALTER SEQUENCE ref_status_pattern_id_seq RESTART WITH 3;

ALTER SEQUENCE ref_status_id_seq RESTART WITH 8;
