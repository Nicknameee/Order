INSERT INTO order_history(order_id, old_order, updated_order, iteration, updated_fields)
VALUES(?, ?::JSON, ?::JSON,
(SELECT COALESCE(MAX(history.iteration), 1) FROM order_history history WHERE history.order_id = ?),
                                 ?::VARCHAR[])