DROP TABLE IF EXISTS usage_points;
DROP TABLE IF EXISTS retail_customers;

CREATE TABLE retail_customers (
  id BIGINT(20) NOT NULL AUTO_INCREMENT,
  first_name VARCHAR(30),
  last_name  VARCHAR(30),
  PRIMARY KEY (id)
);

CREATE TABLE usage_points (
  id BIGINT(20) NOT NULL AUTO_INCREMENT,
  title      VARCHAR(100) NOT NULL,
  retail_customer_id BIGINT(20) NOT NULL,
  PRIMARY KEY (id)
);

ALTER TABLE usage_points
  ADD CONSTRAINT usage_points_retail_customer_fk
  FOREIGN KEY (retail_customer_id)
  REFERENCES retail_customers (id);
