CREATE TABLE IF NOT EXISTS product_sales_statistic(
    id BIGSERIAL PRIMARY KEY,
    product_id UUID NOT NULL,
    time TIMESTAMPTZ DEFAULT NOW(),
    items_sold INTEGER CHECK(items_sold > 0),
    total_cost DOUBLE PRECISION CHECK(total_cost >= 0),
    vendor_id UUID NOT NULL,
    category_id UUID NOT NULL,
    product_version INT NOT NULL CHECK(product_version > 0)
)