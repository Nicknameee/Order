CREATE TABLE IF NOT EXISTS order_history(
    id BIGSERIAL PRIMARY KEY,
    order_id BIGINT NOT NULL REFERENCES orders(id),
    old_order JSON DEFAULT '{}'::JSON,
    updated_order JSON DEFAULT '{}'::JSON,
    iteration INT NOT NULL CHECK (iteration > 0),
    updated_fields VARCHAR[],
    update_time TIMESTAMPTZ DEFAULT NOW()
)