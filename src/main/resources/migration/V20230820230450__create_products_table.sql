CREATE TABLE IF NOT EXISTS products(
    id BIGSERIAL PRIMARY KEY,
    product_name VARCHAR,
    brand VARCHAR,
    product_code UUID UNIQUE,
    product_cost FLOAT NOT NULL CHECK ( product_cost >= 0),
    items_left INTEGER CHECK ( items_left >= 0 ),
    category_id INTEGER REFERENCES category(id)
)