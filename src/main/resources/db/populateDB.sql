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

INSERT INTO usage_points (id, description, serviceCategory_kind, retail_customer_id) VALUES (1, 'House meter', 1, 1);
INSERT INTO usage_points (id, description, serviceCategory_kind, retail_customer_id) VALUES (2, 'Gas meter', 1, 1);
INSERT INTO usage_points (id, description, serviceCategory_kind, retail_customer_id) VALUES (3, 'House meter', 1, 2);
INSERT INTO usage_points (id, description, serviceCategory_kind, retail_customer_id) VALUES (4, 'House meter', 1, 6);
INSERT INTO usage_points (id, description, serviceCategory_kind, retail_customer_id) VALUES (5, 'Gas meter', 1, 6);

INSERT INTO meter_readings (id, description, usage_point_id) VALUES (1, "Electricity consumption", 1)
INSERT INTO meter_readings (id, description, usage_point_id) VALUES (2, "Gas consumption", 2)
