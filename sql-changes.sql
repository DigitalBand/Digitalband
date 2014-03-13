ALTER TABLE dbs.order_items CHANGE orderId order_id INT;
ALTER TABLE dbs.order_items CHANGE productId product_id INT;
ALTER TABLE dbs.order_items CHANGE unitPrice unit_price DOUBLE;
ALTER TABLE dbs.orders CHANGE orderId id INT;
ALTER TABLE dbs.orders CHANGE userId user_id INT;
ALTER TABLE dbs.orders CHANGE placeDate place_date TIMESTAMP;
ALTER TABLE dbs.product_images CHANGE imageId image_id INT;
ALTER TABLE dbs.product_images CHANGE productId product_id int;
ALTER TABLE dbs.products CHANGE productId id INT;
ALTER TABLE dbs.products CHANGE addedDate added_date TIMESTAMP;
ALTER TABLE dbs.products CHANGE discountPrice discount_price decimal;
ALTER TABLE dbs.products DROP discount_price;
ALTER TABLE dbs.products CHANGE shortDescription short_description TEXT;
ALTER TABLE dbs.products CHANGE defaultImageId default_image_id INT;
ALTER TABLE dbs.products CHANGE visitCounter visit_counter INT;
ALTER TABLE dbs.products CHANGE createdByUser created_by_user INT;
ALTER TABLE dbs.products CHANGE isAvailable is_available BIT;
ALTER TABLE dbs.products_categories CHANGE productId product_id INT;
ALTER TABLE dbs.products_categories CHANGE categoryId category_id INT;
ALTER TABLE dbs.roles CHANGE roleId role_id INT;
ALTER TABLE dbs.shop RENAME TO dbs.shops;
ALTER TABLE dbs.shopping_items CHANGE userId user_id INT;
