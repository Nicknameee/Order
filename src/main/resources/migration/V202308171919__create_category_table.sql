CREATE TABLE IF NOT EXISTS category(
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR UNIQUE,
    parent_category_id BIGINT references category(id)
)