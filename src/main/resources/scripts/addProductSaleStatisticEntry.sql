INSERT INTO product_sales_statistic(product_id, items_sold, total_cost, vendor_id, category_id, product_version)
VALUES(?, ?, ?, ?, ?, (SELECT (COALESCE(MAX(product_version), 1))
                       FROM product_sales_statistic WHERE product_id = ?))