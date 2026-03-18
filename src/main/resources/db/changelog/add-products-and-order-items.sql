CREATE TABLE products (
    id          SERIAL PRIMARY KEY,
    name        VARCHAR(200) NOT NULL,
    price       NUMERIC(10,2) NOT NULL,
    created_at  TIMESTAMP DEFAULT NOW(),
    updated_at  TIMESTAMP DEFAULT NOW()
);

CREATE TABLE order_items (
    order_id    INT REFERENCES orders(id) ON DELETE CASCADE,
    product_id  INT REFERENCES products(id),
    quantity    INT NOT NULL CHECK (quantity > 0),
    price       NUMERIC(10,2) NOT NULL,
    updated_at  TIMESTAMP DEFAULT NOW(),
    PRIMARY KEY (order_id, product_id)
);

CREATE INDEX idx_products_updated ON products(updated_at);
CREATE INDEX idx_order_items_updated ON order_items(updated_at);