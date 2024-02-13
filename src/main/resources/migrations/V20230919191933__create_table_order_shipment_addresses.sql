CREATE TABLE IF NOT EXISTS order_shipment_addresses(
    id BIGSERIAL PRIMARY KEY,
    order_id BIGINT,
    customer_id BIGINT,
    address JSON
)