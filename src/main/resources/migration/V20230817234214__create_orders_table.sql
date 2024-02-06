CREATE TABLE IF NOT EXISTS orders(
    id BIGSERIAL PRIMARY KEY,
    customer_id INT NOT NULL,
    delivery_cost FLOAT NOT NULL DEFAULT 0,
    order_number BIGINT UNIQUE,
    ordered_products_cost FLOAT NOT NULL DEFAULT 0,
    order_date TIMESTAMPTZ DEFAULT NOW(),
    order_status VARCHAR(32) NOT NULL,
    payment_type VARCHAR,
    processing_operator_id INT,
    paid BOOLEAN,
    update_order_date TIMESTAMPTZ
)