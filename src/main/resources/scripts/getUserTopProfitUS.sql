SELECT o.customer_id, SUM(
        CASE
            WHEN q.currency = 'UAH' THEN (o.ordered_products_cost * (q.margin_rate - 1)) / ?
            ELSE (o.ordered_products_cost * (q.margin_rate - 1)) END
    ) as sum
FROM orders o
INNER JOIN ordered_products ooq ON ooq.order_id = o.id
INNER JOIN products q ON q.id = ooq.product_id
WHERE o.creation_date BETWEEN ? AND ?
GROUP BY o.customer_id
ORDER BY sum DESC
LIMIT ? OFFSET ?