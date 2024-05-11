SELECT
    sale.time,
    SUM(
            CASE
                WHEN product.currency = 'USD' THEN sale.total_cost * (product.margin_rate - 1) * ?
                ELSE sale.total_cost * (product.margin_rate - 1)
                END
        ) as ovle
FROM
    product_sales_statistic sale
        INNER JOIN
    products product ON product.product_id = sale.product_id
WHERE
    sale.time BETWEEN ? AND ?
GROUP BY
    sale.time
ORDER BY
    sale.time DESC;
