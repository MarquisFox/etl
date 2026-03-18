ALTER TABLE customers ADD COLUMN deleted_at TIMESTAMP;
ALTER TABLE orders    ADD COLUMN deleted_at TIMESTAMP;
ALTER TABLE products  ADD COLUMN deleted_at TIMESTAMP;
ALTER TABLE order_items ADD COLUMN deleted_at TIMESTAMP;  -- опционально