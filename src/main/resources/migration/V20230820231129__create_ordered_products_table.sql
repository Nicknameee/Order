CREATE TABLE IF NOT EXISTS ordered_products(
    id BIGSERIAL PRIMARY KEY,
    order_id BIGINT REFERENCES orders(id),
    product_id BIGINT REFERENCES products(id),
    products_amount BIGINT NOT NULL DEFAULT 1 CHECK ( products_amount > 0 ),
    UNIQUE(order_id, product_id)
)