CREATE TABLE IF NOT EXISTS order_shipment_addresses(
    id          BIGSERIAL PRIMARY KEY,
    order_id    BIGINT NOT NULL,
    customer_id BIGINT NOT NULL,
    address     JSON
)