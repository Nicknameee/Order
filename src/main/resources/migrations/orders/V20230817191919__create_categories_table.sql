CREATE TABLE IF NOT EXISTS categories(
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR UNIQUE NOT NULL,
    category_id UUID NOT NULL UNIQUE,
    parent_category_id UUID REFERENCES categories(category_id),
    picture_url VARCHAR UNIQUE,
    enabled BOOLEAN DEFAULT FALSE NOT NULL
)