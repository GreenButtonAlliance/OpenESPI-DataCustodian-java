INSERT INTO service_categories (kind) VALUES (0);
INSERT INTO service_categories (kind) VALUES (1);
INSERT INTO service_categories (kind) VALUES (2);
INSERT INTO service_categories (kind) VALUES (3);
INSERT INTO service_categories (kind) VALUES (4);
INSERT INTO service_categories (kind) VALUES (5);
INSERT INTO service_categories (kind) VALUES (6);
INSERT INTO service_categories (kind) VALUES (7);
INSERT INTO service_categories (kind) VALUES (8);
INSERT INTO service_categories (kind) VALUES (9);

INSERT INTO retail_customers (username, first_name, last_name, password, enabled, role) VALUES ('alan',    'Alan',    'Turing',      'koala', 1, 'ROLE_USER');
INSERT INTO retail_customers (username, first_name, last_name, password, enabled, role) VALUES ('donald',  'Donald',  'Knuth',       'koala', 1, 'ROLE_USER');
INSERT INTO retail_customers (username, first_name, last_name, password, enabled, role) VALUES ('paul',    'Paul',    'Dirac',       'koala', 1, 'ROLE_USER');
INSERT INTO retail_customers (username, first_name, last_name, password, enabled, role) VALUES ('alonzo',  'Alonzo',  'Church',      'koala', 1, 'ROLE_USER');
INSERT INTO retail_customers (username, first_name, last_name, password, enabled, role) VALUES ('charles', 'Charles', 'Babbage',     'koala', 1, 'ROLE_USER');
INSERT INTO retail_customers (username, first_name, last_name, password, enabled, role) VALUES ('john',    'John',    'von Neumann', 'koala', 1, 'ROLE_USER');
INSERT INTO retail_customers (username, first_name, last_name, password, enabled, role) VALUES ('marian',  'Marian',  'Rejewski',    'koala', 1, 'ROLE_USER');
INSERT INTO retail_customers (username, first_name, last_name, password, enabled, role) VALUES ('grace',   'Grace',   'Hopper',      'koala', 1, 'ROLE_CUSTODIAN');

INSERT INTO usage_points (description, serviceCategory_kind, retail_customer_id, uuid) VALUES ('Front Electric Meter', 1, 1, '7BC41774-7190-4864-841C-861AC76D46C2');
INSERT INTO usage_points (description, serviceCategory_kind, retail_customer_id, uuid) VALUES ('Gas meter', 1, 1, '7BC41774-7190-4864-841C-861AC76D46C3');
INSERT INTO usage_points (description, serviceCategory_kind, retail_customer_id, uuid) VALUES ('Front Electric Meter', 1, 2, '7BC41774-7190-4864-841C-861AC76D46C4');
INSERT INTO usage_points (description, serviceCategory_kind, retail_customer_id, uuid) VALUES ('Front Electric Meter', 1, 6, '7BC41774-7190-4864-841C-861AC76D46C5');
INSERT INTO usage_points (description, serviceCategory_kind, retail_customer_id, uuid) VALUES ('Gas meter', 1, 6, '7BC41774-7190-4864-841C-861AC76D46C6');