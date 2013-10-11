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

INSERT INTO retail_customers (id, username, first_name, last_name, password, enabled, role) VALUES (1, 'alan',    'Alan',    'Turing',      'koala', 1, 'ROLE_USER');
INSERT INTO retail_customers (id, username, first_name, last_name, password, enabled, role) VALUES (2, 'donald',  'Donald',  'Knuth',       'koala', 1, 'ROLE_USER');
INSERT INTO retail_customers (id, username, first_name, last_name, password, enabled, role) VALUES (3, 'paul',    'Paul',    'Dirac',       'koala', 1, 'ROLE_USER');
INSERT INTO retail_customers (id, username, first_name, last_name, password, enabled, role) VALUES (4, 'alonzo',  'Alonzo',  'Church',      'koala', 1, 'ROLE_USER');
INSERT INTO retail_customers (id, username, first_name, last_name, password, enabled, role) VALUES (5, 'charles', 'Charles', 'Babbage',     'koala', 1, 'ROLE_USER');
INSERT INTO retail_customers (id, username, first_name, last_name, password, enabled, role) VALUES (6, 'john',    'John',    'von Neumann', 'koala', 1, 'ROLE_USER');
INSERT INTO retail_customers (id, username, first_name, last_name, password, enabled, role) VALUES (7, 'marian',  'Marian',  'Rejewski',    'koala', 1, 'ROLE_USER');
INSERT INTO retail_customers (id, username, first_name, last_name, password, enabled, role) VALUES (8, 'grace',   'Grace',   'Hopper',      'koala', 1, 'ROLE_CUSTODIAN');

INSERT INTO usage_points (id, description, serviceCategory_kind, retail_customer_id, uuid) VALUES (1, 'Front Electric Meter', 1, 1, '7BC41774-7190-4864-841C-861AC76D46C2');
INSERT INTO usage_points (id, description, serviceCategory_kind, retail_customer_id, uuid) VALUES (2, 'Gas meter', 1, 1, '7BC41774-7190-4864-841C-861AC76D46C3');
INSERT INTO usage_points (id, description, serviceCategory_kind, retail_customer_id, uuid) VALUES (3, 'Front Electric Meter', 1, 2, '3');
INSERT INTO usage_points (id, description, serviceCategory_kind, retail_customer_id, uuid) VALUES (4, 'Front Electric Meter', 1, 6, '4');
INSERT INTO usage_points (id, description, serviceCategory_kind, retail_customer_id, uuid) VALUES (5, 'Gas meter', 1, 6, '5');
