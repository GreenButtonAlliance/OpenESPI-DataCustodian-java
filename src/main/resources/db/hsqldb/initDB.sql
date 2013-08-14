DROP TABLE usage_points IF EXISTS;
DROP TABLE retail_customers IF EXISTS;

CREATE TABLE retail_customers (
  id         INTEGER IDENTITY PRIMARY KEY,
  first_name VARCHAR(30),
  last_name  VARCHAR(30)
);

CREATE TABLE usage_points (
  id         INTEGER IDENTITY PRIMARY KEY,
  title      VARCHAR(100) NOT NULL,
  retail_customer_id INTEGER NOT NULL
);

ALTER TABLE usage_points
    ADD CONSTRAINT retail_customers_usage_points_fk FOREIGN KEY (retail_customer_id)
    REFERENCES retail_customers (id);
