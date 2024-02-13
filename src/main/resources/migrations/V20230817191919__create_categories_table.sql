CREATE TABLE IF NOT EXISTS categories(
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR UNIQUE,
    parent_category_id BIGINT references categories(id)
)