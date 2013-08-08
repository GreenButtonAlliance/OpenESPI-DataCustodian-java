DROP TABLE retail_customers IF EXISTS;

CREATE TABLE retail_customers (
  id         INTEGER IDENTITY PRIMARY KEY,
  first_name VARCHAR(30),
  last_name  VARCHAR(30)
);
