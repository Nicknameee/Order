CREATE TABLE IF NOT EXISTS products(
    id BIGSERIAL PRIMARY KEY,
    product_name VARCHAR,
    product_characteristics JSONB,
    product_description VARCHAR,
    vendor_id UUID REFERENCES vendors(id),
    product_id UUID UNIQUE,
    product_cost FLOAT DEFAULT 0 NOT NULL CHECK ( product_cost >= 0),
    items_left INTEGER CHECK ( items_left >= 0 ),
    category_id INTEGER REFERENCES categories(id)
)