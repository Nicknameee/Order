CREATE TABLE IF NOT EXISTS orders(
    id BIGSERIAL PRIMARY KEY,
    order_number BIGINT UNIQUE,
    ordered_products_cost FLOAT NOT NULL DEFAULT 0,
    delivery_cost FLOAT NOT NULL DEFAULT 0,
    order_date TIMESTAMPTZ DEFAULT NOW(),
    order_status VARCHAR(32) NOT NULL,
    payment_type VARCHAR,
    customer_id INT NOT NULL,
    processing_operator_id INT
)