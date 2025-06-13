CREATE DATABASE customers_db;

USE customers_db;

CREATE TABLE customers (
    id BIGINT PRIMARY KEY,
    name VARCHAR(255),
    email VARCHAR(255)
);


INSERT INTO customers (id, name, email) VALUES
(2, 'Bob', 'bob@example.com'),
(4, 'Diana', 'diana.james@example.com'),
(6, 'Fiona', 'fiona.jones@example.com'),
(8, 'Hannah', 'hannah.green@example.com'),
(10, 'Julia', 'julia.moore@example.com');