CREATE TABLE IF NOT EXISTS customer_waiting_list(
    id          BIGSERIAL PRIMARY KEY,
    product_id  UUID   NOT NULL REFERENCES products(product_id),
    customer_id BIGINT NOT NULL
)