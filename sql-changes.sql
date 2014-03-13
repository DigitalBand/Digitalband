ALTER TABLE dbs.order_items CHANGE orderId order_id INT;
ALTER TABLE dbs.order_items CHANGE productId product_id INT;
ALTER TABLE dbs.order_items CHANGE unitPrice unit_price DOUBLE;
ALTER TABLE dbs.orders CHANGE orderId id INT;
ALTER TABLE dbs.orders CHANGE userId user_id INT;