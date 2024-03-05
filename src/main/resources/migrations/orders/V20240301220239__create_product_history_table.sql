CREATE TABLE IF NOT EXISTS product_history(
    id BIGSERIAL PRIMARY KEY,
    product_id UUID NOT NULL REFERENCES products(product_id),
    old_product JSON DEFAULT '{}'::JSON,
    updated_product JSON DEFAULT '{}'::JSON,
    iteration INT NOT NULL CHECK (iteration > 0),
    updated_fields VARCHAR[],
    update_time TIMESTAMPTZ DEFAULT NOW()
)