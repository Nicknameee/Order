CREATE TABLE IF NOT EXISTS products(
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR,
    brand VARCHAR,
    parameters JSON,
    description VARCHAR,
    vendor_id UUID REFERENCES vendors(id),
    product_id UUID UNIQUE,
    cost FLOAT DEFAULT 0 NOT NULL CHECK (cost >= 0),
    currency VARCHAR NOT NULL,
    items_left INTEGER CHECK (items_left >= 0),
    blocked BOOLEAN DEFAULT FALSE NOT NULL,
    category_id UUID REFERENCES categories(category_id),
    introduction_picture_url VARCHAR,
    picture_urls VARCHAR[],
    margin_rate DOUBLE PRECISION CHECK(margin_rate >= 1)
)