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

INSERT INTO usage_points (id, description, serviceCategory_kind, retail_customer_id, uuid) VALUES (1, 'House meter', 1, 1, '1');
INSERT INTO usage_points (id, description, serviceCategory_kind, retail_customer_id, uuid) VALUES (2, 'Gas meter', 1, 1, '2');
INSERT INTO usage_points (id, description, serviceCategory_kind, retail_customer_id, uuid) VALUES (3, 'House meter', 1, 2, '3');
INSERT INTO usage_points (id, description, serviceCategory_kind, retail_customer_id, uuid) VALUES (4, 'House meter', 1, 6, '4');
INSERT INTO usage_points (id, description, serviceCategory_kind, retail_customer_id, uuid) VALUES (5, 'Gas meter', 1, 6, '5');

INSERT INTO third_parties (id, name, url) VALUES(1, 'Pivotal Energy (localhost)', 'http://localhost:8080/ThirdParty/RetailCustomer/ScopeSelection')
INSERT INTO third_parties (id, name, url) VALUES(2, 'Heroku Analytics (localhost)', 'http://localhost:8080/ThirdParty/RetailCustomer/ScopeSelection')
