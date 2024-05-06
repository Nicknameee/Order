SELECT o.customer_id , q.currency, SUM(o.ordered_products_cost * q.margin_rate) as sum
FROM orders o
INNER JOIN ordered_products ooq ON ooq.order_id = o.id
INNER JOIN products q ON q.id = ooq.product_id
WHERE o.creation_date BETWEEN ? AND ?
GROUP BY o.customer_id, q.currency
ORDER BY sum DESC
LIMIT ? OFFSET ?