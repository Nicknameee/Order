CREATE TABLE IF NOT EXISTS order_shipment_addresses(
    id BIGSERIAL PRIMARY KEY,
    order_id BIGINT REFERENCES orders(id),
    shipper_id BIGINT NOT NULL,
    address JSONB
)