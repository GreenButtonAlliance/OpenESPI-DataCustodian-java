INSERT INTO retail_customers (id, first_name, last_name) VALUES (1, 'Alan', 'Turing');
INSERT INTO retail_customers (id, first_name, last_name) VALUES (2, 'Donald', 'Knuth');
INSERT INTO retail_customers (id, first_name, last_name) VALUES (3, 'Paul', 'Dirac');
INSERT INTO retail_customers (id, first_name, last_name) VALUES (4, 'Alonso', 'Church');
INSERT INTO retail_customers (id, first_name, last_name) VALUES (5, 'Charles', 'Babbage');
INSERT INTO retail_customers (id, first_name, last_name) VALUES (6, 'John', 'von Neumann');
INSERT INTO retail_customers (id, first_name, last_name) VALUES (7, 'Marian', 'Rejewski');

INSERT INTO usage_points (id, title, retail_customer_id) VALUES (1, 'House meter', 1);
INSERT INTO usage_points (id, title, retail_customer_id) VALUES (2, 'Gas meter', 1);
INSERT INTO usage_points (id, title, retail_customer_id) VALUES (3, 'House meter', 2);
INSERT INTO usage_points (id, title, retail_customer_id) VALUES (4, 'House meter', 6);
INSERT INTO usage_points (id, title, retail_customer_id) VALUES (5, 'Gas meter', 6);

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
