CREATE DATABASE customers_db;

USE customers_db;

CREATE TABLE customers (
    id BIGINT PRIMARY KEY,
    name VARCHAR(255),
    email VARCHAR(255)
);

INSERT INTO customers (id, name, email) VALUES
(1, 'Alice', 'alice@example.com'),
(3, 'Charlie', 'charlie.williams@example.com'),
(5, 'Ethan', 'ethan.smith@example.com'),
(7, 'George', 'george.brown@example.com'),
(9, 'Ian', 'ian.miller@example.com');