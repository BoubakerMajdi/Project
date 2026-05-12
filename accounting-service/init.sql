CREATE TABLE IF NOT EXISTS accounts (
    customer_id VARCHAR(255) NOT NULL PRIMARY KEY,
    balance     DOUBLE PRECISION NOT NULL
);

INSERT INTO accounts (customer_id, balance) VALUES
    ('customer-1', 500.00),
    ('customer-2', 100.00),
    ('customer-3', 0.00);
