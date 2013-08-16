DROP TABLE IF EXISTS usage_points;
DROP TABLE IF EXISTS retail_customers;
DROP SEQUENCE retail_customers_seq;
DROP SEQUENCE usage_points_seq;

CREATE SEQUENCE retail_customers_seq START 8;

CREATE TABLE retail_customers (
  id INTEGER PRIMARY KEY DEFAULT nextval('retail_customers_seq'),
  first_name VARCHAR(30),
  last_name  VARCHAR(30)
);

CREATE SEQUENCE usage_points_seq START 4;

CREATE TABLE usage_points (
  id INTEGER PRIMARY KEY DEFAULT nextval('usage_points_seq'),
  title      VARCHAR(100) NOT NULL,
  retail_customer_id INTEGER NOT NULL references retail_customers(id)
);

