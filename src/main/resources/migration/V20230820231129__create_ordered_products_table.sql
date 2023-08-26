CREATE TABLE IF NOT EXISTS ordered_products(
    id BIGSERIAL PRIMARY KEY,
    order_id BIGINT REFERENCES orders(id),
    product_id BIGINT REFERENCES products(id)
)