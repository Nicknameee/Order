CREATE TABLE IF NOT EXISTS vendors(
    id UUID PRIMARY KEY,
    name VARCHAR UNIQUE,
    joining_date TIMESTAMPTZ DEFAULT NOW(),
    detaching_date TIMESTAMPTZ,
    site VARCHAR,
    phone VARCHAR UNIQUE,
    email VARCHAR UNIQUE
)