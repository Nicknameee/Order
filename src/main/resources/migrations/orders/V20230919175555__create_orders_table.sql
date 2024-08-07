CREATE TABLE IF NOT EXISTS orders(
    id BIGSERIAL PRIMARY KEY,
    customer_id INT NOT NULL,
    delivery_cost FLOAT NOT NULL DEFAULT 0,
    number BIGINT UNIQUE NOT NULL,
    ordered_products_cost FLOAT NOT NULL DEFAULT 0,
    creation_date TIMESTAMPTZ DEFAULT NOW(),
    status VARCHAR(32) NOT NULL,
    payment_type VARCHAR NOT NULL,
    processing_operator_id INT,
    paid BOOLEAN DEFAULT FALSE NOT NULL,
    last_update_date TIMESTAMPTZ,
    delivery_service_type VARCHAR NOT NULL,
    address_id BIGINT REFERENCES order_shipment_addresses(id),
    transaction_id UUID REFERENCES transactions(id)
)