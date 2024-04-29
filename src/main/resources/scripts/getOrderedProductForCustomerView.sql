SELECT product.name,
       product.brand,
       product.product_id AS productId,
       product.cost,
       product.currency,
       ordered.products_amount AS sold
FROM products product
INNER JOIN ordered_products ordered ON product.id = ordered.product_id
WHERE ordered.order_id = ?