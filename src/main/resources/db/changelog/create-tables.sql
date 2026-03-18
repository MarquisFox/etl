CREATE TABLE customers (
    id          SERIAL PRIMARY KEY,
    name        VARCHAR(100) NOT NULL,
    email       VARCHAR(150) UNIQUE NOT NULL,
    created_at  TIMESTAMP DEFAULT NOW(),
    updated_at  TIMESTAMP DEFAULT NOW()
);

CREATE TABLE orders (
    id           SERIAL PRIMARY KEY,
    customer_id  INT REFERENCES customers(id) ON DELETE CASCADE,
    status       VARCHAR(50) DEFAULT 'pending',
    created_at   TIMESTAMP DEFAULT NOW(),
    updated_at   TIMESTAMP DEFAULT NOW()
);

CREATE INDEX idx_customers_updated ON customers(updated_at);
CREATE INDEX idx_orders_updated ON orders(updated_at);