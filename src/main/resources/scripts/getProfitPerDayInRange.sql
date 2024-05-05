SELECT sale.time, SUM(sale.total_cost * (product.margin_rate - 1)) as ovle
FROM product_sales_statistic sale
INNER JOIN products product ON product.product_id = sale.product_id
WHERE sale.time BETWEEN ? AND ?
GROUP BY sale.time
ORDER BY sale.time